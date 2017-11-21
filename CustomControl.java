import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class CustomControl extends Control {
    private GoBoard gb_board;

    // Default constructor
    public CustomControl() {
        // Set a default skin and generate a game board
        setSkin(new CustomControlSkin(this));
        gb_board = new GoBoard();
        getChildren().add(gb_board);

        // Add a mouse clicked listener that will try to place a piece
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                gb_board.PlaceStone(event.getX(), event.getY());
            }
        });

        // Add a key pressed listener to reset the game if spacebar is clicked
        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.SPACE)
                    gb_board.ResetGame();
            }
        });
    }

    // Overridden version of the resize method
    @Override
    public void resize(double width, double height) {
        // Update the size of the rectangle
        super.resize(width, height);
        gb_board.resize(width, height);
    }
}
