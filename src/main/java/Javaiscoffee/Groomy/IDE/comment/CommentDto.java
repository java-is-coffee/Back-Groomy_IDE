package Javaiscoffee.Groomy.IDE.comment;

import Javaiscoffee.Groomy.IDE.member.Member;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Data data;
    @lombok.Data
    public static class Data{
        private long commentId;
        private Comment originComment;
        private Board board;
        private Member member;
        private String content;
        private String nickname;
        private int helpNumber;
//        private CommentStatus commentStatus; ?? 이거 넣어놓는게 맞나????
        private LocalDateTime createdTime;

    }
}
