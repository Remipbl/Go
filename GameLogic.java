import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

public class GameLogic {
    // Constants of the class
    private final int BOARD_SIZE = 7; // Constant to define the size of the board

    private GoBoard board;
    // The current player and the opposing one
    public int current_player;
    private int opposing_player;
    // To check if the game is currently in play
    public boolean in_play;
    // Current scores of player 1 and player 2
    private int player1_score;
    private int player2_score;

    private SimpleIntegerProperty scoreProperty;
    private Integer score;

    // Constructor
    public GameLogic(GoBoard board) {
        super();
        this.board = board;
        this.score = 1;
        //Making a SimpleIntegerProperty which will bind to the TextField in the controlPanel
        this.scoreProperty = new SimpleIntegerProperty(this.score);
        resetGame();
    }

    // Method to return the board
    public GoBoard getBoard() {
        return this.board;
    }

    // Method to return the score of the player
    public IntegerProperty getScore() {
        return this.scoreProperty;
    }

    // Method to reset the game at his starting state
    public void resetGame() {
        board.ResetRenders();
        in_play = true;
        current_player = 2;
        opposing_player = 1;
        System.out.println("The game has been reset successfully");
    }

    // Method that will try to place a piece in the given x,y coordinate
    public void placeStone(final double x, final double y) {
        int cx, cy;

        cx = (int) (x / board.cell_width);
        cy = (int) (y / board.cell_height);

        // Check if a game is in play or if the place is already taken
        if (!in_play) {
            System.out.println("The game is not in play");
            return;
        } else if (board.render[cx][cy].GetStone() != 0) {
            System.out.println("You can't place a stone here, this place is already taken");
            return;
        }

        // We place the stone
        board.render[cx][cy].SetStone(current_player);
        // We swap the players
        swapPlayers();
    }

    // Method for swapping the players
    public void swapPlayers() {
        int tmp = current_player;

        current_player = opposing_player;
        opposing_player = tmp;

        System.out.println("The current player is player " + current_player);
    }
}
