import java.util.stream.Collectors;
import javolution.util.FastSortedMap;
import javolution.util.FastTable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import java.util.stream.IntStream;
import java.util.Random;

public abstract class Grid<G> {
    public static int  arithmeticSequenceTerm(int init, int step, int n){
        return init + (n - 1) * step;
    }

    public static int arithmeticPartialSum(int init, int step, int n){
        return init * n + n * (n - 1) / 2 * step;
    };

    //CONSTRUCTORS
    public Grid(G gridID, int row, int col){
        this.id = gridID;
        this.m = new Matrix<>(row, col, new Cell());
    }

    public Grid(G gridID, int row, int col, Cell val){
        this.id = gridID;
        this.m = new Matrix<>(row, col, val);
    }

    //ACCESSORS
    abstract public int getPlayableSize();
    abstract public int countAdjacentMines(int i, int j);
    public G getID(){
        return this.id;
    }

    //MUTATORS
    public void setID(G identifier){
        this.id = identifier;
        return;
    }

    public boolean resize(int row, int col){
        return this.resize(row, col, new Cell());
    }

    public boolean resize(int row, int col, Cell val){
        Matrix<Cell> newMatrix = new Matrix<>(row, col, val);
        if(row < 0 || col < 0)
            return false;
        this.renew(row, col, val);
        for(int i = 1; i <= (this.m.getRow() <= row ? this.m.getRow() : row); i++)
            for(int j = 1; j <= (this.m.getCol() <= col ? this.m.getCol() : col); j++)
                newMatrix.setEntry(i, j, this.m.getEntry(i, j));
        this.m = newMatrix;
        return true;
    }

    public boolean renew(int row, int col){
        return this.renew(row, col, new Cell());
    }

    public boolean renew(int row, int col, Cell val){
        if(row < 0 || col < 0)
            return false;
        this.m = new Matrix<>(row, col, val);
        return true;
    }

    //MEMBER VARIABLES
    private G id;
    protected Matrix<Cell> m;

}

//grid with square or square-like cells
class SquareCellGrid<G> extends Grid<G> {
    //ENUMS
    public enum GridStyle {
        RECT,
        CIRCLE,
        CIRCLE_BRICKED;
    }

    //CONSTRUCTORS
    public SquareCellGrid(G gridID, int row, int col, Cell val) {
        super(gridID, row, col, val);

        FastTable<ImmutablePair<Integer, Integer>> rect = new FastTable<>();
        FastTable<ImmutablePair<Integer, Integer>> circle = new FastTable<>();
        FastTable<ImmutablePair<Integer, Integer>> circle_bricked = new FastTable<>();

        rect.add(new ImmutablePair<>(1, 0));
        rect.add(new ImmutablePair<>(-1, 0));
        rect.add(new ImmutablePair<>(0, 1));
        rect.add(new ImmutablePair<>(0, -1));
        rect.add(new ImmutablePair<>(1, 1));
        rect.add(new ImmutablePair<>(-1, 1));
        rect.add(new ImmutablePair<>(-1, -1));
        rect.add(new ImmutablePair<>(1, -1));

        circle.add(new ImmutablePair<>(1, 0));
        circle.add(new ImmutablePair<>(-1, 0));
        circle.add(new ImmutablePair<>(0, 1));
        circle.add(new ImmutablePair<>(0, -1));
        circle.add(new ImmutablePair<>(1, 1));
        circle.add(new ImmutablePair<>(-1, 1));
        circle.add(new ImmutablePair<>(-1, -1));
        circle.add(new ImmutablePair<>(1, -1));

        circle_bricked.add(new ImmutablePair<>(0, 2));
        circle_bricked.add(new ImmutablePair<>(0, -2));
        circle_bricked.add(new ImmutablePair<>(-1, 1));
        circle_bricked.add(new ImmutablePair<>(1, 1));
        circle_bricked.add(new ImmutablePair<>(-1, -1));
        circle_bricked.add(new ImmutablePair<>(1, -1));

        this.mineRange.put(GridStyle.RECT, rect);
        this.mineRange.put(GridStyle.CIRCLE, circle);
        this.mineRange.put(GridStyle.CIRCLE_BRICKED, circle_bricked);
    }

    //ACCESSORS
    @Override
    public int getPlayableSize() {
        if(this.style.equals(GridStyle.RECT) || this.style.equals(GridStyle.CIRCLE))
            return this.m.getRow() * this.m.getCol();
        else if(this.style.equals(GridStyle.CIRCLE_BRICKED))
            return this.m.getRow() * this.m.getCol() / 2;
        return 0;
    }

    @Override
    public int countAdjacentMines(int i, int j){
        int mineCount = 0;
        int r, c;
        if(this.style.equals(GridStyle.RECT)){
            for(ImmutablePair<Integer, Integer> temp : this.mineRange.get(GridStyle.RECT)){
                r = temp.getLeft();
                c = temp.getRight();
                if(this.m.withinBounds(i + r, j + c) && this.m.getEntry(i + r, j + c).isMine())
                    ++mineCount;
            }
            return mineCount;
        } else if(this.style.equals(GridStyle.CIRCLE)){
            if(i == 1) {
                for (ImmutablePair<Integer, Integer> temp : this.mineRange.get(GridStyle.CIRCLE).stream().filter(p -> !p.getLeft().equals(-1)).collect(Collectors.toUnmodifiableList())) {
                    r = temp.getLeft();
                    c = temp.getRight();
                    if (this.m.getEntryWrappable(i + r, j + c).isMine())
                        ++mineCount;
                }
            } else if(i == this.m.getRow()){
                for (ImmutablePair<Integer, Integer> temp : this.mineRange.get(GridStyle.CIRCLE).stream().filter(p -> !p.getLeft().equals(1)).collect(Collectors.toUnmodifiableList())) {
                    r = temp.getLeft();
                    c = temp.getRight();
                    if (this.m.getEntryWrappable(i + r, j + c).isMine())
                        ++mineCount;
                }
            } else {
                for(ImmutablePair<Integer, Integer> temp : this.mineRange.get(GridStyle.CIRCLE_BRICKED)){
                    r = temp.getLeft();
                    c = temp.getRight();
                    if(this.m.getEntry(i + r, j + c).isMine())
                        ++mineCount;
                }
            }
        } else if(this.style.equals(GridStyle.CIRCLE_BRICKED)){
            if(i == 1){
                for(ImmutablePair<Integer, Integer> temp : this.mineRange.get(GridStyle.CIRCLE_BRICKED).stream().filter(p -> !p.getLeft().equals(-1)).collect(Collectors.toUnmodifiableList())) {
                    r = temp.getLeft();
                    c = temp.getRight();
                    if (this.m.getEntryWrappable(i + r, j + c).isMine())
                        ++mineCount;
                }
            } else if(i == this.m.getRow()){
                    for(ImmutablePair<Integer, Integer> temp : this.mineRange.get(GridStyle.CIRCLE_BRICKED).stream().filter(p -> !p.getLeft().equals(1)).collect(Collectors.toUnmodifiableList())){
                        r = temp.getLeft();
                        c = temp.getRight();
                        if (this.m.getEntryWrappable(i + r, j + c).isMine())
                            ++mineCount;
                    }
            } else {
                for(ImmutablePair<Integer, Integer> temp : this.mineRange.get(GridStyle.CIRCLE_BRICKED)){
                    r = temp.getLeft();
                    c = temp.getRight();
                    if(this.m.getEntry(i + r, j + c).isMine())
                        ++mineCount;
                }
            }
        }

        return mineCount;
    }


    //MUTATORS
    public void setGridStyle(GridStyle gs){
        this.style = gs;
        return;
    }

    //classic rectangle minesweeper field
    public boolean createRectangleField(int row, int col, int numMines){
        GridStyle oldStyle = this.style;
        Matrix<Cell> oldMatrix = this.m;

        this.style = GridStyle.RECT;
        this.renew(row, col, new Cell(GameState.ZERO));
        if(numMines > this.getPlayableSize() || numMines < 0) {
            this.style = oldStyle;
            this.m = oldMatrix;
            return false;
        }

        int mineCount = 0;
        int index, r, c;
        Random rand = new Random();
        while (mineCount < numMines) {
            index = rand.nextInt(this.m.getRow() * this.m.getCol());
            r = this.m.getCoordinatesFromFlattened(index + 1).getLeft();
            c = this.m.getCoordinatesFromFlattened(index + 1).getRight();
            if (!this.m.getEntry(r, c).isMine()) {
                this.m.getEntry(r, c).setMine();
                ++mineCount;
            }
        }

        for (int i = 1; i <= this.m.getRow(); i++)
            for (int j = 1; j <= this.m.getCol(); j++)
                if (!this.m.getEntry(i, j).isMine())
                    this.m.getEntry(i, j).cellstate.setAdjacentMines(this.countAdjacentMines(i, j));

        return true;
    }

    //cells arranged in circular arcs layered around the center like rings on a tree trunk
    //if bricked, the radial line of symmetry of each cell aligns with the border between the
    //2 cells above and 2 cells below
    public boolean createCircleField(int layers, int cellsPerLayer, int numMines){
        GridStyle oldStyle = this.style;
        Matrix<Cell> oldMatrix = this.m;

        this.style = GridStyle.CIRCLE;
        this.renew(layers, cellsPerLayer, new Cell(GameState.ZERO));

        if(numMines > this.getPlayableSize() || numMines < 0) {
            this.style = oldStyle;
            this.m = oldMatrix;
            return false;
        }

        int mineCount = 0;
        int index, r, c;
        Random rand = new Random();
        while (mineCount < numMines) {
            index = rand.nextInt(this.m.getRow() * this.m.getCol());
            r = this.m.getCoordinatesFromFlattened(index + 1).getLeft();
            c = this.m.getCoordinatesFromFlattened(index + 1).getRight();
            if (!this.m.getEntry(r, c).isMine()) {
                this.m.getEntry(r, c).setMine();
                ++mineCount;
            }
        }

        for (int i = 1; i <= this.m.getRow(); i++)
            for (int j = 1; j <= this.m.getCol(); j++)
                if (!this.m.getEntry(i, j).isMine())
                    this.m.getEntry(i, j).cellstate.setAdjacentMines(this.countAdjacentMines(i, j));

        return true;
    }

    public boolean createBrickedCircleField(int layers, int cellsPerLayer, int numMines){
        GridStyle oldStyle = this.style;
        Matrix<Cell> oldMatrix = this.m;

        this.style = GridStyle.CIRCLE_BRICKED;
        this.renew(layers, 2 * cellsPerLayer, new Cell(GameState.ZERO));
        for(int i = 1; i <= this.m.getRow(); i++)
            for(int j = 1; j <= this.m.getCol(); j++)
                if(i % 2 != j % 2)
                    this.m.getEntry(i, j).cellstate.setGameState(GameState.NULL);

        if(numMines > this.getPlayableSize() || numMines < 0){
            this.style = oldStyle;
            this.m = oldMatrix;
            return false;
        }

        FastSortedMap<Integer, FastTable<Integer>> compactMatrix = new FastSortedMap<>();
        for(int i = 1; i <= layers; i++){
            if(i % 2 == 1){
                FastTable<Integer> temp = new FastTable<>();
                temp.addAll(IntStream.rangeClosed(1, 2 * cellsPerLayer).filter(num -> num % 2 == 1).boxed().collect(Collectors.toUnmodifiableList()));
                compactMatrix.put(i, temp);
            } else {
                FastTable<Integer> temp = new FastTable<>();
                temp.addAll(IntStream.rangeClosed(2, 2 * cellsPerLayer).filter(num -> num % 2 == 0).boxed().collect(Collectors.toUnmodifiableList()));
                compactMatrix.put(i, temp);
            }
        }

        int mineCount = 0;
        int r = 0, c = 0;
        Random rand = new Random();
        while (mineCount < numMines){
            r = compactMatrix.keySet().stream().collect(Collectors.toUnmodifiableList()).get(rand.nextInt(compactMatrix.keySet().size()));
            c = compactMatrix.get(r).remove(rand.nextInt(compactMatrix.get(r).size()));
            if(compactMatrix.get(r).size() == 0)
                compactMatrix.remove(r);
            this.m.getEntry(r, c).setMine();
            ++mineCount;
        }

        for (int i = 1; i <= this.m.getRow(); i++)
            for (int j = 1; j <= this.m.getCol(); j++)
                if (this.m.getEntry(i, j).cellstate.getGameState().equals(GameState.ZERO))
                    this.m.getEntry(i, j).cellstate.setAdjacentMines(this.countAdjacentMines(i, j));

        return true;
    }

    //MEMBER VARIABLES
    private GridStyle style;
    private final FastSortedMap<GridStyle, FastTable<ImmutablePair<Integer, Integer>>> mineRange = new FastSortedMap<>();
}

//grid with equilateral triangle cells
class TriCellGrid<G> extends Grid<G> {
    //ENUMS
    private enum GridStyle{
        ACCORDION,
        TRIANGLE;
    }

    //CONSTRUCTORS
    public TriCellGrid(G gridID, int row, int col, Cell val){
        super(gridID, row, col, val);
        this.style = GridStyle.ACCORDION;
    }

    //ACESSORS
    @Override
    public int getPlayableSize(){
        if(this.style.equals(GridStyle.ACCORDION))
            return this.m.getRow() * this.m.getCol();
        else if(this.style.equals(GridStyle.TRIANGLE))
            return (int) Math.pow(this.m.getRow(), 2);
        return 0;
    }

    @Override
    public int countAdjacentMines(int i, int j) {
        //tells the potential points of a mine relative to coordinate (i, j) given whether triangle cell at (i, j) has base as top or bottom side
        final FastTable<ImmutablePair<Integer, Integer>> mineCheckPointsTop = new FastTable<>();
        final FastTable<ImmutablePair<Integer, Integer>> mineCheckPointsBottom = new FastTable<>();

        mineCheckPointsTop.add(ImmutablePair.of(0, 1));
        mineCheckPointsTop.add(ImmutablePair.of(0, -1));
        mineCheckPointsTop.add(ImmutablePair.of(-1, 0));
        mineCheckPointsBottom.addAll(mineCheckPointsTop.stream().filter(p -> p.getLeft().equals(0)).collect(Collectors.toUnmodifiableList()));
        mineCheckPointsBottom.add(ImmutablePair.of(1, 0));

        int mineCount = 0;
        int r = 0, c = 0;
        if (this.style.equals(GridStyle.ACCORDION) || (this.style.equals(GridStyle.TRIANGLE) && this.m.getRow() % 2 == 0)) {
            if (i % 2 == j % 2)
                for (ImmutablePair<Integer, Integer> temp : mineCheckPointsTop) {
                    r = temp.getLeft();
                    c = temp.getRight();
                    if (this.m.withinBounds(i + r, j + c) && this.m.getEntry(i + r, j + c).isMine())
                        ++mineCount;
            } else if (i % 2 != j % 2)
                for (ImmutablePair<Integer, Integer> temp : mineCheckPointsBottom){
                    r = temp.getLeft();
                    c = temp.getRight();
                    if (this.m.withinBounds(i + r, j + c) && this.m.getEntry(i + r, j + c).isMine())
                        ++mineCount;
                }
        } else if(this.style.equals(GridStyle.TRIANGLE) && this.m.getRow() % 2 == 1){
            if(i % 2 != j % 2)
                for (ImmutablePair<Integer, Integer> temp : mineCheckPointsTop) {
                    r = temp.getLeft();
                    c = temp.getRight();
                    if (this.m.getEntry(i + r, j + c).isMine())
                        ++mineCount;
                }
            else if(i % 2 == j % 2)
                for (ImmutablePair<Integer, Integer> temp : mineCheckPointsBottom){
                    r = temp.getLeft();
                    c = temp.getRight();
                    if (this.m.withinBounds(i + r, j + c) && this.m.getEntry(i + r, j + c).isMine())
                        ++mineCount;
                }
        }

        return mineCount;
    }

    //given an equilateral triangle field created by the function createTriangleField, finds the coordinates of the k^th
    //non-null cell in sequence from the top vertex to the bottom right vertex of the field
    public ImmutablePair<Integer, Integer> getCoordinatesFromTriCellGrid(int side, int k){
        int n = 1;
        while(k - this.arithmeticPartialSum(1, 2, n) > this.arithmeticSequenceTerm(1, 2, n + 1))
            ++n;
        int row = n + 1, col = (side - row) + (k - this.arithmeticPartialSum(1, 2, n));
        return ImmutablePair.of(row, col);
    }

    //MUTATORS
    public boolean createAccordionFieldField(int row, int col, int numMines){
        GridStyle oldStyle = this.style;
        Matrix<Cell> oldMatrix = this.m;

        this.renew(row, col, new Cell(GameState.ZERO));
        this.style = GridStyle.ACCORDION;
        if(numMines > this.getPlayableSize() || numMines < 0) {
            this.style = oldStyle;
            this.m = oldMatrix;
            return false;
        }

        FastTable<Integer> availablePos = new FastTable<>();
        availablePos.addAll(IntStream.rangeClosed(1, this.getPlayableSize()).boxed().collect(Collectors.toUnmodifiableList()));

        int mineCount = 0;
        int pos = 0, r = 0, c = 0;
        Random rand = new Random();
        while (mineCount < numMines) {
            pos = availablePos.remove(rand.nextInt(availablePos.size()));
            r = this.m.getCoordinatesFromFlattened(pos).getLeft();
            c = this.m.getCoordinatesFromFlattened(pos).getRight();
            this.m.getEntry(r, c).setMine();
            ++mineCount;
        }

        for (int i = 1; i <= this.m.getRow(); i++)
            for (int j = 1; j <= this.m.getCol(); j++)
                if (!this.m.getEntry(i, j).isMine())
                    this.m.getEntry(i, j).cellstate.setAdjacentMines(this.countAdjacentMines(i, j));

        return true;
    }

    public boolean createTriangleField(int side, int numMines){
        GridStyle oldStyle = this.style;
        Matrix<Cell> oldMatrix = this.m;

        int startPos = 1, endPos = 2 * side - 1;
        this.renew(side, endPos);
        this.style = GridStyle.TRIANGLE;
        if(numMines > this.getPlayableSize() || numMines < 0){
            this.style = oldStyle;
            this.m = oldMatrix;
            return false;
        }

        for(int layer = side; layer >= 1; layer--) {
            for(int j = startPos; j <= endPos; j++)
                this.m.setEntry(layer, j, new Cell(GameState.ZERO));
            ++startPos;
            --endPos;
        }

        FastTable<Integer> availablePos = new FastTable<>();
        availablePos.addAll(IntStream.rangeClosed(1, this.getPlayableSize()).boxed().collect(Collectors.toUnmodifiableList()));

        int mineCount = 0;
        int pos = 0, r = 0, c = 0;
        Random rand = new Random();
        while(mineCount < numMines){
            pos = availablePos.remove(rand.nextInt(availablePos.size()));
            r = this.getCoordinatesFromTriCellGrid(side, pos).getLeft();
            c = this.getCoordinatesFromTriCellGrid(side, pos).getRight();
            this.m.getEntry(r, c).setMine();
            ++mineCount;
        }
        for (int i = 1; i <= this.m.getRow(); i++)
            for (int j = 1; j <= this.m.getCol(); j++)
                if(this.m.getEntry(i, j).cellstate.getGameState().equals(GameState.ZERO))
                    this.m.getEntry(i, j).cellstate.setAdjacentMines(this.countAdjacentMines(i, j));

        return true;
    }

    //MEMBER VARIABLES
    GridStyle style;

}

//rectangular grid, but each cell can access info from 6 adjacent cells
class HexCellGrid<G> extends Grid<G> {
    //ENUM
    private enum Orientation{
        ROW_MAJOR,
        COL_MAJOR;
    }

    //CONSTRUCTORS
    public HexCellGrid(G gridID, int row, int col, Cell val){
        super(gridID, row, col, val);
        this.direction = Orientation.ROW_MAJOR;

        FastTable<ImmutablePair<Integer, Integer>> row_major = new FastTable<>();
        FastTable<ImmutablePair<Integer, Integer>> col_major = new FastTable<>();

        row_major.add(ImmutablePair.of(0, 2));
        row_major.add(ImmutablePair.of(0, -2));
        row_major.add(ImmutablePair.of(-1, 1));
        row_major.add(ImmutablePair.of(1, 1));
        row_major.add(ImmutablePair.of(-1, -1));
        row_major.add(ImmutablePair.of(1, -1));

        col_major.add(ImmutablePair.of(-2, 0));
        col_major.add(ImmutablePair.of(2, 0));
        col_major.add(ImmutablePair.of(-1, 1));
        col_major.add(ImmutablePair.of(1, 1));
        col_major.add(ImmutablePair.of(-1, -1));
        col_major.add(ImmutablePair.of(1, -1));

        this.mineRange.put(Orientation.ROW_MAJOR, row_major);
        this.mineRange.put(Orientation.COL_MAJOR, col_major);
    }

    public HexCellGrid(G gridID, int row, int col, Cell val, Orientation o){
        this(gridID, row, col, val);
        this.direction = o;
    }

    //ACCESSORS
    @Override
    public int getPlayableSize(){
        return this.m.getRow() * this.m.getCol() / 2;
    }

    @Override
    public int countAdjacentMines(int i, int j){
        int mineCount = 0;
        int r = 0, c = 0;
        if(this.direction.equals(Orientation.ROW_MAJOR))
            for(ImmutablePair<Integer, Integer> temp : mineRange.get(Orientation.COL_MAJOR)){
                r = temp.getLeft();
                c = temp.getRight();
                if(this.m.withinBounds(i + r, j + c) && this.m.getEntry(i + r, j + c).isMine())
                    ++mineCount;
            }
        else
            for(ImmutablePair<Integer, Integer> temp : this.mineRange.get(Orientation.COL_MAJOR)){
                r = temp.getLeft();
                c = temp.getRight();
                if(this.m.withinBounds(i + r, j + c) && this.m.getEntry(i + r, j + c).isMine())
                    ++mineCount;
            }
        return mineCount;
    }

    public Orientation getOrientation(){
        return this.direction;
    }

    //returns an ordered pair coordinate of the k^th cell in a flattened HexCellGrid,
    //with sequence starting top left cell and ending bottom right cell
    public ImmutablePair<Integer, Integer> getCoordinatesFromHexCellGrid(int row, int cellsPerRow, int k){
        int r = (k % cellsPerRow == 0) ? k / cellsPerRow : k / cellsPerRow + 1;
        int rectC = (k % cellsPerRow == 0) ? cellsPerRow : k % cellsPerRow;
        int c = (row % 2 == 0) ? rectC * 2 : rectC * 2 - 1;
        return ImmutablePair.of(r, c);
    }


    //MUTATORS
    public boolean createHoneycombField(int row, int cellsPerRow, int numMines, Orientation o){
        Orientation oldDirection = this.direction;
        Matrix<Cell> oldMatrix = this.m;

        this.renew(row, cellsPerRow * 2);
        for(int i = 1; i <= this.m.getRow(); i++)
            for(int j = 1; j <= this.m.getCol(); j++)
                if(i % 2 == j % 2)
                    this.m.getEntry(i, j).cellstate.setGameState(GameState.ZERO);
        this.direction = o;
        if(numMines > this.getPlayableSize() || numMines < 0){
            this.direction = oldDirection;
            this.m = oldMatrix;
            return false;
        }

        FastTable<Integer> availablePos = new FastTable<>();
        availablePos.addAll(IntStream.rangeClosed(1, this.getPlayableSize()).boxed().collect(Collectors.toUnmodifiableList()));
        int mineCount = 0;
        int pos = 0, r = 0, c = 0;
        Random rand = new Random();
        while(mineCount < numMines){
            pos = availablePos.remove(rand.nextInt(availablePos.size()));
            r = this.getCoordinatesFromHexCellGrid(row, cellsPerRow, pos).getLeft();
            c = this.getCoordinatesFromHexCellGrid(row, cellsPerRow, pos).getRight();
            this.m.getEntry(r, c).setMine();
            ++mineCount;
        }

        for(int i = 1; i <= this.m.getRow(); i++)
            for(int j = 1; j <= this.m.getCol(); j++)
                if(this.m.getEntry(i, j).cellstate.getGameState().equals(GameState.ZERO))
                    this.m.getEntry(i, j).cellstate.setAdjacentMines(this.countAdjacentMines(i, j));

        return true;
    }

    //MEMBER VARIABLES
    private Orientation direction;
    public final FastSortedMap<Orientation, FastTable<ImmutablePair<Integer, Integer>>> mineRange = new FastSortedMap<>(); 

}
