package javaiscoffee.groomy.ide.comment;

import lombok.Data;
import lombok.ToString;

@Data
public class CommentEditRequestDto {
    private Data data;
    @lombok.Data
    public static class Data{
        private String nickname;
        private String content;
    }
}
