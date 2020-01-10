import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import controller.IImageController;
import controller.ImageController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * JUnit tests for the ImageController's script mode (e.g. the commandReader method) using a mocked
 * model. The mocked model records image editing commands received from the controller and throws
 * exceptions for the controller to handle.  File I/O testing is performed using the LOAD_PATH and
 * SAVE_PATH file paths.  Effectiveness of image edits are not tested - these tests only use the
 * mocked model's action log to confirm that it received the expected editing instructions from the
 * controller. Separate testing must be performed for the controller's interactive GUI mode.
 */
public class ControllerTestScriptMode {

  private final String LOAD_PATH = "test/testImage.png";
  private final String SAVE_PATH = "test/testImageResult.png";

  private IImageController testController;
  private MockedModel mockedModel;

  @Before
  public void setUp() {
    mockedModel = new MockedModel();
    testController = new ImageController(mockedModel, null);
  }

  // ------------------ Testing that Controller works with valid script input -------------------//

  @Test
  public void testValidStringScript() {
    String validScript = "load " + LOAD_PATH + "\n"
            + "blur\n"
            + "save " + SAVE_PATH + "\n";
    String expected = "Loading image into model\n"
            + "Applied a blur filter to saved image\n"
            + "Sending image in model to controller\n";

    testController.commandReader(new StringReader(validScript));
    assertEquals(expected, mockedModel.getActionLog());
    assertTrue(fileSaved(SAVE_PATH));
  }

  @Test
  public void testValidFileScriptAllCommands() throws IOException {
    String expected = "Generated checkerboard with square size 25\n"
            + "Sending image in model to controller\n"
            + "Generated vertical rainbow with width 200 and height 300\n"
            + "Sending image in model to controller\n"
            + "Loading image into model\n"
            + "Applied a blur filter to saved image\n"
            + "Applied a sepia filter to saved image\n"
            + "Applied a mosaic filter to saved image\n"
            + "Applied a dither filter to saved image\n"
            + "Sending image in model to controller\n";

    testController.commandReader(new FileReader("test/testScript.txt"));
    assertEquals(expected, mockedModel.getActionLog());
    assertTrue(fileSaved(SAVE_PATH));
  }

  @Test
  public void testScriptWithCapLetters() throws IOException {
    String expected = "Generated checkerboard with square size 25\n"
            + "Sending image in model to controller\n"
            + "Generated vertical rainbow with width 200 and height 300\n"
            + "Sending image in model to controller\n"
            + "Loading image into model\n"
            + "Applied a blur filter to saved image\n"
            + "Applied a sepia filter to saved image\n"
            + "Applied a mosaic filter to saved image\n"
            + "Applied a dither filter to saved image\n"
            + "Sending image in model to controller\n";

    testController.commandReader(new FileReader("test/testScriptCaps.txt"));
    assertEquals(expected, mockedModel.getActionLog());
    assertTrue(fileSaved(SAVE_PATH));
  }

  @Test
  public void testValidScriptWithBlankLines() {
    String validScript = "load " + LOAD_PATH + "\n"
            + "blur\n"
            + "\n"
            + "save " + SAVE_PATH + "\n";
    String expected = "Loading image into model\n"
            + "Applied a blur filter to saved image\n"
            + "Sending image in model to controller\n";

    testController.commandReader(new StringReader(validScript));
    assertEquals(expected, mockedModel.getActionLog());
    assertTrue(fileSaved(SAVE_PATH));
  }

  @Test
  public void testValidScriptWithExtraSpacesTabs() {
    String validScript = "load " + LOAD_PATH + "\n"
            + "\tblur\t\n  "
            + "save\t     " + SAVE_PATH + "\n";
    String expected = "Loading image into model\n"
            + "Applied a blur filter to saved image\n"
            + "Sending image in model to controller\n";

    testController.commandReader(new StringReader(validScript));
    assertEquals(expected, mockedModel.getActionLog());
    assertTrue(fileSaved(SAVE_PATH));
  }

  @Test()
  public void testScriptWithNoCommands() {
    testController.commandReader(new StringReader(""));
    assertEquals("", mockedModel.getActionLog());
  }

  // ------------------------ Testing exceptions thrown by controller  ------------------------ //

  @Test(expected = IllegalArgumentException.class)
  public void testNullModel() {
    IImageController noModel = new ImageController(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullArgument() {
    testController.commandReader(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCommand() {
    testController.commandReader(new StringReader("not a valid command\n"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadInvalidFileName() {
    String script = "load fakeImage.png";
    testController.commandReader(new StringReader(script));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadMissingArgument() {
    testController.commandReader(new StringReader("load"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadInvalidFileExtension() {
    String script = "load testImage.txt";
    testController.commandReader(new StringReader(script));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadNoFileExtension() {
    String script = "load testImage";
    testController.commandReader(new StringReader(script));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadInvalidFilePath() {
    String script = "load fakeDirectory/testImage.png";
    testController.commandReader(new StringReader(script));
  }

  @Test(expected = IllegalStateException.class)
  public void testNoImageToSave() {
    String script = "save " + SAVE_PATH + "\n";
    testController.commandReader(new StringReader(script));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSaveMissingArgument() {
    String script = "load " + LOAD_PATH + "\n"
            + "save";
    testController.commandReader(new StringReader(script));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSaveInvalidFileExtension() {
    String script = "load " + LOAD_PATH + "\n"
            + "save newFile.jpeeg";
    testController.commandReader(new StringReader(script));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSaveNoFileExtension() {
    String script = "load " + LOAD_PATH + "\n"
            + "save newFile";
    testController.commandReader(new StringReader(script));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSaveInvalidFilePath() {
    String script = "load " + LOAD_PATH + "\n"
            + "save fakeDirectory/newFile.jpeg";
    testController.commandReader(new StringReader(script));
  }

  //------------------------ Testing exceptions passed up from model --------------------------- //


  @Test(expected = IllegalStateException.class)
  public void testNoImageToBlur() {
    testController.commandReader(new StringReader("blur"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNoImageToSharpen() {
    testController.commandReader(new StringReader("sharpen"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNoImageToGreyscale() {
    testController.commandReader(new StringReader("greyscale"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNoImageToSepia() {
    testController.commandReader(new StringReader("sepia"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNoImageToDither() {
    testController.commandReader(new StringReader("dither"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNoImageToMosaic() {
    testController.commandReader(new StringReader("mosaic 500"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCheckerboardInput() {
    testController.commandReader(new StringReader("checkerboard -25"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRainbowInvalidOrientation() {
    testController.commandReader(new StringReader("rainbow upside-down 200 300"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRainbowInvalidWidth() {
    testController.commandReader(new StringReader("rainbow vertical 6 300"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRainbowInvalidHeight() {
    testController.commandReader(new StringReader("rainbow vertical 200 6"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMosaicTooFewSeeds() {
    String script = "load " + LOAD_PATH + "\n"
            + "mosaic 0";
    testController.commandReader(new StringReader(script));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMosaicTooManySeeds() {
    String script = "load " + LOAD_PATH + "\n"
            + "mosaic 1000000";
    testController.commandReader(new StringReader(script));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingArgumentCheckerboard() {
    testController.commandReader(new StringReader("checkerboard"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingArgumentRainbow() {
    testController.commandReader(new StringReader("rainbow vertical 200"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingArgumentMosaic() {
    String script = "load " + LOAD_PATH + "\n"
            + "mosaic";
    testController.commandReader(new StringReader(script));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidArgumentTypeCheckers() {
    testController.commandReader(new StringReader("checkerboard fifty"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidArgumentTypeMosaic() {
    String script = "load " + LOAD_PATH + "\n"
            + "mosaic fifty";
    testController.commandReader(new StringReader(script));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidWidthTypeRainbow() {
    testController.commandReader(new StringReader("rainbow vertical ten 300"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidHeightTypeRainbow() {
    testController.commandReader(new StringReader("rainbow vertical 200 ten"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDecimalArgumentCheckers() {
    testController.commandReader(new StringReader("checkerboard 30.52"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDecimalArgumentMosaic() {
    String script = "load " + LOAD_PATH + "\n"
            + "mosaic 1000.53";
    testController.commandReader(new StringReader(script));
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
