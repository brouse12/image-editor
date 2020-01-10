package controller;

import java.util.List;

/**
 * An interface for the controller of the program, when run in interactive GUI mode. This interface
 * represents a set of features that the program offers. Each feature is exposed as a function in
 * this interface. This function is used suitably as a callback by the view, to pass control to the
 * controller. How the view uses them as callbacks is up to how the view is designed (e.g. it could
 * use them as a callback for a button, or a callback for a dialog box, or a set of text inputs,
 * etc.)
 */
public interface Features {

  /**
   * Exits the program.
   */
  void exitProgram();

  /**
   * Asks the model to perform the image filtering operation specified by the filter input, using
   * the provided arguments as applicable (not all filtering operations require arguments, see
   * documentation for the model), then updates the view with the result.  If the model throws an
   * exception, the view will be instructed to display an error message.
   *
   * @param filter a filter enum specifying the operation to be performed
   * @param args   a list of arguments which may be referenced by the corresponding filter method.
   */
  void filter(Filter filter, List<String> args);

  /**
   * Takes a script provided by the view and then uses the script mode functionality to evaluate it.
   * Unlike normal script mode, the view will be updated after resolution.  Any exception thrown
   * during script execution will be passed to the view as an error display message.
   *
   * @param script a command script - see controller's commandReader documentation.
   */
  void executeScript(String script);

  /**
   * Asks the view to request square size input from the user, then asks the model to generate a
   * checkerboard of the specified size, to be passed to the view for display. If the model throws
   * an exception, it will be passed to the view as an error display message.
   */
  void generateCheckerboard();

  /**
   * Asks the view to request rainbow size and orientation input from the user, then asks the model
   * to generate a rainbow image, to be passed to the view for display. If the model throws an
   * exception, it will be passed to the view as an error display message.
   */
  void generateRainbow();

  /**
   * Asks the view to let user choose a file to open. Will only allow users to open file types that
   * are supported by ImageIO class (and thus the image utilities used by this program). Adds the
   * selected file to the program's model and view.  Any errors that occur will be passed to the
   * view as an error display message.
   */
  void openFileFromGUI();

  /**
   * Saves the loaded image. User specifies the filename and path using the GUI. New directories can
   * be created in the window using the "New Folder" button. Any errors that occur will be passed to
   * the view as an error display message.
   */
  void saveFileFromGUI();

  /**
   * Calls the undo() method in the model and handles the result accordingly. If the model throws
   * an exception, it will instruct the view to display the error message. Otherwise, it will
   * update the view with the new image.
   */
  void undoOp();

  /**
   * Calls the redo() method in the model and handles the result accordingly. If the model throws
   * an exception, it will instruct the view to display the error message. Otherwise, it will
   * update the view with the new image.
   */
  void redoOp();
}

