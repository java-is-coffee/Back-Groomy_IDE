package Javaiscoffee.Groomy.IDE.board;

import Javaiscoffee.Groomy.IDE.member.JpaMemberRepository;
import Javaiscoffee.Groomy.IDE.member.Member;
import Javaiscoffee.Groomy.IDE.response.MyResponse;
import Javaiscoffee.Groomy.IDE.response.ResponseStatus;
import Javaiscoffee.Groomy.IDE.response.Status;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
     * @param boardDto
     * @return
     */
    public MyResponse<Board> createBoard(BoardDto boardDto) {
        Board newBoard = new Board();
        BeanUtils.copyProperties(boardDto.getData(),newBoard);
        Member creatorMember = memberRepository.findByMemberId(boardDto.getData().getMemberId()).get();
        newBoard.setMember(creatorMember);
        log.info("입력 받은 게시글 정보 = {}",boardDto);
        log.info("새로 저장할 게시글 = {}",newBoard);
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), boardRepository.saveBoard(newBoard));
    }

    /**
     * 게시글 조회
     *
     * @param boardId
     * @return
     */
    public MyResponse<Optional<Board>> getBoardById(Long boardId) {
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), boardRepository.findByBoardId(boardId));
    }

    /**
     * 게시글 수정
     *
     * @param editedBoard
     * @return
     */
    public MyResponse<Board> editBoard(Board editedBoard) {
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), boardRepository.updateBoard(editedBoard));
    }

    /**
     * 게시글 삭제
     *
     * @param boardId
     * @return
     */
    public MyResponse<Null> deleteBoard(Long boardId) {
        boardRepository.deleteBoard(boardId);
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS));
    }

    /**
     * 사용자가 작성한 모든 게시글 조회
     * @param member
     * @return
     */
    public MyResponse<List<Board>> getBoardByMember(Member member) {
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), boardRepository.findByMember(member));
    }

}
//비즈니스로직
