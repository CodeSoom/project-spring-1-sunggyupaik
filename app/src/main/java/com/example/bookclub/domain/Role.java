package com.example.bookclub.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
    @Id
    @GeneratedValue
    private Long id;

    private String email;

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
