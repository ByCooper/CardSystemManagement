package com.work_test.www.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    private final String secret = "MySuperSecretKey64"; //секретный ключ
    private final long expiration = 86400000; //указываем время жизни токена

    /**
     * Генерация JWT-токена
     */
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Валидация JWT-токена
     */
    public boolean validateToken(String token){
        try{
//            Jwts.parserBuilder().setSigningKey(secret).build();
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e){
            System.err.println("Неверная подпись: " + e.getMessage());
        } catch (MalformedJwtException e){
            System.err.println("Некорректный токен: " + e.getMessage());
        } catch (ExpiredJwtException e){
            System.err.println("Токен просрочен: " + e.getMessage());
        } catch (UnsupportedJwtException e){
            System.err.println("Неподдерживаемый токен: " + e.getMessage());
        } catch (IllegalArgumentException e){
            System.err.println("Пустой токен: " + e.getMessage());
        }
        return false;
    }

    /**
     * Получение имени пользователя из токена
     */
    public String getUsernameFromToken(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
