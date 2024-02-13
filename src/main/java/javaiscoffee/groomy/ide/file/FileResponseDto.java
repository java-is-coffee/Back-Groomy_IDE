package javaiscoffee.groomy.ide.file;

import lombok.Data;

import java.util.List;

@Data
public class FileResponseDto {
    private String id;  //파일 생성 시간
    private String name; //파일 이름
    private String path;
    private FileType type;
    private String lastUpdatedTime;
    private List<FileResponseDto> children;
}
