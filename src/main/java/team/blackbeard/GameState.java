package team.blackbeard;

import team.blackbeard.calc.Board;

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
    private Board.Backdrop game = null;
    public static Color[] getAllPossiblePixelColors(){
        return new Color[]{Main.white, Main.green, Main.purple, Main.yellow};
    }

    public void init(){
        State[][] board = getBoard();

        game = new Board.Backdrop();

        game.findPixel(0,0).type = Board.PIXEL_TYPE.GREEN;
        game.findPixel(1,0).type = Board.PIXEL_TYPE.GREEN;
        game.findPixel(1,1).type = Board.PIXEL_TYPE.GREEN;

        for(Board.Pixel pixel : game.pixels){
            State state = State.Empty;
            switch (pixel.type){
                case GREEN:
                    state = State.Green;
                    break;
                case PURPLE:
                    state = State.Purple;
                    break;
                case YELLOW:
                    state = State.Yellow;
                case WHITE:
                    state = State.White;
                case NONE:
                    state = State.Empty;
            }
            board[pixel.x][pixel.y] = state;
        }


        System.out.println("Initialized!");
    }

    public void start() throws InterruptedException {
        System.out.println("Started!");

        System.out.println(game.score());
    }




    private State[][] getBoard(){
        return Main.getInstance().board;
    }

    private void setBoard(State[][] board){
        Main.getInstance().board = board;
        Main.getInstance().update();
    }

}
