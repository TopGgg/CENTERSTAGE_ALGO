package team.blackbeard;

import java.awt.*;
import java.util.ArrayList;

public class Reward {

    static class Mosaic{
        int[] pixel1;
        int[] pixel2;
        int[] pixel3;

        public Mosaic(int[] pixel1, int[] pixel2, int[] pixel3){
            this.pixel1 = pixel1;
            this.pixel2 = pixel2;
            this.pixel3 = pixel3;
        }
    }

    public static int heightCalc(GameState.State[][] b){
        int maxHeight = -1;
        for(int x = 0; x < 7; x++){
            for(int y = 0; y < 12; y++){
                if(b[x][y] != GameState.State.Empty && b[x][y] != GameState.State.Invalid){
                    maxHeight = Math.max(maxHeight, y);
                }
            }
        }
        return (int) (10*Math.floor(Math.min(11, maxHeight+1)/3));
    }

    public static ArrayList<GameState.Point> getNeighbors(int[] point){
        int x = point[0];
        int y = point[1];

        ArrayList<GameState.Point> neighbors = new ArrayList<>();

        if(y % 2 == 0){
            GameState.Point[] directions = {
                    new GameState.Point(1, 0),
                    new GameState.Point(-1, 0),
                    new GameState.Point(0, 1),
                    new GameState.Point(1, 1),
                    new GameState.Point(0, -1),
                    new GameState.Point(1, -1)
            };

            for(GameState.Point p : directions){
                if(x+p.x < 0 || y+p.y < 0 || y + p.y > 11 || x + p.x > 5)
                    continue;
                neighbors.add(new GameState.Point(x+p.x, y + p.y));
            }

        }
        return neighbors;
    }


    public static int calcMosaics(GameState.State[][] b){
        ArrayList<Mosaic> mosaics = new ArrayList<>();
        for (int x = 0; x < 7; x++){
            for(int y = 0; y < 12; y++){
                if(b[x][y] == GameState.State.Empty)
                    continue;
                if(y % 2 == 0){
                    if(b[x-1][y] != GameState.State.Empty){
                        if(b[x][y+1] != GameState.State.Empty){
                            if((b[x][y+1] == b[x-1][y] && b[x][y] == b[x][y+1]) || (b[x][y+1] != b[x-1][y] && b[x][y] != b[x][y+1])){
                                mosaics.add(new Mosaic(new int[]{x,y}, new int[]{x, y+1}, new int[]{x-1,y}));
                            }
                        }
                        if(b[x][y-1] != GameState.State.Empty){
                            if((b[x][y-1] == b[x-1][y] && b[x][y] == b[x][y-1]) || (b[x][y-1] != b[x-1][y] && b[x][y] != b[x][y-1])) {
                                mosaics.add(new Mosaic(new int[]{x, y}, new int[]{x, y - 1}, new int[]{x - 1, y}));
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }
}
