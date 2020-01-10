import java.util.List;

import controller.Filter;
import model.ImageProcessor;

/**
 * Variation of the ImageProcessor model that records a log of image editing actions taken, to be
 * used for testing.  This way, controller tests can confirm if the controller instructed the model
 * to perform an editing operation or not. Includes a getActionLog() method to obtain a string
 * version of the log.
 */
public class MockedModel extends ImageProcessor {

  // Logs actions taken by this mock, to be validated by tests.
  private String actionLog;

  /**
   * Constructs a mocked ImageProcessor model with a blank action log.
   */
  public MockedModel() {
    super();
    actionLog = "";
  }

  /**
   * Returns all the actions taken by this mocked model since instantiation, as a string.
   *
   * @return the model's action log.
   */
  public String getActionLog() {
    return this.actionLog;
  }

  /**
   * Adds a logging feature to the loadImage() method.
   *
   * @param imagePixels a 3D array of pixels representing an image.
   */
  @Override
  public void loadImage(int[][][] imagePixels) {
    super.loadImage(imagePixels);
    this.actionLog += "Loading image into model\n";
  }

  /**
   * Adds a logging feature to the getImage() method.
   *
   * @return a 3D array, an image
   */
  @Override
  public int[][][] getImage() {
    int[][][] toReturn = super.getImage();
    this.actionLog += "Sending image in model to controller\n";
    return toReturn;
  }

  /**
   * Adds a logging feature to the filterImage() method.
   *
   * @param filter a Filter specifying the type of filter to be applied.
   * @param args   A list of arguments which may be referenced by the corresponding filter method
   *               next called.  See utilities.FilterUtil.
   * @throws IllegalStateException    if one is passed from the super.
   * @throws IllegalArgumentException if one is passed from the super.
   */
  @Override
  public void filterImage(Filter filter, List<String> args)
          throws IllegalStateException, IllegalArgumentException {
    super.filterImage(filter, args);
    this.actionLog += "Applied a " + filter.toString() + " filter to saved image\n";
  }

  /**
   * Adds a logging feature to generateRainbow() method.
   *
   * @param args A list of arguments needed to generate a rainbow. Must include the following, in
   *             order: the specified orientation - vertical or horizontal; the width of the rainbow
   *             in pixels; and the height of the rainbow in pixels.
   * @throws IllegalArgumentException if one is passed from the super.
   */
  @Override
  public void generateRainbow(List<String> args) throws IllegalArgumentException {
    super.generateRainbow(args);
    this.actionLog += "Generated " + args.get(0).toLowerCase() + " rainbow with width "
            + args.get(1) + " and height " + args.get(2) + "\n";
  }

  /**
   * Adds a logging feature to generateCheckerboard() method.
   *
   * @param args A list of arguments for the checkerboard method. args[0] must be the side length in
   *             pixels for each of the 64 squares in the board.
   * @throws IllegalArgumentException if one is passed from the super.
   */
  @Override
  public void generateCheckerboard(List<String> args) throws IllegalArgumentException {
    super.generateCheckerboard(args);
    this.actionLog += "Generated checkerboard with square size " + args.get(0) + "\n";
  }

  /**
   * Adds a logging feature to the undo() method.
   *
   * @throws IllegalStateException if there are no more states in the deque.
   */
  @Override
  public void undo() throws IllegalStateException {
    super.undo();
    this.actionLog += "Undid previous edit\n";
  }

  /**
   * Adds a logging feature to the reoo() method.
   *
   * @throws IllegalStateException if there are no more states in the deque.
   */
  @Override
  public void redo() throws IllegalStateException {
    super.redo();
    this.actionLog += "Redid previous edit\n";
  }
}
