package model.utilities;

import java.util.Objects;

/**
 * A class representing a clustered pixel. This class is used by FilterUtil.mosaic() in order to
 * keep track of each pixel's original location after being paired with its seedpixel.
 */
public class ClusteredPixel {

  /**
   * An int, the pixel's row.
   */
  private int row;

  /**
   * An int, the pixel's column.
   */
  private int column;

  /**
   * an 3D integer array, the pixel's color data.
   */
  private int[] colors;

  /**
   * Instantiates a clustered pixel. Sets its instance variables according to the data passed
   * to the constructor.
   *
   * @param r an int, the pixel's row.
   * @param c an int, the pixel's column.
   * @param cs a 3D integer array, the pixel's color data.
   */
  public ClusteredPixel(int r, int c, int[] cs) {
    this.row = r;
    this.column = c;
    this.colors = cs;
  }

  /**
   * A getter for the pixel's row.
   *
   * @return an int, the pixel's row.
   */
  public int getRow() {
    return this.row;
  }

  /**
   * A getter for the pixel's column.
   *
   * @return an int, the pixel's column.
   */
  public int getCol() {
    return this.column;
  }

  /**
   * A getter for the pixel's color data.
   *
   * @return a 3D integer array, the pixel's color data.
   */
  public int[] getColors() {
    return this.colors;
  }

  /**
   * An equals method for a ClusteredPixel. If the pixel's row and column match, the pixels are
   * deemed equivalent. Used when checking if a pixel has already been selected as a seed pixel.
   *
   * @param o an Object, the object being compared.
   * @return true or false, depending on whether or not the object is a clustered pixel and
   *         the row and column match.
   */
  @Override
  public boolean equals(Object o) {
    if (o.getClass() == ClusteredPixel.class) {
      ClusteredPixel other = (ClusteredPixel) o;
      return this.row == other.row && this.column == other.column;
    }

    return false;
  }

  /**
   * An override of the hashCode() method. As equals() was overwritten, so is this. Any two
   * clustered pixels deemed to be equal will thus produce the same hashcode.
   *
   * @return an int, the hashCode for a ClusteredPixel based on its row and height.
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.row, this.column);
  }
}
