package pong.javafx.game.remote;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.lang.Nullable;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import lombok.Getter;
import lombok.Setter;
import pong.javafx.game.model.Ball;
import pong.javafx.game.model.Game;
import pong.javafx.game.model.GameScore;
import pong.javafx.game.model.Paddle;

public class PongWebSocketClient {

    public static final Logger logger = Logger.getLogger(PongWebSocketClient.class);

    private String url = "ws://lyra.et-inf.fho-emden.de:19036/ws";
    // private String url = "ws://localhost:8080/ws";

    private String token = "hier_Ihr_Token";

    @Getter
    private ExecutorService webSocketExecutor = Executors.newFixedThreadPool(10);

    @Getter
    private ExecutorService webSocketExecutorSendMessage = Executors.newFixedThreadPool(1);

    private String gameId;

    @Setter
    private GameStartListener gameStartListener;

    @Setter
    private GameListener gameListener;

    @Getter
    private StompSession stompSession;

    @Getter
    private WebSocketStompClient stompClient;

    private WebSocketHttpHeaders headers;

    public PongWebSocketClient(String bearerToken) {
	this.token = bearerToken;
    }

    public void start() {
	webSocketExecutor.execute(this::handleWebSocketCommunication);
    }

    private void handleWebSocketCommunication() {
	setupClient();
    }

    public void setupClient() {
	headers = new WebSocketHttpHeaders();
	headers.add("Authorization", "Bearer " + token);

	StandardWebSocketClient client = new StandardWebSocketClient();
	stompClient = new WebSocketStompClient(client);

	stompClient.setMessageConverter(new SimpleMessageConverter());

	StompSessionHandler sessionHandler = new PongStompSessionHandler();
	try {
	    stompSession = stompClient.connect(url, headers, sessionHandler).get();
	} catch (InterruptedException | ExecutionException e) {
	    logger.error("Failed to connect stomp session: " + e.getMessage());
	}
    }

    public void connectUser() {
	try {
	    stompSession = stompClient.connect(url, headers, new StompSessionHandlerAdapter() {

		@Override
		public void afterConnected(StompSession stompSession, StompHeaders connectedHeaders) {
		    String userMail = connectedHeaders.get("user-name").get(0);
		    initGame(userMail);
		    stompSession.send("/pong/game", null);
		}
	    }).get();
	} catch (InterruptedException | ExecutionException e) {
	    logger.error("Failed to connect user and init game: " + e.getMessage());
	}
    }

    private void initGame(String userMail) {

	stompSession.subscribe("/game/" + userMail + "/init", new StompFrameHandler() {

	    @Override
	    public Type getPayloadType(StompHeaders headers) {
		// System.out.println("INIT PAYLOAD");
		return byte[].class;
	    }

	    @Override
	    public void handleFrame(StompHeaders headers, @Nullable Object payload) {
		byte[] byteArray = (byte[]) payload;
		String jsonString = new String(byteArray, java.nio.charset.StandardCharsets.UTF_8);
		JSONObject jsonObject = new JSONObject(jsonString);
		System.out.println("GameId: " + jsonObject.get("id"));
		System.out.println("Payload Game Init: " + jsonObject);
		ObjectMapper mapper = new ObjectMapper();
		try {
		    Game game = mapper.readValue(jsonString, Game.class);
		    // vom StartView-> GameView aufrufen mit Spieldaten
		    if (gameStartListener != null && game != null) {
			Platform.runLater(() -> {
			    gameStartListener.onGameStart(game);
			});
		    }
		    // hier kommen die Subscriptions mit der gameId
		    setGameId(jsonObject.get("id").toString());
		} catch (IOException e) {
		    logger.error("Failed to receive game init: " + e.getMessage());
		}
	    }

	});
    }

    private void setGameId(String gameId) {
	this.gameId = gameId;
	subscribe();
    }

    private void subscribe() {

	stompSession.subscribe("/game/" + this.gameId + "/paddleMovement", new StompFrameHandler() {

	    @Override
	    public Type getPayloadType(StompHeaders headers) {
		return byte[].class;
	    }

	    @Override
	    public void handleFrame(StompHeaders headers, @Nullable Object payload) {
		// Payload:
		// {"paddleOwner":"PLAYER2","width":20,"position":{"x":920,"y":0},"height":60}
		byte[] byteArray = (byte[]) payload;
		String jsonString = new String(byteArray, java.nio.charset.StandardCharsets.UTF_8);
		// System.out.println("Payload Paddle Movement: " + new JSONObject(jsonString));
		ObjectMapper mapper = new ObjectMapper();
		try {
		    Paddle paddle = mapper.readValue(jsonString, Paddle.class);
		    if (gameListener != null && paddle != null) {
			Platform.runLater(() -> {
			    gameListener.onPaddleMovement(paddle);
			});
		    }
		} catch (IOException e) {
		    logger.error("Failed to receive/read paddle movement: " + e.getMessage());
		}
	    }

	});

	stompSession.subscribe("/game/" + gameId + "/ballMovement", new StompFrameHandler() {

	    @Override
	    public Type getPayloadType(StompHeaders headers) {
		return byte[].class;
	    }

	    @Override
	    public void handleFrame(StompHeaders headers, @Nullable Object payload) {
		// Payload:
		// {"distance":79.75587752636166,"width":20,"timeForDistanceInSeconds":0.3987793876318083,"position":{"x":0,"y":159},"movement":{"b":159.2478795864432,"m":"DEGREE_108"},"height":20}
		byte[] byteArray = (byte[]) payload;
		String jsonString = new String(byteArray, java.nio.charset.StandardCharsets.UTF_8);
		// System.out.println("Payload Ball Movement: " + new JSONObject(jsonString));
		ObjectMapper mapper = new ObjectMapper();
		try {
		    Ball ball = mapper.readValue(jsonString, Ball.class);
		    if (gameListener != null && ball != null) {
			Platform.runLater(() -> {
			    gameListener.onBallMovement(ball);
			});
		    }
		} catch (IOException e) {
		    logger.error("Failed to receive/read ball movement: " + e.getMessage());
		}
	    }
	});

	stompSession.subscribe("/game/" + gameId + "/score", new StompFrameHandler() {

	    @Override
	    public Type getPayloadType(StompHeaders headers) {
		return byte[].class;
	    }

	    @Override
	    public void handleFrame(StompHeaders headers, @Nullable Object payload) {
		// Payload Score:
		// {"goalByPlayer":{"id":13,"playername":"11"},"winner":null,"player1Score":1,"id":114,"player2Score":0,"scoresUntilWin":5}
		byte[] byteArray = (byte[]) payload;
		String jsonString = new String(byteArray, java.nio.charset.StandardCharsets.UTF_8);
		// System.out.println("Payload Score: " + new JSONObject(jsonString));
		ObjectMapper mapper = new ObjectMapper();
		try {
		    GameScore gameScore = mapper.readValue(jsonString, GameScore.class);
		    if (gameListener != null && gameScore != null) {
			Platform.runLater(() -> {
			    gameListener.onScore(gameScore);
			});
		    }
		} catch (IOException e) {
		    logger.error("Failed to receive/read game score: " + e.getMessage());
		}
	    }
	});

    }

    // 1 = up / -1 = down / 0 = none
    public void sendPaddleDirection(String direction) {
	// stompSession.send("/pong/game/" + gameId + "/paddle" {}, paddleDirection);
	if (this.stompSession != null && this.stompSession.isConnected() && this.gameId != null) {
	    webSocketExecutorSendMessage.execute(() -> {
		try {
		    StompHeaders headers = new StompHeaders();
		    byte[] bytePayload = direction.getBytes(StandardCharsets.UTF_8);
		    headers.setDestination("/pong/game/" + this.gameId + "/paddle");
		    this.stompSession.send(headers, bytePayload);
		} catch (Exception e) {
		    logger.error("Failed to send paddle movement: " + e.getMessage());
		}
	    });
	} else {
	    String errorMsg = "StompSession ist nicht verbunden oder gameId ist null";
	    logger.error(errorMsg);
	    System.err.println(errorMsg);
	}
    }

    public void closeConnection() {
	try {
	    if (stompSession != null && stompSession.isConnected()) {
		stompSession.disconnect();
	    }
	    if (stompClient != null && stompClient.isRunning()) {
		stompClient.stop();
	    }
	    if (webSocketExecutor != null) {
		webSocketExecutor.shutdown();
		try {
		    if (!webSocketExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
			webSocketExecutor.shutdownNow();
		    }
		} catch (InterruptedException e) {
		    logger.error("WebSocket interrupted: " + e.getMessage());
		    webSocketExecutor.shutdownNow();
		    Thread.currentThread().interrupt();
		}
	    }
	    if (webSocketExecutorSendMessage != null) {
		webSocketExecutorSendMessage.shutdown();
		try {
		    if (!webSocketExecutorSendMessage.awaitTermination(800, TimeUnit.MILLISECONDS)) {
			webSocketExecutorSendMessage.shutdownNow();
		    }
		} catch (InterruptedException e) {
		    logger.error("WebSocket interrupted: " + e.getMessage());
		    webSocketExecutorSendMessage.shutdownNow();
		    Thread.currentThread().interrupt();
		}
	    }
	} finally {
	    if (stompClient != null && stompSession != null && !stompClient.isRunning()
		    && !stompSession.isConnected()) {
		System.out.println("Stomp-Session und -Client geschlossen!");
	    }
	}
    }

    private class PongStompSessionHandler extends StompSessionHandlerAdapter {

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
	    String userMail = connectedHeaders.get("user-name").get(0);
	    System.out.println("Verbindung hergestellt: " + userMail);
	    initGame(userMail);
	    stompSession.send("/pong/game", null);
	}

	@Override
	public void handleFrame(StompHeaders headers, @Nullable Object payload) {
	    byte[] msg = (byte[]) payload;
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
	    return byte[].class;
	}

	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
	    System.out.println("Transportfehler: " + exception.getLocalizedMessage());
	}

	@Override
	public void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers,
		byte[] payload, Throwable exception) {
	    System.out.println(exception);
	}
    }

}