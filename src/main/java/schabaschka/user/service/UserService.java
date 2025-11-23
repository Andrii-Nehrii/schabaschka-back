package schabaschka.user.service;

import schabaschka.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserDto> findById(long id);

    List<UserDto> findAll();

    Optional<UserDto> findByEmail(String email);
}
