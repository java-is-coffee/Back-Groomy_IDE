package javaiscoffee.groomy.ide.comment;

import javaiscoffee.groomy.ide.board.Board;
import javaiscoffee.groomy.ide.member.Member;

import java.util.List;

public interface CommentRepository {
    List<Comment> findCommentByBoardId(Board board, CommentStatus status); //해당 게시판에 속한 댓글 조회

    List<Comment> findCommentByMemberId(Member member);  //해당 사용자의 댓글 조회

}


//아까 생성만 하면 된다고 말씀드렸는데 일단은 보안 검증 없이 crud 만드시고 저랑 같이 검증 추가하는 방식으로 가시죠