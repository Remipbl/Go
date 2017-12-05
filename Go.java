import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Go extends Application {

    private BorderPane bp_mainLayout;
    private CustomControl cc_go;
    private GoBoard gb_board;
    private GameLogic gameLogic;
    private ControlPanel cp_gameHUD;

    static void main(String[] args) {
        launch(args);
    }

    public void init() {
        // Initialise all the panels, controls  and layouts
        bp_mainLayout = new BorderPane();
        gb_board = new GoBoard();
        gameLogic = new GameLogic(gb_board);
        cc_go = new CustomControl(gameLogic);
        cp_gameHUD = new ControlPanel(gameLogic);

        // Put the custom control and panel in our border pane
        bp_mainLayout.setLeft(cp_gameHUD);
        bp_mainLayout.setCenter(cc_go);
    }

    public void start(Stage mainStage) {
        mainStage.setTitle("Go");
        mainStage.setScene(new Scene(bp_mainLayout, 1000, 800));
        mainStage.show();
    }

    public void stop() {

    }
}