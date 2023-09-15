package team.blackbeard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Minimax {
    static class Action {
        public Color color;
        public int[] coordinate;

        public Action(Color color, int[] coordinate){
            this.color = color;
            this.coordinate = coordinate;
        }
    }

    public Action[] getAllActions(Color[][] b){
        ArrayList<Action> actions = new ArrayList<>();
        for (int y = 0; y < 13; y++){
            for (int x = 0; x < 7; x++){
                if(y % 2 == 0 && x == 6)
                    continue;
                if (y == 0 || (x == 0 && b[0][Math.max(y-1,0)] != null && y % 2 != 0) || (x == (y % 2 == 0 ? 5 : 6) && b[y % 2 != 0 ? 5 : 6][Math.max(y-1,0)] != null && y % 2 != 0)){ //y >= 0
                    if(b[x][y] == null){
                        for(Color c : GameState.getAllPossiblePixelColors()){
                            actions.add(new Action(c, new int[]{x,y}));
                        }

                    }
                }else if(b[y % 2 != 0 ? Math.max(x-1,0) : x+1][Math.max(y-1,0)] != null && b[x][Math.max(y-1,0)] != null){ //y > 0
                    if(b[x][y] == null){
                        for(Color c : GameState.getAllPossiblePixelColors()){
                            actions.add(new Action(c, new int[]{x,y}));
                        }
                    }
                }
            }
        }
        return actions.toArray(new Action[0]);
    }

    public Color[][] applyAction(Color[][] board, Action action){
        board[action.coordinate[0]][action.coordinate[1]] = action.color;
        return board;
    }



}