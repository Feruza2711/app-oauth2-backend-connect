package uz.pdp.appoauth2backendconnect.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appoauth2backendconnect.entity.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhoneNumber(String username);

    boolean existsByPhoneNumber(String username);

    Optional<User> findByGoogleUsername(String username);

    Optional<User> findByGithubUsername(String username);
}
