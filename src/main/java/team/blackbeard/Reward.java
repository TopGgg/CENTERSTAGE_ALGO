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

        @Override
        public String toString(){
            return "("+pixel1[0]+","+pixel1[1]+"),"+"("+pixel2[0]+","+pixel2[1]+"),"+"("+pixel3[0]+","+pixel3[1]+")";
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
        return 10*(Math.min(11, maxHeight+1)/3);
    }

    public static ArrayList<GameState.Point> getNeighbors(GameState.State[][] b, int[] point){
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
                if(x+p.x < 0 || y+p.y < 0 || y + p.y > 11 || x + p.x > (((y+p.y) % 2==0) ? 5 : 6) || b[x+p.x][y+p.y] == GameState.State.Empty)
                    continue;
                neighbors.add(new GameState.Point(x+p.x, y + p.y));
            }

        }else {
            GameState.Point[] directions = {
                    new GameState.Point(-1, -1),
                    new GameState.Point(0, -1),
                    new GameState.Point(-1, 0),
                    new GameState.Point(-1, +1),
                    new GameState.Point(0, 1),
                    new GameState.Point(1, 0),
                    new GameState.Point(0, -1)

            };

            for(GameState.Point p : directions){
                if(x+p.x < 0 || y+p.y < 0 || y + p.y > 11 || x + p.x > (((y+p.y) % 2==0) ? 5 : 6) || b[x+p.x][y+p.y] == GameState.State.Empty)
                    continue;
                neighbors.add(new GameState.Point(x+p.x, y + p.y));
            }
        }
        return neighbors;
    }

    public static boolean isMosaic(GameState.State[][] b, Mosaic mosaic){
        int x1 = mosaic.pixel1[0];
        int y1 = mosaic.pixel1[1];
        int x2 = mosaic.pixel2[0];
        int y2 = mosaic.pixel2[1];
        int x3 = mosaic.pixel3[0];
        int y3 = mosaic.pixel3[1];
        return b[x1][y1] != GameState.State.White && b[x2][y2] != GameState.State.White && b[x3][y3] != GameState.State.White && ((b[x1][y1] == b[x2][y2] && b[x1][x2] == b[x3][y3]) ||
                (b[x1][y1] != b[x2][y2] && b[x1][y1] != b[x3][y3])) ;
    }


    public static int calcMosaics(GameState.State[][] b){
        ArrayList<Mosaic> mosaics = new ArrayList<>();
        for (int x = 0; x < 7; x++){
            for(int y = 0; y < 12; y++){
                if(b[x][y] == GameState.State.Empty || (y % 2 == 0 && x ==6))
                    continue;
                int i = 0;
                ArrayList<GameState.Point> neighbors = getNeighbors(b, new int[]{x,y});
                for(GameState.Point p : neighbors){
                    if(i >= neighbors.size()-1)
                        break;

                    System.out.println(p);
                    GameState.Point nextP = neighbors.get(i + 1);
                    Mosaic mosaic = new Mosaic(Utils.pointToIntArr(nextP)
                            , new int[]{x,y}, Utils.pointToIntArr(p));
                    if(isMosaic(b, mosaic)){
                        mosaics.add(mosaic);
                    }
                    i++;
                }
            }
        }
        return mosaics.size()*10;
    }
}
