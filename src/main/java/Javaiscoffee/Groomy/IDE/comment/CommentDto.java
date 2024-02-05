package Javaiscoffee.Groomy.IDE.comment;

import Javaiscoffee.Groomy.IDE.board.Board;
import Javaiscoffee.Groomy.IDE.member.Member;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Data data;
    @lombok.Data
    public static class Data{
        private Long boardId;
        private Long memberId;
        private String nickname;
        private String content;
        private Long originComment;
    }
}
