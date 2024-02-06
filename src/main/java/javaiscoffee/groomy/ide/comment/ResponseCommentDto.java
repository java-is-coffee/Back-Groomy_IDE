package javaiscoffee.groomy.ide.comment;

import lombok.Data;
import lombok.Lombok;

@Data
public class ResponseCommentDto {
    private Data data;
    @lombok.Data
    public static class Data {
        private Long boardId;
        private Long memberId;
        private String nickname;
        private String content;
        private Long originComment;
        private Long commentId;
    }
}
