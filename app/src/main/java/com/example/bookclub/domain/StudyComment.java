package com.example.bookclub.domain;

import com.example.bookclub.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class StudyComment extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(name ="COMMENT_ID")
	private Long id;

	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACCOUNT_ID")
	@ToString.Exclude
	private Account account;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STUDY_ID")
	@ToString.Exclude
	private Study study;

	@OneToMany(mappedBy = "studyComment")
	@ToString.Exclude
	List<StudyCommentLike> studyCommentLikes = new ArrayList<>();

	@Transient
	private boolean isWrittenByMe;

	@Transient
	private boolean liked;

	@Transient
	private int likesCount;

	@Builder
	public StudyComment(Long id, String content, Account account, Study study, boolean isWrittenByMe,
						List<StudyCommentLike> studyCommentLikes, boolean liked, int likesCount) {
		this.id = id;
		this.content = content;
		this.account = account;
		this.study = study;
		this.isWrittenByMe = isWrittenByMe;
		this.studyCommentLikes = studyCommentLikes;
		this.liked = liked;
		this.likesCount = likesCount;
	}

	public void setIsWrittenByMeTrue() {
		this.isWrittenByMe = true;
	}

	public void setLikesCount(int likesCount) {
		this.likesCount = likesCount;
	}

	public void addLiked() {
		this.liked = true;
	}
}
