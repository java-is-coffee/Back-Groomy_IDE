package javaiscoffee.groomy.ide.project;

import jakarta.persistence.*;
import javaiscoffee.groomy.ide.chat.ProjectChat;
import javaiscoffee.groomy.ide.member.Member;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "project")
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;
    @ManyToOne @Setter
    @JoinColumn(name = "member_id", nullable = false)
    private Member memberId;  // 프로젝트 생성자
    @Setter
    @Column(nullable = false, length = 255)
    private String projectName;
    @Setter
    @Column(nullable = false, length = 255)
    private String description;
    @Setter
    @Column(nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private ProjectLanguage language;   //프로젝트 언어

    @Column(nullable = false)
    private LocalDate createdDate;  //생성날짜는 Setter 필요 없음
    @Setter
    private LocalDate updatedDate;  //수정일은 Setter 필요함
    @Setter
    @Column(nullable = false)
    private Boolean deleted;    //현재 댓글이 지워졌는지 남아있는지 표시
    @Lob @Setter
    @Column(nullable = false)
    private String projectPath; //프로젝트 접근 경로
    @OneToMany(mappedBy = "project")
    private Set<ProjectMember> projectMembers;
    @OneToMany(mappedBy = "project")
    private List<ProjectChat> projectChat = new ArrayList<>();

    /**
     * 저장되기 전에 미리 수행되는 메서드
     */
    @PrePersist
    public void PrePersist() {
        this.createdDate = LocalDate.now();
        this.updatedDate = LocalDate.now();
        this.deleted = false;
    }
}
