package javaiscoffee.groomy.ide.file;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 웹소켓 통신할 때 파일계층 변경 데이터를 프론트로부터 전달받는 DTO
 */
@Data
public class FileWebsocketRequestDto {
    private RequestData data;
    @Data
    public static class RequestData {
        private String name; // 파일,폴더 이름
        private String path; // 상대경로
        private String content = "";
        private FileType type; // 파일 타입
        private List<String> children;  //파일 생성할 때 필요한 값 보통 빈 배열로 메세지 전달
        private FileWebsocketAction action; // 멤버가 행동한 액션 종류
    }
}
