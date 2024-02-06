package javaiscoffee.groomy.ide.member;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import javaiscoffee.groomy.ide.board.Board;
import javaiscoffee.groomy.ide.comment.Comment;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.groomy.ide.project.ProjectMember;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"comment", "board"})
@Table(name = "member")
public class Member implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @Setter
    @Column(nullable = false, length = 255)
    private String password;
    @Setter
    @Column(nullable = false, length = 255)
    private String name;
    @Setter
    @Column(nullable = false, length = 255)
    private String nickname;
    @Setter
    @Column(nullable = false)
    private Long helpNumber;
    @Setter
    @Column(nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private MemberRole role;
    @OneToMany(mappedBy = "member")
    private Set<ProjectMember> projectMembers;
    @NotNull @OneToMany(mappedBy = "member")
    @JsonManagedReference
    private List<Board> board = new ArrayList<>();
    @NotNull @OneToMany(mappedBy = "member")
    @JsonManagedReference
    private List<Comment> comment = new ArrayList<>();

    @PrePersist
    public void PrePersist() {
        this.helpNumber = 0L;
        this.role = MemberRole.USER;
    }


    //UserDetails를 위한 추가
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 저장되어 있는 현재 비밀번호를 암호화
     * @param passwordEncoder 암호화 할 인코더 클래스
     * @return 변경된 멤버 Entity
     */
    public Member hashPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
        return this;
    }

    /**
     * 비밀번호 확인
     * @param plainPassword
     * @param passwordEncoder
     * @return 입력받은 비밀번호를 암호화해서 db에 있는 암호화되어 있는 값을
     */
    public boolean checkPassword(String plainPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(plainPassword,this.password);
    }


    public Member(String email, String password, String name, String nickname) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
    }

    public Member(String email, String password, String name, String nickname, Long helpNumber, MemberRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.helpNumber = helpNumber;
        this.role = role;
    }
}
