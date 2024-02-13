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

    // 추천
    public HelpBoard findBoardHelpNumber(HelpBoardId id) {
        return em.find(HelpBoard.class, id);
    }

    public HelpBoard saveBoardHelpNumber(HelpBoard helpBoard) {
        em.persist(helpBoard);
        em.flush();
        return helpBoard;
    }

    public boolean deleteBoardHelpNumber(HelpBoard helpBoard) {
        try {
            em.remove(helpBoard);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 스크랩
    public Scrap findBoardScrap(ScrapId id) {
        return em.find(Scrap.class, id);
    }

    public Scrap saveBoardScrap(Scrap scrap) {
        em.persist(scrap);
        em.flush();
        return scrap;
    }

    public boolean deleteBoardScrap(Scrap scrap) {
        try {
            em.remove(scrap);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public List<Board> findBoardByPaging(int paging, int pagingNumber, BoardStatus status) {
        return em.createQuery("SELECT b FROM Board b WHERE b.boardStatus = :status ORDER BY b.createdTime DESC", Board.class)
                .setParameter("status",status)
                .setFirstResult((paging - 1) * pagingNumber)
                .setMaxResults(pagingNumber)
                .getResultList();
    }

    @Override
    public long countBoardsByStatus(BoardStatus status) {
        return em.createQuery("SELECT COUNT(b) FROM Board b WHERE b.boardStatus = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
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