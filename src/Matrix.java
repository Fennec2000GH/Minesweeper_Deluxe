import java.util.Arrays;
import java.util.ArrayList;
import org.apache.commons.collections4.list.FixedSizeList;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class Matrix<E> implements Cloneable {
    //CONSTRUCTORS
    public Matrix(int row, int col, E val) {
        this.row = row;
        this.col = col;
        this.matrix = FixedSizeList.fixedSizeList(new ArrayList<>(this.row));
        for(int i = 1; i <= this.row; i++)
            this.matrix.set(i - 1, FixedSizeList.fixedSizeList(new ArrayList<>(this.col)));
        this.setAll(val);
    }

    public Matrix(Matrix<E> obj){
        this.row = obj.row;
        this.col = obj.col;
        this.matrix = obj.matrix;
    }

    //ACCESSORS
    public E getEntry(int i, int j){
        if (i > this.row || j > this.col)
            return null;
        return this.matrix.get(i - 1).get(j - 1);
    }

    //allows pairs (i, j) to be out of bounds of matrix, and i, j can be negative values by wrapping around the size of the matrix
    public E getEntryWrappable(int i, int j){
        if(i == 0 || j == 0)
            return null;
        int r = i % this.row, c = j % this.col;
        if(r < 0)
            r = r + this.row + 1;
        if(c < 0)
            c = c + this.col + 1;
        return this.getEntry(r, c);
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    //returns the i^th row
    public FixedSizeList<E> getWholeRow(int i){
        return this.matrix.get(i - 1);
    }

    //returns the j^th column
    public FixedSizeList<E> getWholeColumn(int j){
        ArrayList<E> step1 = new ArrayList<>();
        for(int i = 1; i <= this.row; i++)
            step1.add(this.matrix.get(i - 1).get(j - 1));
        FixedSizeList<E> step2 = FixedSizeList.fixedSizeList(step1);
        return step2;
    }

    public ImmutablePair<Integer, Integer> getDimensions() {
        return ImmutablePair.of(this.row, this.col);
    }

    //given the k^th entry from flattened matrix, retrieves the position coordinates in 2D
    public ImmutablePair<Integer, Integer> getCoordinatesFromFlattened(int k) {
        if (k > this.row * this.col)
            return null;
        int x = (k % this.row == 0) ? (k / this.col) : (k / this.col + 1);
        int y = (k % this.row == 0) ? (this.col) : (k % this.col);
        return ImmutablePair.of(x, y);
    }

    public boolean withinBounds(int i, int j){
        return (i <= this.row && j <= this.col);
    }

    //MUTATORS
    public boolean setEntry(int i, int j, E val){
        if(i > this.row || j > this.col)
            return false;
        this.matrix.get(i - 1).set(j - 1, val);
        return this.matrix.get(i - 1).get(j - 1).equals(val);
    }

    public boolean setEntryWrappable(int i, int j, E val){
        if(i == 0 || j == 0)
            return false;
        int r = i % this.row, c = j % this.col;
        if(r < 0)
            r = r + this.row + 1;
        if(c < 0)
            c = c + this.col + 1;
        return this.setEntry(r, c, val);
    }

    public void setAll(E val) {
        for(int i = 1; i <= this.row; i++)
            for(int j = 1; j <= this.col; j++)
                this.setEntry(i, j, val);
        return;
    }

    public void clear() {
        this.setAll(null);
    }

    //MEMBER VARIABLES
    private int row , col;
    private FixedSizeList<FixedSizeList<E>> matrix;

}