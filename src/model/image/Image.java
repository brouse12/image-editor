package model.image;

import java.util.Arrays;

/**
 * Represents a 24-bit image (e.g., an image with three 8-bit color channels in red, green, and
 * blue).  Pixel data for the image is represented by a 3D array in the form: [rows][columns][color
 * channel]. Rows and columns start at 0 in the upper left corner and signify the location of
 * pixels. The color channels are a size 3 integer array representing the 3 color channels of a
 * pixel, in the form: [red][green][blue]. Colors are represented by 8 bits (i.e. an integer ranging
 * from 0 to 255 for each color). pixel, in the form: [red][green][blue]. Colors are represented by
 * 8 bits (i.e. an integer ranging from 0 to 255 for each color).
 */
public class Image implements IImage {

  /**
   * Maximum value for a color channel in a 24-bit image.
   */
  private static final int MAX_COLOR_VALUE = 255;

  /**
   * A 3D integer array representing the pixels in this image.
   */
  private int[][][] pixels;

  /**
   * Constructs an image using the provided pixel data for a 24-bit image. See class documentation
   * for formatting pixel data.
   *
   * @param image the image's pixel data, a 3D array.
   * @throws IllegalArgumentException if input is null, if input contains color values that are not
   *                                  between 0 and 255, or if image has no pixels or less than 3
   *                                  color channels.
   */
  public Image(int[][][] image) throws IllegalArgumentException {
    if (image == null) {
      throw new IllegalArgumentException("Cannot create an image with null pixel input.");
    }

    // Confirm at least one row in the image pixels
    if (image.length < 1) {
      throw new IllegalArgumentException(
              "A 24-bit image must have at least 1 pixel row.");
    }

    // Validate image size and color count
    for (int[][] column : image) {
      if (column.length < 1) {
        throw new IllegalArgumentException(
                "A 24-bit image must have at least 1 pixel column.");
      }
      for (int[] channel : column) {
        if (channel.length != 3) {
          throw new IllegalArgumentException(
                  "A 24-bit image must exactly 3 color channels.");
        }
        for (int color : channel) {
          if (color < 0 || color > MAX_COLOR_VALUE) {
            throw new IllegalArgumentException(
                    "Pixel values for a 24-bit image must be between 0 and 255.");
          }
        }
      }
    }
    this.pixels = image;
  }

  /**
   * Returns the width of the image in number of pixels.
   *
   * @return an int, the width of the image in number of pixels.
   */
  @Override
  public int getWidth() {
    return this.pixels[0].length;
  }

  /**
   * Returns the height of the image in number of pixels.
   *
   * @return an int, the height of the image in number of pixels.
   */
  @Override
  public int getHeight() {
    return this.pixels.length;
  }

  /**
   * Returns a copy of the pixel data for this image as a 3D integer array in the form:
   * [row][column][color channel].
   *
   * @return the pixel data for this image
   */
  @Override
  public int[][][] getPixels() {
    int[][][] copiedPixels
            = new int[this.getHeight()][this.getWidth()][3];

    for (int i = 0; i < this.getHeight(); i++) {
      for (int j = 0; j < this.getWidth(); j++) {
        copiedPixels[i][j] = Arrays.copyOf(this.pixels[i][j], this.pixels[i][j].length);
      }
    }
    return copiedPixels;
  }

  /**
   * Returns true, as this is not an EmptyImage.
   *
   * @return true
   */
  @Override
  public boolean hasPixels() {
    return true;
  }
}
