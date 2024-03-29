package pong.javafx.gamehistory.controller;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Setter;
import pong.javafx.gamehistory.model.GameRecord;
import pong.javafx.gamehistory.model.GameRecord.GameScore;
import pong.javafx.gamehistory.task.GetPlayerTask;
import pong.javafx.gamehistory.view.GameHistoryDialog;
import pong.javafx.user.model.ActiveUser;

public class GameHistoryDialogController implements EventHandler<ActionEvent> {

    public static final Logger logger = Logger.getLogger(GameHistoryDialogController.class);

    @FXML
    private GridPane root;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label totalGamesLabel;
    @FXML
    private Label totalWon;
    @FXML
    private Label totalLose;
    @FXML
    private Label mostFrequentWeekday;
    @FXML
    private Label mostFrequentOpponent;
    @FXML
    private TableView<GameRecord> tableView;
    @FXML
    private TableColumn<GameRecord, String> createDateCol;
    @FXML
    private TableColumn<GameRecord, String> player1Col;
    @FXML
    private TableColumn<GameRecord, String> player2Col;
    @FXML
    private TableColumn<GameRecord, String> score1Col;
    @FXML
    private TableColumn<GameRecord, String> score2Col;
    @FXML
    private TableColumn<GameRecord, String> scoresUntilWin;
    @FXML
    private TableColumn<GameRecord, String> winCol;

    @Setter
    private Stage stage;

    private List<GameRecord> results = new ArrayList<>();

    public GameHistoryDialogController() {
	super();
    }

    @FXML
    public void initialize() {
	// Laden der Daten
	loadGameRecords();
	String activeUserName = ActiveUser.getInstance().getUser().getUserName().getValue();
	int gamesTotal = results.size();
	List<GameRecord> winnerRecords = new ArrayList<>();
	for (GameRecord gameRecord : results) {
	    String ownPlayerId = activeUserName.equals(gameRecord.getPlayer1()) ? "PLAYER1" : "PLAYER2";
	    if (gameRecord.getGameScore() != null && gameRecord.getGameScore().getWinner() != null
		    && !gameRecord.getGameScore().getWinner().isEmpty()) {
		if (gameRecord.getGameScore().getWinner().equals(ownPlayerId)) {
		    winnerRecords.add(gameRecord);
		}
	    }
	}
	int gamesWon = winnerRecords.size();
	int gamesLost = gamesTotal - gamesWon;

	totalGamesLabel.setText(Integer.toString(gamesTotal));
	totalWon.setText(Integer.toString(gamesWon));
	totalLose.setText(Integer.toString(gamesLost));
	mostFrequentWeekday.setText(getMostFrequentWeekday());
	mostFrequentOpponent.setText(getMostFrequentOppenent(activeUserName));
	// Setzen der Daten in der TableView
	ObservableList<GameRecord> observableList = FXCollections.observableArrayList(results);
	tableView.setItems(observableList);
	// Konfigurieren der Spalten
	createDateCol.setCellValueFactory(cellData -> {
	    String createDate = cellData.getValue().getCreateDate();
	    return formattedDate(createDate);
	});
	player1Col.setCellValueFactory(new PropertyValueFactory<>("player1"));
	player2Col.setCellValueFactory(new PropertyValueFactory<>("player2"));
	score1Col.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
		String.valueOf(cellData.getValue().getGameScore().getPlayer1Score())));
	score2Col.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
		String.valueOf(cellData.getValue().getGameScore().getPlayer2Score())));
	scoresUntilWin.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
		String.valueOf(cellData.getValue().getGameScore().getScoresUntilWin())));
	winCol.setCellValueFactory(cellData -> {
	    GameScore gameScore = cellData.getValue().getGameScore();
	    if (gameScore.getWinner() != null) {
		return new ReadOnlyStringWrapper(
			gameScore.getWinner().equals("PLAYER1") ? cellData.getValue().getPlayer1()
				: cellData.getValue().getPlayer2());
	    } else {
		return new ReadOnlyStringWrapper("-");
	    }
	});
    }

    private ReadOnlyStringWrapper formattedDate(String createDate) {
	LocalDateTime dateTime = LocalDateTime.parse(createDate);
	if (dateTime != null) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
	    String formattedDateTime = dateTime.format(formatter);
	    return new ReadOnlyStringWrapper(formattedDateTime);
	} else {
	    return new ReadOnlyStringWrapper("-");
	}
    }

    private List<GameRecord> loadGameRecords() {
	GetPlayerTask playerTask = new GetPlayerTask(ActiveUser.getInstance().getUser().getBearerToken().getValue());
	new Thread(playerTask).start();
	try {
	    results.addAll(playerTask.get());
	    results = results.stream().sorted(Comparator.comparing(GameRecord::getCreateDate).reversed()) // sortieren
		    // .limit(15) // Behalte nur die ersten 15 Einträge
		    .collect(Collectors.toList());
	} catch (InterruptedException | ExecutionException e) {
	    logger.error("Failed to load game records: " + e.getMessage());
	}
	return results;
    }

    private String getMostFrequentOppenent(String activeUserName) {
	Map<String, Long> opponentCounts = results.stream()
		.flatMap(record -> Stream.of(record.getPlayer1(), record.getPlayer2()))
		.filter(player -> !player.equals(activeUserName))
		.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

	Map.Entry<String, Long> mostFrequentOpponent = opponentCounts.entrySet().stream()
		.max(Map.Entry.comparingByValue()).orElse(null);
	String result = (mostFrequentOpponent != null ? mostFrequentOpponent.getKey() : "-");
	return result;
    }

    private String getMostFrequentWeekday() {
	Map<DayOfWeek, Long> dayOfWeekCounts = results.stream()
		.map(record -> LocalDateTime.parse(record.getCreateDate()).getDayOfWeek())
		.collect(Collectors.groupingBy(dayOfWeek -> dayOfWeek, Collectors.counting()));

	DayOfWeek mostFrequentDay = dayOfWeekCounts.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow()
		.getKey();
	switch (mostFrequentDay) {
	case MONDAY:
	    return "Montag";
	case TUESDAY:
	    return "Dienstag";
	case WEDNESDAY:
	    return "Mittwoch";
	case THURSDAY:
	    return "Donnerstag";
	case FRIDAY:
	    return "Freitag";
	case SATURDAY:
	    return "Samstag";
	case SUNDAY:
	    return "Sonntag";
	default:
	    return "-";
	}
    }

    @Override
    public void handle(ActionEvent e) {
	GameHistoryDialog dialog = new GameHistoryDialog();
	Stage stage = dialog.enterGameHistoryStage();
	stage.show();
    }
}
