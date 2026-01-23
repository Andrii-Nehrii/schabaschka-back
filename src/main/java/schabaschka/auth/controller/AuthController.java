package schabaschka.auth.controller;

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schabaschka.auth.dto.AuthLoginRequestDto;
import schabaschka.auth.dto.AuthRegisterRequestDto;
import schabaschka.auth.dto.AuthResponseDto;
import schabaschka.auth.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private  final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/register")
    @Transactional
    public AuthResponseDto register(@RequestBody AuthRegisterRequestDto request) {
        return authService.register(request);
    }
    @PostMapping("/login")
    @Transactional
    public AuthResponseDto login(@RequestBody AuthLoginRequestDto request) {
        return authService.login(request);
    }


}
