package model.image;

/**
 * Interface representing a generic image.  Contains methods for accessing an image's pixel data.
 * Throws exceptions for "empty images" - images classes that have been instantiated but do not
 * contain pixel data.
 */
public interface IImage {

  /**
   * Returns the width of the image in number of pixels.
   *
   * @return an int, the width of the image in number of pixels.
   * @throws IllegalStateException if the image does not have pixel data
   */
  int getWidth() throws IllegalStateException;

  /**
   * Returns the height of the image in number of pixels.
   *
   * @return an int, the height of the image in number of pixels.
   * @throws IllegalStateException if the image does not have pixel data
   */
  int getHeight() throws IllegalStateException;

  /**
   * Returns a copy of the pixel data for this image as a 3D integer array in the form:
   * [row][column][color channel].
   *
   * @return the pixel data for this image
   * @throws IllegalStateException if the image does not have pixel data
   */
  int[][][] getPixels() throws IllegalStateException;

  /**
   * Returns true if the image has usable pixel data, otherwise false.
   */
  boolean hasPixels();
}
