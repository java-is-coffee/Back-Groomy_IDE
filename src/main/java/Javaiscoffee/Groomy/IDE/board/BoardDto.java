package Javaiscoffee.Groomy.IDE.board;

import Javaiscoffee.Groomy.IDE.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class BoardDto {
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
