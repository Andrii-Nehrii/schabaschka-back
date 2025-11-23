package schabaschka.profile.service;

import org.springframework.stereotype.Service;
import schabaschka.profile.dto.ProfileDto;

import java.util.List;
import java.util.Optional;


public interface ProfileService {

    Optional<ProfileDto> findById(long id);

    Optional<ProfileDto> findByUserId(long userId);

    List<ProfileDto> findAll();

}
