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

    @PostMapping("/comment/write/{boardId}")
    public ResponseEntity<?> writeComment(@RequestBody CommentDto commentDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        String email = userDetails.getUsername();
        log.info("입력 받은 댓글 정보 = {}", commentDto);
        Comment savedComment = commentService.createComment(commentDto);

        //댓글 = null 에러 반환
        if (savedComment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        } else {
            return ResponseEntity.ok(savedComment);
        }
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<?> getCommentById(@PathVariable Long commentId) {
        log.info("commentId = {}", commentId);
        Comment findedComment = commentService.getCommentById(commentId);
        if (findedComment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(findedComment);
    }


    @PatchMapping("/comment/edit/{commentId}")
    public ResponseEntity<?> editComment(@RequestBody CommentEditRequestDto requestDto, @PathVariable Long commentId) {
        Comment editedComment = commentService.editComment(requestDto, commentId);
        if (editedComment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        }
        return ResponseEntity.ok(editedComment);
    }


    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<?> deleteCommentById(@PathVariable Long commentId) {
        if(!commentService.deleteComment(commentId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/comment/board/{boardId}")
    public ResponseEntity<?> getCommentByBoardId(@PathVariable Long boardId) {
        return ResponseEntity.ok(commentService.getCommentByBoardId(boardId));
    }


    @GetMapping("/comment/member/{memberId}")
    public ResponseEntity<?> getCommentByMemberId(@PathVariable Long memberId) {
        return ResponseEntity.ok(commentService.getCommentByMemberId(memberId));
    }


}

