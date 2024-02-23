package javaiscoffee.groomy.ide.comment;

import javaiscoffee.groomy.ide.board.Board;
import javaiscoffee.groomy.ide.board.BoardStatus;
import javaiscoffee.groomy.ide.board.JpaBoardRepository;
import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final JpaCommentRepository commentRepository;
    private final JpaMemberRepository memberRepository;
    private final JpaBoardRepository boardRepository;

    /**
     * 댓글 작성
     * @param commentDto
     * @return 작성한 댓글
     */
    @Transactional
    public ResponseCommentDto createComment(CommentDto commentDto, Long memberId) {
        // 작성 요청한 memberId와 작성하려는 memberId가 다른 경우 null 반환
        if (!memberId.equals(commentDto.getData().getMemberId())) {
            return null;
        }

        Comment newComment = new Comment();
        BeanUtils.copyProperties(commentDto.getData(), newComment);
        Member creatorMember = memberRepository.findByMemberId(memberId).get(); // .orElseThrow //토큰 값에서 memebrId 뽑아서 조회
        Board board = boardRepository.findByBoardId(commentDto.getData().getBoardId()).get();

        // 삭제된 게시글인 경우
        if (board.getBoardStatus() == BoardStatus.DELETE) {
            return null;
        }

        // 대댓글
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

        // 댓글 저장
        newComment.setMember(creatorMember);
        newComment.setBoard(board);
        log.info("새로 저장할 댓글 = {}",newComment);
        Comment savedComment = commentRepository.saveComment(newComment);
        return toResponseCommentDto(savedComment);
    }

    /**
     * 댓글 조회
     * @param commentId
     * @return commentId 댓글 조회
     */
    public ResponseCommentDto getCommentById(Long commentId, Long memberId) {
        Comment findedComment = commentRepository.findByCommentId(commentId);
        Board board = boardRepository.findByBoardId(findedComment.getBoard().getBoardId()).get();
        // 삭제된 댓글이나 삭제된 게시글인 경우 null 반환
        if (findedComment == null || findedComment.getCommentStatus() == CommentStatus.DELETED || board == null || board.getBoardStatus() == BoardStatus.DELETE) {
            return null;
        }

        return toResponseCommentDto(findedComment);
    }


    /**
     * 댓글 수정
     * @param requestDto
     * @return nickname, content만 바꿔서 덮어씌운 old 반환
     */
    @Transactional
    public ResponseCommentDto editComment(CommentEditRequestDto requestDto, Long commentId, Long memberId) {
        Comment oldComment = commentRepository.findByCommentId(commentId);
        Board board = boardRepository.findByBoardId(oldComment.getBoard().getBoardId()).get();

        // 원본 댓글과 게시글이 존재하는지, 게시글이 삭제 상태인지
        if(oldComment == null || oldComment.getCommentStatus() == CommentStatus.DELETED || board == null || oldComment.getBoard().getBoardStatus() == BoardStatus.DELETE ) {
            return null;
        }

        Member member = memberRepository.findByMemberId(memberId).get(); // .orElseThrow
        // 원본 댓글 memberId와 수정 요청한 멤버의 memberId가 다른 경우 null 반환
        if(!oldComment.getMember().getMemberId().equals(member.getMemberId())) {
            return null;
        }

        // 댓글 수정
        oldComment.setNickname(requestDto.getData().getNickname());
        oldComment.setContent(requestDto.getData().getContent());
        oldComment.setUpdatedTime(LocalDateTime.now());
        log.info("수정된 댓글 = {}",oldComment);
        Comment updatedComment = commentRepository.updateComment(oldComment);
        return toResponseCommentDto(updatedComment);
    }

    /**
     * 소프트 딜리트
     * @param commentId
     * @return
     */
    @Transactional
    public Boolean deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findByCommentId(commentId);
        // 댓글이 존재하지 않는 경우, 삭제된 댓글인 경우 삭제 실패 false
        if (comment == null || comment.getCommentStatus() == CommentStatus.DELETED) {
            return false;
        }

        // boardId in comment로 boardRepo에서 boardId 조회
        Board board = boardRepository.findByBoardId(comment.getBoard().getBoardId()).get();
        // 게시글이 존재하지 않는 경우, 삭제된 게시글인 경우 삭제 실패 false
        if (board == null || board.getBoardStatus() == BoardStatus.DELETE) {
            return false;
        }

        Member member = memberRepository.findByMemberId(memberId).get(); // .orElseThrow
        // 멤버가 존재하지 않는 경우, member가 댓글 작성자가 아닌 경우 삭제 실패 false
        if(!comment.getMember().getMemberId().equals(member.getMemberId())) {
            return false;
        }

        // 댓글 삭제
        commentRepository.deleteComment(commentId);
        log.info("댓글 삭제 완료 = {}", commentId);
        return true;
    }

    /**
     * 게시판에 딸린 모든 댓글 조회
     * @param boardId
     * @return CommentStatus가 ACTIVE인 모든 댓글 리스트로 반환
     */
    public List<ResponseCommentDto> getCommentByBoardId(Long boardId) {
        // boardId 가져옴
        Board getBoard = boardRepository.findByBoardId(boardId).get();
        // 가져온 board에 속한 ACTIVE 상태인 모든 댓글 가져옴
        List<Comment> commentList = commentRepository.findCommentByBoardId(getBoard, CommentStatus.ACTIVE);
        log.info("해당 게시판에 딸린 모든 댓글들 = {}", commentList);
        return toResponseCommentDtoList(commentList);
    }

    /**
     * 사용자가 작성한 모든 댓글 조회
     * @param memberId
     * @return CommentStatus가 ACTIVE인 모든 댓글 리스트로 반환
     */
    public List<ResponseCommentDto> getCommentByMemberId(Long memberId) {
        Member getMember = memberRepository.findByMemberId(memberId).get();
        List<Comment> commentList = commentRepository.findCommentByMemberId(getMember, CommentStatus.ACTIVE);
        log.info("해당 사용자가 작성한 모든 댓글들 = {}", commentList);
        return toResponseCommentDtoList(commentList);
    }


    // 댓글 추천
    @Transactional
    public ResponseCommentDto toggleGoodComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findByCommentId(commentId);
        Member member = memberRepository.findByMemberId(memberId).get();
        CommentHelpNumberId helpNumberId = new CommentHelpNumberId(member.getMemberId(),comment.getCommentId());
        CommentHelpNumber helpNumber = commentRepository.findCommentHelpNumber(helpNumberId);
        // 댓글이 존재하지 않거나 삭제된 상태인 경우, 자신이 작성한 댓글일 경우 null 반환
        if (comment == null || comment.getCommentStatus() == CommentStatus.DELETED
                || comment.getMember().getMemberId().equals(memberId)) {
            return null;
        }
        else {
            //유저가 댓글을 추천한 적이 없는 경우
            if(helpNumber == null) {
                helpNumber = new CommentHelpNumber(helpNumberId,member,comment);
                commentRepository.saveCommentHelpNumber(helpNumber);
                comment.setHelpNumber(comment.getHelpNumber()+1);
                comment.getMember().setHelpNumber(comment.getMember().getHelpNumber() + 1);
                comment = commentRepository.updateComment(comment);
                log.info("추천합니다");
            }
            //유저가 댓글을 추천한 적이 있는 경우
            else {
                if(!commentRepository.deleteCommentHelpNumber(helpNumber)) {
                    return null;
                }
                comment.setHelpNumber(comment.getHelpNumber()-1);
                comment.getMember().setHelpNumber(comment.getMember().getHelpNumber() - 1);
                comment = commentRepository.updateComment(comment);
            }
            return toResponseCommentDto(comment);
        }

    }



    // Comment 객체를 ResponseCommentDto로 매핑하는 메서드
    public static ResponseCommentDto toResponseCommentDto(Comment comment) {
        if (comment == null) {
            return null; // 주어진 Comment가 null인 경우, null 반환
        }

        ResponseCommentDto responseCommentDto = new ResponseCommentDto();

        responseCommentDto.setBoardId(comment.getBoard() != null ? comment.getBoard().getBoardId() : null);
        //삭제된 원댓글일 때
        if(comment.getCommentStatus() == CommentStatus.DELETED) {
            responseCommentDto.setMemberId(null);
            responseCommentDto.setNickname("알 수 없음");
            responseCommentDto.setContent("삭제된 댓글입니다.");
        }
        //삭제 안된 댓글일 경우
        else {
            responseCommentDto.setMemberId(comment.getMember() != null ? comment.getMember().getMemberId() : null);
            responseCommentDto.setNickname(comment.getNickname());
            responseCommentDto.setContent(comment.getContent());
        }
        responseCommentDto.setOriginComment(comment.getOriginComment() != null ? comment.getOriginComment() : null);
        responseCommentDto.setCommentId(comment.getCommentId());
        responseCommentDto.setHelpNumber(comment.getHelpNumber());
        responseCommentDto.setCreatedTime(comment.getCreatedTime());
        responseCommentDto.setCommentStatus(comment.getCommentStatus());
        responseCommentDto.setMemberHelpNumber(comment.getMember().getHelpNumber());

        return responseCommentDto;
    }

    // Comment 객체 리스트를 ResponseCommentDto객체 리스트로 매핑하는 메서드
    public static List<ResponseCommentDto> toResponseCommentDtoList(List<Comment> comments) {
        if (comments == null) {
            return null; // 주어진 Comment 리스트가 null인 경우, null 반환
        }

        List<ResponseCommentDto> responseCommentDtoList = new ArrayList<>();

        for (Comment comment : comments) {
            ResponseCommentDto responseCommentDto = toResponseCommentDto(comment);
            responseCommentDtoList.add(responseCommentDto);
        }

        return responseCommentDtoList;
    }


}
