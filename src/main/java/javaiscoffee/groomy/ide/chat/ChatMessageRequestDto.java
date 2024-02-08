package javaiscoffee.groomy.ide.chat;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ChatMessageRequestDto {
    private ChatMessageData data;
    @Data
    public static class ChatMessageData {
        private Long memberId;
        private String email;
        private String message;
    }
}