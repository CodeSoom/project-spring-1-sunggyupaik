package com.example.bookclub.domain.account.emailauthentication;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 이메일 인증
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
public class EmailAuthentication {
    /* 식별자 */
    @Id @GeneratedValue
    @Column(name = "EMAILAUTHENTICATION_ID")
    private Long id;

    /* 이메일 */
    private String email;

    /* 인증번호 */
    private String authenticationNumber;

    @Builder
    public EmailAuthentication(Long id, String email, String authenticationNumber) {
        this.id = id;
        this.email = email;
        this.authenticationNumber = authenticationNumber;
    }

    /**
     * 주어진 인증번호가 저장된 인증번호와 일치하는지 여부를 반환한다.
     *
     * @param authenticationNumber 인증번호
     * @return 주어진 인증번호가 저장된 인증번호와 일치하는지 여부
     */
    public boolean isSameWith(String authenticationNumber) {
        return this.authenticationNumber.equals(authenticationNumber);
    }
}
