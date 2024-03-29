package woodythedev.pong.game;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import woodythedev.player.Player;
import woodythedev.player.PlayerHistory;
import woodythedev.pong.game.gamefield.Ball;
import woodythedev.pong.game.gamefield.Gamefield;
import woodythedev.pong.game.gamefield.Paddle;
import woodythedev.pong.game.gamefield.Collision.Line;
import woodythedev.pong.websocket.GameController;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "game")
public class Game implements Runnable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Transient
	private Gamefield gamefield;
	
	@ManyToOne(fetch = FetchType.LAZY)
	// @ManyToMany(mappedBy = "game")
  	// @JoinColumn(name = "player1_id")
	private Player player1;

	@ManyToOne(fetch = FetchType.LAZY)
	// @ManyToMany(mappedBy = "game")
  	// @JoinColumn(name = "player2_id")
	private Player player2;

	public boolean stopGame = false;

	private int scoresUntilWin;

	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "game_score_id")
    private GameScore gameScore  = new GameScore();

	@JsonIgnore
	@OneToMany(mappedBy = "game")
	private List<PlayerHistory> playerHistory;

	@Transient
	private static final int DEFAULT_GAMEFIELD_HEIGHT = 540;
	@Transient
	private static final int DEFAULT_GAMEFIELD_WIDTH = 960;

	@Transient
	@JsonIgnore
	private GameService pongService;

	@Transient
	@JsonIgnore
	private GameController gameController;

	public enum PongElement {
		PLAYER1,
		PLAYER2,
		BALL,
		NONE
	}

	public Game(Player player1, Player player2, GameService pongService, GameController gameController) {
		this.player1 = player1;
		this.player2 = player2;
		this.pongService = pongService;
		this.gameController = gameController;
		gamefield = Gamefield.builder()
			.width(DEFAULT_GAMEFIELD_WIDTH)
			.height(DEFAULT_GAMEFIELD_HEIGHT)
			.build();
		gamefield.setBall(
			new Ball(
				Ball.BALL_DEFAULT_HEIGHT,
				Ball.BALL_DEFAULT_WIDTH,
				gamefield.getCenter(),
				new Line(),
				0.0,
				0.0)
			);
		gamefield.setPlayer1Paddle(
			new Paddle(PongElement.PLAYER1,
					   gamefield.getPlayer1PaddleStartPosition(),
					   DEFAULT_GAMEFIELD_HEIGHT,
					   this)
			);
		gamefield.setPlayer2Paddle(
			new Paddle(PongElement.PLAYER2,
					   gamefield.getPlayer2PaddleStartPosition(),
					   DEFAULT_GAMEFIELD_HEIGHT,
					   this)
			);
	}

	public int getScoresUntilWin() {
		return gameScore.getScoresUntilWin();
	}

	public PongElement getPlayerIdentification(Player player) {
		if(player.getId().equals(player1.getId())) {
			return PongElement.PLAYER1;
		} else if(player.getId().equals(player2.getId())) {
			return PongElement.PLAYER2;
		}else {
			return PongElement.NONE;
		}
	}

	public boolean isPlayerInGame(Player player) {
		return player1.getId().equals(player.getId()) ||
			   player2.getId().equals(player.getId());
	}

	public void sendGameInit() {
		gameController.sendGameInit(player1.getEmail(), this);
		gameController.sendGameInit(player2.getEmail(), this);
	}

	public void sendPaddleMovement(Paddle paddle) {
		gameController.sendPaddleMovement(id, paddle);
	}

	public void setPaddleDirection(Player player, int direction) {
		PongElement playerIdentfication = getPlayerIdentification(player);
		if(playerIdentfication.equals(PongElement.PLAYER1)) {
			gamefield.getPlayer1Paddle().setDirection(direction);
		} else if(playerIdentfication.equals(PongElement.PLAYER2)) {
			gamefield.getPlayer2Paddle().setDirection(direction);
		}
	}

	@Override
	public void run() {
		if(!stopGame) {
			if(!Thread.currentThread().isInterrupted()) {
				gamefield.calcNewBallPosition();
				if(gamefield.getScoredPlayer().equals(PongElement.NONE)) {
					gameController.sendNewBallPosition(id, gamefield.getBall());
					CompletableFuture.delayedExecutor((long) gamefield.getBall().getTimeForDistanceInSeconds(), TimeUnit.SECONDS).execute(() -> {
						run();
					});
				} else {
					scored();
					if(gameScore.getWinner() == null) {
						resetPositionsAndResumeGame();
					} else {
						gameFinished();
					}
				} 
			}
		}
	}

	private void resetPositionsAndResumeGame() {
		gamefield.reset();
		CompletableFuture.delayedExecutor(6, TimeUnit.SECONDS).execute(() -> {
			gameController.sendNewBallPosition(id, gamefield.getBall());
			gameController.sendPaddleMovement(id, gamefield.getPlayer1Paddle());
			gameController.sendPaddleMovement(id, gamefield.getPlayer2Paddle());
		});
		CompletableFuture.delayedExecutor(8, TimeUnit.SECONDS).execute(() -> {
			run();
		});
	}

	private void scored() {
		int score = 0;
		if(gamefield.getScoredPlayer().equals(PongElement.PLAYER1)){
			gameScore.setGoalByPlayer(player1);
			score = gameScore.getPlayer1Score() + 1;
			gameScore.setPlayer1Score(score);
			if(getScoresUntilWin() <= score) {
				gameScore.setWinner(PongElement.PLAYER1);
			}
		} else if(gamefield.getScoredPlayer().equals(PongElement.PLAYER2)){
			gameScore.setGoalByPlayer(player2);
			score = gameScore.getPlayer2Score() + 1;
			gameScore.setPlayer2Score(score);
			if(getScoresUntilWin() <= score) {
				gameScore.setWinner(PongElement.PLAYER2);
			}
		}
		gameController.sendScore(id, gameScore);
		gameController.sendScore(id, gameScore);
	}

	private void gameFinished() {
		pongService.destructGame(this);
	}
}
