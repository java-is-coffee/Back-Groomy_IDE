package javaiscoffee.groomy.ide.board;

import javaiscoffee.groomy.ide.comment.Comment;
import javaiscoffee.groomy.ide.comment.CommentStatus;
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
public class JpaBoardRepository implements BoardRepository {
    @PersistenceContext
    private final EntityManager em;

    public JpaBoardRepository(EntityManager em) {
        this.em = em;
    }

    // C
    public Board saveBoard(Board board) {
        em.persist(board);
        em.flush();
        return board;
    }

    //R
    public Optional<Board> findByBoardId(Long BoardId) {
        return Optional.ofNullable(em.find(Board.class, BoardId));
    }

    //U
    public Board updateBoard(Board updatedBoard) {
        em.merge(updatedBoard);
        return updatedBoard;
    }

    //D
    public void deleteBoard(Board deletedBoard) {
        em.merge(deletedBoard);
    }

    @Override
    public List<Board> findBoardByBoardId(BoardStatus status) {
        return em.createQuery("SELECT b FROM Board b WHERE b.boardStatus = :status ORDER BY b.createdTime ASC", Board.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Board> findBoardByMemberId(Member member) {
        return em.createQuery("SELECT b FROM Board b WHERE b.member = :member", Board.class)
                .setParameter("member", member)
                .getResultList();
    }
}

// 모든 게시글 조회 만들어야하나?