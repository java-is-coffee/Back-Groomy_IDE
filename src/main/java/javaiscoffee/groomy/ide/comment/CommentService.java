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
        // 대댓글인 경우
        if (commentDto.getData().getOriginComment() != null) {
            Comment originCommentEntity = commentRepository.findByCommentId(commentDto.getData().getOriginComment()).orElse(null);

            if (originCommentEntity != null) {
                newComment.setOriginComment(originCommentEntity);
            }
        }
        //null이면 삭제니까 대댓글 등록하면 안돼서 return 마이리스폰스 에러 코드 전송
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
     * @param requestDto
     * @return
     */
    public MyResponse<Comment> editComment(CommentEditRequestDto requestDto, Long commentId) {
        //기존 댓글 조회 후 없을 경우 에러 반환
        Optional<Comment> oldComment = commentRepository.findByCommentId(commentId);
        if(oldComment.isEmpty()) {
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }
        Comment old = oldComment.get();
        BeanUtils.copyProperties(commentId,old);

        old.setNickname(requestDto.getData().getNickname());
        old.setContent(requestDto.getData().getContent());
        log.info("수정된 댓글 = {}",old);
        // commentId로 기존 댓글 조회
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), commentRepository.updateComment(old));
    }
    //커멘트 가져오고
    //가져온 거에서 nickname, content만 바꿔서 덮어씌우기



    /**
     * 소프트 딜리트로 구현할 것
     * @param commentId
     * @return Null 값, 성공 메세지
     */
    //commentStatus 가 ACTIVE인 댓글만 + createdTime 오름차순으로 정렬한 결과값 반환
    public MyResponse<Null> deleteComment(Long commentId) {
        commentRepository.deleteComment(commentId);
        log.info("댓글 삭제 완료 = {}", commentId);
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS));
    }

    /**
     * 게시판에 딸린 모든 댓글 조회
     * @param boardId
     * @return 모든 댓글 리스트로 반환
     */
    public MyResponse<List<Comment>> getCommentByBoardId(Long boardId) {
        Board getBoard = boardRepository.findByBoardId(boardId).get();
        List<Comment> commentList = commentRepository.findCommentByBoardId(getBoard, CommentStatus.ACTIVE);
        log.info("해당 게시판에 딸린 모든 댓글들 = {}", commentList);
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), commentList);
    }

    /**
     * 사용자가 작성한 모든 댓글 조회
     * @param member
     * @return
     */
    public MyResponse<List<Comment>> getCommentByMemberId(Member member) {
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), commentRepository.findCommentByMemberId(member));
    }

}
//비즈니스로직
