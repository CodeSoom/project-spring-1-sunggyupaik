package com.example.bookclub.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class EmailRequestDto {
    String email;

    @Builder
    public EmailRequestDto(String email) {
        this.email = email;
    }
}
