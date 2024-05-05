package org.example.utils;

import java.util.Random;

public class RandomUtils {

    // 生成 6 位随机数字作为短信验证码
    public static String generateVerificationCode(int digit) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < digit; i++) {
            code.append((int) (Math.random() * 10));
        }
        return code.toString();
    }

    public static String generateRandomString(int digit) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        StringBuilder sb = new StringBuilder(digit);
        for (int i = 0; i < digit; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}
