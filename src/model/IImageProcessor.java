package model;

import java.util.List;

import controller.Filter;

/**
 * Serves as the "model" in a model-view-controller design of an image processing program.  Includes
 * methods for editing a loaded image file.  Also includes methods for generating stock images,
 * which can be edited before saving.  Implementation note: the model validates all arguments
 * received by the controller, and the controller is only responsible for parsing input for valid
 * commands with string arguments.
 */
public interface IImageProcessor {
  /**
   * Returns a boolean indicating if image data is currently loaded into the model.  Can be used to
   * to warn a user if they are about to overwrite an edited image in the model, or tries to execute
   * an image transformation operation without loading an image.
   *
   * @return a boolean, true if the model has an image, otherwise false.
   */
  boolean hasImage();

  /**
   * Returns the model's image data as a 3D array representing a 24-bit image, in the form:
   * [rows][columns][RGB channels]
   *
   * @return a 3D representing the image pixels.
   * @throws IllegalStateException if there is no image data to return.
   */
  int[][][] getImage() throws IllegalStateException;

  /**
   * Returns the height of the model's image data, in pixels.
   *
   * @return the height of the model's image data, in pixels.
   * @throws IllegalStateException if there is no image data to return.
   */
  int getImageHeight() throws IllegalStateException;

  /**
   * Returns the width of the model's image data, in pixels.
   *
   * @return the width of the model's image data, in pixels.
   * @throws IllegalStateException if there is no image data to return.
   */
  int getImageWidth() throws IllegalStateException;

  /**
   * Loads an image into the model, overwriting any previous image data. Users should use hasImage()
   * to check if an image is already loaded first.
   *
   * @param imagePixels a 3D array of pixels representing an image.
   * @throws IllegalArgumentException if image does not conform to standards of a 24-bit image with
   *                                  3 color channels for RGB.
   */
  void loadImage(int[][][] imagePixels) throws IllegalArgumentException;

  /**
   * Returns the valid Filter enum corresponding with the provided string filename. Returns null if
   * the string does not correspond to a valid filter type.
   *
   * @param filterName the name of the filter value to be checked.
   * @return the corresponding filter enum
   */
  Filter getFilter(String filterName);

  /**
   * Applies the specified filter (e.g. blur, greyscale, etc.) to the currently loaded image.  See
   * controller.Filter for supported filter operations and utilities.FilterUtil for implementation
   * details.
   *
   * @param filter a Filter specifying the type of filter to be applied.
   * @param args   A list of arguments which may be referenced by the corresponding filter method
   *               next called.  See utilities.FilterUtil.
   * @throws IllegalStateException    if there is no loaded image to filter.
   * @throws IllegalArgumentException if such an exception is thrown by a called method.  See
   *                                  utilities.FilterUtil for how filter arguments are used.
   */
  void filterImage(Filter filter, List<String> args)
          throws IllegalStateException, IllegalArgumentException;

  /**
   * Generates a rainbow image of user-specified size width, height, and orientation. Rainbow
   * orientation can be vertical or horizontal.  Height and width are specified in pixels, and must
   * be greater than 6. The rainbow image will be stored in the model - to be edited or saved.
   *
   * @param args A list of arguments needed to generate a rainbow. Must include the following, in
   *             order: the specified orientation - vertical or horizontal; the width of the rainbow
   *             in pixels; and the height of the rainbow in pixels.
   * @throws IllegalArgumentException If the required arguments are not provided or the pixel count
   *                                  for height or width are not greater than 6.
   */
  void generateRainbow(List<String> args) throws IllegalArgumentException;

  /**
   * Generates an 8 x 8 black and white square checkerboard pursuant to the provided square size -
   * the square size specifies the side length in pixels of each square in the board. The board will
   * be stored in the model - to be edited or saved.
   *
   * @param args A list of arguments for the checkerboard method. args[0] must be the side length in
   *             pixels for each of the 64 squares in the board.
   * @throws IllegalArgumentException if square size is less than 1.
   */
  void generateCheckerboard(List<String> args) throws IllegalArgumentException;

  /**
   * Attempts to revert the state of the image to a previous state.
   *
   * @throws IllegalStateException if there are no more states in the deque or the pop'd element
   *                               is an EmptyImage.
   */
  void undo() throws IllegalStateException;

  /**
   * Attempts to undo an undo() call. For it to be successful, an undo() must have been the previous
   * call made (i.e., undo, blur, redo will not work).
   *
   * @throws IllegalStateException if there are no more states in the deque.
   */
  void redo() throws IllegalStateException;


}
