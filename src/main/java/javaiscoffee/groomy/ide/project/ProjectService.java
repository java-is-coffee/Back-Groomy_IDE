package javaiscoffee.groomy.ide.project;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.*;
import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.security.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        participateProject(createdProject, projectCreator,true);
        //생성된 프로젝트에 초대할 멤버들이 있을 때 구하고 초대하기
        if(requestDtoData.getInviteMembers().size()>0) {
            log.info("프로젝트 생성할 때 초대할 멤버가 있음 = {}명",requestDtoData.getInviteMembers().size());
            List<Member> invitedMembers = memberRepository.findInvitedMembers(requestDtoData.getInviteMembers());
            for(Member invitedMember : invitedMembers) {
                participateProject(createdProject, invitedMember,false);
            }
        }

        //ProjectCreateResponseDto 객체를 포함한 결과 반환
        ProjectCreateResponseDto responseDto = new ProjectCreateResponseDto();
        BeanUtils.copyProperties(newProject, responseDto);
        responseDto.setMemberId(projectCreator.getMemberId()); // memberId만 설정

        //프로젝트 폴더 생성
        createProjectFolder(memberId, createdProject.getProjectId());


        return responseDto;
    }

    /**
     * 용도 : 프로젝트에 입력 받은 멤버를 초대하거나 추가한다.
     * 매개변수 : 대상이 되는 프로젝트, 초대하거나 추가할 멤버, true=추가 false=초대
     */
    private void participateProject(Project createdProject, Member projectCreator, boolean participated) {
        ProjectMemberId projectMemberId = new ProjectMemberId(createdProject.getProjectId(), projectCreator.getMemberId());
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
     * 프로젝트에 멤버가 참가하고 있는지 조회
     */
    public boolean isParticipated (Long memberId, Long projectId) {
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        return projectRepository.isParticipated(projectMemberId);
    }

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

        //응답 객체 매핑
        Project updatedProject = projectRepository.update(oldProject);
        ProjectCreateResponseDto responseDto = new ProjectCreateResponseDto();
        BeanUtils.copyProperties(updatedProject, responseDto);
        responseDto.setMemberId(memberId); // memberId만 설정
        return responseDto;
    }

    @Transactional
    public Boolean deleteProject(Long memberId, Long projectId) {
        Project oldProject = projectRepository.getProjectByProjectId(projectId);
        //프로젝트를 찾을 수 없는 경우 에러 코드 반환
        if(oldProject == null || oldProject.getDeleted() == true) {
            return false;
        }

        //프로젝트 생성자와 토큰의 memberId가 다른 경우 에러 코드 반환
        if(!Objects.equals(memberId, oldProject.getMemberId().getMemberId())) {
            return false;
        }
        //삭제 실패하면 에러 코드 반환
        if(!projectRepository.delete(oldProject)) {
            return false;
        }
        return true;
    }

    /**
     * 멤버와 프로젝트를 찾아서 프로젝트 참가로 변경
     * 입력받는 값 : projectParticipateRequestDto, 토큰에서 뽑은 memberId
     */
    @Transactional
    public Boolean participateAccept(ProjectParticipateRequestDto requestDto, Long memberId) {
        Member findMember = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BaseException("멤버를 찾을 수 없습니다."));
        if(!Objects.equals(findMember.getMemberId(), requestDto.getData().getInvitedMemberId())) {
            return false;
        }
        Project findProject = projectRepository.getProjectByProjectId(requestDto.getData().getProjectId());
        if(findProject == null || findProject.getDeleted()) {
            return false;
        }
        //프로젝트 초대 명단 업데이트
        ProjectMemberId projectMemberId = new ProjectMemberId(findProject.getProjectId(), memberId);
        return projectRepository.acceptProject(projectMemberId);
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

    public boolean createRegisterTaskDefinitionRequest(Long memberId, Long projectId, ProjectLanguage language) {
        AmazonECS ecsClient = AmazonECSClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();

        String taskDefinition = getTaskDefinitionByLanguage(language); // 언어에 따라 태스크 종류 선택

        RunTaskRequest runTaskRequest = new RunTaskRequest()
                .withCluster("groomy-cluster")
                .withTaskDefinition(taskDefinition)
                .withCount(1)
                .withLaunchType("EC2") // 또는 "EC2"
                .withOverrides(new TaskOverride()
                        .withContainerOverrides(new ContainerOverride()
                                .withName("project/"+memberId+"-"+projectId)
                                .withEnvironment(new KeyValuePair()
                                        .withName("PROJECT_PATH")
                                        .withValue("/home/projects/" + memberId + "/" + projectId))));

        RunTaskResult runTaskResult = ecsClient.runTask(runTaskRequest);
        return !runTaskResult.getTasks().isEmpty();
    }

    private String getTaskDefinitionByLanguage(ProjectLanguage language) {
        return switch (language) {
            case JAVA -> "java-task-definition:1";
            case JAVASCRIPT -> "javascript-task-definition:1";
            case PYTHON -> "python-task-definition:1";
            default -> "default-task-definition:1";
        };
    }

    private String createTaskDefinition(ProjectLanguage language) {
        // AWS SDK를 사용하여 ECS Task Definition 생성 로직 구현
        AmazonECS ecsClient = AmazonECSClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();

        // 생성된 Task Definition의 ARN을 반환
        return "arn:aws:ecs:region:account:task-definition/taskName";
    }

    private boolean runEcsTask(String taskDefinitionArn, Long memberId, Long projectId) {
        // AWS SDK를 사용하여 ECS Task 실행 로직 구현
        // 성공적으로 실행되면 true 반환
        return true;
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
