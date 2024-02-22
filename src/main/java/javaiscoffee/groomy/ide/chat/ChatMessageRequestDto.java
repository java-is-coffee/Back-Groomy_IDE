package javaiscoffee.groomy.ide.chat;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 프론트에서 넘겨주는 채팅 데이터 매핑 DTO
 */
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