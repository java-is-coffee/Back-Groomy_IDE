package javaiscoffee.groomy.ide.comment;

import javaiscoffee.groomy.ide.board.Board;
import javaiscoffee.groomy.ide.board.JpaBoardRepository;
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
    private final JpaBoardRepository boardRepository;

    /**
     * 댓글 작성
     * @param commentDto
     * @return
     */
    public MyResponse<Comment> createComment(CommentDto commentDto) {
        Comment newComment = new Comment();
        BeanUtils.copyProperties(commentDto.getData(), newComment);
        Member creatorMember = memberRepository.findByMemberId(commentDto.getData().getMemberId()).get();
        Board board = boardRepository.findByBoardId(commentDto.getData().getBoardId()).get();
//        // 대댓글인 경우
//        if (commentDto.getData().getOriginComment() != null) {
//            Comment originCommentEntity = commentRepository.findByCommentId(commentDto.getData().getOriginComment()).orElse(null);
//
//            if (originCommentEntity != null) {
//                newComment.setOriginComment(originCommentEntity);
//            }
//        }
        newComment.setMember(creatorMember);
        newComment.setBoard(board);
        log.info("입력 받은 댓글 정보 = {}",commentDto);
        log.info("새로 저장할 댓글 = {}",newComment);
//        log.info("저장할 댓글의 board 정보 = {}",board);
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), commentRepository.saveComment(newComment));
    }

    /**
     * 댓글 조회
     * @param commentId
     * @return
     */
    public MyResponse<Optional<Comment>> getCommentById(Long commentId) {
        Comment getComment = commentRepository.findByCommentId(commentId).get();
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), commentRepository.findByCommentId(commentId));
    }

    /**
     * 댓글 수정
     * @param editedComment
     * @return
     */
    public MyResponse<Comment> editComment(CommentEditRequestDto requestDto) {
        Comment editedComment = requestDto.getData().getComment();
        //기존 댓글 조회 후 없을 경우 에러 반환
        Optional<Comment> oldComment = commentRepository.findByCommentId(editedComment.getCommentId());
        if(commentRepository.findByCommentId(editedComment.getCommentId()).isEmpty()) {
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }
        Comment old = oldComment.get();
        BeanUtils.copyProperties(editedComment,old);
        // commentId로 기존 댓글 조회
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), commentRepository.updateComment(old));
    }

    /**
     * 댓글 삭제
     * @param commentId
     * @return
     */
    public MyResponse<Null> deleteComment(Long commentId) {
        commentRepository.deleteComment(commentId);
        log.info("댓글 삭제 완료 = {}", commentId);
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
