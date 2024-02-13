package javaiscoffee.groomy.ide.comment;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
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
public class JpaCommentRepository implements CommentRepository{

    private final EntityManager em;
    public JpaCommentRepository(EntityManager em) {
        this.em = em;
    }

    // C
    public Comment saveComment(Comment comment) {
        em.persist(comment);
        Board board = comment.getBoard(); //댓글에서 게시글 가져오고 +1 하는 쿼리 생성
        Query query = em.createQuery("UPDATE Board b SET b.commentNumber = b.commentNumber + 1 WHERE b.boardId = :boardId");
        query.setParameter("boardId", board.getBoardId()); // :boardId 매개변수에 boardId 값으로 설정
        query.executeUpdate(); // DB에서 댓글 수 업데이트
        return comment;
    }

    //R
    public Comment findByCommentId(Long commentId) {
        Comment comment = em.find(Comment.class, commentId);

        return comment;
    }

    //U
    public Comment updateComment(Comment updatedComment) {
        em.merge(updatedComment);
        return updatedComment;
    }

    //소프트딜리트, 재사용 가능
    public void deleteComment(Long commentId) {
        Comment comment = em.find(Comment.class, commentId);
        comment.setCommentStatus(CommentStatus.DELETED);
        Board board = comment.getBoard(); //댓글에서 게시글 가져오고 -1 하는 쿼리 생성
        Query query = em.createQuery("UPDATE Board b SET b.commentNumber = b.commentNumber - 1 WHERE b.boardId = :boardId");
        query.setParameter("boardId", board.getBoardId()); // :boardId 매개변수에 boardId 값으로 설정
        query.executeUpdate(); // DB에서 댓글 수 업데이트
    }

    public CommentHelpNumber findCommentHelpNumber(CommentHelpNumberId id) {
        return em.find(CommentHelpNumber.class,id);
    }

    public CommentHelpNumber saveCommentHelpNumber(CommentHelpNumber helpNumber) {
        em.persist(helpNumber);
        em.flush();
        return helpNumber;
    }

    public boolean deleteCommentHelpNumber(CommentHelpNumber helpNumber) {
        try {
            em.remove(helpNumber);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    //boardId로 comments 조회
    @Override
    public List<Comment> findCommentByBoardId(Board board, CommentStatus status) {
        return em.createQuery("SELECT c FROM Comment c WHERE (c.board = :board AND c.commentStatus = :status AND c.originComment is not null) OR (c.board = :board AND c.originComment is null) ORDER BY c.createdTime ASC", Comment.class)
                .setParameter("board", board)
                .setParameter("status", status)
                .getResultList();
    }


    @Override
    public List<Comment> findCommentByMemberId(Member member, CommentStatus status) {
        return em.createQuery("SELECT c FROM Comment c WHERE c.member = :member AND c.commentStatus = :status ORDER BY c.createdTime DESC", Comment.class)
                .setParameter("member", member)
                .setParameter("status", status)
                .getResultList();
    }



}
