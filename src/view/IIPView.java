package view;

import java.awt.image.BufferedImage;
import java.io.File;

import controller.Features;

/**
 * An interface for the view of the program - a GUI interface that displays images and receives user
 * input regarding image editing commands.  Responds to user events by calling methods from the
 * controller's Features list.
 */
public interface IIPView {

  /**
   * Display the view. To be called after the the view is fully constructed.
   */
  void display();

  /**
   * Get the set of feature callbacks that the view can call from the model.
   *
   * @param f the set of feature callbacks as a Features object.
   */
  void setFeatures(Features f);

  /**
   * Adds the provided buffered image to the view's display, replacing any existing image.
   */
  void addImage(BufferedImage image);

  /**
   * Opens a dialog box that allows a user to select an image file for opening. The file must be one
   * of the following types: "png", "jpg", "jpeg", "bmp", "wbmp", "gif". Returns a File object
   * referencing the selected file, or null if the dialog box is closed.
   *
   * @return a file object referencing the selected file, or null if the dialog box is closed.
   */
  File getFileToOpen();

  /**
   * Opens a dialog box that allows a user to select or name an image file for opening.  Returns a
   * File object referencing the chosen file name, or null if the dialog box is closed.  Does not
   * check if the named file has a valid image file extension - this must be tested separately.
   *
   * @return a File object referencing the chosen file name, or null if the dialog box is closed.
   */
  File getFileToSaveAs();

  /**
   * Display the provided error message as a popup dialog box.
   *
   * @param errorMessage the error message to be displayed.
   */
  void displayError(String errorMessage);

  /**
   * Requests user input via a popup dialog box - user is expected to provide a seed number for a
   * mosaic editing operation.  Returns null if user exits the dialog box without entering
   * information.
   *
   * @return the user's seed input, as a string; null if user exits the dialog box.
   */
  String getSeedPixelNum();

  /**
   * Requests user input via a popup dialog box - user is expected to provide orientation, height,
   * and width values for a rainbow generation operation.  Returns null if user exits the dialog box
   * without entering information.
   *
   * @return the user's square size input, as a string; null if user exits the dialog box.
   */
  String[] getRainbowSpecs();

  /**
   * Requests user input via a popup dialog box - user is expected to provide a square size value
   * for a checkerboard generation operation.  Returns null if user exits the dialog box without
   * entering information.
   *
   * @return the user's square size input, as a string; null if user exits the dialog box.
   */
  String getCheckerboardSpecs();

  /**
   * Clears out any text in the script editor box.
   */
  void clearScriptBox();

  /**
   * Asks the user if they are sure they want to proceed with overwriting an image.
   *
   * @return true or false, accordingly
   */
  boolean confirmOverwrite();

}
