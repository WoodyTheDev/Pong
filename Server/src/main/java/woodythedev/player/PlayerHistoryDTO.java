package woodythedev.player;

import java.time.LocalDateTime;

import woodythedev.pong.game.GameScore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerHistoryDTO {
	// DTO -> Data Transfer Object
	private String player1;
	private int player1Id;
	private String player2;
	private int player2Id;
	private GameScore gameScore;
	private LocalDateTime createDate;
}
