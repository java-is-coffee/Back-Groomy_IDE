package javaiscoffee.groomy.ide.chat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatLogResponseDto {
    private String name;
    private String email;
    private String message;
    private LocalDateTime createdTime;
}
