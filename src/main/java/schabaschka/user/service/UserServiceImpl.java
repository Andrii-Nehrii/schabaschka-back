package schabaschka.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schabaschka.user.model.User;
import schabaschka.user.dao.UserRepository;
import schabaschka.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly=true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public Optional<UserDto> findById(long id) {
        return userRepository.findById(id).map(this :: toDto);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        if(email==null){
            return Optional.empty();
        }
        String trimmed = email.trim();
        if(trimmed.isEmpty()){
            return Optional.empty();
        }

        return userRepository.findByEmail(email).map(this::toDto);
    }


    private UserDto toDto(User user) {
        if(user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        if (user.getRole() != null) {
            dto.setRole(String.valueOf(user.getRole()));
        }else {
            dto.setRole(null);
        }
        dto.setCreatedAt(user.getCreated_at());
        return dto;
    }
}
