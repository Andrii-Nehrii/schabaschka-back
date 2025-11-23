package schabaschka.profile.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import schabaschka.profile.model.Profile;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile,Long> {
    Optional<Profile> findByUserId(long userId);
}
