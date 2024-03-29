package woodythedev.pong.game;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GameScoreRepository extends JpaRepository<GameScore, Integer>{
	Optional<GameScore> findById(Integer id);
}
