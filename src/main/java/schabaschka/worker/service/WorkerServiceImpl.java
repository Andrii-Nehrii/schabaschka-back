package schabaschka.worker.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schabaschka.profile.dto.ProfileDto;
import schabaschka.profile.service.ProfileService;
import schabaschka.security.SecurityUtils;
import schabaschka.user.dto.UserDto;
import schabaschka.user.service.UserService;
import schabaschka.worker.dto.WorkerDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class WorkerServiceImpl implements WorkerService {

    private final UserService userService;
    private final ProfileService profileService;

    public WorkerServiceImpl(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @Override
    public List<WorkerDto> find(String city, String name, int page, int size) {
        SecurityUtils.getCurrentUserId();
        int pageIndex = Math.max(0, page);
        int pageSize = Math.max(1, size);

        Page<WorkerDto> workerPage = findPage(city, name, pageIndex, pageSize);
        return workerPage.getContent();
    }

    @Override
    public Page<WorkerDto> findPage(String city, String name, int page, int size) {
        SecurityUtils.getCurrentUserId();
        int pageIndex = Math.max(0, page);
        int pageSize = Math.max(1, size);

        List<WorkerDto> allWorkers = findAllFiltered(city, name);

        int fromIndex = pageIndex * pageSize;
        if (fromIndex >= allWorkers.size()) {
            return new PageImpl<>(
                    List.of(),
                    PageRequest.of(pageIndex, pageSize),
                    allWorkers.size()
            );
        }

        int toIndex = Math.min(fromIndex + pageSize, allWorkers.size());
        List<WorkerDto> content = allWorkers.subList(fromIndex, toIndex);

        return new PageImpl<>(
                content,
                PageRequest.of(pageIndex, pageSize),
                allWorkers.size()
        );
    }

    @Override
    public Optional<WorkerDto> findById(long id) {
        SecurityUtils.getCurrentUserId();

        UserDto foundUser = null;
        List<UserDto> allUsers = userService.findAll();
        for (UserDto user : allUsers) {
            if (user != null && user.getId() == id) {
                foundUser = user;
                break;
            }
        }
        if (foundUser == null) {
            return Optional.empty();
        }
        if (!isWorker(foundUser)) {
            return Optional.empty();
        }

        Optional<ProfileDto> profileOpt = profileService.findByUserId(foundUser.getId());
        if (profileOpt.isEmpty()) {
            return Optional.empty();
        }
        ProfileDto profile = profileOpt.get();

        WorkerDto workerDto = new WorkerDto(
                foundUser.getId(),
                profile.getId(),
                profile.getName(),
                profile.getSurname(),
                profile.getPhone(),
                foundUser.getEmail(),
                profile.getCity(),
                foundUser.getRole(),
                profile.getCategories()
        );

        return Optional.of(workerDto);
    }

    private List<WorkerDto> findAllFiltered(String city, String name) {
        String cityFilter = normalize(city);
        String nameFilter = normalize(name);

        List<UserDto> allUsers = userService.findAll();
        List<WorkerDto> result = new ArrayList<>();

        for (UserDto user : allUsers) {
            if (!isWorker(user)) {
                continue;
            }

            Optional<ProfileDto> profileOpt = profileService.findByUserId(user.getId());
            if (profileOpt.isEmpty()) {
                continue;
            }

            ProfileDto profile = profileOpt.get();

            if (cityFilter != null) {
                String profileCity = normalize(profile.getCity());
                if (profileCity == null || !profileCity.equals(cityFilter)) {
                    continue;
                }
            }

            if (nameFilter != null) {
                String profileName = normalize(profile.getName());
                if (profileName == null || !profileName.contains(nameFilter)) {
                    continue;
                }
            }

            WorkerDto workerDto = new WorkerDto(
                    user.getId(),
                    profile.getId(),
                    profile.getName(),
                    profile.getSurname(),
                    profile.getPhone(),
                    user.getEmail(),
                    profile.getCity(),
                    user.getRole(),
                    profile.getCategories()
            );

            result.add(workerDto);
        }

        return result;
    }

    private boolean isWorker(UserDto user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        return "WORKER".equalsIgnoreCase(user.getRole());
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }
}
