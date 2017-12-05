import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.converter.NumberStringConverter;

import java.util.ArrayList;

public class ControlPanel extends Pane{

    public GridPane gp_gameHUD;
    private Label lbl_title, lbl_currentPlayer;
    private Label lbl_player1, lbl_player2, lbl_territory, lbl_prisoners;
    private Button btn_pass, btn_reset, btn_rules;
    private TextField tf_score;

    private GameLogic gameLogic;

    public ControlPanel(GameLogic gameLogic) {
        super();
        this.gameLogic = gameLogic;
        // Initialize the GridPane and all the controls
        gp_gameHUD = new GridPane();
        lbl_title = new Label("Go Control Panel");
        tf_score = new TextField();
        lbl_currentPlayer = new Label("Current player : ");

        lbl_player1 = new Label("Player 1 :");
        lbl_player2 = new Label("Player 2 :");
        lbl_territory = new Label("Territory");
        lbl_prisoners = new Label("Prisoners");

        btn_pass = new Button("Pass");
        btn_reset = new Button("Reset");

        btn_rules = new Button("Rules");

//         Binding the SimpleIntegerProperty scoreProperty in GoGameLogic to the TextField tf_score
        this.tf_score.textProperty().bindBidirectional(this.gameLogic.getScore(), new NumberStringConverter());

        // Some work on the gridPane properties
//        gp_gameHUD.setGridLinesVisible(true);
        gp_gameHUD.setHgap(16);
        gp_gameHUD.setVgap(8);

        this.getChildren().add(gp_gameHUD);

        // Row 0
        gp_gameHUD.add(lbl_title, 0, 0);
//        gp_gameHUD.add(tf_score, 1, 0);

        // Row 1
        gp_gameHUD.add(lbl_currentPlayer, 0, 1, 3, 1);
        // Row 2
        gp_gameHUD.add(lbl_territory, 1, 2);
        gp_gameHUD.add(lbl_prisoners, 2, 2);
        // Row 3
        gp_gameHUD.add(lbl_player1, 0, 3);
        // Row 4
        gp_gameHUD.add(lbl_player2, 0, 4);
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
    }
}
