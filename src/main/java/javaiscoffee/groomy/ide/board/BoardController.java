package javaiscoffee.groomy.ide.board;

import jakarta.validation.constraints.Null;
import javaiscoffee.groomy.ide.response.MyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/write")
    public MyResponse<ResponseBoardDto> createBoard(@RequestBody RequestBoardDto requestBoardDto) {
        return boardService.createBoard(requestBoardDto);
    }

    @GetMapping("/content/{boardId}")
    public MyResponse<ResponseBoardDto> getBoardById(@PathVariable Long boardId) {
        return boardService.getBoardById(boardId);
    }

    @PutMapping("/edit/{boardId}")
    public MyResponse<ResponseBoardDto> editBoard(@RequestBody RequestBoardDto requestBoardDto, @PathVariable Long boardId) {
        return boardService.editBoard(requestBoardDto, boardId);
    }

    @DeleteMapping("/delete/{boardId}")
    public MyResponse<Null> deleteBoard(@PathVariable Long boardId) {
        return boardService.deleteBoard(boardId);
    }
}

//그냥 컨트롤러로 요청 들어오면 해당 하는 게시글은 무조건 삭제 수정하도록