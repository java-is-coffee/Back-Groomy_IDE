package javaiscoffee.groomy.ide.board;

import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import javaiscoffee.groomy.ide.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/write")
    public ResponseEntity<?> createBoard(@RequestBody RequestBoardDto requestBoardDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 게시글 작성 요청한 memebr
        Long memberId = userDetails.getMemberId();

        ResponseBoardDto createdBoard = boardService.createBoard(requestBoardDto, memberId);

        if(createdBoard == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        }

        return ResponseEntity.ok(createdBoard);
    }

    @GetMapping("/content/{boardId}")
    public ResponseEntity<?> getBoardById(@PathVariable Long boardId) {
        ResponseBoardDto foundBoard = boardService.getBoardById(boardId);

        if(foundBoard == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Status(ResponseStatus.NOT_FOUND));
        }

        return ResponseEntity.ok(foundBoard);
    }

    @PatchMapping("/edit/{boardId}")
    public ResponseEntity<?> editBoard(@RequestBody RequestBoardDto requestBoardDto, @PathVariable Long boardId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        ResponseBoardDto editedBoard = boardService.editBoard(requestBoardDto, boardId, memberId);

        if(editedBoard == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        }

        return ResponseEntity.ok(editedBoard);
    }

    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long boardId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();

        if(!boardService.deleteBoard(boardId, memberId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }

        return ResponseEntity.ok(null);
    }

    @GetMapping("/{paging}")
    public ResponseEntity<?> getBoardByPaging(@PathVariable int paging) {
        List<ResponseBoardDto> boardByPaging = boardService.getBoardByPaging(paging);

        if(boardByPaging == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Status(ResponseStatus.NOT_FOUND));
        }

        return ResponseEntity.ok(boardByPaging);
    }

    @GetMapping("/page-number")
    public ResponseEntity<?> getBoardPageNumber() {
        long boardPageNumber = boardService.getBoardPageNumber();

        if(boardPageNumber == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Status(ResponseStatus.NOT_FOUND));
        }

        return ResponseEntity.ok(boardPageNumber);
    }

    @GetMapping("/myList/{paging}")
    public ResponseEntity<?> getMyBoardByPaging(@PathVariable int paging, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ResponseBoardDto> boardByPaging = boardService.getMyBoardByPaging(paging, memberId);

        if(boardByPaging == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Status(ResponseStatus.NOT_FOUND));
        }

        return ResponseEntity.ok(boardByPaging);
    }

    @PostMapping("/content/good/{boardId}")
    public ResponseEntity<?> getBoardGoodById(@PathVariable Long boardId) {
        ResponseBoardDto foundBoard = boardService.getBoardGoodById(boardId);

        if(foundBoard == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Status(ResponseStatus.NOT_FOUND));
        }

        return ResponseEntity.ok(foundBoard);
    }
}

//그냥 컨트롤러로 요청 들어오면 해당 하는 게시글은 무조건 삭제 수정하도록