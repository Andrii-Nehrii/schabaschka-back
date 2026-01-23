package schabaschka.auth.service;

import schabaschka.auth.dto.AuthLoginRequestDto;
import schabaschka.auth.dto.AuthRegisterRequestDto;
import schabaschka.auth.dto.AuthResponseDto;

public interface AuthService {
    AuthResponseDto register(AuthRegisterRequestDto request);
    AuthResponseDto login(AuthLoginRequestDto request);



}
