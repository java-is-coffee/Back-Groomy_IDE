package javaiscoffee.groomy.ide.project;

import jakarta.validation.constraints.Null;
import javaiscoffee.groomy.ide.response.MyResponse;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import javaiscoffee.groomy.ide.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createProject(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ProjectCreateRequestDto projectCreateRequestDto) {
        Long memberId = userDetails.getMemberId();
        ProjectCreateResponseDto savedProject = projectService.createProject(memberId, projectCreateRequestDto);
        if(savedProject == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        }
        return ResponseEntity.ok(savedProject);
    }
    @GetMapping("/list")
    public ResponseEntity<?> getProjectList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(projectService.getProjectList(userDetails.getMemberId(),true));
    }

    @PatchMapping("/edit/{projectId}")
    public ResponseEntity<?> editProject(@PathVariable(name = "projectId") Long projectId,@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ProjectCreateRequestDto requestDto) {
        Long memberId = userDetails.getMemberId();
        log.info("수정할 프로젝트 매핑 = {}",requestDto);
        ProjectCreateResponseDto editedProject = projectService.editProject(projectId, memberId, requestDto);
        if (editedProject == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        }
        return ResponseEntity.ok(editedProject);
    }

    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable(name = "projectId") Long projectId,@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        if (!projectService.deleteProject(memberId, projectId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(null);
    }

    /**
     * 초대받은 프로젝트 리스트만 반환
     */
    @GetMapping("/invited-list")
    public ResponseEntity<?> getInvitedProjects(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(projectService.getProjectList(userDetails.getMemberId(),false));
    }

    /**
     * 초대받은 프로젝트 참가
     */
    @PostMapping("/participate-project")
    public ResponseEntity<?> participateAccept(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ProjectParticipateRequestDto requestDto) {
        if(!projectService.participateAccept(requestDto, userDetails.getMemberId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(null);
    }
}
