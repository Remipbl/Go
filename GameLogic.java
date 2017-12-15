import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameLogic {
    // Constants of the class
    private final int BOARD_SIZE = 7; // Constant to define the size of the board

    // GoBoard instance
    private GoBoard board;

    // The current player and the opposing one
    public int current_player;
    private int opposing_player;

    // To check if the game is currently in play
    public boolean in_play;

    // Current scores of player 1 and player 2
    private int player1_score;
    private int player2_score;

    // Score properties
    private SimpleIntegerProperty currentPlayerProperty; // property to help with binding
    private SimpleIntegerProperty scoreProperty;
    private Integer score;

    // board history
    private ArrayList<Stone[][]> renders;
    private int currenRender; // index of the board currently in use in the board array

    // ************ GAME LOGIC  ************

    // Constructor
    public GameLogic(GoBoard board) {
        super();
        this.board = board;
        this.score = 1;
        this.current_player = 2;

        //Making a SimpleIntegerProperty which will bind to the TextField in the controlPanel
        this.scoreProperty = new SimpleIntegerProperty(this.score);
        this.currentPlayerProperty = new SimpleIntegerProperty(this.current_player);
        this.resetGame();
    }

    public void resetGame() {
        // reset the render
        board.ResetRenders();

        // reset the variables monitoring the game
        in_play = true;
        current_player = 2;
        opposing_player = 1;

        player1_score = 0;
        player2_score = 0;

        // reset renders (board history)
        this.renders = new ArrayList<Stone[][]>();

        // save the board in renders
        this.boardSave();

        System.out.println("The game has been reset successfully");
    }

    public GoBoard getGoBoard() {

        return board;
    }

    // ************ TRYING TO PLACE A PIECE FUNCTIONS  ************

    //Try to place a piece in the given x,y coordinate
    public void placeStoneTry(double x, double y) {
        System.out.println("tryPlaceStone()*******************************");
        this.score++;

        //Update the SimpleIntegerProperty scoreProperty when you update the int score so that the TextField tf_score in the GoControlPanel updates automatically
        this.scoreProperty.setValue(this.score);

        // Determine which cell the current player has clicked on
        final int cellx = (int) (x / board.cell_width);
        final int celly = (int) (y / board.cell_height);

        // if the game is not in play exit method
        System.out.println("in_play:" + in_play);

        if (!in_play) {
            System.out.println("The game is not in play");
            return;
        }

        // if piece is not empty exit method
        if (board.render[cellx][celly].GetStone() != 0) {
            System.out.println("You can't place a stone here, this place is already taken");
            return;
        }

        // Attempt to capture

        this.capture(this.current_player, cellx, celly);

        // Test to is if it is KO
        if(isKO()){
            // you might want to prevent the move or make the move and rewind the board history
            System.out.println("Player " + current_player + " can't place a stone here because the original board position will repeat (KO Rule)");
            return;
        }

        if(isSuicide(cellx, celly)){
            // you might want to prevent the move or make the move and rewind the board history
            System.out.println("You can't move here. It's a suicide move.");
            return;
        }

        // Default case - place the piece
        this.placeStone(cellx, celly);

        // if we get to this point then a successful move has been made so swap the
        // players and update the scores
        swapPlayers();
        boardSave();
        updateScores(); // NB captureAndIncrement() will update some of the score values
        determineEndGame();

        // Print out information on game status
    }

    // Method to return the board
    public GoBoard getBoard() {

        return this.board;
    }

    // Places a piece
    public void placeStone(final int x, final int y) {

        board.render[x][y].SetStone(current_player);
        System.out.println("Player 1 Score : " + player1_score);
        System.out.println("Player 2 Score : " + player2_score);
    }

    // Method for swapping the players
    public void swapPlayers() {

        int tmp = current_player;
        current_player = opposing_player;
        opposing_player = tmp;

        System.out.println("The current player is player " + current_player);
    }

    // ************ KO FUNCTIONS  ************

    // Method which returns true if current board is equal to the board
    // the last time it was this players turn
    private boolean isKO() {
        boardSave();

        System.out.println("***********************TEST IsKO !!!***********************");

        // If there are enough boards in the history
        if (renders.size() == 3) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (renders.get(0)[i][j].GetStone() != board.render[i][j].GetStone()
                            || renders.get(0)[i][j].GetX() != board.render[i][j].GetX()
                            || renders.get(0)[i][j].GetY() != board.render[i][j].GetY()) {
                        renders.remove(0);
                        return false;
                    }
                }
            }
            undo();
            return true;
        }
        return false;
    }

    // Saves a copy of the board in board history
    private void boardSave(){
        // Stone array use to make a hard copy of the current board
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

    // Reverts to previous board
    public void undo() {

        // Revert the current board to our previous board
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board.render[i][j] = new Stone(renders.get(1)[i][j].GetStone(), renders.get(1)[i][j].GetX(), renders.get(1)[i][j].GetY());
            }
        }

        // We remove the copy of the current board from our history
        renders.remove(2);
        System.out.println("The board has been revert to its previous state because of a ko move");
    }

    // ************ CAPTURE FUNCTIONS  ************

    // Attempts to capture neighbouring opponent groups
    private boolean capture(int player, int x, int y) {

        // Place the piece
        board.render[x][y].SetStone(player);
        boolean captured = false;

        System.out.println("**************************CAPTURE************************");
        // Call captureAndIncrementScore in all for 4 directions provided there is a
        // opposing piece there.
        System.out.println("Test Capture !!!!!!!!!!");
        if((board.getStone(x + 1, y) != null) && (board.getStone(x + 1, y).GetStone() == opposing_player)) {
            System.out.println("Enemy to the right");
            if (captureAndIncrementScore(false, x + 1, y) == true)
                captured = true;
        }
        if((board.getStone(x - 1, y) != null) && (board.getStone(x - 1, y).GetStone() == opposing_player)) {
            System.out.println("Enemy to the left");
            if (captureAndIncrementScore(false, x - 1, y) == true)
                captured = true;
        }
        if((board.getStone(x, y + 1) != null) && (board.getStone(x, y + 1).GetStone() == opposing_player)) {
            System.out.println("Enemy to the bottom");
            if (captureAndIncrementScore(false, x, y + 1) == true)
                captured = true;
        }
        if((board.getStone(x, y - 1) != null) && (board.getStone(x, y - 1).GetStone() == opposing_player)) {
            System.out.println("Enemy to the top");
            if (captureAndIncrementScore(false, x, y - 1) == true)
                captured = true;
        }

        // If you didn't capture reset the piece.
        return captured;
    }

    // Attempt to capture a group in this direction and update the scores
    private boolean captureAndIncrementScore(boolean captured, int x, int y) {

        System.out.println("**************************Capture Increment of : " + x + "; "+ y+"************************");
        System.out.println("Create enemy list");

        // Make a PiecesString starting with the opponents piece
        StoneString enemyList = new StoneString(opposing_player);
        enemyList.add(board.render[x][y]);

        // Call piecesStringAddNeighbours() to add all the neighbouring opponent pieces
        for (int i = 0; i < enemyList.size(); ++i) {

            // Repeatedly call piecesStringAddNeighbours() to make its group
            System.out.println("x : " + enemyList.get(i).GetX()+";" + "y : " + enemyList.get(i).GetY()+";");
            System.out.println("List size : " + enemyList.size());
            piecesStringAddNeighbours(enemyList.get(i).GetX(), enemyList.get(i).GetY(), enemyList);
        }

        // If the piecesString has no liberties capture it and update scores
        if (!enemyList.hasLiberty()) {
            captured = true;
            System.out.println("Enemy group is captured.");

            for (int i = 0; i < enemyList.size(); ++i)
                enemyList.get(i).SetStone(0);

            if (current_player == 1)
                player1_score += enemyList.size();
            else if (current_player == 2)
                player2_score += enemyList.size();

            String ret = renderToString(board.render);
            System.out.println(ret);
            return captured;
        }
        System.out.println("Enemy group has liberties.");
        return captured;
    }

    // The most important function
    // Add the neighbours of the same player to the PiecesString
    public void piecesStringAddNeighbours(int x, int y, StoneString stoneString) {

        System.out.println("Check neighbours of : " + x + "; "+ y);

        // Attempt to add non empty neighbours of the same player type
        // to the PiecesString in all 4 directions
        if ((board.getStone(x + 1, y) != null)) {
            stoneString.add(board.render[x + 1][y]);
            if (stoneString.hasLiberty() == true)
                System.out.println("Liberty on : " + (x + 1) + "; " + y);
            }
        if ((board.getStone(x - 1, y) != null)) {
            stoneString.add(board.render[x - 1][y]);
            if (stoneString.hasLiberty() == true)
                System.out.println("Liberty on : " + (x - 1) + "; " + y);
            }
        if ((board.getStone(x, y + 1) != null)) {
            stoneString.add(board.render[x][y + 1]);
            if (stoneString.hasLiberty() == true)
                System.out.println("Liberty on : " + x + "; " + (y + 1));
        }
        if ((board.getStone(x, y - 1) != null)) {
            stoneString.add(board.render[x][y - 1]);
            if (stoneString.hasLiberty() == true)
                System.out.println("Liberty on : " + x + "; " + (y - 1));
        }
        System.out.println("Group size : " + stoneString.size());
    }

    // ************ SUICIDE FUNCTIONS  ************

    // Place a piece of this player in position x , y. If it is part of a group
    // with no liberties then this is a suicide move.
    private boolean isSuicide(int x, int y) {
        System.out.println("************************Is Suicide************************");
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
            System.out.println("x : " + neighbourList.get(i).GetX()+";" + "y : " + neighbourList.get(i).GetY()+";");
            System.out.println("List size : " + neighbourList.size());

            piecesStringAddNeighbours(neighbourList.get(i).GetX(), neighbourList.get(i).GetY(), neighbourList);

            String ret = renderToString(board.render);
            System.out.println(ret);
        }
        if (neighbourList.hasLiberty() == true) {
            return true;
        }

        // return if it has liberties or not
        return false;
    }

    public static String renderToString(Stone [][] render){
        int player [][] = new int[render.length][render[0].length];
        for(int i=0; i< render.length; i++){
            for (int j=0; j< render[0].length; j++){
                player[i][j] = render[i][j].GetStone();
            }
        }
        String renderToString = twoDArrayToString(player);
        return renderToString;
    }

    public static String twoDArrayToString(int array[][]){
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<array.length; i++){
            for(int j=0; j<array[0].length; j++){
                sb.append(array[j][i]);
                if(j<array[0].length-1){
                    sb.append(",");
                }
            }
            if(i<array[0].length-1){
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static void call(String[] args) {
        int array [][] = new int [7][7];
        for(int i=0; i<array.length; i++)
            for(int j=0; j<array[0].length; j++)
                array[i][j] = i*10+j;
        System.out.println("Array:\n"+twoDArrayToString(array));

    }

    // ************ PRETTY PRINT FUNCTION  ************

    // Return Renders(board history) as a String
//    public String rendersToString(){
//        StringBuffer sb = new StringBuffer();
//        for(int i = 0 ; i<this.renders.size(); i++){
//            sb.append("\nRenders ").append(i).append(renders.get(i)).append("\n");
//            sb.append(GoBoard.renderToString(renders.get(i)));
//        }
//        return sb.toString();
//    }

    // ************ BINDING FUNCTIONS   ************

    // This method is called when binding the SimpleIntegerProperty scoreProperty in this class to the TextField tf_score in controlPanel
    public IntegerProperty getScore() {

        return scoreProperty;
    }

    public IntegerProperty getCurrentPlayer() {

        return this.currentPlayerProperty;
    }

    // ************ ADVANCED FUNCTIONS  ************

    // private method to allow players to pass
    public void pass() {
        /// do some work

        // see if the game is done
        this.determineEndGame();
    }

    // Updates the player's scores
    private void updateScores() {
        // update the score, if you are doing the integerProperty binding you might do it here.
        // but is is best up update the
    }

    // Determines if the end of the game has been reached
    private void determineEndGame() {
        /// have each of the players passed in succession

        determineWinner();
    }

    // Private method to determine if a player has a moves available
    // (advanced so returns true by default)
    private boolean canMove() {
        return true;
    }

    // private method that determines who won the game
    private void determineWinner() {
        // what is the prisoner score

        // what is the territory score (advanced)

        // update the variables

        // show who the winner is
    }
}
















    // Method to return the score of the player
//    public IntegerProperty getScore() {
//        return this.scoreProperty;
//    }

    // Method to reset the game at his starting state
//    public void resetGame() {
//        board.ResetRenders();
//        in_play = true;
//        current_player = 2;
//        opposing_player = 1;
//        System.out.println("The game has been reset successfully");
//    }

    // Method that will try to place a piece in the given x,y coordinate
//    public void placeStone(final double x, final double y) {
//        int cx, cy;
//
//        cx = (int) (x / board.cell_width);
//        cy = (int) (y / board.cell_height);

        // Check if a game is in play or if the place is already taken
//        if (!in_play) {
//            System.out.println("The game is not in play");
//            return;
//        } else if (board.render[cx][cy].GetStone() != 0) {
//            System.out.println("You can't place a stone here, this place is already taken");
//            return;
//        }
//
//
//        if (checkIntersection(cx, cy) == true) {

            // We place the stone
//            CoordList.clear();
//
//            board.render[cx][cy].SetStone(current_player);

            // We swap the players
//            swapPlayers();
//        }
//
//    }


//
//    public boolean surroundedByFriends(int x, int y) {
//
//        if ((board.render[x + 1][y].GetStone() == current_player) || (board.render[x - 1][y].GetStone() == current_player) ||
//                (board.render[x][y + 1].GetStone() == current_player) || (board.render[x][y - 1].GetStone() == current_player))
//            return true;
//        return false;
//    }
//
//    public boolean hasLiberties(int x, int y) {
//
//        if ((board.render[x + 1][y].GetStone() == 0) || (board.render[x - 1][y].GetStone() == 0) ||
//                (board.render[x][y + 1].GetStone() == 0) || (board.render[x][y - 1].GetStone() == 0))
//            return true;
//        return false;
//    }
//
//    public boolean hasAlreadyBeenChecked(int x, int y) {
//
//        return true;
//    }
//
//    public boolean checkIntersection(int x, int y) {

//        Coordinates coord = new Coordinates(x, y);
//        CoordList.add(new Coordinates(x, y));

//        System.out.println(CoordList.get(0).);
//        return true;


//        if (hasLiberties(x, y) == true) {
//            return true;
//        }
//
//        if (!hasAlreadyBeenChecked(x, y) && surroundedByFriends(x, y) == true) {
//            checkIntersection(x + 1, y);
//            checkIntersection(x - 1, y);
//            checkIntersection(x, y + 1);
//            checkIntersection(x, y - 1);
//        }
//    return false;
//    }
//}
