package javaiscoffee.groomy.ide.board;

import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.response.MyResponse;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor //requiredargs~?
//@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final JpaMemberRepository memberRepository;

    /**
     * 게시글 작성
     *
     * @param requestBoardDto
     * @return
     */
    public ResponseBoardDto createBoard(RequestBoardDto requestBoardDto) {
        Board newBoard = new Board();
        BeanUtils.copyProperties(requestBoardDto.getData(), newBoard);
        Member creatorMember = memberRepository.findByMemberId(requestBoardDto.getData().getMemberId()).get();
        newBoard.setMember(creatorMember);
        Board savedBoard = boardRepository.saveBoard(newBoard);

        ResponseBoardDto responseBoardDto = new ResponseBoardDto(
                savedBoard.getBoardId(),
                savedBoard.getMember().getMemberId(),
                savedBoard.getNickname(),
                savedBoard.getTitle(),
                savedBoard.getContent(),
                savedBoard.getCreatedTime(),
                savedBoard.getViewNumber(),
                savedBoard.getCommentNumber(),
                savedBoard.getScrapNumber(),
                savedBoard.getHelpNumber(),
                savedBoard.getBoardStatus(),
                savedBoard.isCompleted()
        );

        return responseBoardDto;
    }

    /**
     * 게시글 조회
     *
     * @param boardId
     * @return
     */
    public ResponseBoardDto getBoardById(Long boardId) {
        Board findBoard = boardRepository.findByBoardId(boardId).get();
        findBoard.setViewNumber(findBoard.getViewNumber() + 1);
        Board updatedFindBoard = boardRepository.updateBoard(findBoard);

        ResponseBoardDto responseBoardDto = new ResponseBoardDto(
                updatedFindBoard.getBoardId(),
                updatedFindBoard.getMember().getMemberId(),
                updatedFindBoard.getNickname(),
                updatedFindBoard.getTitle(),
                updatedFindBoard.getContent(),
                updatedFindBoard.getCreatedTime(),
                updatedFindBoard.getViewNumber(),
                updatedFindBoard.getCommentNumber(),
                updatedFindBoard.getScrapNumber(),
                updatedFindBoard.getHelpNumber(),
                updatedFindBoard.getBoardStatus(),
                updatedFindBoard.isCompleted()
        );

        return responseBoardDto;
    }

    /**
     * 게시글 수정
     *
     * @param requestBoardDto
     * @return
     */
    public ResponseBoardDto editBoard(@RequestBody RequestBoardDto requestBoardDto, Long boardId) {
        Board findBoard = boardRepository.findByBoardId(boardId).get();
        BeanUtils.copyProperties(requestBoardDto.getData(), findBoard);
        Board editedBoard = boardRepository.updateBoard(findBoard);

        ResponseBoardDto responseBoardDto = new ResponseBoardDto(
                editedBoard.getBoardId(),
                editedBoard.getMember().getMemberId(),
                editedBoard.getNickname(),
                editedBoard.getTitle(),
                editedBoard.getContent(),
                editedBoard.getCreatedTime(),
                editedBoard.getViewNumber(),
                editedBoard.getCommentNumber(),
                editedBoard.getScrapNumber(),
                editedBoard.getHelpNumber(),
                editedBoard.getBoardStatus(),
                editedBoard.isCompleted()
        );

        return responseBoardDto;
    }

    /**
     * 게시글 삭제
     *
     * @param boardId
     * @return
     */
    public Boolean deleteBoard(Long boardId) {
        Optional<Board> deletedBoardOptional = boardRepository.findByBoardId(boardId);

        if(deletedBoardOptional.isPresent()) {
            Board deletedBoard = deletedBoardOptional.get();
            deletedBoard.setBoardStatus(BoardStatus.DELETE);
            boardRepository.deleteBoard(deletedBoard);

            return true;
        } else {
            return false;
        }
    }

    /**
     * 사용자가 작성한 모든 게시글 조회
     * @param paging
     * @return
     */
    public List<ResponseBoardDto> getBoardByPaging(int paging) {
        List<Board> boardList = boardRepository.findBoardByPaging(paging, 10, BoardStatus.ACTIVE);
        List<ResponseBoardDto> responseBoardDtoList = new ArrayList<>();

        for(int i = 0; i < boardList.size(); i++) {
            Board boardListIndex = boardList.get(i);

            ResponseBoardDto responseBoardDto = new ResponseBoardDto(
                    boardListIndex.getBoardId(),
                    boardListIndex.getMember().getMemberId(),
                    boardListIndex.getNickname(),
                    boardListIndex.getTitle(),
                    boardListIndex.getContent(),
                    boardListIndex.getCreatedTime(),
                    boardListIndex.getViewNumber(),
                    boardListIndex.getCommentNumber(),
                    boardListIndex.getScrapNumber(),
                    boardListIndex.getHelpNumber(),
                    boardListIndex.getBoardStatus(),
                    boardListIndex.isCompleted()
            );

            responseBoardDtoList.add(responseBoardDto);
        }

        return responseBoardDtoList;
    }

    /**
     * 사용자가 작성한 모든 게시글 조회
     * @param member
     * @return
     */
    public List<Board> getBoardByMemberMemberId(Member member) {
        return boardRepository.findBoardByMemberId(member);
    }
}
//비즈니스로직
