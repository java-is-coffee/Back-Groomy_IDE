package javaiscoffee.groomy.ide.comment;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable //다른 엔티티의 속성으로 사용될 수 있는 클래스를 정의할 때 사용, 해당 클래스를 다른 엔티티의 일부로 포함시킬 수 있다.
public class CommentHelpNumberId implements Serializable {
    private Long memberId;
    private Long commentId;
}
