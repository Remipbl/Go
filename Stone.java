import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Translate;

public class Stone extends Group {
    private int player;        // The player that this stone belongs to
    private Ellipse stone;    // Ellipse representing the player's stone
    private Translate t;    // Translation for the player stone

    // Default constructor for the class
    public Stone(int player) {
        // Initialize the variables
        this.player = player;
        stone = new Ellipse();
        t = new Translate();

        // Add the translate to the stone
        stone.getTransforms().add(t);

        // We set the colors according to the player
        switch (player) {
            case 0:
                stone.setFill(Color.TRANSPARENT);
                break;
            case 1:
                stone.setFill(Color.WHITE);
                break;
            case 2:
                stone.setFill(Color.BLACK);
                break;
        }

        // Add the stone to the children of the Stone class
        getChildren().add(stone);
    }

    // Overridden version of the resize method to give the stone the correct size
    @Override
    public void resize(double width, double height) {
        // Call the superclass method first
        super.resize(width, height);

        // Recenter the ellipse and update the radius
        stone.setCenterX(width / 2);
        stone.setCenterY(height / 2);
        stone.setRadiusX(width / 4);
        stone.setRadiusY(height / 4);
    }

    // Overridden version of the relocate method to position the stone correctly
    @Override
    public void relocate(double x, double y) {
        // Call the superclass method first
        super.resize(x, y);
        // Update the position
        t.setX(x);
        t.setY(y);
    }

    // public method that will swap the colour and type of this stone
    public void SwapStone() {
        if (stone.getFill() == Color.WHITE) {
            stone.setFill(Color.BLACK);
        } else if (stone.getFill() == Color.BLACK) {
            stone.setFill(Color.WHITE);
        }
    }

    // returns the type of this stone
    public int GetStone() {
        return player;
    }

    // method that will set the stone type
    public void SetStone(final int type) {
        player = type;
        // We set the colors according to the player
        switch (player) {
            case 0:
                stone.setFill(Color.TRANSPARENT);
                break;
            case 1:
                stone.setFill(Color.WHITE);
                break;
            case 2:
                stone.setFill(Color.BLACK);
                break;
        }
    }
}
