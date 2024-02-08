package javaiscoffee.groomy.ide.chat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDto {
    private String name;
    private String email;
    private String message;
    private LocalDateTime createdTime;
}
