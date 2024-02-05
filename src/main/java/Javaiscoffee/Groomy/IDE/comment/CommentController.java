package Javaiscoffee.Groomy.IDE.comment;

import Javaiscoffee.Groomy.IDE.response.MyResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public MyResponse<Comment> createComment(@RequestBody Comment comment) {
        return commentService.createComment(comment);
    }

    @GetMapping("/{commentId}")
    public MyResponse<Optional<Comment>> getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }


    @PutMapping
    public MyResponse<Comment> editComment(@RequestBody Comment editedComment) {
        return commentService.editComment(editedComment);
    }


//    @DeleteMapping
//    public MyResponse<>

}

//그냥 컨트롤러로 요청 들어오면 해당 하는 댓글은 무조건 삭제 수정하도록
