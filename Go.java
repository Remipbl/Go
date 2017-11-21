import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;

public class Go extends Application {

    private BorderPane bp_mainLayout;
    private VBox vb_gameHUD;
    private CustomControl cc_go;

    static void main(String[] args) {
        launch(args);
    }

    public void init() {
        // Initialise the stack pane and add a custom control to it
        bp_mainLayout = new BorderPane();
        cc_go = new CustomControl();
        vb_gameHUD = new VBox();

        bp_mainLayout.setLeft(vb_gameHUD);
        bp_mainLayout.setCenter(cc_go);
    }

    public void start(Stage mainStage) {
        mainStage.setTitle("Go");
        mainStage.setScene(new Scene(bp_mainLayout, 800, 800));
        mainStage.show();
    }

    public void stop() {

    }
}