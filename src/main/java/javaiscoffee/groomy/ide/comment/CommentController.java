package javaiscoffee.groomy.ide.comment;

import javaiscoffee.groomy.ide.board.Board;
import javaiscoffee.groomy.ide.response.MyResponse;
import jakarta.validation.constraints.Null;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comment/write/{boardId}")
    public ResponseEntity<?> writeComment(@RequestBody CommentDto commentDto) {
        log.info("입력 받은 댓글 정보 = {}", commentDto);
        Comment savedComment = commentService.createComment(commentDto);
        if (savedComment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        }
        return ResponseEntity.ok(savedComment);
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


    @GetMapping("/comments/member/{memberId}")
    public ResponseEntity<?> getCommentByMemberId(@PathVariable Long memberId) {
        return ResponseEntity.ok(commentService.getCommentByMemberId(memberId));
    }

    //대댓글 API
    @PostMapping("/inner-comment/write/{boardId}/{commentId}")
    public ResponseEntity<?> writeOriginComment(@RequestBody CommentDto commentDto) {
        log.info("입력 받은 대댓글 정보 = {}", commentDto);
        Comment savedComment = commentService.createComment(commentDto);
        if (savedComment == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        }
        return ResponseEntity.ok(savedComment);
    }

}

