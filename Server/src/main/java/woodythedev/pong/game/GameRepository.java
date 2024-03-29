package woodythedev.pong.game;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GameRepository extends JpaRepository<Game, Integer>{
	Optional<Game> findById(Integer id);
}
