package com.example.bookclub.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private static final Long EXISTED_ID = 1L;
    private static final String EXISTED_EMAIL = "setUpEmail";
    private static final String EXISTED_TOKEN = "setUpToken";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void encodeWithIdAndEmail() {
        String token = jwtUtil.encode(EXISTED_ID, EXISTED_EMAIL);

        assertThat(token).isEqualTo(EXISTED_TOKEN);
    }
}
