package Javaiscoffee.Groomy.IDE.board;

import Javaiscoffee.Groomy.IDE.response.MyResponse;
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

    @PostMapping
    public MyResponse<Board> createBoard(@RequestBody BoardDto boardDto) {
        log.info("입력 받은 게시글 정보 = {}",boardDto);
        return boardService.createBoard(boardDto);
    }
    // 지금 환경변수 세팅이 안되어 있을텐데 우측 상단에 점 누르시고 edit 누르시고


    @GetMapping("/{boardId}")
    public MyResponse<Optional<Board>> getBoardById(@PathVariable Long boardId) {
        return boardService.getBoardById(boardId);
    }


}

//그냥 컨트롤러로 요청 들어오면 해당 하는 게시글은 무조건 삭제 수정하도록

// 네 인텔리제이에서 브랜치생성해서 작업중이었어요