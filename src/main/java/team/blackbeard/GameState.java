package team.blackbeard;

import java.awt.*;
import java.util.Arrays;

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
//        board[0][0] = State.Green;
//        board[1][0] = State.Green;
//        board[2][1] = State.Green;
//        board[3][1] = State.Green;
//        board[3][0] = State.Green;
//        board[2][0] = State.Green;
//        board[5][0] = State.Green;
//        board[4][0] = State.Green;
//        board[4][1] = State.Green;
//        board[4][2] = State.Green;
//        board[4][3] = State.Green;
        board[0][0] = State.Green;
        board[1][0] = State.Green;
        board[1][1] = State.Green;
        System.out.println("Initialized!");
    }

    public void start() throws InterruptedException {
        System.out.println("Started!");
        HexagonSorter.Action[] actions = hexagonSorter.getAllActions(getBoard());
        State[][] board = getBoard();
//        for(HexagonSorter.Action action : actions){
//            int[] coordinate = action.coordinate;
//            board[coordinate[0]][coordinate[1]] = State.Purple;
//        }
//        new Thread(()->{
//            for(int y = 0; y < 12; y++){
//                for(int x = 0; x < 7; x++){
//                    board[x][y] = State.Green;
//                    for(Point point : Reward.getNeighbors(new int[]{x,y})){
//                        board[point.x][point.y] = State.Purple;
//                    }
//                    setBoard(board);
//                    Main.getInstance().update();
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    for(int i = 0; i < 12; i++){
//                        for(int j = 0; j < 7; j++){
//                            board[j][i] = State.Empty;
//                        }
//                    }
//                }
//            }
//        }).start();

        System.out.println(Reward.calcMosaics(board));

    }




    private State[][] getBoard(){
        return Main.getInstance().board;
    }

    private void setBoard(State[][] board){
        Main.getInstance().board = board;
        Main.getInstance().update();
    }

}
