package javaiscoffee.groomy.ide.project;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * ProjectMember 테이블의 복합키를 구현하기 위해 만든 클래스
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProjectMemberId implements Serializable {
    private Long projectId;
    private Long memberId;
}
