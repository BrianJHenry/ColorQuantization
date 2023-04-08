/*
 * Author: Brian Henry
 * Project: Color quantization
 * Purpose: Reduce color space according to median cut algorithm in .raw photos; output compressed photos as .bmp files
 * ColorReducer class
 */

// class object: takes in an array of pixels as RGB class objects and an integer for the number of colors desired in the final color palette
public class ColorReducer {

    // primary and secondary array of pixel values from raw image; secondary is necessary for merge sort implementation
    private RGB[] RGBArray;
    private RGB[] secondaryRGBArray;

    // 
    private RGB[] colorPalette;
    private int paletteIndex; //keeps track of where in the color palette the program should be adding coloras
    private int numberOfColors; //the number of colors given in the command line (will be power of 2)

    // constructor
    public ColorReducer(RGB[] container, int numberOfColors) {
        this.RGBArray = container;
        secondaryRGBArray = new RGB[this.RGBArray.length];
        this.numberOfColors = numberOfColors;
        this.colorPalette = new RGB[numberOfColors];
        this.paletteIndex = 0;
    }
        
    // recursive method to progressively cut the color space in half along the channel with the widest value range
    public void medianCut(int startIndex, int endIndex, int depthOfRecursion) {

        // base case
        if (depthOfRecursion <= 0) {

            //finds the average color for the box
            int averageR = (findColorMax(startIndex, endIndex, 'R') + findColorMin(startIndex, endIndex, 'R')) / 2;
            int averageG = (findColorMax(startIndex, endIndex, 'G') + findColorMin(startIndex, endIndex, 'G')) / 2;
            int averageB = (findColorMax(startIndex, endIndex, 'B') + findColorMin(startIndex, endIndex, 'B')) / 2;
            RGB newColor = new RGB(averageR, averageG, averageB);

            // place the color in the output palette and iterate to the next index to continue building the list
            colorPalette[paletteIndex] = newColor;
            paletteIndex ++;
            return;
        }

        // find which color channel has the largest range
        char largestRange = findLargestRange(startIndex, endIndex);

        // sorts according to the color channel with the largest range
        mergeSort(startIndex, endIndex, largestRange);

        // find median of color box once the pixels are sorted
        int middle = (endIndex + startIndex) / 2;

        // recursively call median cut on the two halves
        medianCut(startIndex, middle, depthOfRecursion - 1);
        medianCut(middle + 1, endIndex, depthOfRecursion - 1);
    }

    // method to access output color palette
    public RGB[] getColorPalette() {
        return colorPalette;
    }

    // method to
    public int getPaletteIndex() {
        return paletteIndex;
    }

    //computes the power of 2 that the number of colors is equivalent to (ex. 8 returns 3, 32 returns 5)
    public int colorDepth() {
        int numColors = numberOfColors;
        int depthNum = 0;
        while (numColors > 1) {
            numColors = numColors / 2;
            depthNum ++;
        }
        return depthNum;
    }

    // method to find maximum value for the specified color channel
    // searches for max among RGBs between (inclusive) the start and end indices
    public int findColorMax(int startIndex, int endIndex, char color) {
        int max = RGBArray[startIndex].getChannel(color);
        for (int i = startIndex + 1; i <= endIndex; i++) {
            if (RGBArray[i].getChannel(color) > max) {
                max = RGBArray[i].getChannel(color);
            }
        }
        return max;
    }

    // method to find minimum value for the specified color channel
    // searches for min among RGBs between (inclusive) the start and end indices
    public int findColorMin(int startIndex, int endIndex, char color) {
        int min = RGBArray[startIndex].getChannel(color);
        for (int i = startIndex + 1; i <= endIndex; i++) {
            if (RGBArray[i].getChannel(color) < min) {
                min = RGBArray[i].getChannel(color);
            }
        }
        return min;
    }
    
    // method to find the color channel with the largest value range within the start and end indices
    public char findLargestRange(int startIndex, int endIndex) {

        // finds the range of each color channel within the portion of the list we are looking at
        int rRange = findColorMax(startIndex, endIndex, 'R') - findColorMin(startIndex, endIndex, 'R');
        int gRange = findColorMax(startIndex, endIndex, 'G') - findColorMin(startIndex, endIndex, 'G');
        int bRange = findColorMax(startIndex, endIndex, 'B') - findColorMin(startIndex, endIndex, 'B');

        // returns the color with the largest range
        if (rRange >= gRange && rRange >= bRange) {
            return 'R';
        }
        else if (gRange >= bRange) {
            return 'G';
        }
        else {
            return 'B';
        }
    }

    // recursive method to perform mergesort
    void mergeSort(int startIndex, int endIndex, char color)
    {
        // checks if the size of the piece of the array to be sorted is greater than 1
        if (startIndex < endIndex) {

            // finds the middle index
            int middle = startIndex + (endIndex - startIndex) / 2;

            // calls mergeSort recursively on the two halves
            mergeSort(startIndex, middle, color);
            mergeSort(middle + 1, endIndex, color);

            // merges the two halves
            merge(startIndex, middle + 1, endIndex, color);
        }
    }

    // merge helper function for mergeSort
    void merge(int leftStartIndex, int rightStartIndex, int endIndex, char color) {

        // counter starting at the beginning of the left side that is to be merged
        int leftCounter = leftStartIndex;

        // counter at the start of the beginning of the right side that is to be sorted
        int rightCounter = rightStartIndex;

        // tracks where in the secondaryRGBArray the values should be placed
        int currIndex = leftStartIndex;

        // checks to see if the array sides are empty
        boolean endPassed = false;
        boolean middlePassed = false;

        // while either of the two arrays to be merged have unused elements
        while(!endPassed || !middlePassed) {

            // checks if the left side is empty
            if (middlePassed) {
                secondaryRGBArray[currIndex] = RGBArray[rightCounter];
                rightCounter++;
                currIndex++;
            }

            // checks if the right side is empty
            else if (endPassed) {
                secondaryRGBArray[currIndex] = RGBArray[leftCounter];
                leftCounter++;
                currIndex++;
            }

            // if both sides still have elements, compare the values between the two arrays
            else {
                if (RGBArray[leftCounter].compareToChannel(RGBArray[rightCounter], color) == -1) {
                    secondaryRGBArray[currIndex] = RGBArray[leftCounter];
                    leftCounter++;
                }
                else {
                    secondaryRGBArray[currIndex] = RGBArray[rightCounter];
                    rightCounter++;
                }
                currIndex++;
            }

            // check to see if either array has been emptied by the previous step
            if (leftCounter >= rightStartIndex) {
                middlePassed = true;
            }
            if (rightCounter >= (endIndex + 1)) {
                endPassed = true;
            }
        }  

        // writes the now sorted values back into the RGBArray class variable
        for (int i = leftStartIndex; i <= endIndex; i++) {
            RGBArray[i] = secondaryRGBArray[i];
        }
    }

    // method to fill out any spots in the palette that may have been left unfilled
    // prevents crashes if the original .raw image had fewer colors than were specified to be used in the palette
    public void fillOutPalette() {
        for (int i = 0; i < numberOfColors; i++) {
            if (colorPalette[i] == null) {
                colorPalette[i] = new RGB(0, 0, 0);
            }
        }
    }
}
