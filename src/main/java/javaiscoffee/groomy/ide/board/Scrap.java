package javaiscoffee.groomy.ide.board;

import jakarta.persistence.*;
import javaiscoffee.groomy.ide.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "scrap")
public class Scrap {
    @EmbeddedId
    private ScrapId id;

    @MapsId("memberId")
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @MapsId("boardId")
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board boardId;
}
