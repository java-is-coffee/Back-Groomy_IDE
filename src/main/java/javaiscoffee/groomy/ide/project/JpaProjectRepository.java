package javaiscoffee.groomy.ide.project;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class JpaProjectRepository {
    private final EntityManager em;

    public Project save(Project newProject) {
        try {
            em.persist(newProject);
            em.flush(); // ID를 즉시 할당받기 위해 호출

            // 저장된 프로젝트를 반환
            return newProject;
        } catch (PersistenceException e) {
            // 예외가 발생한 경우 null을 반환
            log.error("프로젝트 생성 오류",e.getMessage());
            return null;
        }
    }

    /**
     * 프로젝트에 참가시키기
     * 요구 데이터 : projectMember
     * 반환 데이터 : 프로젝트 인원 추가에 성공하면 true, 실패하면 false
     */
    public boolean participateProject(ProjectMember projectMember) {
        log.info("프로젝트 참가 객체 = {}",projectMember);
        try {
            em.persist(projectMember);
            return true;
        } catch (PersistenceException e) {
            log.error("프로젝트 참가 오류 발생",e);
            return false;
        }
    }

    /**
     * 참가하고 있는 프로젝트 리스트 반환
     * 요구 데이터 : 참가하고 있는 멤버의 memberId
     * 반환 데이터 : 멤버가 참가하고 있는 프로젝트들이 담긴 ArrayList
     */
    public List<ProjectCreateResponseDto> getProjectList(Long memberId) {
        String jpql = "SELECT p FROM Project p JOIN p.projectMembers pm WHERE pm.member.memberId = :memberId AND pm.participated = TRUE AND p.deleted = false";
        TypedQuery<Project> query = em.createQuery(jpql, Project.class);
        query.setParameter("memberId", memberId);

        List<Project> projects = query.getResultList();

        // 조회된 프로젝트를 ProjectCreateResponseDto로 변환
        List<ProjectCreateResponseDto> projectList = projects.stream().map(project -> {
            ProjectCreateResponseDto dto = new ProjectCreateResponseDto();
            dto.setProjectId(project.getProjectId());
            dto.setMemberId(memberId);
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
