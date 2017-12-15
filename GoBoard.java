import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

public class GoBoard extends Pane {
    // Constants of the class
    private final int BOARD_SIZE = 7; // Constant to define the size of the board

    // Rectangle for the background of the board
    private Rectangle background;
    // Arrays for the lines that makeup the horizontal and vertical grid lines
    private Line[] horizontal;
    private Line[] vertical;
    // Arrays holding translate objects for the horizontal and vertical grid lines
    private Translate[] horizontal_t;
    private Translate[] vertical_t;
    // Arrays for the internal representation of the board and the stones that are in place
    public Stone[][] render;
    // The width and height of a cell in the board
    public double cell_width;
    public double cell_height;

    // Default contructor
    public GoBoard() {
        // Initialize all the boards
        horizontal = new Line[BOARD_SIZE];
        vertical = new Line[BOARD_SIZE];
        horizontal_t = new Translate[BOARD_SIZE];
        vertical_t = new Translate[BOARD_SIZE];
        render = new Stone[BOARD_SIZE][BOARD_SIZE];
        // Initialize the GameLogic object

//        this.gameLogic = gameLogic;

        // We initialize the lines and background
        InitializeLinesBackground();
        // We initialize the render array
        InitialiseRender();
        System.out.println("The board and render have been initialized");
    }

    // Methods to initialize the background and the lines that make the board
    private void InitializeLinesBackground() {
        // Initialise the background
        background = new Rectangle();
        background.setFill(Color.BURLYWOOD);
        // Add the rectangle as child of the Go board
        getChildren().addAll(background);

        // Initialize the horizontal and vertical lines and the translates
        for (int i = 0; i < BOARD_SIZE; i++) {
            horizontal[i] = new Line();
            vertical[i] = new Line();

            horizontal_t[i] = new Translate(0, 0);
            vertical_t[i] = new Translate(0, 0);

            horizontal[i].getTransforms().add(horizontal_t[i]);
            vertical[i].getTransforms().add(vertical_t[i]);

            horizontal[i].setStroke(Color.BLACK);
            vertical[i].setStroke(Color.BLACK);

            // Add the lines as childs of the Go board
            getChildren().addAll(horizontal[i], vertical[i]);
        }
    }

    // Initialise the renders array to create all the stones properly (none of them is visible at this point)
    private void InitialiseRender() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                render[i][j] = new Stone(0, i, j);
                getChildren().add(render[i][j]);
            }
        }
    }



    // Overridden version of the resize method to give the board the correct size
    @Override
    public void resize(double width, double height) {
        // Call the superclass method first
        super.resize(width, height);
        // Figure out the width and height of a cell (we add 2 to the board size to let a space on each sides of our widget)
        cell_width = width / BOARD_SIZE;
        cell_height = height / BOARD_SIZE;

        // Resize the rectangle according to the board size
        background.setWidth(width);
        background.setHeight(height);

        // We call all the ResizeRelocate methods to set up the lines and stones correctly
        HorizontalResizeRelocate(width);
        VerticalResizeRelocate(height);
        StoneResizeRelocate();
    }

    // private method for resizing and relocating the horizontal lines
    private void HorizontalResizeRelocate(final double width) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            horizontal[i].setStartX(cell_width / 2);
            horizontal[i].setStartY(cell_height / 2);
            horizontal[i].setEndX(width - (cell_width / 2));
            horizontal[i].setEndY(cell_height / 2);
            horizontal_t[i].setY(i * cell_height);
        }
    }

    // private method for resizing and relocating the vertical lines
    private void VerticalResizeRelocate(final double height) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            vertical[i].setStartX(cell_width / 2);
            vertical[i].setStartY(cell_height / 2);
            vertical[i].setEndX(cell_width / 2);
            vertical[i].setEndY(height - (cell_height / 2));
            vertical_t[i].setX(i * cell_width);
        }
    }

    // Private method for resizing and relocating all the pieces
    private void StoneResizeRelocate() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                render[i][j].relocate(i* cell_width, j * cell_height);
                render[i][j].resize(cell_width, cell_height);
            }
        }
    }

    // Reset the render array
    public void ResetRenders() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                render[i][j].SetStone(0);
            }
        }
    }

    public Stone getStone(int x, int y) {
        if (x < 0 || y < 0 || x > 6 || y > 6)
            return null;
        else
            return render[x][y];
    }
}
