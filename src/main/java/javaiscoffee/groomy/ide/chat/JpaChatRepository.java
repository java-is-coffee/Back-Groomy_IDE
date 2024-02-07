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
@Transactional
@RequiredArgsConstructor
public class JpaChatRepository {
    private final EntityManager em;

    public List<ProjectChat> getProjectChatLogs(Long projectId,int paging,int pagingNumber) {
        return em.createQuery("SELECT pc FROM ProjectChat pc where pc.project.id = :projectId ORDER BY pc.createdTime DESC", ProjectChat.class)
                .setParameter("projectId", projectId)
                .setFirstResult((paging - 1) * pagingNumber)
                .setMaxResults(pagingNumber)
                .getResultList();
    }

    public ProjectChat writeProjectChat(ProjectChat projectChat) {
        em.persist(projectChat);
        em.flush();
        return projectChat;
    }
}
