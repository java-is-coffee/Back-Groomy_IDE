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

    //소프트딜리트로
    public void deleteComment(Long commentId) {
        Comment comment = em.find(Comment.class, commentId);
        comment.setCommentStatus(CommentStatus.DELETED);
    }

    //commentStatus 가 ACTIVE인 댓글만 + createdTime 오름차순으로 정렬한 결과값 반환
    @Override
    public List<Comment> findCommentByBoardId(Board board, CommentStatus status) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.board = :board AND c.commentStatus = :status ORDER BY c.createdTime ASC", Comment.class)
                .setParameter("board", board)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Comment> findCommentByMemberId(Member member, CommentStatus status) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.member = :member AND c.commentStatus = :status ORDER BY c.createdTime ASC", Comment.class)
                .setParameter("member", member)
                .setParameter("status", status)
                .getResultList();
    }


}
