package javaiscoffee.groomy.ide.chat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaChatRepository {
    private final EntityManager em;

    /**
     * 채팅메세지 리스트 불러오기
     * paging = 몇 번째 메세지부터 불러올 것인지
     * pagingNumber = 한 번에 불러올 메세지 개수
     */
    public List<ProjectChat> getProjectChatLogs(Long projectId,int paging,int pagingNumber) {
        return em.createQuery("SELECT pc FROM ProjectChat pc where pc.project.id = :projectId ORDER BY pc.createdTime DESC", ProjectChat.class)
                .setParameter("projectId", projectId)
                .setFirstResult((paging - 1) * pagingNumber)
                .setMaxResults(pagingNumber)
                .getResultList();
    }

    /**
     * 프로젝트 채팅 저장
     */
    public ProjectChat writeProjectChat(ProjectChat projectChat) {
        em.persist(projectChat);
        em.flush();
        return projectChat;
    }
}
