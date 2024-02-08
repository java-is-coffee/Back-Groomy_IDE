package javaiscoffee.groomy.ide.project;

import lombok.Data;

import java.util.List;

@Data
public class ProjectParticipateRequestDto {
    private Data data;
    @lombok.Data
    public static class Data {
        private Long projectId;
        private Long hostMemberId;
        private Long invitedMemberId;
    }
}
