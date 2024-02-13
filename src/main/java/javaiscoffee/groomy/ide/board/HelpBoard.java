package javaiscoffee.groomy.ide.board;

import jakarta.persistence.*;
import javaiscoffee.groomy.ide.member.Member;
import lombok.*;

/**
 * 게시글 추천 저장하는 테이블
 */
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "help_board")
public class HelpBoard {
    //복합키 매핑을 위한 클래스 지정 (@Embeddable 어노테이션이 적용된 클래스를 PK로 사용하기 위해 사용된다.)
    @EmbeddedId
    private HelpBoardId id;

    @MapsId("memberId") //식별자 클래스(HelpBoardId)에서 맵핑되는 필드명으로 적어준다.
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @MapsId("boardId")
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board boardId;
}
