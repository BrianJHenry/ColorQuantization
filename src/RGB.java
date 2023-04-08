/*
 * Author: Brian Henry
 * Project: Color quantization
 * Purpose: Reduce color space according to median cut algorithm in .raw photos; output compressed photos as .bmp files
 * RGB class
 */

// class object: represents one pixel with red, green, and blue channels
public class RGB {
    
    private int R;
    private int G;
    private int B;

    // constructor
    public RGB(int R, int G, int B) {
        this.R = R;
        this.G = G;
        this.B = B;
    }

    // access methods
    public int getR() {
        return R;
    }

    public int getG() {
        return G;
    }

    public int getB() {
        return B;
    }

    // access method allowing the color channel to be specified as a character argument
    public int getChannel(char color) {
        if (color == 'R') {
            return R;
        } else if (color == 'G') {
            return G;
        } else {
            return B;
        }
    }

    // comparison methods
    public int compareToR(RGB pxl) {
        if (this.R > pxl.R) {
            return 1;
        } else if (this.R == pxl.R) {
            return 0;
        } else {
            return -1;
        }
    }

    public int compareToG(RGB pxl) {
        if (this.G > pxl.G) {
            return 1;
        } else if (this.G == pxl.G) {
            return 0;
        } else {
            return -1;
        }
    }

    public int compareToB(RGB pxl) {
        if (this.B > pxl.B) {
            return 1;
        }
        else if (this.B == pxl.B) {
            return 0;
        }
        else {
            return -1;
        }
    }

    public int compareToChannel(RGB pxl, char color) {
        if (this.getChannel(color) > pxl.getChannel(color)) {
            return 1;
        }
        else if (this.getChannel(color) == pxl.getChannel(color)) {
            return 0;
        }
        else {
            return -1;
        }
    }

    // method to find the mean average squared distance between two pixels
    public int meanSqdDist(RGB pxl) {
        return (this.R - pxl.R)*(this.R - pxl.R) + (this.G - pxl.G)*(this.G - pxl.G) + (this.B - pxl.B)*(this.B - pxl.B);
    }

    // method to print the contents of the RGB object
    public void printData() {
        System.out.println("R: " + this.R + "\tG: " + this.G + "\tB: " + this.B);
    }
}
