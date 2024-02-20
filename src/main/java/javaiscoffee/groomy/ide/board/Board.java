package javaiscoffee.groomy.ide.board;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javaiscoffee.groomy.ide.comment.Comment;
import javaiscoffee.groomy.ide.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@ToString(exclude = {"member", "comment"})
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "boardId"
)
public class Board {
    @Id @Column(name = "board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @Setter
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;

    @NotNull @Setter
    private String nickname;
    @NotNull @Setter
    private String title;
    @NotNull @Setter
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull @Column(name = "created_time")
    private LocalDateTime createdTime;
    @Setter
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @NotNull @Setter @Column(name = "view_number")
    private int viewNumber;
    @NotNull @Setter @Column(name = "comment_number")
    private int commentNumber;
    @NotNull @Setter @Column(name = "scrap_number")
    private int scrapNumber;
    @NotNull @Setter @Column(name = "help_number")
    private int helpNumber;

    @NotNull @Setter @Column(name = "is_completed")
    private boolean isCompleted;

    @NotNull @Setter @Enumerated(EnumType.STRING)
    private BoardStatus boardStatus;

    @NotNull @OneToMany(mappedBy = "board")
    private List<Comment> comment = new ArrayList<>();

    // 게시글 조회 할 때 정상상태 게시글 조회 하는 메서드
    public boolean isActive() {
        return boardStatus == BoardStatus.ACTIVE;
    }

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        this.viewNumber = 0;
        this.commentNumber = 0;
        this.scrapNumber = 0;
        this.helpNumber = 0;
        this.boardStatus = BoardStatus.ACTIVE;
    }
}
