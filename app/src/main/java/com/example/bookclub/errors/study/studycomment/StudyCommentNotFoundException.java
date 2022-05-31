package com.example.bookclub.errors.study.studycomment;

/**
 * 주어진 스터디 댓글 식별자에 해당하는 스터디 댓글이 없는 경우 예외
 */
public class StudyCommentNotFoundException extends RuntimeException{
	public  StudyCommentNotFoundException(Long id) {
		super("StudyComment not found : " + id);
	}
}
