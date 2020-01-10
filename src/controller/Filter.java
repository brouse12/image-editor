package controller;

/**
 * Class representing the available filtering operations in FilterUtils.
 */
public enum Filter {
  BLUR("blur"), SHARPEN("sharpen"), GREYSCALE("greyscale"),
  SEPIA("sepia"), DITHER("dither"), MOSAIC("mosaic");

  private String filterName;

  /**
   * Creates a new filter object with the corresponding filter name in string format.
   *
   * @param filterName a string representation of the filter name
   */
  Filter(String filterName) {
    this.filterName = filterName;
  }

  /**
   * Returns a string version of this filter.
   *
   * @return a string version of this filter
   */
  @Override
  public String toString() {
    return this.filterName;
  }
}
