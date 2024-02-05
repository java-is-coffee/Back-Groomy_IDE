package javaiscoffee.groomy.ide.comment;

import javaiscoffee.groomy.ide.board.Board;
import javaiscoffee.groomy.ide.member.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional
public class JpaCommentRepository implements CommentRepository{

    private final EntityManager em;
    public JpaCommentRepository(EntityManager em) {
        this.em = em;
    }


    // C
    public Comment saveComment(Comment comment) {
        em.persist(comment);
        return comment;
    }

    //R
    public Optional<Comment> findByCommentId(Long commentId) {
        return Optional.ofNullable(em.find(Comment.class, commentId));
    }

    //U
    public Comment updateComment(Comment updatedComment) {
        em.merge(updatedComment);
        return updatedComment;
    }

    //D 소프트딜리트X
    public void deleteComment(Long commentId) {
        Comment comment = em.find(Comment.class, commentId);
        if ((comment != null)) {
            em.remove(comment);
        }
    }


    @Override
    public List<Comment> findByBoard(Board board) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.board = :board", Comment.class)
                .setParameter("board", board)
                .getResultList();
    }

    @Override
    public List<Comment> findByMember(Member member) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.member = :member", Comment.class)
                .setParameter("member", member)
                .getResultList();
    }
}
