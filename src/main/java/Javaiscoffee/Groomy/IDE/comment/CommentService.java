package Javaiscoffee.Groomy.IDE.comment;

import Javaiscoffee.Groomy.IDE.member.Member;
import Javaiscoffee.Groomy.IDE.response.MyResponse;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor //requiredargs~?
//@Transactional
public class CommentService {
    private final JpaCommentRepository commentRepository;

    /**
     * 댓글 작성
     *
     * @param comment
     * @return
     */
    public MyResponse<Comment> createComment(Comment comment) {
        return
    }

    /**
     * 댓글 조회
     *
     * @param commentId
     * @return
     */
    public MyResponse<Comment> getCommentById(Long commentId) {
        return
    }

    /**
     * 댓글 수정
     *
     * @param editedComment
     * @return
     */
    public MyResponse<Comment> editComment(Comment editedComment) {
        return
    }

    /**
     * 댓글 삭제
     *
     * @param commentId
     * @return
     */
    public MyResponse<Null> deleteComment(Long commentId) {
        return
    }

    /**
     * 게시판에 딸린 모든 댓글 조회
     * @param board
     * @return
     */
    public MyResponse<List<Comment>> getCommentByBoard(Board board) {
        return
    }

    /**
     * 사용자가 작성한 모든 댓글 조회
     * @param member
     * @return
     */
    public MyResponse<List<Comment>> getCommentByMember(Member member) {
        return
    }

}
//비즈니스로직
