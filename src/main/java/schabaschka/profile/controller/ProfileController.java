package schabaschka.profile.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import schabaschka.profile.dto.ProfileDto;
import schabaschka.profile.service.ProfileService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService profileService;
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getById(@PathVariable("id") long id) {
        Optional<ProfileDto> profileOpt = profileService.findById(id);
        return profileOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ProfileDto> getByUserId(@PathVariable("userId") long userId) {
        Optional<ProfileDto> profileOpt = profileService.findByUserId(userId);
        return profileOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping
    public List<ProfileDto> list() {
        return profileService.findAll();
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileDto> getMyProfile() {
        Optional<ProfileDto> profileOpt = profileService.findMyProfile();
        return profileOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ProfileDto updateMyProfile(@RequestBody ProfileDto profileDto) { 
        return profileService.updateMyProfile(profileDto);
    }



}
