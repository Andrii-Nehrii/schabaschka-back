package schabaschka.profile.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schabaschka.profile.dao.ProfileRepository;
import schabaschka.profile.dto.ProfileDto;
import schabaschka.profile.model.Profile;
import schabaschka.security.SecurityUtils;

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

    @Override
    public Optional<ProfileDto> findMyProfile() {
        long currentUserId = SecurityUtils.getCurrentUserId();
        return profileRepository.findByUserId(currentUserId).map(this::toDto);
    }

    @Override
    @Transactional
    public ProfileDto updateMyProfile(ProfileDto profileDto) {
        if (profileDto == null) {
            throw new IllegalArgumentException("profileDto must not be null");
        }

        long currentUserId = SecurityUtils.getCurrentUserId();

        Profile profile = profileRepository.findByUserId(currentUserId).orElseGet(() -> {
            Profile p = new Profile();
            p.setUserId(currentUserId);
            return p;
        });

        String name = normalize(profileDto.getName());
        String surname = normalize(profileDto.getSurname());
        String city = normalize(profileDto.getCity());
        String phone = normalize(profileDto.getPhone());

        if (name != null) {
            profile.setName(name);
        }
        if (surname != null) {
            profile.setSurname(surname);
        }
        if (city != null) {
            profile.setCity(city);
        }
        if (phone != null) {
            profile.setPhone(phone);
        }

        if (profileDto.getCategories() != null) {
            profile.setCategories(profileDto.getCategories());
        }

        Profile saved = profileRepository.save(profile);
        return toDto(saved);
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

    private ProfileDto toDto(Profile profile) {
        if (profile == null) {
            return null;
        }

        ProfileDto dto = new ProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUserId());
        dto.setName(profile.getName());
        dto.setSurname(profile.getSurname());
        dto.setPhone(profile.getPhone());
        dto.setCity(profile.getCity());
        dto.setCategories(profile.getCategories());

        return dto;
    }
}
