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
    public ResponseEntity<?> getBoardById(@PathVariable Long boardId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        ResponseBoardDto foundBoard = boardService.getBoardById(boardId, memberId);

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
    public ResponseEntity<?> getBoardByPaging(@PathVariable int paging, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        List<ResponseBoardDto> boardByPaging = boardService.getBoardByPaging(paging, memberId);

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


    // 게시글 추천
    @PostMapping("/content/good/{boardId}")
    public ResponseEntity<?> clickGood(@PathVariable Long boardId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        ResponseBoardDto updatedHelpNumber = boardService.toggleGoodBoard(boardId, memberId);
        if (updatedHelpNumber == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(updatedHelpNumber);
    }

    // 게시글 스크랩
    @PostMapping("/scrap/{boardId}")
    public ResponseEntity<?> clickScrap(@PathVariable Long boardId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        ResponseBoardDto updatedScrap = boardService.toggleScrap(boardId, memberId);
        if (updatedScrap == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(updatedScrap);
    }


    @GetMapping("/scrap/list/{paging}")
    public ResponseEntity<?> getGoodListByPaging(@PathVariable int paging, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("스크랩 게시물 목록 조회 요청");
        Long memberId = userDetails.getMemberId();
        List<ResponseBoardDto> goodByPaging = boardService.getHelpBoardByPaging(paging, memberId);
        if(goodByPaging == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Status(ResponseStatus.NOT_FOUND));
        }

        return ResponseEntity.ok(goodByPaging);
    }

    @GetMapping("/search/{paging}")
    public ResponseEntity<?> searchBoardByPaging(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int paging, @RequestParam(name="search_keyword") String searchKeyword, @RequestParam(name="completed", required = false) Boolean completed) {
        Long memberId = userDetails.getMemberId();
        List<ResponseBoardDto> searchBoard = boardService.searchBoardByPaging(memberId, paging, searchKeyword, completed);

        if(searchBoard == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Status(ResponseStatus.NOT_FOUND));
        }

        return ResponseEntity.ok(searchBoard);
    }

    @GetMapping("/search/page-number")
    public ResponseEntity<?> searchBoardPageNumber(@RequestParam(name="search_keyword") String searchKeyword, @RequestParam(name="completed", required = false) Boolean completed) {
        long boardPageNumber = boardService.searchBoardPageNumber(searchKeyword, completed);

        if(boardPageNumber == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Status(ResponseStatus.NOT_FOUND));
        }

        return ResponseEntity.ok(boardPageNumber);
    }
}