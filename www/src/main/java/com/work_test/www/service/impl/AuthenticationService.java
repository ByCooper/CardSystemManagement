package com.work_test.www.service.impl;

import com.work_test.www.dto.JwtResponse;
import com.work_test.www.dto.LoginRequest;
import com.work_test.www.dto.RegisterRequest;
import com.work_test.www.jwt.JwtUtils;
import com.work_test.www.model.Role;
import com.work_test.www.model.Token;
import com.work_test.www.model.User;
import com.work_test.www.repo.RoleRepository;
import com.work_test.www.repo.TokenRepository;
import com.work_test.www.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    

    public AuthenticationService(UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenRepository tokenRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
        this.roleRepository = roleRepository;
    }

    public void register(RegisterRequest request){
        User user = new User();
        user.setName(request.getUserName());
        Role role = roleRepository.findByRole(request.getRole()).orElseThrow();
        user.setRoles(Collections.singleton(role));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        System.out.println();
    }

    private void revokeAllToken(User user){
        List<Token> validTokens = tokenRepository.findAllAccessTokenByUser(user.getId());
        if(!validTokens.isEmpty()){
            validTokens.forEach(t -> {
                t.setLoggedOut(true);
            });
        }
        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, User user){
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    public JwtResponse login(LoginRequest loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        User user = userRepository.findByName(loginRequest.getUsername()).orElseThrow();

        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        revokeAllToken(user);

        saveUserToken(accessToken, refreshToken, user);

        return new JwtResponse(accessToken, refreshToken);
    }

    public ResponseEntity<JwtResponse> refreshToken(HttpServletRequest request, HttpServletResponse response){
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authorizationHeader.substring(7);
        String username = jwtUtils.extractUsername(token);

        User user = userRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        if(jwtUtils.isValidRefreshToken(token, user)){
            String accessToken = jwtUtils.generateAccessToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(user);

            revokeAllToken(user);

            saveUserToken(accessToken, refreshToken, user);

            return new ResponseEntity<>(new JwtResponse(accessToken, refreshToken), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
