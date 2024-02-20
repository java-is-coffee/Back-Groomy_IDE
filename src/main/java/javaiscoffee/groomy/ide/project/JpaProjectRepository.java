package javaiscoffee.groomy.ide.project;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import javaiscoffee.groomy.ide.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
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
     * 요구 데이터 : projectMember(참여했는지 안했는지는 이전 과정에서 설정)
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
     * 프로젝트 초대 수락하기
     * 요구 데이터 : Member와 Proejct
     * 반환 데이터 : 성공하면 true, 실패하면 false
     */
    public boolean acceptProject(ProjectMemberId projectMemberId) {
        try {
            ProjectMember projectMember = em.find(ProjectMember.class, projectMemberId);
            projectMember.setParticipated(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 프로젝트 초대 삭제(거절)하기
     * 요구 데이터 : ProjectMemberId
     * 반환 데이터 : 성공하면 true, 실패하면 false
     */
    public boolean removeMemberFromProject(ProjectMemberId projectMemberId) {
        //프로젝트멤버 객체 조회
        ProjectMember projectMember = em.find(ProjectMember.class, projectMemberId);
        try {
            em.remove(projectMember);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 참가하고 있는 프로젝트 리스트 반환
     * 요구 데이터 : 참가하고 있는 멤버의 memberId, 참가하고 있는 프로젝트 반환은 true, 초대받은 프로젝트 반환은 false
     * 반환 데이터 : 멤버가 참가하고 있는 프로젝트들이 담긴 ArrayList
     */
    public List<Project> getProjectList(Long memberId, boolean participated) {
        String jpql = "SELECT p FROM Project p JOIN p.projectMembers pm WHERE pm.member.memberId = :memberId AND pm.participated = :participated AND p.deleted = false";
        TypedQuery<Project> query = em.createQuery(jpql, Project.class).setParameter("participated",participated);
        query.setParameter("memberId", memberId);

        return query.getResultList();
    }

    /**
     * 현재 프로젝트에 참가하고 있는 멤버 리스트 반환
     */
    public List<Member> getProjectMemberList(Long projectId, Long memberId) {
        String jpql = "SELECT pm.member FROM ProjectMember pm WHERE pm.project.projectId = :projectId AND pm.member.memberId != :memberId";
        TypedQuery<Member> query = em.createQuery(jpql, Member.class)
                .setParameter("projectId",projectId)
                .setParameter("memberId", memberId);
        return query.getResultList();
    }

    /**
     * 해당 유저가 프로젝트에 참가하고 있는지 확인
     * 요구 데이터 : projectMemberId
     * 반환 데이터 : 참가하고 있으면 true, 아니면 false
     */
    public boolean isParticipated(ProjectMemberId projectMemberId) {
        ProjectMember projectMember = em.find(ProjectMember.class, projectMemberId);
        if(projectMember == null || projectMember.getParticipated() == false) {
            return false;
        }
        return true;
    }

    public boolean isInvited(ProjectMemberId projectMemberId) {
        ProjectMember projectMember = em.find(ProjectMember.class, projectMemberId);
        if(projectMember == null || projectMember.getParticipated() == true) {
            return false;
        }
        return true;
    }

    /**
     * 프로젝트 정보 수정
     */
    public Project update(Project editedProject) {
        em.persist(editedProject);
        return editedProject;
    }

    /**
     * 프로젝트 소프트 딜리트 처리
     */
    public boolean delete(Project project) {
        try {
            project.setDeleted(true); // 소프트 삭제
            em.merge(project);
            return true;
        } catch (Exception e) {
            // 예외 발생 시 삭제 실패 처리
            return false;
        }
    }

    public Project getProjectByProjectId(Long projectId) {
        return em.find(Project.class, projectId);
    }
}
