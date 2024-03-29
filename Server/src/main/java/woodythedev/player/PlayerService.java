package woodythedev.player;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import woodythedev.user.User;

@Service
@AllArgsConstructor
public class PlayerService {
	private final PlayerRepository playerRepository;
	private final PlayerHistoryRepository playerHistoryRepository;

	public List<PlayerHistoryDTO> getAllPlayerHistories(User user) {
		
		Player player = playerRepository.findByUser(user).orElseThrow();
		List<PlayerHistory> playerHistory = playerHistoryRepository.findAllByPlayer(player);
		return playerHistory.stream().map(PlayerHistoryMapper::mapToPlayerHistoryDTO)
			.collect(Collectors.toList());
	}
}
