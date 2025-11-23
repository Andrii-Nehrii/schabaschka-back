package schabaschka.profile.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schabaschka.profile.dao.ProfileRepository;
import schabaschka.profile.dto.ProfileDto;
import schabaschka.profile.model.Profile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }


    @Override
    public Optional<ProfileDto> findById(long id) {
        return profileRepository.findById(id).map(this::toDto);
    }

    @Override
    public Optional<ProfileDto> findByUserId(long userId) {
        return profileRepository.findByUserId(userId).map(this::toDto);
    }


    @Override
    public List<ProfileDto> findAll() {
        return profileRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private ProfileDto toDto(Profile profile) {
        if (profile == null) {
            return null;
        }

        ProfileDto dto = new ProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUserId());
        dto.setName(profile.getName());
        dto.setPhone(profile.getPhone());
        dto.setCity(profile.getCity());

        return dto;

    }
}
