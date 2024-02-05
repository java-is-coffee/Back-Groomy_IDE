package javaiscoffee.groomy.ide.project;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectCreateResponseDto {
    private Long projectId;
    private Long memberId;
    private String projectName;
    private String description;
    private ProjectLanguage language;
    private LocalDate createdDate;
    private Boolean deleted;
    private String projectPath;
}
