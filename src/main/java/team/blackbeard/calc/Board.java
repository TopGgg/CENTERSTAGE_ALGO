package team.blackbeard.calc;

import java.util.ArrayList;
import java.util.Arrays;

public class Board {
    public enum PIXEL_TYPE {
        NONE("N"),
        WHITE("W"),
        YELLOW("Y"),
        PURPLE("P"),
        GREEN("G");
        final String type;
        PIXEL_TYPE(String type){
            this.type = type;
        }
        @Override
        public String toString(){
            return type;
        }
    }

    public static class VALID_MOSAICS {
        public static final PIXEL_TYPE[] GREEN = {PIXEL_TYPE.GREEN,PIXEL_TYPE.GREEN,PIXEL_TYPE.GREEN};
        public static final PIXEL_TYPE[] PURPLE = {PIXEL_TYPE.PURPLE,PIXEL_TYPE.PURPLE,PIXEL_TYPE.PURPLE};
        public static final PIXEL_TYPE[] YELLOW = {PIXEL_TYPE.YELLOW,PIXEL_TYPE.YELLOW,PIXEL_TYPE.YELLOW};
        public static final PIXEL_TYPE[] MIXED = {PIXEL_TYPE.GREEN,PIXEL_TYPE.PURPLE,PIXEL_TYPE.YELLOW};

        public static ArrayList<PIXEL_TYPE[]> getAsList(){
            ArrayList<PIXEL_TYPE[]> list = new ArrayList<>();
            list.add(GREEN);
            list.add(PURPLE);
            list.add(YELLOW);
            list.add(MIXED);
            return list;
        }
    }

    public static final int TOTAL_ROWS = 11;
    public static final int LONG_ROW_LENGTH = 7;
    public static final int POINTS_PER_PIXEL = 3;
    public static final int POINTS_PER_MOSAIC = 10;
    public static final int POINTS_PER_SET_LINE = 10;
    public static final int[] SET_LINES = {2, 5, 8};

    public static class Pixel {
        public int index;
        public int x;
        public int y;
        public PIXEL_TYPE type = PIXEL_TYPE.NONE;
        public int rowLength;
        public ArrayList<Pixel> neighbors = new ArrayList<>();
        public boolean inMosaic = false;

        public Pixel(int index, int x, int y, int rowLength){
            this.index = index;
            this.x = x;
            this.y = y;
            this.rowLength = rowLength;
        }

        public boolean isMosaic(){
            // Don't bother if this pixel is already in a mosaic, not populated, or not a colored pixel.
            if (inMosaic || type == PIXEL_TYPE.NONE || type == PIXEL_TYPE.WHITE)
            {
                return false;
            }

            ArrayList<Pixel> coloredNeighbors = new ArrayList<>();
            for (int i = 0; i < neighbors.size(); i++)
            {
                if (neighbors.get(i).type != PIXEL_TYPE.NONE && neighbors.get(i).type != PIXEL_TYPE.WHITE)
                {
                    coloredNeighbors.add(neighbors.get(i));
                }
            }

            // If there are more or less than two colored neighbors, then this pixel is not part of a mosaic.
            if (coloredNeighbors.size() != 2)
            {
                return false;
            }

            // Start tracking the colors forming this mosaic.
            ArrayList<String> colors = new ArrayList<>();
            colors.add(type.toString());

            // If the 2 colored neighbors also only have two colored neighbors, then this may be a mosaic.
            for (int i = 0; i < coloredNeighbors.size(); i++)
            {
                Pixel neighbor = coloredNeighbors.get(i);

                ArrayList<Pixel> neighborsColoredNeighbors = new ArrayList<>();
                for (int j = 0; j < neighbor.neighbors.size(); j++)
                {
                    if (neighbor.neighbors.get(j).type != PIXEL_TYPE.NONE && neighbor.neighbors.get(j).type != PIXEL_TYPE.WHITE)
                    {
                        neighborsColoredNeighbors.add(neighbor.neighbors.get(j));
                    }
                }

                if (neighborsColoredNeighbors.size() != 2)
                {
                    return false;
                }

                colors.add(neighbor.type.toString());
            }

            // Verify that the color combination forms a valid mosaic.
            String[] colorsArray = new String[colors.size()];
            colorsArray = colors.toArray(colorsArray);
            Arrays.sort(colorsArray);

            for (PIXEL_TYPE[] mosaicType : VALID_MOSAICS.getAsList())
            {
                String colorString = colorsArray[0]+colorsArray[1]+colorsArray[2];
                String mosaicString = mosaicType[0].toString()+mosaicType[1].toString()+mosaicType[2].toString();
                if (colorString.equals(mosaicString))
                {
                    // Mark all pixels as in a mosaic before proceeding.
                    inMosaic = true;
                    for (int i = 0; i < coloredNeighbors.size(); i++)
                    {
                        coloredNeighbors.get(i).inMosaic = true;
                    }

                    return true;
                }
            }

            return false;
        }
    }

    public static class Backdrop {

        public ArrayList<Pixel> pixels = new ArrayList<>();

        public Backdrop()
        {
            for (int i = 0; i < TOTAL_ROWS; i++)
            {
                int rowLength = LONG_ROW_LENGTH - ((i + 1) % 2);

                for (int j = 0; j < rowLength; j++)
                {
                    pixels.add(new Pixel(pixels.size(), j, i, rowLength));
                }
            }

            // Set the neighbors for each pixel.
            for (int i = 0; i < pixels.size(); i++)
            {
                Pixel pixel = pixels.get(i);

                // Left
                if (pixel.x != 0)
                {
                    pixel.neighbors.add(pixels.get(i - 1));
                }

                // Right
                if (pixel.x != (pixel.rowLength - 1))
                {
                    pixel.neighbors.add(pixels.get(i + 1));
                }

                // Bottom
                if (pixel.y != 0)
                {
                    if (pixel.rowLength == LONG_ROW_LENGTH)
                    {
                        if (pixel.x != 0)
                        {
                            pixel.neighbors.add(pixels.get(i - LONG_ROW_LENGTH));
                        }

                        if (pixel.x != (pixel.rowLength - 1))
                        {
                            pixel.neighbors.add(pixels.get(i - (pixel.rowLength - 1)));
                        }
                    }
                    else
                    {
                        pixel.neighbors.add(pixels.get(i - (LONG_ROW_LENGTH - 1)));
                        pixel.neighbors.add(pixels.get(i - LONG_ROW_LENGTH));
                    }
                }

                // Top
                if (pixel.y != (TOTAL_ROWS - 1))
                {
                    if (pixel.rowLength == LONG_ROW_LENGTH)
                    {
                        if (pixel.x != 0)
                        {
                            pixel.neighbors.add(pixels.get(i + (LONG_ROW_LENGTH - 1)));
                        }

                        if (pixel.x != (LONG_ROW_LENGTH - 1))
                        {
                            pixel.neighbors.add(pixels.get(i + LONG_ROW_LENGTH));
                        }
                    }
                    else
                    {
                        pixel.neighbors.add(pixels.get(i + (LONG_ROW_LENGTH - 1)));
                        pixel.neighbors.add(pixels.get(i + LONG_ROW_LENGTH));
                    }
                }
            }
        }

        public int score()
        {
            // Clear the inMosaic flags for the pixels.
            for (int i = 0; i < this.pixels.size(); i++)
            {
                pixels.get(i).inMosaic = false;
            }

            int totalPixels = 0;
            int pixelScore = 0;

            int highestRow = -1;
            int totalSetLines = 0;
            int setLineScore = 0;

            int mosaicCount = 0;
            int mosaicScore = 0;

            for (int i = 0; i < this.pixels.size(); i++)
            {
                if (pixels.get(i).type != PIXEL_TYPE.NONE)
                {
                    // Score each pixels individually.
                    totalPixels++;

                    // Update the highest row if necessary.
                    if (pixels.get(i).y > highestRow)
                    {
                        highestRow = pixels.get(i).y;
                    }

                    // Increment the mosaic counter if necessary.
                    if (pixels.get(i).isMosaic())
                    {
                        mosaicCount++;
                    }
                }
            }

            // Calculate the score.
            pixelScore = totalPixels * POINTS_PER_PIXEL;
            mosaicScore = mosaicCount * POINTS_PER_MOSAIC;

            for (int i = 0; i < SET_LINES.length; i++)
            {
                if (highestRow >= SET_LINES[i])
                {
                    totalSetLines++;
                }
            }

            setLineScore = totalSetLines * POINTS_PER_SET_LINE;

            return pixelScore + setLineScore + mosaicScore;
        }

        public Pixel findPixel(int x, int y){
            for(Pixel pixel : pixels){
                if(pixel.x == x && pixel.y == y){
                    return pixel;
                }
            }
            return null;
        }
    }

}
