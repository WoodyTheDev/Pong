package woodythedev.image;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import woodythedev.user.User;

public interface ImageDataRepository extends JpaRepository<ImageData, Long> {
    Optional<ImageData> findByUser(User user);
}