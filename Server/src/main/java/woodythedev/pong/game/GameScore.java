package woodythedev.pong.game;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import woodythedev.player.Player;
import woodythedev.pong.game.Game.PongElement;

@Getter
@Setter
@Entity
public class GameScore {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private int player1Score;
	private int player2Score;

	@Transient
	private Player goalByPlayer;

	private int scoresUntilWin = 5;

	private PongElement winner;
}
