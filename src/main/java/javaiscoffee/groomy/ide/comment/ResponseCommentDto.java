package javaiscoffee.groomy.ide.comment;

import lombok.Data;
import lombok.Lombok;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class ResponseCommentDto {
    private Long boardId;
    private Long memberId;
    private Long memberHelpNumber;
    private String nickname;
    private String content;
    private Long originComment;
    private Long commentId;
    private int helpNumber;
    private LocalDateTime createdTime;
    private CommentStatus commentStatus;
}
