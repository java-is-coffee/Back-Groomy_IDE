package javaiscoffee.groomy.ide.project;

import jakarta.persistence.*;
import javaiscoffee.groomy.ide.member.Member;
import lombok.*;

/**
 * 프로젝트 참가 인원을 저장하는 테이블
 */
@Entity
@Getter @Setter
@Table(name = "project_member")
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMember {
    //복합키때문에 사용하는 클래스
    @EmbeddedId
    private ProjectMemberId id;

    //아래 두 개의 필드는 즉시로딩할지 지연로딩할지 고민 해봐야 함
    @ManyToOne
    @MapsId("projectId")
    private Project project;
    @ManyToOne
    @MapsId("memberId")
    private Member member;
    @Column(nullable = false)
    private Boolean participated;   //초대된 상태(false)인지 프로젝트에 참가했는지(true) 저장

    public ProjectMember(Member projectCreator, Project createdProject, boolean participated) {
        this.member = projectCreator;
        this.project = createdProject;
        this.participated = participated;
    }
}

