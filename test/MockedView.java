import java.awt.image.BufferedImage;
import java.io.File;

import view.IPView;

/**
 * Variation of the view that records a log of GUI actions taken instead of updating an actual GUI
 * interface, to be used for testing.  This way, controller tests can confirm if the controller
 * instructed the view to perform an operation or not. Includes a getActionLog() method to obtain a
 * string version of the log.  This mock returns valid user input when input is requested by the
 * controller.
 */
public class MockedView extends IPView {
  public final String LOAD_PATH = "test/testImage.png";
  public final String SAVE_PATH = "test/test-save.png";

  protected String actionLog;

  /**
   * Constructs a mocked view with a blank action log.
   */
  public MockedView() {
    super("testProgram");
    this.actionLog = "";
  }


  /**
   * Returns the actionLog for this mocked view.
   *
   * @return a String, the actionLog.
   */
  public String getActionLog() {
    return this.actionLog;
  }


  /**
   * Sets display to invisible for testing.
   */
  @Override
  public void display() {
    setVisible(false);
  }


  /**
   * Adds a logging feature for the addImage() method.
   *
   * @param image the image to be displayed by the view.
   */
  @Override
  public void addImage(BufferedImage image) {
    super.addImage(image);
    this.actionLog += "Adding image to View display\n";
  }


  /**
   * Opens a dialog box that allows a user to select an image file for opening. The file must be one
   * of the following types: "png", "jpg", "jpeg", "bmp", "wbmp", "gif". Returns a File object
   * referencing the selected file, or null if the dialog box is closed.
   *
   * @return a file object referencing the selected file, or null if the dialog box is closed.
   */
  @Override
  public File getFileToOpen() {
    this.actionLog += "Loading test file\n";
    return new File(this.LOAD_PATH);
  }

  /**
   * Opens a dialog box that allows a user to select or name an image file for opening.  Returns a
   * File object referencing the chosen file name, or null if the dialog box is closed.  Does not
   * check if the named file has a valid image file extension - this must be tested separately.
   *
   * @return a File object referencing the chosen file name, or null if the dialog box is closed.
   */
  @Override
  public File getFileToSaveAs() {
    this.actionLog += "Saving test file\n";
    return new File(SAVE_PATH);
  }

  /**
   * Display the provided error message as a popup dialog box.
   *
   * @param errorMessage the error message to be displayed.
   */
  @Override
  public void displayError(String errorMessage) {
    this.actionLog += "Displayed error message: " + errorMessage + "\n";
  }

  /**
   * Requests user input via a popup dialog box - user is expected to provide a seed number for a
   * mosaic editing operation.  Returns null if user exits the dialog box without entering
   * information.
   *
   * @return the user's seed input, as a string; null if user exits the dialog box.
   */
  @Override
  public String getSeedPixelNum() {
    this.actionLog += "Returning test number of seed pixels\n";
    return new String("100");
  }

  /**
   * Requests user input via a popup dialog box - user is expected to provide orientation, height,
   * and width values for a rainbow generation operation.  Returns null if user exits the dialog box
   * without entering information.
   *
   * @return the user's square size input, as a string; null if user exits the dialog box.
   */
  @Override
  public String[] getRainbowSpecs() {
    this.actionLog += "Returning test specs for a rainbow\n";
    return new String[]{"vertical", "50", "100"};
  }

  /**
   * Requests user input via a popup dialog box - user is expected to provide a square size value
   * for a checkerboard generation operation.  Returns null if user exits the dialog box without
   * entering information.
   *
   * @return the user's square size input, as a string; null if user exits the dialog box.
   */
  @Override
  public String getCheckerboardSpecs() {
    this.actionLog += "Returning test specs for a checkerboard\n";
    return new String("10");
  }

}
