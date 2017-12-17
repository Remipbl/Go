import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

public class ControlPanel extends Pane {

    public GridPane gp_gameHUD;
    private Label lbl_title, lbl_currentPlayer, lbl_currentPlayerNumber;
    private Label lbl_player1, lbl_player2, lbl_score1, lbl_score2, lbl_prisoners;
    private Button btn_pass, btn_reset, btn_rules;
    private TextField tf_score;
    private Alert alert_rules;

    private GameLogic gameLogic;

    public ControlPanel(GameLogic gameLogic, Stage stage) {
        super();

        this.gameLogic = gameLogic;
        // Initialize the GridPane and all the controls
        gp_gameHUD = new GridPane();
        lbl_title = new Label("Go Control Panel");
        tf_score = new TextField();
        lbl_currentPlayer = new Label("Current player : ");
        lbl_currentPlayerNumber = new Label();

        lbl_player1 = new Label("Player 1 score :");
        lbl_player2 = new Label("Player 2 score :");
        lbl_prisoners = new Label("Prisoners");
        lbl_score1 = new Label();
        lbl_score2 = new Label();

        btn_pass = new Button("Pass");
        btn_reset = new Button("Reset");

        btn_rules = new Button("Rules");

        // Binding
        this.lbl_currentPlayerNumber.textProperty().bindBidirectional(this.gameLogic.getCurrentPlayer(), new NumberStringConverter());
        this.lbl_score1.textProperty().bindBidirectional(this.gameLogic.getScore1(), new NumberStringConverter());
        this.lbl_score2.textProperty().bindBidirectional(this.gameLogic.getScore2(), new NumberStringConverter());

        // Some work on the gridPane properties
        gp_gameHUD.setHgap(16);
        gp_gameHUD.setVgap(8);

        this.getChildren().add(gp_gameHUD);

        // Row 0
        gp_gameHUD.add(lbl_title, 0, 0, 2, 1);
        // Row 1
        gp_gameHUD.add(lbl_currentPlayer, 0, 1, 2, 1);
        gp_gameHUD.add(lbl_currentPlayerNumber, 2, 1);
        // Row 2
        gp_gameHUD.add(lbl_prisoners, 2, 2);
        // Row 3
        gp_gameHUD.add(lbl_player1, 0, 3);
        gp_gameHUD.add(lbl_score1, 2, 3);
        // Row 4
        gp_gameHUD.add(lbl_player2, 0, 4);
        gp_gameHUD.add(lbl_score2, 2, 4);
        // Row 5
        gp_gameHUD.add(btn_pass, 0, 5);
        gp_gameHUD.add(btn_reset, 1, 5);
        // Row 6
        gp_gameHUD.add(btn_rules, 0, 6);

        btn_reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameLogic.resetGame();
            }
        });

        btn_pass.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameLogic.pass();
            }
        });

        btn_rules.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                alert_rules = new Alert(Alert.AlertType.INFORMATION);
                alert_rules.setTitle("Rules");
                alert_rules.setHeaderText(null);
                alert_rules.setContentText("1 - The player 1 is white\n" +
                        "2 - The player 2 is black\n" +
                        "3 - The player 2 always starts\n" +
                        "4 - You can place stones on the intersections of the board\n" +
                        "5 - Stone and groups of stones can be captured if all their adjacent intersections are taken by opposite stones\n" +
                        "6 - A player gain 1 point by opposing stones captured\n" +
                        "7 - A player can pass his turn by clicking on the pass button\n" +
                        "8 - If both players pass in a row, the game ends\n" +
                        "9 - The game can be reset at any time by clicking on the reset button\n" +
                        "10 - Active feature : single/multiple capture / Suicide Rule / KO Rule / capture points");
                alert_rules.showAndWait();
            }
        });
    }
}
