package javaiscoffee.groomy.ide.file;

import lombok.Data;

@Data
public class FileRenameRequestDto {
    private RequestData data;
    @Data
    public static class RequestData {
        Long projectId;
        String oldPath;
        String newName;
    }
}
