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

/**
 * 추가해야 하는 API
 * 프로젝트 초대 거절 API
 * 프로젝트 떠나기 API
 * 프로젝트 초대하기 API
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ide")
public class ProjectController {
    private final ProjectService projectService;

    /**
     * 프로젝트 생성
     */
    @PostMapping("/create")
    public ResponseEntity<?> createProject(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ProjectCreateRequestDto projectCreateRequestDto) {
        Long memberId = userDetails.getMemberId();
        ProjectCreateResponseDto savedProject = projectService.createProject(memberId, projectCreateRequestDto);
        if(savedProject == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.INPUT_ERROR));
        }
        return ResponseEntity.ok(savedProject);
    }

    /**
     * 프로젝트 인원 초대
     * 리스트로 받아서 한 번에 초대할지, 아니면 한 명씩 초대할지 결정해야함
     */
    @PostMapping("/inviteMembers/{projectId}")
    public ResponseEntity<?> inviteMembersToProject(@PathVariable(name = "projectId") Long projectId, @AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ProjectCreateRequestDto requestDto) {
        Long memberId = userDetails.getMemberId();
        projectService.inviteMemberByList(memberId, projectId, requestDto);
        return ResponseEntity.ok(null);
    }

    /**
     * 이미 참가하고 있는 프로젝트 리스트 조회
     */
    @GetMapping("/list")
    public ResponseEntity<?> getProjectList(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(projectService.getProjectList(userDetails.getMemberId(),true));
    }

    /**
     * 프로젝트 정보 수정
     */
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

    /**
     * 프로젝트 삭제 API
     * 생성자가 삭제하기 하면 프로젝트 소프트 딜리트
     * 초대받은 멤버가 삭제하기 하면 프로젝트 멤버 삭제
     */
    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable(name = "projectId") Long projectId,@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        if (!projectService.deleteProject(memberId, projectId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(null);
    }

    /**
     * 아직 참가하지 않은 프로젝트 리스트 조회
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
        if(!projectService.participateAccept(requestDto, userDetails.getMemberId(),true)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(null);
    }

    /**
     * 초대받은 프로젝트 거절
     */
    @PostMapping("/reject-project")
    public ResponseEntity<?> projectReject(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ProjectParticipateRequestDto requestDto) {
        if(!projectService.participateAccept(requestDto, userDetails.getMemberId(),false)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Status(ResponseStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(null);
    }
}
