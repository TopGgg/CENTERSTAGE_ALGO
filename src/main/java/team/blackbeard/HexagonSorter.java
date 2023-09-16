package team.blackbeard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

public class HexagonSorter {
    static class Action {
        public GameState.State state;
        public int[] coordinate;

        public Action(GameState.State state, int[] coordinate){
            this.state = state;
            this.coordinate = coordinate;
        }
    }

    public Action[] getAllActions(GameState.State[][] b){
        ArrayList<Action> actions = new ArrayList<>();
        for (int y = 0; y < 12; y++){
            for (int x = 0; x < 7; x++){
                if(b[x][y] == GameState.State.Invalid)
                    continue;
                if(b[x][y] == GameState.State.Empty){
                    if(y == 0 ||
                            (x == 0 && b[x][Math.max(y-1, 0)] != GameState.State.Empty) ||
                            (x == 6 && b[Math.max(x-1, 0)][Math.max(y-1, 0)] != GameState.State.Empty) ||
                            (b[y % 2 != 0 ? Math.max(x-1, 0) : x+1][Math.max(y-1, 0)] != GameState.State.Empty && b[x][Math.max(y-1, 0)] != GameState.State.Empty)){
                        actions.add(new Action(GameState.State.Purple, new int[]{x,y}));
                    }
                }
            }
        }
        return actions.toArray(new Action[0]);
    }



    public GameState.State[][] applyAction(GameState.State[][] board, Action action){
        board[action.coordinate[0]][action.coordinate[1]] = action.state;
        return board;
    }



}