package javaiscoffee.groomy.ide.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@ToString
public class ResponseBoardDto {
    private Long boardId;
    private Long memberId;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime createdTime;
    private int viewNumber;
    private int commentNumber;
    private int scrapNumber;
    private int helpNumber;
    private BoardStatus boardStatus;
    private boolean isCompleted;
    private Boolean memberScraped;
}
