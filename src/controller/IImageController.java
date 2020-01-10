package controller;

/**
 * An interface for the controller of the program, when run in script mode. Gets script input and
 * calls the relevant methods from the program's model. Supports parsing a properly formatted .txt
 * file that has different commands on each line, or any other Readable input with newline-separated
 * commands, such as a String or standard input.
 */
public interface IImageController {
  /**
   * Starts up the controller by taking a readable object that specifies a series of image
   * processing commands, to be run as a script.  The input can be a string, a file, standard input,
   * or any other class of input that implements the Readable interface.  Individual commands must
   * be on separate lines. This method will only parse the first argument on each line and pass the
   * rest of the arguments to the appropriate method. If the first argument on a line cannot be
   * matched, throws an exception.
   *
   * @param input the image processing script to be implemented
   * @throws IllegalArgumentException If a command is invalid or if a submethod determines that the
   *                                  command's arguments are invalid.
   * @throws IllegalStateException    If such an exception is passed up from a submethod.
   */
  void commandReader(Readable input) throws IllegalArgumentException, IllegalStateException;

}
