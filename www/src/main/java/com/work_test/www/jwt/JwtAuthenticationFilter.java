package com.work_test.www.jwt;


import com.work_test.www.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        System.out.println("inside method doFilterInternal");
        try {
            System.out.println("Зашел в try");
            final String authHeader = request.getHeader("Authorization");//Получаем заголовок Authorization
            logger.info("Заголовок получен");
            //Проверяем наличие формата и заголовка
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            logger.info("Наличие и формат заголовка проверены");
            String jwt = authHeader.substring(7);//Извлекаем токен (после "Bearer ")
            logger.info("Токен извлечен: {}", jwt);
            //Получаем имя пользователя
            String username = jwtUtils.extractUsername(jwt);
            logger.info("Имя из токена получено: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //Загружаем пользователя из БД
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtils.isValid(jwt, userDetails)) {

                    //Создаем объект аутентификации
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    //Сохраняем в контекст секьюрити
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Невозможно создать объект аутентификации для юзера: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);//Передаем запрос дальше по цепочке фильтров
    }
}
