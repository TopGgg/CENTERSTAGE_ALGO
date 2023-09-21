package team.blackbeard;

import java.util.ArrayList;

public class Utils {

    public static int[] pointToIntArr(GameState.Point point){
        return new int[]{point.x,point.y};
    }
    public static GameState.Point IntArrToPoint(int[] arr){
        return new GameState.Point(arr[0], arr[1]);
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {
        ArrayList<T> newList = new ArrayList<T>();

        for (T element : list) {
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        return newList;
    }


}
