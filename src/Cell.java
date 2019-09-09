import javolution.io.Struct;
import java.util.ArrayList;
import java.util.Arrays;

public class Cell implements Cloneable {
    //CONSTRUCTORS
    public Cell(){
        this(GameState.NULL);
    }

    public Cell(GameState gs){
        this.cellstate.setGameState(gs);
    }

    public Cell(Cell obj){
        this.revealed = obj.revealed;
        this.cellstate.setGameState(obj.cellstate.getGameState());
        this.cellstate.setPlayerState(obj.cellstate.getPlayerState());
    }

    //ACCESSORS
    public boolean isMine(){
        return (this.cellstate.getGameState().equals(GameState.MINE)) ? true : false;
    }

    public boolean isRevealed(){
        return this.revealed;
    }


    //MUTATORS
    public void changePlayerState(){
        this.cellstate.setPlayerState(this.cellstate.getPlayerState().getNext());
        return;
    }

    public boolean setMine(){
        this.cellstate.setGameState(GameState.MINE);
        return this.cellstate.getGameState().equals(GameState.MINE);
    }

    public boolean revealMine(){
        if(this.isMine()){
            this.revealed = true;
            return true;
        }
        return false;
    }

    //MEMBER VARIABLES
    private boolean revealed = false;
    CellState cellstate = new CellState();

}

//ENUMERATIONS
//number of mines adjacent to cell, unless this is a mine
enum GameState {
    NULL,
    ZERO,
    NUMBER,
    MINE;

    public GameState getGameState(int index){
        return this.arr.get(index);
    }
    private ArrayList<GameState> arr = new ArrayList<>(Arrays.asList(GameState.values()));

}

//the symbol the player casts on this cell, except for CLICK which will reveal the cell's GameState
enum PlayerState {
    NOTHING,
    FLAG,
    QUESTION,
    CLICK;

    public PlayerState getPlayerState(int i){
        return arr.get(i - 1);
    }

    public PlayerState getNext(){
        return arr.get((this.ordinal() + 1) % this.arr.size());
    }
    private ArrayList<PlayerState> arr = new ArrayList<>(Arrays.asList(PlayerState.values()));
}

//struct holds most of the information for each cell
class CellState extends Struct {
    //ACCESSORS
    public int getAdjacentMines(){
        return this.adjacentMines;
    }

    public GameState getGameState(){
        return this.GS;
    }

    public PlayerState getPlayerState(){
        return this.PS;
    }

    public void setAdjacentMines(int mineCount){
        this.adjacentMines = mineCount;
        if(this.adjacentMines > 0)
            this.GS = GameState.NUMBER;
        else
            this.GS = GameState.ZERO;
        return;
    }

    //MUTATORS
    public boolean setGameState(GameState gs){
        this.GS = gs;
        return this.GS.equals(gs);
    }

    public boolean setPlayerState(PlayerState ps){
        this.PS = ps;
        return this.PS.equals(ps);
    }

    //MEMBER VARIABLES
    private int adjacentMines = 0;
    private GameState GS = GameState.NULL;
    private PlayerState PS = PlayerState.NOTHING;

}
