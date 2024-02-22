package javaiscoffee.groomy.ide.chat;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 채팅 로그 불러올 때나 웹소켓을 통해 전달되는 채팅메세지 응답 DTO
 */
@Data
public class ChatMessageDto {
    private String name;
    private String email;
    private String message;
    private LocalDateTime createdTime;
}
