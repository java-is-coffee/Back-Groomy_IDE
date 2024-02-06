package javaiscoffee.groomy.ide.comment;

import javaiscoffee.groomy.ide.board.Board;
import javaiscoffee.groomy.ide.response.MyResponse;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comments/write/{boardId}")
    public MyResponse<Comment> writeComment(@RequestBody CommentDto commentDto) {
        log.info("입력 받은 댓글 정보 = {}", commentDto);
        return commentService.createComment(commentDto);
    }

    @GetMapping("/comments/{commentId}")
    public MyResponse<Optional<Comment>> getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }


    @PutMapping("/edit/{commentId}")
    public MyResponse<Comment> editComment(@RequestBody Comment editedComment) {
        return commentService.editComment(editedComment);
    }


    @DeleteMapping("/comments/delete/{commentId}")
    public MyResponse<Null> deleteCommentById(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }

    @GetMapping("/comments/board/{boardId}")
    public MyResponse<List<Comment>> getCommentByBoardId(@PathVariable Long boardId) {
        return commentService.getCommentByBoardId(boardId);
    }


    @GetMapping("/comments/member/{memberId}")
    public MyResponse<List<Comment>> getCommentByMemberId(@PathVariable Long memberId) {
        return commentService.getCommentByMemberId(memberId);
    }

    //대댓글 API
    @PostMapping("/inner-comment/write/{boardId}/{commentId}")
    public MyResponse<Comment> writeOriginComment(@RequestBody CommentDto commentDto) {
        log.info("입력 받은 대댓글 정보 = {}", commentDto);
        return commentService.createComment(commentDto);
    }

}

