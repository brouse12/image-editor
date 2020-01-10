package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import model.IImageProcessor;
import model.utilities.ImageUtil;
import view.IIPView;

/**
 * A class representing the image processing controller.  Supports two usage modes - a command
 * line-operated script reading mode (see IImageController Interface) and an interactive GUI mode
 * (see Features Interface).  The script mode supports parsing a properly formatted .txt file that
 * has different commands on each line, or any other Readable input with newline-separated commands,
 * such as a String or standard input.  The GUI mode uses an event-driven model - the view will call
 * features from the Features Interface as the user interacts with it, and the controller handles
 * the response accordingly.
 */
public class ImageController implements IImageController, Features {

  /**
   * Image file types supported by this program.
   */
  public static final String[] VALID_FILE_EXTENSIONS = {"png", "jpg", "jpeg", "bmp", "wbmp", "gif"};

  /**
   * An IImageProcessor, the program's model (where functionality is stored).
   */
  private IImageProcessor model;

  /**
   * An IIPView, the program's view (the UI).
   */
  private IIPView view;

  /**
   * Initializes an ImageController and stores the provided model and views as instance variables.
   *
   * @param m an IImageProcessor, the model for the program.
   * @param v and IIPView, the view for the program.
   * @throws IllegalArgumentException if the model or view are null.
   */
  public ImageController(IImageProcessor m, IIPView v) throws IllegalArgumentException {
    if (m == null) {
      throw new IllegalArgumentException("Controller cannot reference a null model.");
    }
    this.model = m;
    if (v != null) {
      this.view = v;
      // Pass the available features list to the view for callbacks.
      view.setFeatures(this);
      this.view.display();
    }
  }

  //==================================== Script Parsing Mode =====================================//

  /**
   * Starts up the controller by taking a readable object that specifies a series of image
   * processing commands, to be run as a script.  The input can be a string, a file, standard input,
   * or any other class of input that implements the Readable interface.  Individual commands must
   * be on separate lines. This method will only parse the first argument on each line and pass the
   * rest of the arguments to the appropriate method. If the first argument on a line cannot be
   * matched, throws an exception.  See README for valid commands.
   *
   * @param input the image processing script to be implemented
   * @throws IllegalArgumentException If a command is invalid, if the model determines that the
   *                                  command's arguments are invalid, or if the provided Readable
   *                                  is null.
   * @throws IllegalStateException    If such an exception is thrown by the model.
   */
  public void commandReader(Readable input) throws IllegalArgumentException, IllegalStateException {
    if (input == null) {
      throw new IllegalArgumentException("Script input cannot be null.");
    }

    // read file and compile a list of command lines, each line represented by a list of items
    Scanner s = new Scanner(input);
    ArrayList<String[]> commands = new ArrayList<>();
    while (s.hasNextLine()) {
      commands.add(s.nextLine().trim().split("\\s+"));
    }
    s.close();

    // for each command (i.e., each "line" in the input file), try to execute
    for (String[] commandArgs : commands) {
      List<String> args = new ArrayList<>(Arrays.asList(commandArgs));
      String command = args.remove(0).toLowerCase();

      switch (command) {
        case "exit":
          System.exit(0);
          break;

        case "load":
          loadImage(args);
          break;

        case "save":
          saveImage(args);
          break;

        case "checkerboard":
          this.model.generateCheckerboard(args);
          break;

        case "rainbow":
          this.model.generateRainbow(args);
          break;

        case "":
          // Ignore blank lines
          break;

        default:
          // if none of the above commands were a match, check if the requested operation
          // is a supported filter. If not, throw an exception.
          Filter filter = this.model.getFilter(command);
          if (filter != null) {
            this.model.filterImage(filter, args);
          } else {
            throw new IllegalArgumentException("The following is not a valid image processing"
                    + " instruction: " + command + ".");
          }
      }
    }
  }
  //==================================== Controller Utilities ====================================//

  /**
   * Requests the current image data from the model and saves it as an image file with the given
   * name, which should be included as the first value in the args list.  The filename should
   * include the path of the file (relative or absolute) and a valid image file extension (example:
   * subfolder/myFile.jpeg or /absolutePath/myFile.jpeg). Note: this method is for script mode only;
   * it does not interact with the view.
   *
   * @param args a list of arguments passed from getCommand, the first argument to be a file name.
   * @throws IllegalStateException    if there is no image to be saved.
   * @throws IllegalArgumentException if the file name is not included, the file path is invalid, or
   *                                  the file extension is invalid.
   */
  private void saveImage(List<String> args) throws IllegalStateException, IllegalArgumentException {
    if (!this.model.hasImage()) {
      throw new IllegalStateException("Cannot save until an image has been loaded or generated.");
    }
    if (args.size() < 1) {
      throw new IllegalArgumentException("Filename to save not specified.");
    }
    String filename = args.get(0);
    if (!hasValidFileExtension(filename)) {
      throw new IllegalArgumentException("Save Error: Specified file extension not supported.");
    }

    try {
      ImageUtil.writeImage(
              this.model.getImage(), this.model.getImageWidth(),
              this.model.getImageHeight(), filename);
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not save file as " + filename + ".");
    }
  }

  /**
   * Loads an image file with the given name, which should be included as the first value in the
   * args list, and passes it to the model for editing.  The filename should include the path of the
   * file (relative or absolute) and the file extension (example: subfolder/myFile.jpeg or
   * /absolutePath/myFile.jpeg).  Note: this method is for script mode only; it does not interact
   * with the view.
   *
   * @param args a list of arguments passed from getCommand, the first argument to be a file name.
   * @throws IllegalArgumentException if the file name is not included, the file path is invalid,
   *                                  the file extension is invalid, or the model determines that
   *                                  the loaded data is not a valid 24-bit image.
   */
  private void loadImage(List<String> args) throws IllegalArgumentException {
    if (args.size() < 1) {
      throw new IllegalArgumentException("Filename to load not specified.");
    }

    String filename = args.get(0);
    if (!hasValidFileExtension(filename)) {
      throw new IllegalArgumentException("Load Error: Specified file extension not supported.");
    }

    try {
      int[][][] load = ImageUtil.readImage(filename);
      this.model.loadImage(load);
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid filename or path:  " + filename + ".");
    }

  }

  /**
   * Checks if the provided file name has a supported image file extension. See
   * VALID_FILE_EXTENSIONS for supported types.
   *
   * @param fileName a file name (e.g. myImage.jpeg).
   * @return a boolean, true if there is a valid file extension, otherwise false.
   */
  private boolean hasValidFileExtension(String fileName) {
    String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
    List<String> supportedExt = Arrays.asList(VALID_FILE_EXTENSIONS);
    return supportedExt.contains(extension);
  }


  /**
   * Takes a 3D pixel array from the model and converts it to a BufferedImage, which can be
   * displayed by the view.
   *
   * @return a BufferedImage version of the model's current image.
   * @throws IllegalStateException if the model does not have image data to return.
   */
  private BufferedImage getBufferedImage() throws IllegalStateException {
    int[][][] pixels = this.model.getImage();
    BufferedImage image = ImageUtil.arrayToImage(
            pixels, this.model.getImageWidth(), this.model.getImageHeight());
    return image;
  }


  private boolean checkOverwrite() {
    if (this.model.hasImage()) {
      return this.view.confirmOverwrite();
    }

    return true;
  }


  //================================= Callbacks for GUI Mode =====================================//

  /**
   * Exits the program.
   */
  @Override
  public void exitProgram() {
    System.exit(0);
  }


  /**
   * Asks the model to perform the image filtering operation specified by the filter input, using
   * the provided arguments as applicable (not all filtering operations require arguments, see
   * documentation for the model), then updates the view with the result.  If the model throws an
   * exception, the view will be instructed to display an error message.
   *
   * @param filter a filter enum specifying the operation to be performed
   * @param args   a list of arguments which may be referenced by the corresponding filter method.
   */
  @Override
  public void filter(Filter filter, List<String> args) {
    if (!this.model.hasImage()) {
      this.view.displayError("An image must be loaded or generated before "
              + "performing this operation.");
      return;
    }
    if (filter == Filter.MOSAIC) {
      args.add(this.view.getSeedPixelNum());
      if (args.get(0) == null) {
        return; // User has exited the dialog box.
      }
    }
    try {
      this.model.filterImage(filter, args);
      this.view.addImage(this.getBufferedImage());
    } catch (Exception e) {
      this.view.displayError(e.getMessage());
    }
  }

  /**
   * Takes a script provided by the view and then uses the script mode functionality to evaluate it.
   * Unlike normal script mode, the view will be updated after resolution.  Any exception thrown
   * during script execution will be passed to the view as an error display message.
   *
   * @param script a command script - see controller's commandReader documentation.
   */
  @Override
  public void executeScript(String script) {
    if (script != null && script.length() > 0) {
      try {
        this.commandReader(new StringReader(script));
        this.view.clearScriptBox();
      } catch (Exception e) {
        this.view.displayError(e.getMessage());
      }
    } else {
      this.view.displayError("Please enter text before trying to execute.");
      return;
    }

    // Update the view, even if exception was thrown. The script may make successful model updates
    // before triggering an exception.
    try {
      this.view.addImage(this.getBufferedImage());
    } catch (IllegalStateException e) {
      // If we get here, error message has already made it to the user.
    }
  }

  /**
   * Asks the view to request square size input from the user, then asks the model to generate a
   * checkerboard of the specified size, to be passed to the view for display. If the model throws
   * an exception, it will be passed to the view as an error display message.
   */
  @Override
  public void generateCheckerboard() {
    if (this.checkOverwrite()) {
      String size = this.view.getCheckerboardSpecs();
      if (size == null) {
        return; // user closed the dialog box
      }

      List<String> args = new ArrayList<>();
      args.add(size);
      try {
        this.model.generateCheckerboard(args);
        this.view.addImage(this.getBufferedImage());
      } catch (Exception e) {
        this.view.displayError(e.getMessage());
      }
    }
  }

  /**
   * Asks the view to request rainbow size and orientation input from the user, then asks the model
   * to generate a rainbow image, to be passed to the view for display. If the model throws an
   * exception, it will be passed to the view as an error display message.
   */
  @Override
  public void generateRainbow() {
    if (this.checkOverwrite()) {
      String[] input = this.view.getRainbowSpecs();
      if (input == null) {
        return; // user closed the dialog box
      }

      List<String> args = Arrays.asList(input);
      try {
        this.model.generateRainbow(args);
        this.view.addImage(this.getBufferedImage());
      } catch (Exception e) {
        this.view.displayError(e.getMessage());
      }
    }
  }

  /**
   * Asks the view to let user choose a file to open. Will only allow users to open file types that
   * are supported by ImageIO class (and thus the image utilities used by this program). Adds the
   * selected file to the program's model and view.  Any errors that occur will be passed to the
   * view as an error display message.
   */
  @Override
  public void openFileFromGUI() {
    if (this.checkOverwrite()) {
      File toOpen = this.view.getFileToOpen();
      if (toOpen == null) {
        return; // user closed the dialog box
      } else if (toOpen.length() < 3) {
        this.view.displayError("File must be at least 3 bytes.");
        return;
      }

      try {
        int[][][] image = ImageUtil.readImage(toOpen.getAbsolutePath());
        this.model.loadImage(image);
        this.view.addImage(this.getBufferedImage());
      } catch (IOException e) {
        // could not read file
        this.view.displayError("File could not be read.");
      }
    }
  }


  /**
   * Saves the loaded image. User specifies the filename and path using the GUI. New directories can
   * be created in the window using the "New Folder" button. Any errors that occur will be passed to
   * the view as an error display message.
   */
  @Override
  public void saveFileFromGUI() {
    if (!this.model.hasImage()) {
      this.view.displayError("An image must be loaded or generated before "
              + "performing this operation.");
      return;
    }
    File toSaveAs = this.view.getFileToSaveAs();
    if (toSaveAs == null) {
      return; // user closed the dialog box
    }

    // Get file name and check if it is a valid file type
    String filename = toSaveAs.getName();
    if (!hasValidFileExtension(filename)) {
      this.view.displayError("Save Error: Specified file extension not supported.");
      return;
    }

    try {
      // write image
      ImageUtil.writeImage(this.model.getImage(), this.model.getImageWidth(),
              this.model.getImageHeight(), toSaveAs.getAbsolutePath());
    } catch (IOException e) {
      // could not write file
      this.view.displayError("Image could not be saved. Write Error.");
    }
  }

  /**
   * Calls the undo() method in the model and handles the result accordingly. If the model throws an
   * exception, it will instruct the view to display the error message. Otherwise, it will update
   * the view with the new image.
   */
  @Override
  public void undoOp() {
    try {
      this.model.undo();
    } catch (IllegalStateException e) {
      this.view.displayError(e.getMessage());
      return;
    }

    this.view.addImage(this.getBufferedImage());

  }

  /**
   * Calls the redo() method in the model and handles the result accordingly. If the model throws an
   * exception, it will instruct the view to display the error message. Otherwise, it will update
   * the view with the new image.
   */
  @Override
  public void redoOp() {
    try {
      this.model.redo();
    } catch (IllegalStateException e) {
      this.view.displayError(e.getMessage());
      return;
    }

    this.view.addImage(this.getBufferedImage());
  }
}
