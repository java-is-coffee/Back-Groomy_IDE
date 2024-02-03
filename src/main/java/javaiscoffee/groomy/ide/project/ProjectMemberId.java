package javaiscoffee.groomy.ide.project;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ProjectMember 테이블의 복합키를 구현하기 위해 만든 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProjectMemberId implements Serializable {
    private Long projectId;
    private Long memberId;
}
