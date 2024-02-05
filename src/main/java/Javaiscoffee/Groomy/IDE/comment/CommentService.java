package Javaiscoffee.Groomy.IDE.comment;

import Javaiscoffee.Groomy.IDE.board.Board;
import Javaiscoffee.Groomy.IDE.member.Member;
import Javaiscoffee.Groomy.IDE.response.MyResponse;
import Javaiscoffee.Groomy.IDE.response.ResponseStatus;
import Javaiscoffee.Groomy.IDE.response.Status;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
//@Transactional
public class CommentService {
    private final JpaCommentRepository commentRepository;

    /**
     * 댓글 작성
     * @param comment
     * @return
     */
    public MyResponse<Comment> createComment(Comment comment) {
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), commentRepository.saveComment(comment));
    }

    /**
     * 댓글 조회
     * @param commentId
     * @return
     */
    public MyResponse<Optional<Comment>> getCommentById(Long commentId) {
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), commentRepository.findByCommentId(commentId));
    }

    /**
     * 댓글 수정
     * @param editedComment
     * @return
     */
    public MyResponse<Comment> editComment(Comment editedComment) {
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), commentRepository.updateComment(editedComment));
    }

    /**
     * 댓글 삭제
     * @param commentId
     * @return
     */
    public MyResponse<Null> deleteComment(Long commentId) {
        commentRepository.deleteComment(commentId);
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS));
    }

    /**
     * 게시판에 딸린 모든 댓글 조회
     * @param board
     * @return
     */
    public MyResponse<List<Comment>> getCommentByBoard(Board board) {
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), commentRepository.findByBoard(board));
    }

    /**
     * 사용자가 작성한 모든 댓글 조회
     * @param member
     * @return
     */
    public MyResponse<List<Comment>> getCommentByMember(Member member) {
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), commentRepository.findByMember(member));
    }

}
//비즈니스로직
