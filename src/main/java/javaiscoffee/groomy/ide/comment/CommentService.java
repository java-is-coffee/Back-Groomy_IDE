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
     * @return 작성한 댓글
     */
    public Comment createComment(CommentDto commentDto) {
        Comment newComment = new Comment();
        BeanUtils.copyProperties(commentDto.getData(), newComment);
        Member creatorMember = memberRepository.findByMemberId(commentDto.getData().getMemberId()).get();
        Board board = boardRepository.findByBoardId(commentDto.getData().getBoardId()).get();

        Comment originComment = null;
        //Dto에서 가져온 대댓글이 null이 아니면
        if (commentDto.getData().getOriginComment() != null) {
            //레포지토리에 있는 대댓글 값 가져옴
            originComment = commentRepository.findByCommentId(commentDto.getData().getOriginComment());
            //대댓글 null, 댓글이 DELETE 일 때
            if (originComment == null || originComment.getCommentStatus() == CommentStatus.DELETED) {
                return null;
            }
            //댓글에 대댓글 값 넣어줌
            newComment.setOriginComment(originComment);
        }
        newComment.setMember(creatorMember);
        newComment.setBoard(board);
        log.info("입력 받은 댓글 정보 = {}",commentDto);
        log.info("새로 저장할 댓글 = {}",newComment);
        return commentRepository.saveComment(newComment);
    }

    /**
     * 댓글 조회
     * @param commentId
     * @return commentId 댓글 조회
     */
    public Comment getCommentById(Long commentId) {
        Comment findedComment = commentRepository.findByCommentId(commentId);
        return findedComment;
    }

    /**
     * 댓글 수정
     * @param requestDto
     * @return nickname, content만 바꿔서 덮어씌운 old 반환
     */
    public Comment editComment(CommentEditRequestDto requestDto, Long commentId) {
        //기존 댓글 조회 후 없을 경우 에러 반환
        Comment oldComment = commentRepository.findByCommentId(commentId);
        if(oldComment == null) {
            return null;
        }
        oldComment.setNickname(requestDto.getData().getNickname());
        oldComment.setContent(requestDto.getData().getContent());
        log.info("수정된 댓글 = {}",oldComment);
        return commentRepository.updateComment(oldComment);
    }



    /**
     * 소프트 딜리트
     * @param commentId
     * @return Null 값, 성공 메세지
     */
    public Boolean deleteComment(Long commentId) {
        commentRepository.deleteComment(commentId);
        log.info("댓글 삭제 완료 = {}", commentId);
        return true;
    }
    //성공 true, 실패하면 false , 컨트롤러에서 메세지 보내는거

    /**
     * 게시판에 딸린 모든 댓글 조회
     * @param boardId
     * @return CommentStatus가 ACTIVE인 모든 댓글 리스트로 반환
     */
    public List<Comment> getCommentByBoardId(Long boardId) {
        Board getBoard = boardRepository.findByBoardId(boardId).get();
        List<Comment> commentList = commentRepository.findCommentByBoardId(getBoard, CommentStatus.ACTIVE);
        log.info("해당 게시판에 딸린 모든 댓글들 = {}", commentList);
        return commentList;
    }

    /**
     * 사용자가 작성한 모든 댓글 조회
     * @param memberId
     * @return CommentStatus가 ACTIVE인 모든 댓글 리스트로 반환
     */
    public List<Comment> getCommentByMemberId(Long memberId) {
        Member getMember = memberRepository.findByMemberId(memberId).get();
        List<Comment> commentList = commentRepository.findCommentByMemberId(getMember, CommentStatus.ACTIVE);
        log.info("해당 사용자가 작성한 모든 댓글들 = {}", commentList);
        return commentList;
    }

}
