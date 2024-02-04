package javaiscoffee.groomy.ide.project;

import javaiscoffee.groomy.ide.response.MyResponse;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ide")
public class ProjectController {
    private final ProjectService projectService;
    @PostMapping("/create")
    public MyResponse<ProjectCreateResponseDto> createProject(@RequestBody ProjectCreateRequestDto projectCreateRequestDto) {
        return projectService.createProject(projectCreateRequestDto);
    }
    @GetMapping("/list")
    public MyResponse<List<ProjectCreateResponseDto>> getProjectList(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String email = userDetails.getUsername();
            // 여기서 email 변수를 사용하여 필요한 로직을 수행
            return projectService.getProjectList(email);
        } else {
            // userDetails가 null인 경우의 처리
            log.error("인증된 사용자가 없음");
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }
    }
}
