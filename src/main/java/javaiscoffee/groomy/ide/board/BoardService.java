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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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
    public MyResponse<ResponseBoardDto> createBoard(RequestBoardDto requestBoardDto) {
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

        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), responseBoardDto);
    }

    /**
     * 게시글 조회
     *
     * @param boardId
     * @return
     */
    public MyResponse<ResponseBoardDto> getBoardById(Long boardId) {
        Board findBoard = boardRepository.findByBoardId(boardId).get();

        ResponseBoardDto responseBoardDto = new ResponseBoardDto(
                findBoard.getBoardId(),
                findBoard.getMember().getMemberId(),
                findBoard.getNickname(),
                findBoard.getTitle(),
                findBoard.getContent(),
                findBoard.getCreatedTime(),
                findBoard.getViewNumber(),
                findBoard.getCommentNumber(),
                findBoard.getScrapNumber(),
                findBoard.getHelpNumber(),
                findBoard.getBoardStatus(),
                findBoard.isCompleted()
        );

        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), responseBoardDto);
    }

    /**
     * 게시글 수정
     *
     * @param requestBoardDto
     * @return
     */
    public MyResponse<ResponseBoardDto> editBoard(@RequestBody RequestBoardDto requestBoardDto, Long boardId) {
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

        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), responseBoardDto);
    }

    /**
     * 게시글 삭제
     *
     * @param boardId
     * @return
     */
    public MyResponse<Null> deleteBoard(Long boardId) {
        Optional<Board> deletedBoardOptional = boardRepository.findByBoardId(boardId);

        if(deletedBoardOptional.isPresent()) {
            Board deletedBoard = deletedBoardOptional.get();
            deletedBoard.setBoardStatus(BoardStatus.DELETE);
            boardRepository.deleteBoard(deletedBoard);

            return new MyResponse<>(new Status(ResponseStatus.SUCCESS));
        } else {
            return new MyResponse<>(new Status(ResponseStatus.FORBIDDEN));
        }
    }

    /**
     * 사용자가 작성한 모든 게시글 조회
     * @param paging
     * @return
     */
    public MyResponse<List<ResponseListBoardDto>> getBoardByPaging(Long paging) {






        return null;
//        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), boardRepository.findBoardByBoardId(member));
    }

    /**
     * 사용자가 작성한 모든 게시글 조회
     * @param member
     * @return
     */
    public MyResponse<List<Board>> getBoardByMemberMemberId(Member member) {
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), boardRepository.findBoardByMemberId(member));
    }

}
//비즈니스로직
