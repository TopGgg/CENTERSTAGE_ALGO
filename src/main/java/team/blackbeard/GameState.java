package team.blackbeard;

import team.blackbeard.calc.Board;

import java.awt.*;
import java.util.ArrayList;
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
//        game.findPixel(1,1).type = Board.PIXEL_TYPE.GREEN;

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


        int currentScore = game.score();
        Board.Action[] possibleActions = game.getPossibleActions();
        int maxScore = currentScore;
        Board.Action[] maxScoreActions = null;
        for(Board.Action action : possibleActions){
            Board.Pixel pixel = game.findPixel(action.x, action.y);
            Board.PIXEL_TYPE type = pixel.type;
            pixel.type = action.type;
            for(Board.Action action1 : game.getPossibleActions()){
                Board.Pixel pixel1 = game.findPixel(action1.x, action1.y);
                Board.PIXEL_TYPE type1 = pixel1.type;
                pixel1.type = action1.type;
                for (Board.Action action2 : game.getPossibleActions()){
                    Board.Pixel pixel2 = game.findPixel(action2.x, action2.y);
                    Board.PIXEL_TYPE type2 = pixel2.type;
                    pixel2.type = action2.type;
                    int score = game.score();
                    if(score > maxScore){
                        maxScore = score;
                        maxScoreActions = new Board.Action[] {action, action1, action2};
                    }
                    game.findPixel(action2.x, action2.y).type = type2;
                }
                game.findPixel(action1.x, action1.y).type = type1;
            }
            game.findPixel(action.x, action.y).type = type;
        }

        if(maxScoreActions != null){
            System.out.println(maxScore);
            System.out.println(Arrays.toString(maxScoreActions));
            game.findPixel(maxScoreActions[0].x, maxScoreActions[0].y).type = maxScoreActions[0].type;
            game.findPixel(maxScoreActions[1].x, maxScoreActions[1].y).type = maxScoreActions[1].type;
            game.findPixel(maxScoreActions[2].x, maxScoreActions[2].y).type = maxScoreActions[2].type;
        }

        State[][] board = getBoard();

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

        Main.getInstance().update();
    }




    private State[][] getBoard(){
        return Main.getInstance().board;
    }

    private void setBoard(State[][] board){
        Main.getInstance().board = board;
        Main.getInstance().update();
    }

}
