package com.work_test.www.jwt;

import com.work_test.www.model.User;
import com.work_test.www.repo.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {


    private final String secretKey = "4eeab38d706831be4b36612ead768ef8182d1dd6f0e14e5dc934652e297fb16a"; //секретный ключ

    private final long accessTokenExpiration = 36000000; // 10 hours; //указываем время жизни токена

    private final long refreshTokenExpiration = 252000000; // 7 days;

    private final TokenRepository tokenRepository;

    public JwtUtils(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Генерация JWT-токена
     */
    public String generateToken(UserDetails userDetails, long expiryTime){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Валидация JWT-токена
     */
    public boolean isValid(String token, UserDetails user){

        String username = extractUsername(token);
        boolean isValidRefreshToken = tokenRepository.findByRefreshToken(token)
                .map(t -> !t.isLoggedOut()).orElse(false);

        return username.equals(user.getUsername()) && isAccessTokenExpired(token) && isValidRefreshToken;
    }

    public boolean isValidRefreshToken(String token, User user){
        String username = extractUsername(token);
        boolean isValidToken = tokenRepository.findByAccessToken(token)
                .map(t -> !t.isLoggedOut()).orElse(false);

        return username.equals(user.getName()) && isAccessTokenExpired(token) && isValidToken;
    }

    /**
     * Получение имени пользователя из токена
     */
    public String getUsernameFromToken(String token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private boolean isAccessTokenExpired(String token){
        return !extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    public String generateAccessToken(User user){
        return generateToken((UserDetails) user, accessTokenExpiration);
    }

    public String generateRefreshToken(User user){
        return generateToken((UserDetails) user, refreshTokenExpiration);
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
