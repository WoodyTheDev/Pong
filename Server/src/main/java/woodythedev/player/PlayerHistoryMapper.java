package woodythedev.player;

import woodythedev.pong.game.Game;

public class PlayerHistoryMapper {
	public static PlayerHistoryDTO mapToPlayerHistoryDTO(PlayerHistory playerHistory) {
		Game game = playerHistory.getGame();
		PlayerHistoryDTO playerHistoryDTO = new PlayerHistoryDTO(
			game.getPlayer1().getPlayername(),
			game.getPlayer1().getId(),
			game.getPlayer2().getPlayername(),
			game.getPlayer2().getId(),
			game.getGameScore(),
			playerHistory.getCreateDate()
		);
		return playerHistoryDTO;
	}
}
