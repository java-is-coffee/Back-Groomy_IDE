package javaiscoffee.groomy.ide.board;

import jakarta.validation.constraints.Null;
import javaiscoffee.groomy.ide.response.MyResponse;
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
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/write")
    public ResponseEntity<?> createBoard(@RequestBody RequestBoardDto requestBoardDto) {
        ResponseBoardDto createdBoard = boardService.createBoard(requestBoardDto);
        if(createdBoard == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        }
        return ResponseEntity.ok(createdBoard);
    }

    @GetMapping("/content/{boardId}")
    public ResponseEntity<?> getBoardById(@PathVariable Long boardId) {
        ResponseBoardDto findedBoard = boardService.getBoardById(boardId);
        if(findedBoard == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(findedBoard);
    }

    @PatchMapping("/edit/{boardId}")
    public ResponseEntity<?> editBoard(@RequestBody RequestBoardDto requestBoardDto, @PathVariable Long boardId) {
        ResponseBoardDto editedBoard = boardService.editBoard(requestBoardDto, boardId);
        if(editedBoard == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        }
        return ResponseEntity.ok(editedBoard);
    }

    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long boardId) {
        if(!boardService.deleteBoard(boardId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{paging}")
    public ResponseEntity<?> getBoardByPaging(@PathVariable int paging) {
        //임시로 작성 수정 필요
        List<ResponseBoardDto> boardByPaging = boardService.getBoardByPaging(paging);
        return ResponseEntity.ok(boardByPaging);
    }
}

//그냥 컨트롤러로 요청 들어오면 해당 하는 게시글은 무조건 삭제 수정하도록