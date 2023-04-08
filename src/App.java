/*
 * Author: Brian Henry
 * Project: Color quantization
 * Purpose: Reduce color space according to median cut algorithm in .raw photos; output compressed photos as .bmp files
 * Main method
 */

import java.io.*;

public class App {

    public static boolean checkIsPower(int inputNumber, int baseNumber) {
        while (inputNumber > 1) {
            if (inputNumber % baseNumber != 0) {
                return false;
            }
            inputNumber = inputNumber / baseNumber;
        }
        return true;
    }

    /*
     * Main method
     * Reads from .raw file as specified
     * Instantiates object of ColorReducer class and reduces colors down to desired palette
     * Writes output to .bmp file
     * Args:
     * path to .raw file, palette size (number or range)
     */
    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("Please re-run with the correct number of arguments.");
            System.out.println("Example: scriptName fileFlag rangeFlag path paletteSize");
            return;
        }

        // path of the input directory or file
        String path = args[0];

        // number or range for the palette size (note: input integer(s) should be powers of 2)
        // Example: 8 or 8-32
        String[] paletteSizeArgs = args[1].split("-",2);
        int[] paletteSizeArray = new int[paletteSizeArgs.length];

        // check and parse paletteSize argument
        int i = 0;
        for (String stringSize : paletteSizeArgs) {
            int sizeInt;
            try {
                sizeInt = Integer.parseInt(stringSize);
            } catch (Exception e) {
                System.out.println("Please enter an integer (int) or range of integers (int-int) as the second argument.");
                return;
            }
            if (!checkIsPower(sizeInt, 2)) {
                System.out.println("Please enter power(s) of 2 as the second argument.");
                return;
            }
            paletteSizeArray[i] = sizeInt;
            i++;
        }
        if (paletteSizeArray.length == 2) {
            if (paletteSizeArray[0] > paletteSizeArray[1]) {
                System.out.println("Please enter a range where the first integer is less than or equal to the second.");
                return;
            }
        }

        // check .raw file and parse out relevant info
        // filename must be formatted as: name_widthxheight.raw

        // split path to separate filename
        String[] splitPath = path.split("/");
        String filename = splitPath[splitPath.length-1];

        String[] splitExtension = filename.split("\\.",2);
        if ((splitExtension.length == 1) ||  !splitExtension[splitExtension.length-1].equals("raw")) {
            System.out.println("Please enter a path with file ending in .raw extension.");
            return;
        }
        String[] splitDimensions = splitExtension[0].split("_",2);
        if (splitDimensions.length != 2) {
            System.out.println("Please enter enter a path with file in the format: path/name_widthxheight.raw.");
            System.out.println("Where width and height are numbers specifying the width and height of the .raw file in the filename.");
            return;
        }
        String[] splitWH = splitDimensions[1].split("x",2);
        if (splitWH.length != 2) {
            System.out.println("Please enter enter a path with file in the format: path/name_widthxheight.raw.");
            System.out.println("Where width and height are numbers specifying the width and height of the .raw file in the filename.");
            return;
        }
        int width;
        int height;
        try {
            width = Integer.parseInt(splitWH[0]);
            height = Integer.parseInt(splitWH[1]);
        } catch (Exception e) {
            System.out.println("Please enter enter a path with file in the format: path/name_widthxheight.raw.");
            System.out.println("Where width and height are numbers specifying the width and height of the .raw file in the filename.");
            return;
        }

        InputStream is = new FileInputStream(path);
        DataInputStream input = new DataInputStream(is);

        // 2d array to save pixel positions
        RGB[][] pixelArray = new RGB[height][width];

        // 1d array to save pixels for median cut algorithm
        RGB[] container = new RGB[height*width];

        //reads in pixel data to both arrays
        int index = 0;
        for(int h = 0; h < height; h++) {
            for(int w = 0; w < width; w++) {
                int R = input.readUnsignedByte();
                int G = input.readUnsignedByte();
                int B = input.readUnsignedByte();
                RGB pixel = new RGB(R, G, B);

                pixelArray[h][w] = pixel;

                container[index] = pixel;
                index++;
            }
        }
        input.close();

        // main loop
        // for each palette size specified in the range, perform medianCut algorithm and output to .bmp file
        for (int numColors = paletteSizeArray[0]; numColors <= paletteSizeArray[paletteSizeArray.length-1]; numColors *= 2){

            //creates median cutter object
            ColorReducer colorPaletteFinder = new ColorReducer(container, numColors);

            System.out.println();

            long startTime = System.nanoTime();

            //call to the median cut algorithm to find the colors for the color table
            colorPaletteFinder.medianCut();

            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;

            System.out.println("Total execution time of medianCut in milliseconds: " + timeElapsed / 1000000);

            System.out.println();

            System.out.println("Number of colors found: " + (colorPaletteFinder.getPaletteIndex()));

            //retrieves the color table
            RGB[] palette = colorPaletteFinder.getColorPalette();

            //allocate 2d array to contain color info relating pixels with location to color palette
            int [][] condensedPixelArray = new int[height][width];

            //loop through the original 2d pixel array
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {

                    //set starting index of the minimum mean squared distance
                    int minIndex = 0;

                    //loop through the color palette to find the minimum mean squared distance for the current pixel
                    for (i = 1; i < numColors; i++) {

                        //checks if the min mean sqd distance of the current color in color palette is smaller than the current smallest found so far
                        if (pixelArray[h][w].meanSqdDist(palette[i]) < pixelArray[h][w].meanSqdDist(palette[minIndex])) {

                            //if smaller then changes the minIndex to be the current index
                            minIndex = i;
                        }
                    }
                    //once the whole palette has been cycled through assigns the minimum sqd distance to the pixel location
                    condensedPixelArray[h][w] = minIndex;

                }
            }

            String outputFilePath = "./output/" + splitDimensions[0] + "_" + Integer.toString(numColors) + ".bmp";

            //calls method to write data to the bmp file
            BMPWriter outWriter = new BMPWriter(); 
            outWriter.writeBMPFile(width, height, numColors, palette, condensedPixelArray, outputFilePath);
        }
    }
}
