package javaiscoffee.groomy.ide.comment;

import javaiscoffee.groomy.ide.board.Board;
import javaiscoffee.groomy.ide.member.Member;

import java.util.List;

public interface CommentRepository {
    List<Comment> findCommentByBoardId(Board board, CommentStatus status); //해당 게시판에 속한 댓글 조회

    List<Comment> findCommentByMemberId(Member member, CommentStatus status);  //해당 사용자의 댓글 조회

}