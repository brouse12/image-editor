import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import controller.IImageController;
import controller.ImageController;
import model.ImageProcessor;
import view.IPView;

/**
 * A driver for the imaging processing program. Houses the main() method and begins program
 * execution. Instantiates a controller, model, and view, and passes control to the controller.
 * Based on user input, program will either run in script mode or interactive GUI mode.  For script
 * mode, enter "-script filePath/myScript.txt" as a command line argument, using any absolute or
 * relative file path to a script.  For GUI mode, the command line argument "-interactive" will
 * launch the GUI interface.
 */
public class Driver {

  /**
   * A main() method which supports two modes: loading a text (.txt) script into the program to
   * execute a series of commands or launching a GUI interface.
   *
   * @param args a command-line argument. must be "-script" with a filepath to a text script file,
   *             or the "-interactive" flag.
   * @throws IOException if a designated script file could not be read.
   */
  public static void main(String[] args) throws IOException {

    // If "-interactive" mode is specified, launch GUI interface.
    if (args.length == 1 && args[0].equals("-interactive")) {
      IImageController controller = new ImageController(
              new ImageProcessor(), new IPView("Image Processor"));
      return;
    }

    // Else if "-script" mode is specified, read the designated script file.
    if (args.length == 2 && args[0].equals("-script")) {
      String pattern = "^[\\w\\d\\/]+\\.txt$";
      if (args[1].matches(pattern) && new File(args[1]).isFile()) {
        IImageController controller = new ImageController(new ImageProcessor(), null);
        controller.commandReader(new FileReader(args[1]));
        return;
      }
    }

    // Otherwise, print usage instructions to the terminal.
    System.out.println(
            "Usage: specify '-interactive' for GUI mode or '-script [filepath]' for script mode.");
  }
}
