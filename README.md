## Color Quantization 

This is a Java implementation of the median cut algorithm to perform color quantization. 
It compresses images via a reduction of the color space of the image.
It takes in images as .raw files and outputs their compressed versions as .bmp files.

## Running Projectt

The program requires 2 command-line arguments.

Arg 0: 
- The path (absolute or relative) to the .raw file you want compressed.
- Note, in order to work properly, the .raw filenames have a specific naming convention as follows:
- Picture_WidthxHeight.raw
- Picture can be any string, excluding underscores and dots, as they are used to parse out the relevant pieces of data from the filename.
- Width should be the width in number of pixels of the raw image.
- Height should be the height in number of pixels of the raw image.
- The filename should include the .raw extension.
- There are examples of the naming convention applied to raw image files in the input directory of this project

Arg 1:
- The total number of colors wanted in the compressed photo's color palette.
- This can either be a single number or a range in the format number-number with the larger number coming after the hyphen.
- All numbers must be powers of 2.
- If you put just a single number it could look like: 8 or 32.
- If you put a range it could look like: 4-16 or 32-128.
- For a range, the program will output a file for all powers of two contained within the range inclusive of the specified endpoints.