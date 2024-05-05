package org.example.utils;

import java.util.Base64;
import java.util.Date;

import io.jsonwebtoken.*;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class JwtUtils {

    private static final String SECRET_KEY = "7fcad27003e63d7841f9576882848280"; // 秘钥
    private static final long VALID_TIME = 3*60 * 60 * 1000; // 有效时间30分钟



    /**
     * 生成Token
     * @param userId 用户唯一标识
     * @return Token
     */
    public static String generateToken(Integer userId) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + VALID_TIME);
        byte[] secretBytes = Base64.getEncoder().encode(SECRET_KEY.getBytes());
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .setSubject(userId.toString())
                .signWith(SignatureAlgorithm.HS256, secretBytes);
        return builder.compact();
    }

    public static boolean isTokenExpired(String token) throws ExpiredJwtException {
        Claims claims = Jwts.parser()
                .setSigningKey(Base64.getEncoder().encode(SECRET_KEY.getBytes()))
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().before(new Date());
    }

    public static String refreshToken(String token) {
        try{
            isTokenExpired(token);
            return token;
        }catch (ExpiredJwtException e){
            Claims claims=e.getClaims();
            System.out.println("异常后");
            Date now = new Date();
            Date expireTime = new Date(now.getTime() + VALID_TIME);
            System.out.println("subject:"+claims.getSubject());
            JwtBuilder builder = Jwts.builder()
                    .setIssuedAt(now)
                    .setExpiration(expireTime)
                    .setSubject(claims.getSubject())
                    .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encode(SECRET_KEY.getBytes()));
            return builder.compact();
        }
    }



    public static Integer getUserId(String token) {
        System.out.println(token);
        String userId = Jwts.parser()
                .setSigningKey(Base64.getEncoder().encode(SECRET_KEY.getBytes()))
                .parseClaimsJws(token)
                .getBody().getSubject();
        return Integer.valueOf(userId);
    }

    public static String generateAdminToken(String username) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + VALID_TIME);
        byte[] secretBytes = Base64.getEncoder().encode(SECRET_KEY.getBytes());
        JwtBuilder builder = Jwts.builder()
                .setId(username)
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .setSubject("jwt")
                .signWith(SignatureAlgorithm.HS256, secretBytes);
        return builder.compact();
    }

}
