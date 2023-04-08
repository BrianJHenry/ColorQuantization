/*
 * Author: Brian Henry
 * Project: Color quantization
 * Purpose: Reduce color space according to median cut algorithm in .raw photos; output compressed photos as .bmp files
 * Main method
 */

import java.io.*;

public class App {

    // helper function to write .bmp format header and body
    public static void writeBMPFile(int width, int height, int numberOfColors, RGB[] colorPalette, int [][] compressedPixelArray, String outputFileName) throws IOException {


        FileOutputStream os = new FileOutputStream(outputFileName);
        DataOutputStream output = new DataOutputStream(os);

        try (output) {

            byte[] twoByteArray = new byte[2];
            twoByteArray[0] = 'B';
            twoByteArray[1] = 'M';
            output.write(twoByteArray);

            int fileSize = width*height;
            output.writeInt(fileSize);

            short zero = 0;
            output.writeShort(Short.reverseBytes(zero));
            output.writeShort(Short.reverseBytes(zero));

            //54 bytes in the first and second part of the header + the number of bytes in the color table
            int offsetToPixelData = 54 + numberOfColors * 4;
            output.writeInt(Integer.reverseBytes(offsetToPixelData));

            int headerSize = 40;
            output.writeInt(Integer.reverseBytes(headerSize));

            //width
            output.writeInt(Integer.reverseBytes(width));

            //height
            output.writeInt(Integer.reverseBytes(height));

            short one = 1;
            output.writeShort(Short.reverseBytes(one));

            short bitsPerChannel = 8;
            output.writeShort(Short.reverseBytes(bitsPerChannel));

            int compressionType = 0;
            output.writeInt(Integer.reverseBytes(compressionType));

            int compression = 0;
            output.writeInt(Integer.reverseBytes(compression));

            //x resolution
            output.writeInt(Integer.reverseBytes(width));

            //y resolution
            output.writeInt(Integer.reverseBytes(height));

            //number of colors
            output.writeInt(Integer.reverseBytes(numberOfColors));

            //number of significant colors
            output.writeInt(Integer.reverseBytes(numberOfColors));

            //array of bytes to help build the color chart
            byte[] fourBytes = new byte[4];

            for (int i = 0; i < numberOfColors; i++) {

                //when you showed yours in class I think I remember seeing something like B, then G, then R. Im not sure
               fourBytes[0] = (byte) colorPalette[i].getB();
               fourBytes[1] = (byte) colorPalette[i].getG();
               fourBytes[2] = (byte) colorPalette[i].getR();
               fourBytes[3] = (byte) 0;
               output.write(fourBytes);
            }

            byte [] bytePixelArray = new byte[width*height];
            int bytePixelArrayIndex = 0;

            //writes the pixel info into a byte array to write to the file
            for (int h = height - 1; h >= 0; h--) {
                for (int w = 0; w < width; w++) {

                    bytePixelArray[bytePixelArrayIndex] = (byte) compressedPixelArray[h][w];
                    bytePixelArrayIndex ++;

                }
            }
            output.write(bytePixelArray);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        output.close();

    }

    /*
     * Main method
     * Reads from .raw file as specified
     * Instantiates object of ColorReducer class and reduces colors down to desired palette
     * Writes output to .bmp file
     * Args:
     * file/directory flag (f or d), number/range flag (n or r), 
     * path (file or directory as indicated by flag), palette size (number or range as indicated by flag)
     */
    public static void main(String[] args) throws Exception {

        if (args.length != 4) {
            System.out.println("Please re-run with the correct number of arguments.");
            System.out.println("Example: scriptName fileFlag rangeFlag path paletteSize");
        }

        // flag for whether the script should be executed for a single .raw file, or a directory of .raw files
        String fileFlag = args[0];
        String numberFlag = args[1];
        String path = args[2];
        


    }
}
