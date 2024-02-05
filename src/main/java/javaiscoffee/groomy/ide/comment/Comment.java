package javaiscoffee.groomy.ide.comment;

import javaiscoffee.groomy.ide.board.Board;
import javaiscoffee.groomy.ide.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 *  이거 테스트하시려면 환경변수에다가 application.properties에 있는 값 4개 넣으셔야 할텐데
 *  ddl 어쩌구 주석 처리 되어있는거 해제하시고 실행 하시면 자동으로 테이블 생성해줍니다.
 *  테스트 할 때 @ToString 안 하면 주소로 뜸
 */
@Entity
@Getter
public class Comment {
    @Id @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;

    //대댓글
    @NotNull @Setter @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_comment", referencedColumnName = "comment_id")
    private Comment originComment;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
    @NotNull @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull @Setter
    private String content;
    @NotNull @Setter
    private String nickname;
    @NotNull @Setter
    private int helpNumber;
    @NotNull
    private LocalDateTime createdTime;

    @NotNull @Enumerated(EnumType.STRING)
    private CommentStatus commentStatus;


    // 댓글 조회 할 때 정상상태 댓글 조회 하는 메서드
    public boolean isActive() {
        return commentStatus == CommentStatus.ACTIVE;
    }

    // 댓글 생성 시 작성 시간 넣으려고 어노테이션 달아놨습니다
    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
        this.commentStatus = CommentStatus.ACTIVE;
        this.helpNumber = 0;
    }


}
