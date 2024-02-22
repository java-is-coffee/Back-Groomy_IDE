package javaiscoffee.groomy.ide.file;

import lombok.Data;

/**
 * 파일 이름 변경, 삭제할 때 사용하는 DTO
 */
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
