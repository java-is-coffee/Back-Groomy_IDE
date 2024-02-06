package javaiscoffee.groomy.ide.board;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RequestBoardDto {
    private Data data;

    @lombok.Data
    public static class Data{
        private Long memberId;
        private String nickname;
        private String title;
        private String content;
        private boolean isCompleted;
    }
}
