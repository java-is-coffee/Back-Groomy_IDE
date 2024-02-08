package javaiscoffee.groomy.ide.chat;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.project.Project;
import lombok.*;

import java.time.LocalDateTime;

/**
 * - 메시지 내용 검증: 사용자가 입력한 메시지 내용에 대한 검증 로직을 추가할 수 있습니다.
 * 예를 들어, 스크립트 태그와 같은 잠재적으로 위험한 내용을 필터링하는 등의 보안 관련 처리가 필요할 수 있습니다.
 * - 시간대 처리: LocalDateTime을 사용하는 것은 좋으나, 사용자의 시간대를 고려하여 메시지의 생성 시간을 표시하는 방법을 고려할 수 있습니다.
 * 시스템 전역에서 UTC를 사용하고 클라이언트 측에서 사용자의 로컬 시간대로 변환하여 표시하는 방법이 일반적입니다.
 * - 긴 메시지 처리: UI/UX 측면에서 매우 긴 메시지를 효과적으로 표시하기 위한 방안을 고려해야 합니다.
 * 예를 들어, 긴 메시지를 일정 길이까지만 미리 보여주고, "더 보기" 옵션을 제공하여 사용자가 전체 메시지를 볼 수 있도록 하는 방법 등이 있습니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"member","project"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "projectChatId"
)
public class ProjectChat {
    @Id
    @Column(name = "project_chat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long projectChatId;

    @NotNull @Setter @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @NotNull @Setter @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull @Setter
    @Column(nullable = false, length = 1000) // 1000자로 임시 제한
    private String message;

    @NotNull
    private LocalDateTime createdTime;

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
    }

    @JsonProperty("boardId")
    public Long getProjectId() {
        return project != null ? project.getProjectId() : null;
    }

    @JsonProperty("memberId")
    public Long getMemberId() {
        return member != null ? member.getMemberId() : null;
    }
}
