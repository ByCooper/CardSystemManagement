package com.work_test.www.controller;


import com.work_test.www.dto.JwtResponse;
import com.work_test.www.dto.LoginRequest;
import com.work_test.www.dto.RegisterRequest;
import com.work_test.www.jwt.JwtUtils;
import com.work_test.www.repo.UserRepository;
import com.work_test.www.service.impl.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/bank/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public AuthController(
            JwtUtils jwtUtils,
            UserRepository userRepository,
            AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByName(request.getUserName())) {
            return ResponseEntity.badRequest().body("Ошибка: пользователь существует!");
        }
        authenticationService.register(request);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<JwtResponse> getRefreshToken(
            HttpServletRequest request,
            HttpServletResponse response)
    {
        return authenticationService.refreshToken(request, response);
    }

    @GetMapping("/check")
    public String checkUrl() {
        return "Чик-чирик";
    }
}
