package com.example.bookclub.common;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * 생성시간, 수저시간을 생성한다.
 */
@EntityListeners(value = AuditingEntityListener.class)
@MappedSuperclass
@Getter
@ToString
public class BaseTimeEntity {
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdDate;

	@LastModifiedDate
	private LocalDateTime updatedDate;
}
