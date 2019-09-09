import java.awt.CardLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.collections4.list.FixedSizeList;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class App {
    //ENUMS
    private enum Difficulty{
        Easy,
        Normal,
        Hard;

        public static String[] asStringArray(){
            return Arrays.stream(Difficulty.values()).map(d -> d.toString()).collect(Collectors.toUnmodifiableList()).toArray(String[]::new);
        }
    }

    //MEMBER VARIABLES
    public static final Table numRows = Table.create("Row Size").addColumns(
            StringColumn.create("Difficulty", Difficulty.asStringArray()),
            DoubleColumn.create("SquareCellGrid", new int[]{6, 10, 10}),
            DoubleColumn.create("TriCellGrid", new int[]{6, 7, 9}),
            DoubleColumn.create("HexCellGrid", new int[]{6, 10, 10})
    );

    public static final numCols = Table.create("Column Size");
//            DoubleColumn.create("SquareCellGrid", new int[]{6, 6, 8}),
//            DoubleColumn.create("TriCellGrid", new int[]{11, 13, 17}),
//            DoubleColumn.create("HexCellGrid", new int[]{6, 6, 8})


    private static int level = 1;
    private static Difficulty difficulty = Difficulty.Normal;

    //MEMBER FUNCTIONS
    public static int getLevel(){
        return level;
    }

    public static boolean setLevel(int l){
        if(l >= 1 && l <= 4)
            return false;
        level = l;
        return true;
    }


    public App(){
        //set of pages with different frames
        CardLayout card = new CardLayout(12, 12);

        //HOME PAGE
        JFrame homeFrame = new JFrame();

        JLabel title = new JLabel("Minesweeper Deluxe");
        title.setBounds(0, 0, 200, 20);
        homeFrame.add(title);

        FixedSizeList<JButton> buttonList = FixedSizeList.fixedSizeList(Arrays.asList(new JButton[4]));
        buttonList.set(0, new JButton("Play"));
        buttonList.set(1, new JButton("Level"));
        buttonList.set(2, new JButton("Settings"));
        buttonList.set(3, new JButton("Exit"));
        for(int i = 1; i <= buttonList.size(); i++) {
            buttonList.get(i - 1).setBounds(40, i * 20, 120, 20);
            homeFrame.add(buttonList.get(i - 1));
        }

        homeFrame.setSize(200, 200);
        homeFrame.setLayout(null);
        homeFrame.setVisible(true);
        card.addLayoutComponent(homeFrame, null);
        card.show(homeFrame, null);

        //LEVEL 1: SQUARE CELL GRID
        JFrame frame_1 = new JFrame();

        JLabel title_1 = new JLabel("Classic Minesweeper");
        title_1.setBounds(0, 0, 200, 20);
        frame_1.add(title_1);

        SquareCellGrid<String> minefield_1 = new SquareCellGrid<>("Level 1", 12, 8, new Cell(GameState.ZERO));
        minefield_1.createRectangleField(12, 8, 48);
        GridLayout grid_1 = new GridLayout(12, 8);
        for(int i = 1; i <= minefield_1.getPlayableSize(); i++)
            grid_1.addLayoutComponent("Cell " + String.valueOf(i), new JButton(String.valueOf(i)));

        //LEVEL 2
        JFrame frame_2 = new JFrame();
        GridLayout grid_2;
        grid_2 = new GridLayout(numRows.get()







        //LEVEL 3



    }

    public static void main(String[] args) {
        App app = new App();
    }


}