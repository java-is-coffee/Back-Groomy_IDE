package Javaiscoffee.Groomy.IDE.board;

import Javaiscoffee.Groomy.IDE.comment.Comment;
import Javaiscoffee.Groomy.IDE.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *  이거 테스트하시려면 환경변수에다가 application.properties에 있는 값 4개 넣으셔야 할텐데
 *  ddl 어쩌구 주석 처리 되어있는거 해제하시고 실행 하시면 자동으로 테이블 생성해줍니다.
 */
@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id @Column(name = "board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @Setter
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull @Setter
    private String nickname;
    @NotNull @Setter
    private String title;
    @NotNull @Setter
    private String content;

    @NotNull @Column(name = "created_time")
    private LocalDateTime createdTime;

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

    @NotNull @Enumerated(EnumType.STRING)
    private BoardStatus boardStatus;

    @NotNull @OneToMany(mappedBy = "comment")
    private List<Comment> comment = new ArrayList<>();

    // 게시글 조회 할 때 정상상태 게시글 조회 하는 메서드
    public boolean isActive() {
        return boardStatus == BoardStatus.ACTIVE;
    }

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
        this.viewNumber = 0;
        this.commentNumber = 0;
        this.scrapNumber = 0;
        this.helpNumber = 0;
        this.boardStatus = BoardStatus.ACTIVE;
    }
}
