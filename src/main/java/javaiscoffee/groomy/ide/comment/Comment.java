package javaiscoffee.groomy.ide.comment;

import com.fasterxml.jackson.annotation.*;
import javaiscoffee.groomy.ide.board.Board;
import javaiscoffee.groomy.ide.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"member"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "commentId"
)
public class Comment {
    @Id @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;
    @Setter @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_comment", referencedColumnName = "comment_id")
    private Comment originComment; //대댓글

    @NotNull
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @NotNull @Setter @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @NotNull @Setter
    @Column(columnDefinition = "TINYTEXT")
    private String content;
    @NotNull @Setter
    private String nickname;
    @NotNull @Setter
    private int helpNumber;
    @NotNull
    private LocalDateTime createdTime;
    @NotNull @Setter @Enumerated(EnumType.STRING)
    private CommentStatus commentStatus;


    // 저장 전 실행되는 메서드
    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.commentStatus = CommentStatus.ACTIVE;
        this.helpNumber = 0;
    }

    @JsonProperty("boardId")
    public Long getBoardId() {
        return board != null ? board.getBoardId() : null;
    }

    @JsonProperty("memberId")
    public Long getMemberId() {
        return member != null ? member.getMemberId() : null;
    }

    @JsonProperty("originComment")
    public Long getOriginComment() {
        return originComment != null ? originComment.getCommentId() : null;
    }


}
