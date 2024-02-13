package javaiscoffee.groomy.ide.board;

import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor //requiredargs~?
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;
    private final JpaMemberRepository memberRepository;
    private final JpaBoardRepository jpaBoardRepository;

    /**
     * 게시글 작성
     *
     * @param requestBoardDto
     * @return
     */
    @Transactional
    public ResponseBoardDto createBoard(RequestBoardDto requestBoardDto, Long memberId) {
        Board newBoard = new Board();
        BeanUtils.copyProperties(requestBoardDto.getData(), newBoard);
        Member creatorMember = memberRepository.findByMemberId(memberId).get();

        if(Objects.equals(memberId, requestBoardDto.getData().getMemberId()) && creatorMember != null) {
            newBoard.setMember(creatorMember);
            Board savedBoard = boardRepository.saveBoard(newBoard);
            ResponseBoardDto responseBoardDto = responseBoardDto(savedBoard);

            return responseBoardDto;
        } else {
            return null;
        }
    }

    /**
     * 게시글 조회
     *
     * @param boardId
     * @return
     */
    public ResponseBoardDto getBoardById(Long boardId) {
        Board findBoard = boardRepository.findByBoardId(boardId).get();

        if(findBoard != null && findBoard.getBoardStatus() == BoardStatus.ACTIVE) {
            findBoard.setViewNumber(findBoard.getViewNumber() + 1);
            Board updatedFindBoard = boardRepository.updateBoard(findBoard);

            ResponseBoardDto responseBoardDto = responseBoardDto(updatedFindBoard);

            return responseBoardDto;
        } else {
            return null;
        }
    }

    /**
     * 게시글 수정
     *
     * @param requestBoardDto
     * @return
     */
    @Transactional
    public ResponseBoardDto editBoard(@RequestBody RequestBoardDto requestBoardDto, Long boardId, Long memberId) {
        Board findBoard = boardRepository.findByBoardId(boardId).get();
        Member member = memberRepository.findByMemberId(memberId).get();

        if(member != null && member.equals(findBoard.getMember()) &&
                findBoard != null && findBoard.getBoardStatus() == BoardStatus.ACTIVE) {
            BeanUtils.copyProperties(requestBoardDto.getData(), findBoard);
            Board editedBoard = boardRepository.updateBoard(findBoard);
            ResponseBoardDto responseBoardDto = responseBoardDto(editedBoard);

            return responseBoardDto;
        } else {
            return null;
        }
    }

    /**
     * 게시글 삭제
     *
     * @param boardId
     * @return
     */
    @Transactional
    public Boolean deleteBoard(Long boardId, Long memberId) {
        Optional<Board> findBoardOptional = boardRepository.findByBoardId(boardId);
        Member member = memberRepository.findByMemberId(memberId).get();

        if(member != null && findBoardOptional.isPresent()) {
            Board findBoard = findBoardOptional.get();

            if(member.equals(findBoard.getMember()) && findBoard.getBoardStatus() == BoardStatus.ACTIVE) {
                findBoard.setBoardStatus(BoardStatus.DELETE);
                boardRepository.deleteBoard(findBoard);

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 게시판 리스트 확인
     * @param paging
     * @return
     */
    public List<ResponseBoardDto> getBoardByPaging(int paging) {
        List<Board> boardList = boardRepository.findBoardByPaging(paging, 10, BoardStatus.ACTIVE);

        if(!boardList.isEmpty()) {
            List<ResponseBoardDto> responseBoardDtoList = new ArrayList<>();

            for(int i = 0; i < boardList.size(); i++) {
                Board boardListIndex = boardList.get(i);

                if(boardListIndex.getBoardStatus() == BoardStatus.ACTIVE) {
                    ResponseBoardDto responseBoardDto = responseBoardDto(boardListIndex);
                    responseBoardDtoList.add(responseBoardDto);
                }
            }

            return responseBoardDtoList;
        } else {
            return null;
        }
    }

    /**
     * 게시판 페이지 갯수 확인
     * @param
     * @return
     */
    public long getBoardPageNumber() {
        return (long)Math.ceil(boardRepository.countBoardsByStatus(BoardStatus.ACTIVE) / (double)10);
    }

    /**
     * 내가 적은 게시글 조회
     * @param paging
     * @return
     */
    public List<ResponseBoardDto> getMyBoardByPaging(int paging, Long memberId) {
        List<Board> boardList = boardRepository.findBoardByPaging(paging, 10, BoardStatus.ACTIVE);
        Member member = memberRepository.findByMemberId(memberId).get();

        if(member != null && !boardList.isEmpty()) {
            List<ResponseBoardDto> responseBoardDtoList = new ArrayList<>();

            for(int i = 0; i < boardList.size(); i++) {
                Board boardListIndex = boardList.get(i);

                if(member.equals(boardListIndex.getMember()) && boardListIndex.getBoardStatus() == BoardStatus.ACTIVE) {
                    ResponseBoardDto responseBoardDto = responseBoardDto(boardListIndex);
                    responseBoardDtoList.add(responseBoardDto);
                }
            }

            return responseBoardDtoList;
        } else {
            return null;
        }
    }

    /**
     * 게시글 추천
     * @param boardId
     * @param memberId
     * @return
     */
    @Transactional
    public ResponseBoardDto toggleGoodBoard(Long boardId, Long memberId) {
        Board findBoard = boardRepository.findByBoardId(boardId).get();
        Member member = memberRepository.findByMemberId(memberId).get();
        HelpBoardId helpBoardId = new HelpBoardId(member.getMemberId(), findBoard.getBoardId());
        HelpBoard helpBoard = jpaBoardRepository.findBoardHelpNumber(helpBoardId);
        // 게시글이 존재하거나 ACTIVE인 상태, 자신이 작성한 댓글이 아닌 경우 추천 가능
        if(findBoard != null && findBoard.getBoardStatus() == BoardStatus.ACTIVE
                && !findBoard.getMember().getMemberId().equals(memberId)) {
            // 유저가 게시글을 추천한 적이 없는 경우
            if (helpBoard == null) {
                helpBoard = new HelpBoard(helpBoardId, member, findBoard);
                jpaBoardRepository.saveBoardHelpNumber(helpBoard);
                findBoard.setHelpNumber(findBoard.getHelpNumber()+1);
                findBoard = boardRepository.updateBoard(findBoard);
                log.info("게시글 추천");
            }
            // 유저가 게시글을 추천한 적이 있다면
            else {
                if (!(jpaBoardRepository.deleteBoardHelpNumber(helpBoard))) {
                    return null;
                }
                findBoard.setHelpNumber(findBoard.getHelpNumber()-1);
                findBoard = boardRepository.updateBoard(findBoard);
                log.info("게시글 추천 취소");
            }
            return responseBoardDto(findBoard);
        } else {
            log.info("게시글 추천 예외 발생 = {}",memberId);
            return null;
        }
    }

    /**
     * 스크랩
     * @param boardId
     * @param memberId
     * @return
     */
    @Transactional
    public ResponseBoardDto toggleScrap(Long boardId, Long memberId) {
        Board board = boardRepository.findByBoardId(boardId).get();
        Member member = memberRepository.findByMemberId(memberId).get();
        ScrapId scrapId = new ScrapId(member.getMemberId(), board.getBoardId());
        Scrap scrap = jpaBoardRepository.findBoardScrap(scrapId);
        // 게시글이 존재하거나 ACTIVE인 상태, 자신이 작성한 게시글이 아닌 경우 스크랩 가능
        if (board != null && board.getBoardStatus() == BoardStatus.ACTIVE
                && !board.getMember().getMemberId().equals(memberId)) {
            // 유저가 게시글을 스크랩한 적이 없는 경우
            if (scrap == null) {
                scrap = new Scrap(scrapId, member, board);
                jpaBoardRepository.saveBoardScrap(scrap);
                board.setScrapNumber(board.getScrapNumber()+1);
                board = boardRepository.updateBoard(board);
            }
            // 유저가 스크랩을 한 적이 있다면
            else {
                if (!(jpaBoardRepository.deleteBoardScrap(scrap))) {
                    return null;
                }
                board.setScrapNumber(board.getScrapNumber() - 1);
                board = boardRepository.updateBoard(board);
            }
            return responseBoardDto(board);
        }
        else {
            return null;
        }

    }



    /**
     * 사용자가 작성한 모든 게시글 조회
     * @param member
     * @return
     */
    public List<Board> getBoardByMemberMemberId(Member member) {
        return boardRepository.findBoardByMemberId(member);
    }

    public ResponseBoardDto responseBoardDto(Board board) {
        return new ResponseBoardDto(
                board.getBoardId(),
                board.getMember().getMemberId(),
                board.getNickname(),
                board.getTitle(),
                board.getContent(),
                board.getCreatedTime(),
                board.getViewNumber(),
                board.getCommentNumber(),
                board.getScrapNumber(),
                board.getHelpNumber(),
                board.getBoardStatus(),
                board.isCompleted()
        );
    }
}
//비즈니스로직
