package com.hsbc.common.utils.sm4;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 加解密
 * </p>
 *
 * @author A80429
 * @since 2021-10-29
 */
@Slf4j
@Component("smUtils")
public class SmUtils implements StringEncryptor {

    public static final Pattern P_MATCH = Pattern.compile("\\s*|\t|\r|\n");

    /**
     * 加解密key
     */
    @Getter
    private static volatile String secretKey;

    public SmUtils(String secretKey) {
        SmUtils.secretKey = secretKey;
    }

    /**
     * SM4加密
     */
    @Override
    public String encrypt(String plainText) throws IllegalArgumentException {
        if (StringUtils.isBlank(plainText)) {
            throw new IllegalArgumentException("不合法参数:{}" + plainText);
        }
        try {
            SM4Context ctx = new SM4Context();
            ctx.isPadding = true;
            ctx.mode = 1;
            byte[] keyBytes = secretKey.getBytes();
            SM4Utils sm4 = new SM4Utils();
            sm4.sm4SetkeyEnc(ctx, keyBytes);
            byte[] encrypted = sm4.sm4CryptEcb(ctx, plainText.getBytes(StandardCharsets.UTF_8));
            String cipherText = Base64.getEncoder().encodeToString(encrypted);
            if (cipherText != null && !cipherText.trim().isEmpty()) {
                Matcher m = P_MATCH.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText;
        } catch (Exception e) {
            log.error("SmUtils.encrypt error: ", e);
            return null;
        }
    }

    /**
     * SM4解密
     */
    @Override
    public String decrypt(String cipherText) throws IllegalArgumentException {
        if (StringUtils.isBlank(cipherText)) {
            throw new IllegalArgumentException("不合法参数:{}" + cipherText);
        }
        try {
            SM4Context ctx = new SM4Context();
            ctx.isPadding = true;
            ctx.mode = 0;
            byte[] keyBytes = secretKey.getBytes();
            SM4Utils sm4 = new SM4Utils();
            sm4.sm4SetkeyDec(ctx, keyBytes);
            byte[] decrypted = sm4.sm4CryptEcb(ctx, Base64.getDecoder().decode(cipherText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("SmUtils.decrypt error: ", e);
            return null;
        }
    }

}
