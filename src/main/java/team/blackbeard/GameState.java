package team.blackbeard;

import java.awt.*;

public class GameState {


    public enum State{
        Invalid(-1),
        Empty(0),
        White(0xFFFFF),
        Purple(0xae23d9),
        Green(0x1f8f03),
        Yellow(0xe8ac07);

        public int state;

        State(int state){
            this.state = state;
        }
    }

    static class Point{
        int x;
        int y;

        public Point(int x, int y){
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString(){
            return "("+x+","+y+")";
        }

    }
    private HexagonSorter hexagonSorter = null;
    public static Color[] getAllPossiblePixelColors(){
        return new Color[]{Main.white, Main.green, Main.purple, Main.yellow};
    }

    public void init(){
        hexagonSorter = new HexagonSorter();
        State[][] board = getBoard();
        board[0][0] = State.Green;
        board[1][0] = State.Green;
        board[2][1] = State.Green;
        board[3][1] = State.Green;
        board[3][0] = State.Green;
        board[2][0] = State.Green;
        board[5][0] = State.Green;
        board[4][0] = State.Green;
        board[4][1] = State.Green;
        board[4][2] = State.Green;
        board[4][3] = State.Green;
        board[4][4] = State.Green;
        board[4][5] = State.Green;
//        board[1][1] = Main.green;
        System.out.println("Initialized!");
    }

    public void start(){
        System.out.println("Started!");
        HexagonSorter.Action[] actions = hexagonSorter.getAllActions(getBoard());
        State[][] board = getBoard();
//        for(HexagonSorter.Action action : actions){
//            int[] coordinate = action.coordinate;
//            board[coordinate[0]][coordinate[1]] = State.Purple;
//        }
        System.out.println(Reward.getNeighbors(new int[]{2,4}));
        System.out.println(Reward.heightCalc(board));
        setBoard(board);
    }




    private State[][] getBoard(){
        return Main.getInstance().board;
    }

    private void setBoard(State[][] board){
        Main.getInstance().board = board;
        Main.getInstance().update();
    }

}
