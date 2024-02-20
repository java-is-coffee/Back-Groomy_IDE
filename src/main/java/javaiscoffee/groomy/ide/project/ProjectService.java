package javaiscoffee.groomy.ide.project;

import javaiscoffee.groomy.ide.aws.EcsService;
import javaiscoffee.groomy.ide.member.FindMemberByEmailResponseDto;
import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.security.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.ecs.model.RunTaskResponse;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {
    private final JpaProjectRepository projectRepository;
    private final JpaMemberRepository memberRepository;
    private final EcsService ecsService;

    /**
     * 사용 용도 : 프로젝트 생성하고 나서 생성자를 프로젝트에 참여시키기 + 초대할 사람들을 초대하기
     * 요구 데이터 : 프로젝트 생성에 필요한 정보가 담긴 ProjectCreateRequestDto 객체를 포함한 응답 객체
     * 반환 데이터 : 프로젝트 생성 정보가 담긴 ProjectCreateResponseDto 객체를 포함한 응답 객체
     */
    @Transactional
    public ProjectCreateResponseDto createProject(Long memberId, ProjectCreateRequestDto requestDto) {
        ProjectCreateRequestDto.Data requestDtoData = requestDto.getData();
        //토큰에서 멤버ID를 얻은 값과 요청받은 생성자 멤버ID를 비교해서 틀리면 Error Response 반환
        if(!Objects.equals(memberId, requestDtoData.getMemberId())) {
            return null;
        }

        //Dto에서 newProject 객체로 정보 복사
        Project newProject = new Project();
        BeanUtils.copyProperties(requestDtoData, newProject);

        // memberId를 사용하여 생성자의 Member 객체 찾기
        Optional<Member> member = memberRepository.findByMemberId(requestDtoData.getMemberId());
        if (member.isEmpty()) {
            log.error("프로젝트 생성자를 찾을 수 없습니다. {}", requestDtoData.getMemberId());
            return null;
        }
        //생성자가 있으므로 저장
        Member projectCreator = member.get();
        newProject.setMemberId(projectCreator); // 새로운 프로젝트에 생성자 Member 설정
        //아직 프로젝트 컨테이너 생성을 구현 못했으므로 임시 경로 저장
        newProject.setProjectPath("/groomy-project");

        // 생성된 객체 받음
        // 생성이 안되었으면 null Response 반환
        Project createdProject = projectRepository.save(newProject);
        if(createdProject==null) {
            return null;
        }
        log.info("new project = {}", newProject);

        //생성된 프로젝트에 생성자 참가시키기
        inviteProject(createdProject, projectCreator,true);
        //생성된 프로젝트에 초대할 멤버들이 있을 때 구하고 초대하기
        if(requestDtoData.getInviteMembers().size()>0) {
            log.info("프로젝트 생성할 때 초대할 멤버가 있음 = {}명",requestDtoData.getInviteMembers().size());
            List<Member> invitedMembers = memberRepository.findInvitedMembers(requestDtoData.getInviteMembers());
            for(Member invitedMember : invitedMembers) {
                inviteProject(createdProject, invitedMember,false);
            }
        }

        //ProjectCreateResponseDto 객체를 포함한 결과 반환
        ProjectCreateResponseDto responseDto = new ProjectCreateResponseDto();
        BeanUtils.copyProperties(newProject, responseDto);
        responseDto.setMemberId(projectCreator.getMemberId()); // memberId만 설정

        //프로젝트 폴더 생성
        createProjectFolder(memberId, createdProject.getProjectId());

        //프로젝트 태스크 정의 생성 및 태스크 실행
//        RunTaskResponse taskResponse = ecsService.createAndRunTask(memberId, createdProject.getProjectId(), createdProject.getLanguage());
//        log.info("태스크 생성 및 실행 = {}",taskResponse);

        return responseDto;
    }

    /**
     * 리스트로 입력 받은 멤버들을 초대한다.
     */
    @Transactional
    public void inviteMemberByList(Long memberId, Long projectId, ProjectCreateRequestDto requestDto) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new BaseException(ResponseStatus.NOT_FOUND.getMessage()));
        Project project = projectRepository.getProjectByProjectId(projectId);
        //프로젝트가 없거나 생성자랑 초대를 요청한 멤버가 다르면 예외 처리
        if (project == null || !Objects.equals(project.getMemberId().getMemberId(), member.getMemberId())) {  throw new BaseException(ResponseStatus.NOT_FOUND.getMessage()); }

        ProjectCreateRequestDto.Data requestDtoData = requestDto.getData();
        //비어 있으면 그냥 무효
        if(!requestDtoData.getInviteMembers().isEmpty()) {
            log.info("프로젝트 생성할 때 초대할 멤버가 있음 = {}명",requestDtoData.getInviteMembers().size());
            List<Member> invitedMembers = memberRepository.findInvitedMembers(requestDtoData.getInviteMembers());
            for(Member invitedMember : invitedMembers) {
                inviteProject(project, invitedMember,false);
            }
        }
    }

    /**
     * 용도 : 프로젝트에 입력 받은 멤버를 초대하거나 추가한다.
     * 매개변수 : 대상이 되는 프로젝트, 초대하거나 추가할 멤버, true=추가 false=초대
     */
    private void inviteProject(Project createdProject, Member projectCreator, boolean participated) {
        ProjectMemberId projectMemberId = new ProjectMemberId(createdProject.getProjectId(), projectCreator.getMemberId());
        //이미 참여하고 있는 멤버면 초대하지 않음
        if (projectRepository.isParticipated(projectMemberId)) return;
        //참여 안하고 있으면 생성
        ProjectMember projectMember = new ProjectMember(projectMemberId, createdProject, projectCreator,participated);
        projectRepository.participateProject(projectMember);
    }

    /**
     * 프로젝트 목록 조회
     */
    public List<ProjectCreateResponseDto> getProjectList(Long memberId, boolean participated) {
        List<Project> projectList = projectRepository.getProjectList(memberId,participated);
        return toProjectCreateResponseDtoList(projectList);
    }

    /**
     * 현재 프로젝트에 참가하고 있는 멤버 리스트 조회
     */
    public List<FindMemberByEmailResponseDto> getProjectMemberList(Long memberId, Long projectId) {
        Project project = projectRepository.getProjectByProjectId(projectId);
        //프로젝트가 없으면 예외처리
        //요청한 멤버가 프로젝트 생성자가 아니면 예외 처리 => 프로젝트에 참가하고 있는지도 같이 검사됨
        if (project == null || !project.getMemberId().getMemberId().equals(memberId)) {
            throw new BaseException(ResponseStatus.FORBIDDEN.getMessage());
        }
        List<Member> projectMemberList = projectRepository.getProjectMemberList(projectId, memberId);
        //멤버 객체를 응답 객체로 변환
        return projectMemberList.stream().map(member -> {
            FindMemberByEmailResponseDto responseDto = new FindMemberByEmailResponseDto();
            BeanUtils.copyProperties(member,responseDto);
            return responseDto;
        }).collect(Collectors.toList());
    }

    /**
     * 프로젝트에 멤버가 참가하고 있는지 조회
     */
    public boolean isParticipated (Long memberId, Long projectId) {
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        return projectRepository.isParticipated(projectMemberId);
    }

    /**
     * 프로젝트 정보 수정
     * 프로젝트 이름과 설명만 변경 가능
     */
    @Transactional
    public ProjectCreateResponseDto editProject(Long projectId,Long memberId, ProjectCreateRequestDto requestDto) {

        Project oldProject = projectRepository.getProjectByProjectId(projectId);
        //프로젝트를 찾을 수 없는 경우 에러 코드 반환
        if(oldProject == null) {
            return null;
        }

        //프로젝트 생성자와 토큰의 memberId가 다른 경우 에러 코드 반환
        if(!Objects.equals(memberId, oldProject.getMemberId().getMemberId())) {
            return null;
        }

        //프로젝트 정보 수정
        ProjectCreateRequestDto.Data data = requestDto.getData();
        oldProject.setProjectName(data.getProjectName());
        oldProject.setDescription(data.getDescription());
        oldProject.setUpdatedDate(LocalDate.now());

        //응답 객체 매핑
        Project updatedProject = projectRepository.update(oldProject);
        ProjectCreateResponseDto responseDto = new ProjectCreateResponseDto();
        BeanUtils.copyProperties(updatedProject, responseDto);
        responseDto.setMemberId(memberId); // memberId만 설정
        return responseDto;
    }

    /**
     * 프로젝트 삭제 = 소프트 딜리트
     */
    @Transactional
    public Boolean deleteProject(Long memberId, Long projectId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new BaseException(ResponseStatus.NOT_FOUND.getMessage()));
        Project oldProject = projectRepository.getProjectByProjectId(projectId);
        //프로젝트를 찾을 수 없는 경우 에러 코드 반환
        if(oldProject == null || oldProject.getDeleted() == true) {
            return false;
        }
        //프로젝트에 참여하고 있지 않으면 에러 코드 반환
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        if (!projectRepository.isParticipated(projectMemberId)) {
            return false;
        }

        //프로젝트 생성자와 토큰의 memberId가 다른 경우 프로젝트 멤버에서 삭제
        if(!Objects.equals(memberId, oldProject.getMemberId().getMemberId())) {
            return projectRepository.removeMemberFromProject(projectMemberId);
        }
        //생성자와 요청한 memberId가 같으면 프로젝트 삭제(소프트 딜리트)
        else {
            return projectRepository.delete(oldProject);
        }
    }

    /**
     * 프로젝트 멤버 추방
     * 생성자만 가능하고, 생성자 본인은 추방 불가
     */
    @Transactional
    public void kickProjectMember (Long memberId, Long projectId, Long kickMemberId) {
        Project project = projectRepository.getProjectByProjectId(projectId);
        //프로젝트가 없거나, 프로젝트 생성자가 아니거나,
        // 추방하려는 멤버가 생성자이거나, 추방하려는 멤버가 프로젝트에 참가하고 있지 않을 경우 예외처리
        if(project == null || !project.getMemberId().getMemberId().equals(memberId) ||
                memberId.equals(kickMemberId) || !isParticipated(kickMemberId, projectId)) {
            log.error("잘못된 추방 요청 memberId = {}, projectId = {}, kickMemberId = {}",memberId,projectId,kickMemberId);
            throw new BaseException(ResponseStatus.DELETE_FAILED.getMessage());
        }

        ProjectMemberId kickProjectMemberId = new ProjectMemberId(projectId, kickMemberId);
        if(!projectRepository.removeMemberFromProject(kickProjectMemberId)) {
            log.error("멤버 추방 실패 IOException = {}",kickProjectMemberId);
            throw new BaseException(ResponseStatus.DELETE_FAILED.getMessage());
        }
    }

    /**
     * 멤버와 프로젝트를 찾아서 프로젝트 참가로 변경
     * 입력받는 값 : projectParticipateRequestDto, 토큰에서 뽑은 memberId
     */
    @Transactional
    public Boolean participateAccept(ProjectParticipateRequestDto requestDto, Long memberId, boolean isAccept) {
        Member findMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BaseException("멤버를 찾을 수 없습니다."));
        if(!Objects.equals(findMember.getMemberId(), requestDto.getData().getInvitedMemberId())) {
            return false;
        }
        Project findProject = projectRepository.getProjectByProjectId(requestDto.getData().getProjectId());
        if(findProject == null || findProject.getDeleted()) {
            return false;
        }
        ProjectMemberId projectMemberId = new ProjectMemberId(findProject.getProjectId(), memberId);
        //초대 수락했으면
        if(isAccept) {
            return projectRepository.acceptProject(projectMemberId);
        }

        //초대 거절했으면 초대 받은 리스트에서 프로젝트 삭제
        return projectRepository.removeMemberFromProject(projectMemberId);
    }

    /**
     * ec2 내부에 /home/projects/{memberId}/{projectId} 주소로 폴더를 생성
     */
    public boolean createProjectFolder(Long memberId, Long projectId) {
        Path path = Paths.get("/home/projects/" + memberId + "/" + projectId);
        try {
            Files.createDirectories(path);
            log.info("폴더 생성 성공: {}", path);
            return true; // 폴더 생성 성공
        } catch (IOException e) {
            log.error("프로젝트 폴더 생성 실패", e);
            throw new BaseException("프로젝트 폴더 생성 실패");
        }
    }

    //프로젝트 List를 ProjectCreateResponseDto List로 변환
    private static List<ProjectCreateResponseDto> toProjectCreateResponseDtoList(List<Project> projects) {
        List<ProjectCreateResponseDto> projectList = projects.stream().map(project -> {
            ProjectCreateResponseDto dto = new ProjectCreateResponseDto();
            dto.setProjectId(project.getProjectId());
            dto.setMemberId(project.getMemberId().getMemberId());
            dto.setProjectName(project.getProjectName());
            dto.setDescription(project.getDescription());
            dto.setLanguage(project.getLanguage());
            dto.setCreatedDate(project.getCreatedDate());
            dto.setDeleted(project.getDeleted());
            dto.setProjectPath(project.getProjectPath());
            return dto;
        }).collect(Collectors.toList());

        return projectList;
    }
}
