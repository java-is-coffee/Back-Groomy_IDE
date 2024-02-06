package javaiscoffee.groomy.ide.project;

import jakarta.validation.constraints.Null;
import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.response.MyResponse;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
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

    public List<ProjectCreateResponseDto> getProjectList(Long memberId) {
        return projectRepository.getProjectList(memberId);
    }

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

    public Boolean deleteProject(Long memberId, Long projectId) {
        Project oldProject = projectRepository.getProjectByProjectId(projectId);
        //프로젝트를 찾을 수 없는 경우 에러 코드 반환
        if(oldProject == null) {
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
}
