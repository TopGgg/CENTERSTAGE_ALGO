package team.blackbeard;

import javax.imageio.stream.ImageInputStream;
import java.awt.*;

public class GameState {

    private Minimax minimax = null;
    public static Color[] getAllPossiblePixelColors(){
        return new Color[]{Main.white, Main.green, Main.purple, Main.yellow};
    }

    public void init(){
        minimax = new Minimax();
        Color[][] board = getBoard();
        board[0][0] = Main.green;
        board[1][0] = Main.green;
        board[2][1] = Main.green;
        board[3][1] = Main.green;
        board[3][0] = Main.green;
        board[2][0] = Main.green;
        board[5][0] = Main.green;
        board[4][0] = Main.green;
        board[4][1] = Main.green;
//        board[1][1] = Main.green;
        System.out.println("Initialized!");
    }

    public void start(){
        System.out.println("Started!");
        Minimax.Action[] actions = minimax.getAllActions(getBoard());
        Color[][] board = getBoard();
        for(Minimax.Action action : actions){
            int[] coordinate = action.coordinate;
            board[coordinate[0]][coordinate[1]] = Main.purple;
        }
        setBoard(board);
    }




    private Color[][] getBoard(){
        return Main.getInstance().board;
    }

    private void setBoard(Color[][] board){
        Main.getInstance().board = board;
        Main.getInstance().update();
    }

}
