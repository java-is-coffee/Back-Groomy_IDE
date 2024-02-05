package javaiscoffee.groomy.ide.project;

import javaiscoffee.groomy.ide.response.MyResponse;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import javaiscoffee.groomy.ide.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ide")
public class ProjectController {
    private final ProjectService projectService;
    @PostMapping("/create")
    public MyResponse<ProjectCreateResponseDto> createProject(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ProjectCreateRequestDto projectCreateRequestDto) {
        if (userDetails != null) {
            Long memberId = userDetails.getMemberId();
            return projectService.createProject(memberId, projectCreateRequestDto);
        } else {
            // userDetails가 null인 경우의 처리
            log.error("인증된 사용자가 없음");
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }
    }
    @GetMapping("/list")
    public MyResponse<List<ProjectCreateResponseDto>> getProjectList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            return projectService.getProjectList(userDetails.getMemberId());
        } else {
            // userDetails가 null인 경우의 처리
            log.error("인증된 사용자가 없음");
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }
    }

    @PatchMapping("/edit")
    public MyResponse<ProjectCreateResponseDto> editProject(@AuthenticationPrincipal CustomUserDetails userDetails, ProjectCreateRequestDto requestDto) {
        Long memberId = userDetails.getMemberId();
        log.info("프로젝트 수정 memberId={}",memberId);
        return null;
    }
}
