package Javaiscoffee.Groomy.IDE.comment;

import Javaiscoffee.Groomy.IDE.member.Member;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    List<Comment> findByBoard(Board board); //해당 게시판에 속한 댓글 조회

    List<Comment> findByMember(Member member);  //해당 사용자의 댓글 조회

}




// save 저장, 수정(Entity가 식별자를 갖고 있다면)
// find~~ 읽기
// delete 삭제

// create comment => 생성
// read comment => 읽기 , 특정한 요소 or 많은 요소 바꾸고싶을 때..
// update comment => 수정
// delete comment => 삭제
// update content => 수정
// update time..!?
//findBycommentId로 조회
//아까 생성만 하면 된다고 말씀드렸는데 일단은 보안 검증 없이 crud 만드시고 저랑 같이 검증 추가하는 방식으로 가시죠