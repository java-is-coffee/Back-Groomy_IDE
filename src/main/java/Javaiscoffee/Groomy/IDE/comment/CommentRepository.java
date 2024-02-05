package Javaiscoffee.Groomy.IDE.comment;

import Javaiscoffee.Groomy.IDE.board.Board;
import Javaiscoffee.Groomy.IDE.member.Member;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    List<Comment> findByBoard(Board board); //해당 게시판에 속한 댓글 조회

    List<Comment> findByMember(Member member);  //해당 사용자의 댓글 조회

}


//아까 생성만 하면 된다고 말씀드렸는데 일단은 보안 검증 없이 crud 만드시고 저랑 같이 검증 추가하는 방식으로 가시죠