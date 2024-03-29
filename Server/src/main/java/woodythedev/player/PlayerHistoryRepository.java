package woodythedev.player;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerHistoryRepository extends JpaRepository<PlayerHistory, Integer> {

  List<PlayerHistory> findAllByPlayer(Player player);

}
