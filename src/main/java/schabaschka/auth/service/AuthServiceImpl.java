package schabaschka.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schabaschka.auth.dto.AuthLoginRequestDto;
import schabaschka.auth.dto.AuthRegisterRequestDto;
import schabaschka.auth.dto.AuthResponseDto;
import schabaschka.profile.dao.ProfileRepository;
import schabaschka.profile.model.Profile;
import schabaschka.security.JwtTokenService;
import schabaschka.user.dao.UserRepository;
import schabaschka.user.model.User;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;//?
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl(UserRepository userRepository, ProfileRepository profileRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }


    @Override
    @Transactional
    public AuthResponseDto register(AuthRegisterRequestDto request) {
        if(request==null){
            throw new IllegalArgumentException("request is null");
        }
        String email = normalizeEmail(request.getEmail());
        String rawPassword = normalize(request.getPassword());//?
        String roleString = normalize(request.getRole());


        if(email == null || rawPassword == null){
            throw new IllegalArgumentException("email or password is null");
        }
        Optional<User> exUser = userRepository.findByEmail(email);
        if(exUser.isPresent()){
            throw new IllegalArgumentException("email already exists");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));//?
        user.setCreated_at(OffsetDateTime.now());

        if(roleString != null){
            setUserRoleFromString(user, roleString);
        }
        User savedUser = userRepository.save(user);

        Profile profile = new Profile(0L,
                savedUser.getId(),
                request.getName(),
                request.getSurname(),
                request.getCity(),
                request.getPhone(),
                request.getCategories());
        Profile savedProfile = profileRepository.save(profile);

        String roleForToken = savedUser.getRole() != null ? String.valueOf(savedUser.getRole()) : null;

        String token = jwtTokenService.generateToken(savedUser.getId(), savedUser.getEmail(),roleForToken );

        return  toAuthResponse(savedUser,savedProfile,token);

    }

    @Override
    public AuthResponseDto login(AuthLoginRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("request can't be null");
        }

        String email = normalizeEmail(request.getEmail());
        String rawPassword = normalize(request.getPassword());

        if (email == null || rawPassword == null) {
            throw new IllegalArgumentException("email and password are required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        Profile profile = profileRepository.findByUserId(user.getId())
                .orElse(null);

        String roleForToken = user.getRole() != null ? String.valueOf(user.getRole()) : null;
        String token = jwtTokenService.generateToken(user.getId(), user.getEmail(),roleForToken );


        return toAuthResponse(user, profile, token);
    }









    private AuthResponseDto toAuthResponse(User user, Profile profile, String token) {
        if (user == null) {
            return null;
        }

        AuthResponseDto dto = new AuthResponseDto();
        dto.setUserId(user.getId());
        dto.setEmail(user.getEmail());

        if (user.getRole() != null) {
            dto.setRole(String.valueOf(user.getRole()));
        } else {
            dto.setRole(null);
        }

        if (profile != null) {
            dto.setName(profile.getName());
            dto.setSurname(profile.getSurname());
            dto.setPhone(profile.getPhone());
            dto.setCategories(profile.getCategories());
            dto.setCity(profile.getCity());
        } else {
            dto.setName(null);
            dto.setSurname(null);
            dto.setPhone(null);
            dto.setCategories(null);
            dto.setCity(null);
        }

        dto.setToken(token);

        return dto;
    }



    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }
    private String normalizeEmail(String email) {
        String normalized = normalize(email);
        if (normalized == null) {
            return null;
        }
        return normalized.toLowerCase();
    }

    private void setUserRoleFromString(User user, String roleValue) {
        String normalized = normalize(roleValue);
        if (normalized == null) {
            return;
        }

        String upper = normalized.toUpperCase();

        try {
            Field roleField = User.class.getDeclaredField("role");
            roleField.setAccessible(true);
            Class<?> enumType = roleField.getType();

            if (!enumType.isEnum()) {
                throw new IllegalStateException("User.role is not an enum");
            }

            @SuppressWarnings("unchecked")
            Enum<?> enumValue = Enum.valueOf((Class<Enum>) enumType, upper);

            roleField.set(user, enumValue);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalStateException("Failed to set role on User entity", e);
        }
    }
}






