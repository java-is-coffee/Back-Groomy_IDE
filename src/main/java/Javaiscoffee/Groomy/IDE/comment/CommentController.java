package Javaiscoffee.Groomy.IDE.comment;

import Javaiscoffee.Groomy.IDE.response.MyResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public MyResponse<Comment> getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }


}

//그냥 컨트롤러로 요청 들어오면 해당 하는 댓글은 무조건 삭제 수정하도록

// 네 인텔리제이에서 브랜치생성해서 작업중이었어요