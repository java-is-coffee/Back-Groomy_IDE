package javaiscoffee.groomy.ide.project;

import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.response.MyResponse;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final JpaProjectRepository projectRepository;
    private final JpaMemberRepository memberRepository;

    /**
     * 사용 용도 : 프로젝트 생성하고 나서 생성자를 프로젝트에 참여시키기
     * 요구 데이터 : 프로젝트 생성에 필요한 정보가 담긴 ProjectCreateRequestDto 객체를 포함한 응답 객체
     * 반환 데이터 : 프로젝트 생성 정보가 담긴 ProjectCreateResponseDto 객체를 포함한 응답 객체
     */
    public MyResponse<ProjectCreateResponseDto> createProject(ProjectCreateRequestDto requestDto) {
        //Dto에서 newProject 객체로 정보 복사
        Project newProject = new Project();
        BeanUtils.copyProperties(requestDto.getData(), newProject);

        // memberId를 사용하여 생성자의 Member 객체 찾기
        Optional<Member> member = memberRepository.findByMemberId(requestDto.getData().getMemberId());
        if (member.isEmpty()) {
            log.error("프로젝트 생성자를 찾을 수 없습니다. {}", requestDto.getData().getMemberId());
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }
        //생성자가 있으므로 저장
        Member projectCreator = member.get();
        newProject.setMemberId(projectCreator); // 새로운 프로젝트에 생성자 Member 설정
        newProject.setDeleted(false);
        //아직 프로젝트 컨테이너 생성을 구현 못했으므로 임시 경로 저장
        newProject.setProjectPath("/groomy-project");
        newProject.setCreatedDate(LocalDate.now());

        // 생성된 객체 받음
        // 생성이 안되었으면 null Response 반환
        Project createdProject = projectRepository.save(newProject);
        if(createdProject==null) {
            return new MyResponse<>(new Status(ResponseStatus.ERROR));
        }
        log.info("new project = {}", newProject);

        //생성된 프로젝트에 생성자 참가시키기
        ProjectMemberId projectMemberId = new ProjectMemberId(createdProject.getProjectId(), projectCreator.getMemberId());
        ProjectMember projectMember = new ProjectMember(projectMemberId,createdProject,projectCreator,true);
        projectRepository.participateProject(projectMember);

        //ProjectCreateResponseDto 객체를 포함한 결과 반환
        ProjectCreateResponseDto responseDto = new ProjectCreateResponseDto();
        BeanUtils.copyProperties(newProject, responseDto);
        responseDto.setMemberId(projectCreator.getMemberId()); // memberId만 설정
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS),responseDto);
    }

    public MyResponse<List<ProjectCreateResponseDto>> getProjectList(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if(member.isEmpty()) {
            log.error("프로젝트 멤버를 찾을 수 없습니다. {}", email);
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }
        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), projectRepository.getProjectList(member.get().getMemberId()));
    }
}
