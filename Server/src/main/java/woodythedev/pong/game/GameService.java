package woodythedev.pong.game;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import woodythedev.player.Player;
import woodythedev.player.PlayerHistory;
import woodythedev.player.PlayerHistoryRepository;
import woodythedev.pong.websocket.GameController;
import woodythedev.pong.websocket.WebSocketException;

@Service
public class GameService {
	private ConcurrentHashMap<Integer, Game> gameList = new ConcurrentHashMap<Integer, Game>();
	private ConcurrentHashMap<Integer, Thread> threadList = new ConcurrentHashMap<Integer, Thread>();
	private LinkedList<Player> waitingList = new LinkedList<Player>();
	private LinkedList<Player> playerList = new LinkedList<Player>();
	@Value("${application.pong.max-games-concurrent:10}")
	private int maxGamesConcurrent;
	private boolean isCapacityReached = false;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GameScoreRepository gameScoreRepository;

	@Autowired
	private PlayerHistoryRepository playerHistoryRepository;

	private GameController gameController;
	
	@Autowired
	public GameService(@Lazy GameController gameController) {
		this.gameController = gameController;
	}

	private synchronized void createGame() {
		Integer gameId;
		while(waitingList.size() >= 2) {
			if(gameList.size() >= maxGamesConcurrent) {
				isCapacityReached = true;
				return;
			}
			isCapacityReached = false;
			Game game = new Game(waitingList.poll(), waitingList.poll(), this, gameController);
			game = gameRepository.save(game);
			gameId = game.getId();
			gameList.put(gameId, game);
			game.sendGameInit();

			final int inmutableGameId = gameId;
			CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS).execute(() -> {
				startGame(inmutableGameId);
			});
		}
	}

	private synchronized void startGame(Integer gameId) {
		Thread thread = new Thread(gameList.get(gameId));
		thread.start();
		threadList.put(gameId, thread);
	}

	private void checkIfPlayerAlreadyConnected(Player player) {
		playerList.stream().forEach(p -> {
			if(p.getId().equals(player.getId())){
				throw WebSocketException.builder()
										.errorCode(105)
										.playerId(p.getId().toString())
										.msg("Player already connected")
										.build();
			}
		});
	}

	private Player getPlayerFromList(Integer playerId) {
		Player player = playerList.stream()
			.filter(p -> p.getId().equals(playerId))
			.findFirst()
			.orElse(null);
		return player;
	}

	private void removePlayerFromWaitingList(Player player) {
		if(waitingList != null && waitingList.size() > 0)
			waitingList.removeIf(p -> p.getId().equals(player.getId()));
	}

	private void removePlayerFromPlayerList(Player player) {
		if(playerList != null && playerList.size() > 0)
			playerList.removeIf(p -> p.getId().equals(player.getId()));
	}

	public void addToGame(Player player) {
		checkIfPlayerAlreadyConnected(player);
		waitingList.add(player);
		playerList.add(player);
		createGame();
	}

	public synchronized void playerDisconnected(Player player)  {
		removePlayerFromWaitingList(player);
		gameList.forEachValue(1, game -> {
			if(game.isPlayerInGame(player)) {
				destructGame(game);
				return;
			}
		});
	}

	public synchronized void setPaddleDirection(String id, Player player, int direction) {
		Game game;
		try {
			game = gameList.get(Integer.valueOf(id));
		} catch(NumberFormatException ex) {
			throw WebSocketException.builder()
				.errorCode(106)
				.playerId(player.getId().toString())
				.msg(ex.getMessage())
				.build();
		}
		if(game != null) {
			if(player != null)
				game.setPaddleDirection(player, direction);
		}
	}

	public boolean isCapacityReached() {
		return isCapacityReached;
	}

	public void destructGame(Game game) {
		Thread thread = threadList.get(game.getId());
		if(thread != null) {
			thread.interrupt();
		}
		game.stopGame = true;
		removePlayerFromPlayerList(game.getPlayer1());
		removePlayerFromPlayerList(game.getPlayer2());
		gameList.remove(game.getId());
		threadList.remove(game.getId());
		writeToHistory(game);
		
		//if cap is reached and players are waiting
		createGame();
	}

	private void writeToHistory(Game game) {
		gameScoreRepository.save(game.getGameScore());
		gameRepository.save(game);
		//Add entry to history of player1
		playerHistoryRepository.save(
			PlayerHistory.builder()
				.player(game.getPlayer1())
				.game(game)
				.build()
		);
		//Add entry to history of player2
		playerHistoryRepository.save(
			PlayerHistory.builder()
				.player(game.getPlayer2())
				.game(game)
				.build()
		);
	}
}
