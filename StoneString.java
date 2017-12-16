import java.util.ArrayList;

public class StoneString {

    private ArrayList<Stone> stoneString;
    private int player;
    private boolean hasLiberty;

    public StoneString(int player) {
        this.stoneString = new ArrayList<Stone>();
        this.player = player;
        hasLiberty = false;
    }

    public void add(Stone stone) {

        if (stone.GetStone() == this.player)
            if (!this.stoneString.contains(stone)) {
                // Add a stone to stoneString
                this.stoneString.add(stone);
            } else
                System.out.println(stone + " already added");
            // else if empty
        else if (stone.GetStone() == 0) {
            // output group has liberty
//            System.out.println("Group has liberty");
            // update hasLiberty to true
            this.hasLiberty = true;
        }
        // else (piece opposite colour
//        else
//            // output piece is opposite player
//            System.out.println(stone+" is oppsite player");
    }

    public ArrayList<Stone> getStoneString() {

        return this.stoneString;
    }

    public Stone get(int index) {

        return this.stoneString.get(index);
    }

    public int size() {

        return this.stoneString.size();
    }

    public int getPlayer() {

        return this.player;
    }

    public boolean hasLiberty() {

        return this.hasLiberty;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.stoneString.size(); i++) {
            Stone current_piece = this.get(i);
            sb.append("[").append(current_piece.GetX()).append(",").append(current_piece.GetY()).append("]");
        }
        sb.append("hasLibery:").append(this.hasLiberty);
        return sb.toString();
    }
}
