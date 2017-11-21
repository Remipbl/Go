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
    private Stone[][] render;
    // The current player and the opposing one
    private int current_player;
    private int opposing_player;
    // To check if the game is currently in play
    private boolean in_play;
    // Current scores of player 1 and player 2
    private int player1_score;
    private int player2_score;
    // The width and height of a cell in the board
    private double cell_width;
    private double cell_height;
    // 3x3 array that holds the pieces that surround a given piece
    private int[][] surrounding;
    // 3x3 array that determines if a reverse can be made in any direction
    private boolean[][] can_reverse;

    // Default contructor
    GoBoard() {
        // Initialise all the boards
        horizontal = new Line[BOARD_SIZE];
        vertical = new Line[BOARD_SIZE];
        horizontal_t = new Translate[BOARD_SIZE];
        vertical_t = new Translate[BOARD_SIZE];
        render = new Stone[BOARD_SIZE][BOARD_SIZE];

        // We initialize the lines and background
        InitializeLinesBackground();
        // We initialize the render array
        InitialiseRender();
        System.out.println("The board and render have been initialized");
        // Set the state of the game to the start
        ResetGame();
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
                render[i][j] = new Stone(0);
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
        System.out.println("The board has been properly set");
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

    // Public method to reset the game at his starting state
    public void ResetGame() {
//        System.out.println("test");
        ResetRenders();
        in_play = true;
        current_player = 2;
        opposing_player = 1;
        System.out.println("The game has been reset successfully");
    }

    // Reset the render array
    private void ResetRenders() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                render[i][j].SetStone(0);
            }
        }
    }

    // Public method that will try to place a piece in the given x,y coordinate
    public void PlaceStone(final double x, final double y) {
        int cx, cy;


        cx = (int) (x / cell_width);
        cy = (int) (y / cell_height);

        // Check if a game is in play or if the place is already taken
        if (!in_play) {
            System.out.println("The game is not in play");
            return;
        } else if (render[cx][cy].GetStone() != 0) {
            System.out.println("You can't place a stone here, this place is already taken");
            return;
        }
//        else if ((cx < 1) || (cx > 7) || (cy < 0) || (cy > 7))
//            return;

        // We place the stone
        render[cx][cy].SetStone(current_player);
        System.out.println("The stone has been place successfully");
        SwapPlayers();
    }

    // Private method for swapping the players
    private void SwapPlayers() {
        int tmp = current_player;

        current_player = opposing_player;
        opposing_player = tmp;

        System.out.println("The current player is player " + current_player);
    }
}
