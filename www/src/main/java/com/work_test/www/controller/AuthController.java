package com.work_test.www.controller;


import com.work_test.www.dto.JwtResponse;
import com.work_test.www.dto.LoginRequest;
import com.work_test.www.dto.RegisterRequest;
import com.work_test.www.jwt.JwtUtils;
import com.work_test.www.model.Role;
import com.work_test.www.model.User;
import com.work_test.www.repo.RoleRepository;
import com.work_test.www.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/bank/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken((UserDetails) authentication.getPrincipal());
            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        } catch (DisabledException e) {
            return ResponseEntity.status(401).body("User disabled4");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByName(request.getUserName())) {
            return ResponseEntity.badRequest().body("Ошибка: пользователь существует!");
        }
        User user = new User();
        user.setName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role userRole = roleRepository.findByRole(request.getRole()).orElseThrow();
        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }

    @GetMapping("/check")
    public String checkUrl() {
        return "Чик-чирик";
    }
}
