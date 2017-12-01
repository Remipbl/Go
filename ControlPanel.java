import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

public class ControlPanel extends Pane{
    public VBox vb_gameHUD;
    private Label lbl_currentPlayer;
    private Button btn_rules, btn_pass, btn_reset;
    private TextField tf_score;

    private GameLogic gameLogic;

    public ControlPanel(GameLogic gameLogic) {
        super();
        this.gameLogic = gameLogic;
        // Initialize the VBox and all the controls
        vb_gameHUD = new VBox();
        lbl_currentPlayer = new Label("Current player : ");
        btn_rules = new Button("Rules");
        btn_pass = new Button("Pass");
        btn_reset = new Button("Reset game");

        // Binding the SimpleIntegerProperty scoreProperty in GoGameLogic to the TextField tf_score
//        this.tf_score.textProperty().bindBidirectional(this.gameLogic.getScore(), new NumberStringConverter());

        this.getChildren().add(vb_gameHUD);
        vb_gameHUD.getChildren().addAll(btn_rules, lbl_currentPlayer, btn_pass, btn_reset);
    }
}
