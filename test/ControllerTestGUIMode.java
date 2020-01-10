import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;

import controller.Filter;
import controller.ImageController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnit tests for the ImageController's GUI mode using a mocked model and view - the tests here
 * cover each of the controller's command callbacks. The mocked model and view record commands
 * received from the controller and throw exceptions for the controller to handle, as applicable.
 * The effectiveness of the GUI display and image editing operations are not tested here - only the
 * controller's instructions are checked.  Separate testing must be performed for the controller's
 * script mode.
 */
public class ControllerTestGUIMode {

  private ImageController testController;
  private MockedModel mockedModel;
  private MockedView mockedView;
  private MockedViewInvalidInput badInputView;


  @Before
  public void setUp() {
    mockedModel = new MockedModel();
    mockedView = new MockedView();
    badInputView = new MockedViewInvalidInput();
    testController = new ImageController(mockedModel, mockedView);


  }


  // ------------------------ Testing View/Controller/Model Interfacing -------------------------//


  @Test
  public void testLoadFromGUI() {
    testController.openFileFromGUI();
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Loading test file\n"
            + "Adding image to View display\n", viewLog);
    assertEquals("Loading image into model\n"
            + "Sending image in model to controller\n", modelLog);

  }

  @Test
  public void testSaveFromGUI() {
    testController.openFileFromGUI();
    testController.saveFileFromGUI();
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Loading test file\n"
            + "Adding image to View display\n"
            + "Saving test file\n", viewLog);
    assertEquals("Loading image into model\n"
            + "Sending image in model to controller\n" // called twice: once to load image into
            // view; once to get data to save
            + "Sending image in model to controller\n", modelLog);
    assertTrue(fileSaved(this.mockedView.SAVE_PATH));

  }

  @Test
  public void testGenerateMosaic() {
    testController.openFileFromGUI();
    testController.filter(Filter.MOSAIC, new ArrayList<String>());
    testController.saveFileFromGUI();
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Loading test file\n"
            + "Adding image to View display\n"
            + "Returning test number of seed pixels\n"
            + "Adding image to View display\n"
            + "Saving test file\n", viewLog);
    assertEquals("Loading image into model\n"
            + "Sending image in model to controller\n"
            + "Applied a mosaic filter to saved image\n"
            + "Sending image in model to controller\n"
            + "Sending image in model to controller\n", modelLog);
    assertTrue(fileSaved(this.mockedView.SAVE_PATH));

  }

  @Test
  public void testGenerateRainbow() {
    testController.generateRainbow();
    testController.saveFileFromGUI();
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Returning test specs for a rainbow\n"
            + "Adding image to View display\n"
            + "Saving test file\n", viewLog);
    assertEquals("Generated vertical rainbow with width 50 and height 100\n"
            + "Sending image in model to controller\n"
            + "Sending image in model to controller\n", modelLog);
    assertTrue(fileSaved(this.mockedView.SAVE_PATH));

  }

  @Test
  public void testGenerateCheckerboard() {
    testController.generateCheckerboard();
    testController.saveFileFromGUI();
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Returning test specs for a checkerboard\n"
            + "Adding image to View display\n"
            + "Saving test file\n", viewLog);
    assertEquals("Generated checkerboard with square size 10\n"
            + "Sending image in model to controller\n"
            + "Sending image in model to controller\n", modelLog);
    assertTrue(fileSaved(this.mockedView.SAVE_PATH));

  }

  @Test
  public void testGeneralFilter() {
    testController.openFileFromGUI();
    testController.filter(Filter.BLUR, null);
    testController.saveFileFromGUI();
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Loading test file\n"
            + "Adding image to View display\n"
            + "Adding image to View display\n"
            + "Saving test file\n", viewLog);
    assertEquals("Loading image into model\n" +
            "Sending image in model to controller\n" +
            "Applied a blur filter to saved image\n" +
            "Sending image in model to controller\n" +
            "Sending image in model to controller\n", modelLog);
    assertTrue(fileSaved(this.mockedView.SAVE_PATH));
  }

  @Test
  public void testUndo() {
    testController.generateCheckerboard();
    testController.filter(Filter.SEPIA, null);
    testController.undoOp();
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Returning test specs for a checkerboard\n"
            + "Adding image to View display\n"
            + "Adding image to View display\n"
            + "Adding image to View display\n", viewLog);
    assertEquals("Generated checkerboard with square size 10\n"
            + "Sending image in model to controller\n"
            + "Applied a sepia filter to saved image\n"
            + "Sending image in model to controller\n"
            + "Undid previous edit\n"
            + "Sending image in model to controller\n", modelLog);
  }

  @Test
  public void testRedo() {
    testController.generateCheckerboard();
    testController.filter(Filter.SEPIA, null);
    testController.undoOp();
    testController.redoOp();
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Returning test specs for a checkerboard\n"
            + "Adding image to View display\n"
            + "Adding image to View display\n"
            + "Adding image to View display\n"
            + "Adding image to View display\n", viewLog);
    assertEquals("Generated checkerboard with square size 10\n"
            + "Sending image in model to controller\n"
            + "Applied a sepia filter to saved image\n"
            + "Sending image in model to controller\n"
            + "Undid previous edit\n"
            + "Sending image in model to controller\n"
            + "Redid previous edit\n"
            + "Sending image in model to controller\n", modelLog);
  }


  // ---------------------------------- Testing Error Handling -----------------------------------//

  @Test
  public void testNoImageFilter() {
    testController.filter(Filter.BLUR, null);
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Displayed error message: An image must be loaded or "
            + "generated before performing this operation.\n", viewLog);
    assertEquals("", modelLog);
  }

  @Test
  public void testNoImageMosaic() {
    testController.filter(Filter.MOSAIC, new ArrayList<String>());
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Displayed error message: An image must be loaded or "
            + "generated before performing this operation.\n", viewLog);
    assertEquals("", modelLog);
  }

  @Test
  public void testSaveNoImage() {
    testController.saveFileFromGUI();
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Displayed error message: An image must be loaded or "
            + "generated before performing this operation.\n", viewLog);
    assertEquals("", modelLog);
  }

  @Test
  public void testSaveInvalidFile() {
    ImageController controllerBadInput = new ImageController(mockedModel, badInputView);

    controllerBadInput.commandReader(new StringReader("load test/testImage.png\n"));
    controllerBadInput.saveFileFromGUI();
    String viewLog = badInputView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Saving test file\n"
            + "Displayed error message: Save Error: Specified file extension not"
            + " supported.\n", viewLog);
    assertEquals("Loading image into model\n", modelLog);
  }

  @Test
  public void testOpenInvalidFile() {
    ImageController controllerBadInput = new ImageController(mockedModel, badInputView);

    controllerBadInput.openFileFromGUI();
    String viewLog = badInputView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Loading test file\n"
            + "Displayed error message: File must be at least 3 bytes.\n", viewLog);
    assertEquals("", modelLog);
  }

  @Test
  public void testMosaicInvalidPixels() {
    ImageController controllerBadInput = new ImageController(mockedModel, badInputView);

    controllerBadInput.commandReader(new StringReader("load test/testImage.png\n"));
    controllerBadInput.filter(Filter.MOSAIC, new ArrayList<>());
    String viewLog = badInputView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Returning test number of seed pixels\n"
            + "Displayed error message: Number of seeds may not exceed the number of pixels in"
            + " the image or be less than 1. Number of requested seeds: 0; Number of"
            + " pixels: 100000.\n", viewLog);
    assertEquals("Loading image into model\n", modelLog);
  }

  @Test
  public void testCheckerboardInvalidInput() {
    ImageController controllerBadInput = new ImageController(mockedModel, badInputView);

    controllerBadInput.generateCheckerboard();
    String viewLog = badInputView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Returning test specs for a checkerboard\n"
            + "Displayed error message: Cannot generate a board with squares of size 0 or"
            + " less.\n", viewLog);
    assertEquals("", modelLog);
  }

  @Test
  public void testRainbowInvalidInput() {
    ImageController controllerBadInput = new ImageController(mockedModel, badInputView);

    controllerBadInput.generateRainbow();
    String viewLog = badInputView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Returning test specs for a rainbow\n"
            + "Displayed error message: Rainbow orientation must be either 'vertical' or"
            + " 'horizontal'.\n", viewLog);
    assertEquals("", modelLog);
  }

  @Test
  public void testCannotUndo() {
    testController.undoOp();
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Displayed error message: Error: Cannot undo any further.\n", viewLog);
    assertEquals("", modelLog);
  }

  @Test
  public void testCannotRedo() {
    testController.redoOp();
    String viewLog = mockedView.getActionLog();
    String modelLog = mockedModel.getActionLog();
    assertEquals("Displayed error message: Error: Nothing to redo.\n", viewLog);
    assertEquals("", modelLog);
  }


  //---------------------------------------------------------------------------------------------//

  /**
   * Used to test if the designated file was saved and then cleans it up.
   */
  private boolean fileSaved(String filepath) {
    File testedFile = new File(filepath);
    boolean outcome = testedFile.exists();

    // Cleans up the saved file
    if (outcome) {
      testedFile.delete();
    }

    return outcome;
  }

}
