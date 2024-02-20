package javaiscoffee.groomy.ide.board;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.groomy.ide.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

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

    private LocalDateTime createdTime;

    public Scrap(ScrapId id, Member member, Board boardId) {
        this.id = id;
        this.member = member;
        this.boardId = boardId;
    }

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
    }
}
