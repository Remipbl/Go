import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class CustomControl extends Control {
    private GameLogic gameLogic;

    // Constructor
    public CustomControl(GameLogic gameLogic) {
        this.gameLogic = gameLogic;

        // Set the default skin and generate a game board
        setSkin(new CustomControlSkin(this));
        getChildren().add(gameLogic.getBoard());

        // Add a mouse clicked listener that will try to place a piece
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                gameLogic.placeStoneTry(event.getX(), event.getY());
            }
        });
    }

    // Overridden version of the resize method
    @Override
    public void resize(double width, double height) {
        // Update the size of the rectangle
        super.resize(width, height);
        gameLogic.getBoard().resize(width, height);
    }

    public GoBoard getBoard() {
        return this.gameLogic.getBoard();
    }
}
