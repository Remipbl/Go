import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;

public class GameLogic {
    //region Class constants
    //---------------------------------------------------------------------------------------
    private final int BOARD_SIZE = 7; // Constant to define the size of the board
    //---------------------------------------------------------------------------------------
    //endregion
    // The current player and the opposing one
    public int current_player;
    // To check if the game is currently in play
    public boolean in_play;
    //region Game logic variables
    //---------------------------------------------------------------------------------------
    // GoBoard instance
    private GoBoard board;
    private int opposing_player;
    // Current scores of player 1 and player 2
    private int player1_score;
    private int player2_score;
    // Check if the players pass
    private boolean pass1;
    private boolean pass2;
    // Board history for the KO rule
    private ArrayList<Stone[][]> renders;
    //---------------------------------------------------------------------------------------
    //endregion
    //region Binding properties
    //---------------------------------------------------------------------------------------
    // SimpleIntegerProperty to do the binding of the scores and current player
    private SimpleIntegerProperty currentPlayerProperty;
    private SimpleIntegerProperty score1Property;
    private SimpleIntegerProperty score2Property;
    //---------------------------------------------------------------------------------------
    //endregion

    // Constructor
    public GameLogic(GoBoard board) {
        super();
        this.board = board;
        this.player1_score = 0;
        this.player2_score = 0;
        this.current_player = 2;

        //Making a SimpleIntegerProperty which will bind to the TextField in the controlPanel
        this.score1Property = new SimpleIntegerProperty(this.player1_score);
        this.score2Property = new SimpleIntegerProperty(this.player2_score);
        this.currentPlayerProperty = new SimpleIntegerProperty(this.current_player);

        // we reset the state of the game to set everything properly
        this.resetGame();
    }

    //region Printing Methods (Debug purpose)
    //---------------------------------------------------------------------------------------
    public static String renderToString(Stone[][] render) {
        int player[][] = new int[render.length][render[0].length];
        for (int i = 0; i < render.length; i++) {
            for (int j = 0; j < render[0].length; j++) {
                player[i][j] = render[i][j].GetStone();
            }
        }
        String renderToString = twoDArrayToString(player);
        return renderToString;
    }

    private static String twoDArrayToString(int array[][]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                sb.append(array[j][i]);
                if (j < array[0].length - 1) {
                    sb.append(",");
                }
            }
            if (i < array[0].length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static void call(String[] args) {
        int array[][] = new int[7][7];
        for (int i = 0; i < array.length; i++)
            for (int j = 0; j < array[0].length; j++)
                array[i][j] = i * 10 + j;
        System.out.println("Array:\n" + twoDArrayToString(array));

    }
    //---------------------------------------------------------------------------------------
    //endregion

    // Reset the state of the game
    public void resetGame() {
        // reset the render
        board.ResetRenders();

        // reset the variables monitoring the game
        in_play = true;
        current_player = 2;
        opposing_player = 1;

        player1_score = 0;
        player2_score = 0;

        pass1 = false;
        pass2 = false;

        updateScores();
        //Update the SimpleIntegerProperty for the current player value
        this.currentPlayerProperty.setValue(this.current_player);

        // reset renders (board history)
        this.renders = new ArrayList<Stone[][]>();

        // save the board in renders
        this.boardSave();

        System.out.println("The game has been reset successfully");
    }

    // Method to return the board
    public GoBoard getBoard() {

        return this.board;
    }

    //region Place stone
    //---------------------------------------------------------------------------------------
    // Check if a stone can be placed in the given x,y coordinates
    public void placeStoneTry(double x, double y) {
        // We reinitialize those variables to false because a player made a move
        pass1 = false;
        pass2 = false;

        // Determine which cell the current player has clicked on
        final int cellX = (int) (x / board.cell_width);
        final int cellY = (int) (y / board.cell_height);

        // Check if the game is in play
        if (!in_play) {
            System.out.println("The game is not in play, you can reset the game to play again.");
            return;
        }

        // If the cell is not empty, the player can't play here
        if (board.render[cellX][cellY].GetStone() != 0) {
            System.out.println("This place is already taken. Place your stone elsewhere.");
            return;
        }

        // Attempt to capture
        this.capture(this.current_player, cellX, cellY);

        // Check if the move will lead to a KO state
        if (isKO()) {
            System.out.println("Player " + current_player + " can't place a stone here because the original board position will repeat (KO Rule)");
            return;
        }

        // Check if the move is a suicide move
        if (isSuicide(cellX, cellY)) {
            System.out.println("You can't place a stone here because it's a suicide move.");
            return;
        }

        // If all the check passed, we place the stone
        this.placeStone(cellX, cellY);

        // A move has been made, so we swap the players and update the score
        swapPlayers();
        updateScores();
    }

    // Place a piece at the given x,y coordinates
    public void placeStone(final int x, final int y) {
        // We set the stone to the color of the current player
        board.render[x][y].SetStone(current_player);
    }
    //---------------------------------------------------------------------------------------
    //endregion

    //region KO Rule
    //---------------------------------------------------------------------------------------
    // Returns true if the current board is identical to the state of the board the last time it was this players turn
    private boolean isKO() {
        // We save a copy of the current board
        boardSave();

        // If there are enough boards in the history, we do the comparison between the current board
        // and the previous state of the board the last time it was this players turn
        if (renders.size() == 3) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    // If the boards are not identical, this not a ko and we return false and remove the first board of our history
                    if (renders.get(0)[i][j].GetStone() != board.render[i][j].GetStone()) {
                        renders.remove(0);
                        return false;
                    }
                }
            }
            // If the boards are identical, this is a ko, so we undo the move the player made and return true
            undo();
            return true;
        }
        return false;
    }

    // Saves a copy of the current board in the board history
    private void boardSave() {
        // Stone array used to make a hard copy of the current board
        Stone[][] renderCopy = new Stone[BOARD_SIZE][BOARD_SIZE];

        // Make a hard copy of the render array into the renderCopy using a for loop
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                renderCopy[i][j] = new Stone(board.render[i][j].GetStone(), board.render[i][j].GetX(), board.render[i][j].GetY());
            }
        }

        // Add the renderCopy array to our history
        renders.add(renderCopy);
    }

    // Reverts to the previous board
    public void undo() {
        // Revert the current board to our previous board
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board.render[i][j].SetStone(renders.get(1)[i][j].GetStone());
                board.render[i][j].SetX(renders.get(1)[i][j].GetX());
                board.render[i][j].SetY(renders.get(1)[i][j].GetY());
            }
        }

        // We remove the copy of the current board from our history because it no longer exist
        renders.remove(2);
        System.out.println("The board has been revert to its previous state because of a ko move.");
    }
    //---------------------------------------------------------------------------------------
    //endregion

    //region Capture
    //---------------------------------------------------------------------------------------
    // Attempts to capture neighbour opponent groups
    private boolean capture(int player, int x, int y) {

        // Place the stone
        board.render[x][y].SetStone(player);
        boolean captured = false;

//        System.out.println("**************************CAPTURE************************");
        // Call captureAndIncrementScore in all for 4 directions provided there is a
        // opposing piece there.
//        System.out.println("Test Capture !!!!!!!!!!");
        if ((board.getStone(x + 1, y) != null) && (board.getStone(x + 1, y).GetStone() == opposing_player)) {
//            System.out.println("Enemy to the right");
            if (captureAndIncrementScore(false, x + 1, y) == true)
                captured = true;
        }
        if ((board.getStone(x - 1, y) != null) && (board.getStone(x - 1, y).GetStone() == opposing_player)) {
//            System.out.println("Enemy to the left");
            if (captureAndIncrementScore(false, x - 1, y) == true)
                captured = true;
        }
        if ((board.getStone(x, y + 1) != null) && (board.getStone(x, y + 1).GetStone() == opposing_player)) {
//            System.out.println("Enemy to the bottom");
            if (captureAndIncrementScore(false, x, y + 1) == true)
                captured = true;
        }
        if ((board.getStone(x, y - 1) != null) && (board.getStone(x, y - 1).GetStone() == opposing_player)) {
//            System.out.println("Enemy to the top");
            if (captureAndIncrementScore(false, x, y - 1) == true)
                captured = true;
        }

        // If you didn't capture reset the piece.
        return captured;
    }

    // Attempt to capture a group in this direction and update the scores
    private boolean captureAndIncrementScore(boolean captured, int x, int y) {

//        System.out.println("**************************Capture Increment of : " + x + "; "+ y+"************************");
//        System.out.println("Create enemy list");

        // Make a PiecesString starting with the opponents piece
        StoneString enemyList = new StoneString(opposing_player);
        enemyList.add(board.render[x][y]);

        // Call piecesStringAddNeighbours() to add all the neighbouring opponent pieces
        for (int i = 0; i < enemyList.size(); ++i) {

            // Repeatedly call piecesStringAddNeighbours() to make its group
//            System.out.println("x : " + enemyList.get(i).GetX()+";" + "y : " + enemyList.get(i).GetY()+";");
//            System.out.println("List size : " + enemyList.size());
            piecesStringAddNeighbours(enemyList.get(i).GetX(), enemyList.get(i).GetY(), enemyList);
        }

        // If the piecesString has no liberties capture it and update scores
        if (!enemyList.hasLiberty()) {
            captured = true;
//            System.out.println("Enemy group is captured.");

            for (int i = 0; i < enemyList.size(); ++i)
                enemyList.get(i).SetStone(0);

            if (current_player == 1)
                player1_score += enemyList.size();
            else if (current_player == 2)
                player2_score += enemyList.size();

//            String ret = renderToString(board.render);
//            System.out.println(ret);
            return captured;
        }
//        System.out.println("Enemy group has liberties.");
        return captured;
    }

    // The most important function
    // Add the neighbours of the same player to the PiecesString
    public void piecesStringAddNeighbours(int x, int y, StoneString stoneString) {

//        System.out.println("Check neighbours of : " + x + "; "+ y);

        // Attempt to add non empty neighbours of the same player type
        // to the PiecesString in all 4 directions
        if ((board.getStone(x + 1, y) != null)) {
            stoneString.add(board.render[x + 1][y]);
//            if (stoneString.hasLiberty() == true)
//                System.out.println("Liberty on : " + (x + 1) + "; " + y);
        }
        if ((board.getStone(x - 1, y) != null)) {
            stoneString.add(board.render[x - 1][y]);
//            if (stoneString.hasLiberty() == true)
//                System.out.println("Liberty on : " + (x - 1) + "; " + y);
        }
        if ((board.getStone(x, y + 1) != null)) {
            stoneString.add(board.render[x][y + 1]);
//            if (stoneString.hasLiberty() == true)
//                System.out.println("Liberty on : " + x + "; " + (y + 1));
        }
        if ((board.getStone(x, y - 1) != null)) {
            stoneString.add(board.render[x][y - 1]);
//            if (stoneString.hasLiberty() == true)
//                System.out.println("Liberty on : " + x + "; " + (y - 1));
        }
//        System.out.println("Group size : " + stoneString.size());
    }
    //---------------------------------------------------------------------------------------
    //endregion

    //region Suicide Rule
    //---------------------------------------------------------------------------------------
    // Place a piece of this player in position x , y. If it is part of a group
    // with no liberties then this is a suicide move.
    private boolean isSuicide(int x, int y) {
//        System.out.println("************************Is Suicide************************");
        // Place a piece of this player in x , y

        // Find out if it is part of a group with no liberties by calling piecesStringHasLiberty()
        if (piecesStringHasLiberty(current_player, x, y)) {
            board.render[x][y].SetStone(current_player);
            return false;
        }

        // make sure to reset the piece if it is a suicide move
        board.render[x][y].SetStone(0);
        return true;
    }

    // NB this function also uses piecesStringAddNeighbours() in
    // the above section
    public boolean piecesStringHasLiberty(int player, int x, int y) {

        // Place piece of this player
        board.render[x][y].SetStone(current_player);

        // Create a PiecesString starting with this piece
        StoneString neighbourList = new StoneString(current_player);
        neighbourList.add(board.render[x][y]);

        for (int i = 0; i < neighbourList.size(); ++i) {

            // Repeatedly call piecesStringAddNeighbours() to make its group
//            System.out.println("x : " + neighbourList.get(i).GetX()+";" + "y : " + neighbourList.get(i).GetY()+";");
//            System.out.println("List size : " + neighbourList.size());

            piecesStringAddNeighbours(neighbourList.get(i).GetX(), neighbourList.get(i).GetY(), neighbourList);

//            String ret = renderToString(board.render);
//            System.out.println(ret);
        }
        if (neighbourList.hasLiberty() == true) {
            return true;
        }

        // return if it has liberties or not
        return false;
    }
    //---------------------------------------------------------------------------------------
    //endregion

    //region Binding Methods
    //---------------------------------------------------------------------------------------
    // This method is called when binding the SimpleIntegerProperty scoreProperty in this class to the TextField tf_score in controlPanel
    public IntegerProperty getScore1() {
        return score1Property;
    }

    public IntegerProperty getScore2() {
        return score2Property;
    }

    public IntegerProperty getCurrentPlayer() {
        return this.currentPlayerProperty;
    }
    //---------------------------------------------------------------------------------------
    //endregion

    //region Player methods
    //---------------------------------------------------------------------------------------
    // private method to allow players to pass
    public void pass() {

        System.out.println("Player " + current_player + " has passed his turn");

        if (current_player == 1)
            pass1 = true;
        else if (current_player == 2)
            pass2 = true;

        swapPlayers();

        // Check if the game is done
        this.determineEndGame();
    }

    // We swap the players
    public void swapPlayers() {
        // Swap the current player
        int tmp = current_player;
        current_player = opposing_player;
        opposing_player = tmp;

        //Update the SimpleIntegerProperty for the current player value
        this.currentPlayerProperty.setValue(this.current_player);

        System.out.println("The current player is player " + current_player);
    }
    //---------------------------------------------------------------------------------------
    //endregion

    //region Score
    //---------------------------------------------------------------------------------------
    // Updates the player's scores
    private void updateScores() {
        //Update the SimpleIntegerProperty for the score of the player 1
        this.score1Property.setValue(this.player1_score);
        //Update the SimpleIntegerProperty for the score of the player 2
        this.score2Property.setValue(this.player2_score);
    }
    //---------------------------------------------------------------------------------------
    //endregion

    //region End game
    //---------------------------------------------------------------------------------------
    // Determines if the end of the game has been reached
    private void determineEndGame() {
        if (pass1 && pass2) {
            determineWinner();
        }
    }

    // Determines who won the game
    private void determineWinner() {
        // We update the scores of the players
        updateScores();

        // We determine who won the game and print accordingly
        if (player1_score == player2_score)
            System.out.println("It's a draw !");
        else if (player1_score > player2_score)
            System.out.println("The player 1 has won");
        else
            System.out.println("The player 2 has won");

        System.out.println("The game has ended");
        in_play = false;
    }
    //---------------------------------------------------------------------------------------
    //endregion
}