package model.image;

/**
 * Represents an empty image with no pixel data.  Used as a placeholder when the model has not
 * loaded an image.
 */
public class EmptyImage implements IImage {

  /**
   * As this is an EmptyImage, immediately throws an exception.
   *
   * @throws IllegalStateException as this is an EmptyImage.
   */
  @Override
  public int getWidth() {
    throw new IllegalStateException("An empty image does not have a width."
            + " Please load an image first.");
  }

  /**
   * As this is an EmptyImage, immediately throws an exception.
   *
   * @throws IllegalStateException as this is an EmptyImage.
   */
  @Override
  public int getHeight() {
    throw new IllegalStateException("An empty image does not have a height."
            + " Please load an image first.");
  }

  /**
   * As this is an EmptyImage, immediately throws an exception.
   *
   * @throws IllegalStateException as this is an EmptyImage.
   */
  @Override
  public int[][][] getPixels() {
    throw new IllegalStateException("An empty image does not have pixels."
            + " Please load an image first.");
  }

  /**
   * As this is an EmptyImage, immediately returns false.
   *
   * @return false.
   */
  @Override
  public boolean hasPixels() {
    return false;
  }
}
