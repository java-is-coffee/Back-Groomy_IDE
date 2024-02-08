package javaiscoffee.groomy.ide.comment;

import jakarta.persistence.*;
import javaiscoffee.groomy.ide.member.Member;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * 댓글의 '도움이 됐어요'를 저장하는 테이블
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "help_comment")
public class CommentHelpNumber {
    //복합키 매핑을 위한 클래스 지정 (@Embeddable 어노테이션이 적용된 클래스를 PK로 사용하기 위해 사용된다.)
    @EmbeddedId
    private CommentHelpNumberId id;

    @MapsId("memberId") //식별자 클래스(CommentHelpNumberId)에서 맵핑되는 필드명으로 적어준다.
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @MapsId("commentId")
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment commentId;


}
