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
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/write/{boardId}")
    public MyResponse<Comment> writeComment(@RequestBody CommentDto commentDto) {
        log.info("입력 받은 댓글 정보 = {}", commentDto);
        return commentService.createComment(commentDto);
    }

    @GetMapping("/{commentId}")
    public MyResponse<Optional<Comment>> getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentById(commentId);
    }


    @PutMapping("/edit/{commentId}")
    public MyResponse<Comment> editComment(@RequestBody Comment editedComment) {
        return commentService.editComment(editedComment);
    }


    @DeleteMapping("/delete/{commentId}")
    public MyResponse<Null> deleteCommentById(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }

    @GetMapping("/board/{boardId}")
    public MyResponse<List<Comment>> getCommentByBoardId(@PathVariable Long boardId) {
        return commentService.getCommentByBoardId(boardId);
    }
    // 패스베리어블로 보드 아이디 가져오고
    // 서비스에 주는데 ...


//    @GetMapping("member/{memberId}")

}

//그냥 컨트롤러로 요청 들어오면 해당 하는 댓글은 무조건 삭제 수정하도록
