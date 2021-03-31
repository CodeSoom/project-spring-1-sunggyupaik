package com.example.bookclub.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {
    private static final String SECRET = "12345678901234567890123456789010";
    private static final Long EXISTED_ID = 1L;
    private static final String EXISTED_EMAIL = "setUpEmail";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
    }

    @Test
    void encodeWithIdAndEmail() {
        String token = jwtUtil.encode(EXISTED_ID, EXISTED_EMAIL);

        assertThat(token).contains(".");
    }
}
