package javaiscoffee.groomy.ide.comment;

import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import javaiscoffee.groomy.ide.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 작성 할 때 필요한 API
     */
    @PostMapping("/comment/write/{boardId}")
    public ResponseEntity<?> writeComment(@RequestBody CommentDto commentDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 작성 요청한 member의 memberId
        Long memberId = userDetails.getMemberId();
        log.info("입력 받은 댓글 정보 = {}", commentDto);
        ResponseCommentDto savedComment = commentService.createComment(commentDto, memberId);
        //댓글 = null 에러 반환
        if (savedComment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        } else {
            return ResponseEntity.ok(savedComment);
        }
    }

    /**
     * 단일 댓글 조회 할 때 필요한 API
     */
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<?> getCommentById(@PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        //조회 요청한 member의 memberId
        Long memberId = userDetails.getMemberId();
        log.info("commentId = {}", commentId);
        ResponseCommentDto findedComment = commentService.getCommentById(commentId, memberId);
        if (findedComment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(findedComment);
    }

    /**
     * 댓글 수정할 때 필요한 API
     */
    @PatchMapping("/comment/edit/{commentId}")
    public ResponseEntity<?> editComment(@RequestBody CommentEditRequestDto requestDto, @PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 수정 요청한 member의 memberId
        Long memberId = userDetails.getMemberId();
        // 로직 수행
        ResponseCommentDto editedComment = commentService.editComment(requestDto, commentId, memberId);
        if (editedComment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        }
        return ResponseEntity.ok(editedComment);
    }

    /**
     * 댓글 삭제할 때 필요한 API
     */
    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<?> deleteCommentById(@PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 삭제 요청한 member의 memberId
        Long memberId = userDetails.getMemberId();
        Boolean deletedComment = commentService.deleteComment(commentId, memberId);
        if(!deletedComment) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(null);
    }


    /**
     * 따봉
     */
    @PostMapping("/comment/good/{commentId}")
    public ResponseEntity<?> clickGood(@PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        ResponseCommentDto updatedHelpNumber = commentService.toggleGoodComment(commentId, memberId);

        return ResponseEntity.ok(updatedHelpNumber);
    }


    /**
     * 게시글에 딸린 모든 댓글 조회할 때 필요한 API
     */
    @GetMapping("/comment/board/{boardId}")
    public ResponseEntity<?> getCommentByBoardId(@PathVariable Long boardId) {
        return ResponseEntity.ok(commentService.getCommentByBoardId(boardId));
    }

    /**
     * 멤버가 작성한 모든 댓글 조회할 때 필요한 API
     */
    @GetMapping("/comment/member/{memberId}")
    public ResponseEntity<?> getCommentByMemberId(@PathVariable Long memberId) {
        return ResponseEntity.ok(commentService.getCommentByMemberId(memberId));
    }


}

