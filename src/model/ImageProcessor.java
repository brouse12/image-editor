package model;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;

import controller.Filter;
import model.image.EmptyImage;
import model.image.IImage;
import model.image.Image;
import model.utilities.FilterUtil;

/**
 * The implementation of the image processor model.  Holds one image at a time and performs editing
 * operations on it.  The image must be saved as an image file once edits are complete. Can also
 * generate an 8x8 checkerboard and a seven-color rainbow of a given size, which can be edited
 * further.  Implementation note: the model validates all arguments received by the controller, and
 * the controller is only responsible for parsing input for valid commands with string arguments.
 */
public class ImageProcessor implements IImageProcessor {

  /**
   * The number of squares in a checkerboard.
   */
  private final int CHECKERS_SQUARES = 8;

  /**
   * An array of RBG color values for the seven colors in a VIBGYOR rainbow.
   */
  private static final int[][] RAINBOW_COLORS = {
          {148, 0, 211},
          {75, 0, 130},
          {0, 0, 255},
          {0, 255, 0},
          {255, 255, 0},
          {255, 127, 0},
          {255, 0, 0}
  };

  /**
   * The undo() deque. Stores states before operations overwrite the current state.
   */
  private Deque<IImage> previousImageStates;

  /**
   * The redo() deque. Stores states before undo() calls are completed.
   */
  private Deque<IImage> nextImageStates;

  /**
   * The maximum number of states stored in the undo/redo deques. Can be changed for performance and
   * memory usage.
   */
  private static final int MAX_HISTORY = 15;

  /**
   * The image to be processed by the model.
   */
  private IImage image;

  /**
   * A constructor for an ImageProcessor. Initially sets the Image instance variable to an
   * EmptyImage object. User can load an image to replace this placeholder.
   */
  public ImageProcessor() {
    this.image = new EmptyImage();
    this.previousImageStates = new ArrayDeque<IImage>();
    this.nextImageStates = new ArrayDeque<>();
  }


  /**
   * Returns a boolean indicating if image data is currently loaded into the model.  Can be used to
   * to warn a user if they are about to overwrite an edited image in the model, or tries to execute
   * an image transformation operation without loading an image.
   *
   * @return a boolean, true if the model has an image, otherwise false.
   */
  @Override
  public boolean hasImage() {
    return this.image.hasPixels();
  }

  /**
   * Returns the model's image data as a 3D array representing a 24-bit image, in the form:
   * [rows][columns][RGB channels].
   *
   * @return a 3D representing the image pixels.
   * @throws IllegalStateException if there is no image data to return.
   */
  @Override
  public int[][][] getImage() throws IllegalStateException {
    return this.image.getPixels();
  }

  /**
   * Returns the height of the model's image data, in pixels.
   *
   * @return the height of the model's image data, in pixels.
   * @throws IllegalStateException if there is no image data to return.
   */
  @Override
  public int getImageHeight() throws IllegalStateException {
    return this.image.getHeight();
  }

  /**
   * Returns the width of the model's image data, in pixels.
   *
   * @return the width of the model's image data, in pixels.
   * @throws IllegalStateException if there is no image data to return.
   */
  @Override
  public int getImageWidth() throws IllegalStateException {
    return this.image.getWidth();
  }

  /**
   * Loads an image into the model, overwriting any previous image data. Users should use hasImage()
   * to check if an image is already loaded first.
   *
   * @param imagePixels a 3D array of pixels representing an image.
   * @throws IllegalArgumentException if image does not conform to standards of a 24-bit image with
   *                                  3 color channels for RGB.
   */
  @Override
  public void loadImage(int[][][] imagePixels) throws IllegalArgumentException {
    this.pushState();
    this.image = new Image(imagePixels);
  }

  /**
   * Returns the valid Filter enum corresponding with the provided string filename. Returns null if
   * the string does not correspond to a valid filter type.
   *
   * @param filterName the name of the filter value to be checked.
   * @return the corresponding filter enum
   */
  @Override
  public Filter getFilter(String filterName) {
    Filter filterType = null;
    for (Filter f : Filter.values()) {
      if (f.toString().equals(filterName)) {
        filterType = f;
        break;
      }
    }
    return filterType;
  }

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
  @Override
  public void filterImage(Filter filter, List<String> args)
          throws IllegalStateException, IllegalArgumentException {
    if (!this.image.hasPixels()) {
      throw new IllegalStateException("Please load an image before attempting a filter command.");
    }

    this.pushState();
    this.image = FilterUtil.filterDispatch(this.image, filter, args);
  }

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
  @Override
  public void generateRainbow(List<String> args) throws IllegalArgumentException {
    // parse & validate input, set variables
    if (args.size() < 3) {
      throw new IllegalArgumentException("Not enough arguments provided to "
              + "generate a rainbow.");
    }

    String orientation = args.get(0).toLowerCase();
    if (!(orientation.equals("vertical") || orientation.equals("horizontal"))) {
      throw new IllegalArgumentException(
              "Rainbow orientation must be either 'vertical' or 'horizontal'.");
    }
    boolean vertical = args.get(0).equals("vertical");
    int width;
    int height;
    try {
      width = Integer.parseInt(args.get(1));
      height = Integer.parseInt(args.get(2));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Specified width and height must be numbers.");
    }
    if (width < 7 || height < 7) {
      throw new IllegalArgumentException("Width and height must each be greater than 6 to generate "
              + "a 7-color rainbow.");
    }

    // declare some necessary variables
    int[][][] rainbow = new int[height][width][3];
    int extra;
    int baseLen;
    int baseSeven;

    // check if stripes are vertical or horizontal
    // calculate size of stripes accordingly. last stripe may be up to 6 pixels smaller if req'd.
    int split = vertical ? width : height;

    if (split % 7 != 0) {
      extra = split % 7;
      baseLen = (split + 7) / 7;
      baseSeven = baseLen - (7 - extra);
    } else {
      baseLen = split / 7;
      baseSeven = baseLen;
    }

    // for vertical stripes: iterate through each row, switching colors as appropriate.
    if (vertical) {
      for (int row = 0; row < height; row++) {
        for (int colorNum = 0; colorNum < 6; colorNum++) {
          for (int pixelNum = (baseLen * colorNum);
               pixelNum < (baseLen * (colorNum + 1)); pixelNum++) {
            rainbow[row][pixelNum] = Arrays.copyOf(RAINBOW_COLORS[colorNum], 3);
          }
        }
        for (int pixelNum = 6 * baseLen; pixelNum < (6 * baseLen) + baseSeven; pixelNum++) {
          rainbow[row][pixelNum] = Arrays.copyOf(RAINBOW_COLORS[6], 3);
        }
      }

      // for horizontal stripes: iterate row by row, each row's pixels being set to the same color,
      // and switching row colors as appropriate.
    } else {
      for (int colorNum = 0; colorNum < 6; colorNum++) {
        for (int row = (baseLen * colorNum); row < (baseLen * (colorNum + 1)); row++) {
          for (int pixelNum = 0; pixelNum < width; pixelNum++) {
            rainbow[row][pixelNum] = Arrays.copyOf(RAINBOW_COLORS[colorNum], 3);
          }
        }
      }
      for (int row = 6 * baseLen; row < (6 * baseLen) + baseSeven; row++) {
        for (int pixelNum = 0; pixelNum < width; pixelNum++) {
          rainbow[row][pixelNum] = Arrays.copyOf(RAINBOW_COLORS[6], 3);
        }
      }
    }
    // save resulting 3D array as an image
    this.pushState();
    this.image = new Image(rainbow);
  }

  /**
   * Generates an 8 x 8 black and white square checkerboard pursuant to the provided square size -
   * the square size specifies the side length in pixels of each square in the board. The board will
   * be stored in the model - to be edited or saved.
   *
   * @param args A list of arguments for the checkerboard method. args[0] must be the side length in
   *             pixels for each of the 64 squares in the board.
   * @throws IllegalArgumentException if square size is less than 1.
   */
  @Override
  public void generateCheckerboard(List<String> args) throws IllegalArgumentException {
    if (args.size() < 1) {
      throw new IllegalArgumentException("Square size not specified.");
    }

    int squareSize;
    try {
      squareSize = Integer.parseInt(args.get(0));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Requested checkerboard square size is not a number.");
    }
    if (squareSize < 1) {
      throw new IllegalArgumentException("Cannot generate a board with squares of size 0 or less.");
    }

    int sideLength = CHECKERS_SQUARES * squareSize;
    int[][][] board = new int[sideLength][sideLength][3];
    int[] whitePixel = {255, 255, 255};
    int[] blackPixel = {0, 0, 0};

    boolean isBlack = true;
    for (int i = 0; i < sideLength; i++) {
      if (i % squareSize == 0) {
        isBlack = !isBlack;
      }
      for (int j = 0; j < sideLength; j++) {
        if (j % squareSize == 0) {
          isBlack = !isBlack;
        }
        board[i][j] = isBlack ? Arrays
                .copyOf(blackPixel, 3) : Arrays.copyOf(whitePixel, 3);
      }
    }
    this.pushState();
    this.image = new Image(board);
  }


  /**
   * Attempts to revert the state of the image to a previous state. It will save this state first in
   * a 'redo' accessible deque. If the pop'd item is an EmptyImage, throws an exception and reverts
   * the image to its state before the undo() call. If there are no more states in the deque, again
   * throws an exception.
   *
   * @throws IllegalStateException if there are no more states in the deque or the pop'd element is
   *                               an EmptyImage.
   */
  @Override
  public void undo() throws IllegalStateException {
    this.nextImageStates.push(this.image);
    try {
      this.image = this.previousImageStates.pop();
      if (this.image.getClass() == EmptyImage.class) {
        this.previousImageStates.push(this.image);
        this.image = this.nextImageStates.pop();
        throw new IllegalStateException("Error: Cannot undo initial load.");
      }
    } catch (NoSuchElementException e) {
      this.image = this.nextImageStates.pop();
      throw new IllegalStateException("Error: Cannot undo any further.");
    }
  }

  /**
   * Attempts to undo an undo() call. For it to be successful, an undo() must have been the previous
   * call made (i.e., undo, blur, redo will not work).
   *
   * @throws IllegalStateException if there are no more states in the deque.
   */
  @Override
  public void redo() throws IllegalStateException {
    this.previousImageStates.push(this.image);
    try {
      this.image = this.nextImageStates.pop();
    } catch (NoSuchElementException e) {
      this.image = this.previousImageStates.pop();
      throw new IllegalStateException("Error: Nothing to redo.");
    }

  }

  /**
   * Pushes the current state of this.image to the previousStates deque. It is thus accessible via
   * and undo() call. If the number of states in the deque has reached MAX_HISTORY, the earliest
   * version is removed to make room for the most recent. Also resets the redo() deque.
   */
  private void pushState() {
    this.nextImageStates = new ArrayDeque<IImage>();
    this.previousImageStates.push(this.image);
    if (this.previousImageStates.size() == MAX_HISTORY) {
      this.previousImageStates.removeLast();
    }
  }


}
