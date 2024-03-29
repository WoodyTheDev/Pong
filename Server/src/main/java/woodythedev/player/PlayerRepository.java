package woodythedev.player;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import woodythedev.user.User;

public interface PlayerRepository extends JpaRepository<Player, Integer> {

  Optional<Player> findByUser(User user);

}
