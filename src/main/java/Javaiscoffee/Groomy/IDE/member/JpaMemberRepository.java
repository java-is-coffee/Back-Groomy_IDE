package Javaiscoffee.Groomy.IDE.member;

import Javaiscoffee.Groomy.IDE.login.LoginDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Table;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Table(name = "member")
@Transactional
public class JpaMemberRepository implements MemberRepository {
    private final EntityManager em;

    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Member member) {
        member.setHelpNumber(0L);
        member.setRole(MemberRole.USER);
        em.persist(member);
    }

    public void delete(Member member) {
        em.remove(member);
        return;
    }

    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(em.find(Member.class,id));
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m WHERE m.email = :email", Member.class);
        query.setParameter("email", email);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(m) FROM Member m WHERE m.email = :email", Long.class);
        query.setParameter("email", email);
        long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public Optional<Member> findByMemberId(Long memberId) {
        TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m WHERE m.memberId = :memberId", Member.class);
        query.setParameter("memberId", memberId);
        return Optional.ofNullable(query.getSingleResult());
    }
}
