package javaiscoffee.groomy.ide.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@ToString
public class ResponseListBoardDto {
    private Long boardId;
    private Long memberId;
    private String title;
    private String content;
    private int viewNumber;
    private int commentNumber;
    private int helpNumber;
    private boolean isCompleted;
}
