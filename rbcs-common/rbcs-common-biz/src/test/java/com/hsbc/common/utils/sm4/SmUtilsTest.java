package com.hsbc.common.utils.sm4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class SmUtilsTest {
    @InjectMocks
    private SmUtils smUtils;

    private final String SECRET_KEY = "rbcp-balance2025";

    @BeforeEach
    public void setUp() {
        smUtils = new SmUtils(SECRET_KEY);
    }

    @DisplayName("SM4加密")
    @Test
    void encrypt() {
        String plaintext = "xiaoYi738#";
        String ciphertext = smUtils.encrypt(plaintext);
        System.out.println("加密后的密文：" + ciphertext);
        assertEquals("jrw9moqHuo3gXs1RN0+GUw==", ciphertext);
    }

    @DisplayName("SM4解密")
    @Test
    void decrypt() {
        String ciphertext = "jrw9moqHuo3gXs1RN0+GUw==";
        String plaintext = smUtils.decrypt(ciphertext);
        assertEquals("xiaoYi738#", plaintext);
    }
}