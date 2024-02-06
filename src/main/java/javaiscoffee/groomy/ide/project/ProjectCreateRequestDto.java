package javaiscoffee.groomy.ide.project;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectCreateRequestDto {
    private Data data;
    @lombok.Data
    public static class Data {
        private String projectName;
        private Long memberId;
        private ProjectLanguage language;
        private String description;
        private List<Long> inviteMembers;
    }
}
