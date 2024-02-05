package javaiscoffee.groomy.ide.comment;

import javaiscoffee.groomy.ide.board.Board;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
//@Transactional
public class CommentService {
    private final JpaCommentRepository commentRepository;
    private final JpaMemberRepository memberRepository;

    /**
     * 댓글 작성
     * @param commentDto
     * @return
     */
    public MyResponse<Comment> createComment(CommentDto commentDto) {
        Comment newComment = new Comment();
        BeanUtils.copyProperties(commentDto.getData(), newComment);
        Member creatorMember = memberRepository.findByMemberId(commentDto.getData().getMemberId()).get();
        newComment.setMember(creatorMember);
        log.info("입력 받은 댓글 정보 = {}",commentDto);
        log.info("새로 저장할 댓글 = {}",newComment);
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), commentRepository.saveComment(newComment));
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
