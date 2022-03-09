package com.example.bookclub.domain;

import com.example.bookclub.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 권한
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseTimeEntity {
    /* 식별자 */
    @Id @GeneratedValue
    @Column(name = "ROLE_ID")
    private Long id;

    /* 이메일 */
    private String email;

    /* 이름 */
    private String name;
    
    public Role(String name) {
        this(null, name);
    }

    @Builder
    public Role(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
