package javaiscoffee.groomy.ide.file;

import lombok.Data;

@Data
public class FileRequestDto {
    private RequestData data;
    @Data
    public static class RequestData {
        private Long projectId;
        private String fileName;
        private String filePath;
        private String content;
        private FileType type;
    }
}
