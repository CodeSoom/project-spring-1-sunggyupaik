package com.example.bookclub.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PERSISTENT_LOGINS")
public class PersistentLogins {
	@Id
	private String series;

	private String username;

	private String token;

	@Column(name = "LAST_USED")
	private LocalDateTime lastUsed;
}
