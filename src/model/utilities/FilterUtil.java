package model.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controller.Filter;
import model.image.IImage;
import model.image.Image;

import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.Math.toIntExact;

/**
 * A utility class that stores kernels and matrices for image filtering operations and applies those
 * filters when instructed to.
 */
public class FilterUtil {

  /**
   * The max value of an RGB color channel.
   */
  private static final int MAX_COLOR_VALUE = 255;

  /**
   * A double[][] kernel for blurring an image. Calculates a new channel value by factoring in the
   * channel values of surrounding pixels.
   */
  private static final double[][] blurKernel = {
          {0.0625, 0.125, 0.0625},
          {0.125, 0.25, 0.125},
          {0.0625, 0.125, 0.0625}
  };

  /**
   * A double[][] kernel for sharpening an image. Calculates a new channel value by factoring in the
   * channel values of surrounding pixels.
   */
  private static final double[][] sharpenKernel = {
          {-0.125, -0.125, -0.125, -0.125, -0.125},
          {-0.125, 0.25, 0.25, 0.25, -0.125},
          {-0.125, 0.25, 1.0, 0.25, -0.125},
          {-0.125, 0.25, 0.25, 0.25, -0.125},
          {-0.125, -0.125, -0.125, -0.125, -0.125}
  };

  /**
   * A double[][] matrix for converting an image to sepia tone. Gets channel values by applying each
   * row to the original channel values (R, G, then B respectively).
   */
  private static final double[][] sepia = {
          {0.393, 0.769, 0.189},
          {0.349, 0.686, 0.168},
          {0.272, 0.534, 0.131}
  };

  /**
   * A double[][] matrix for converting an image to greyscale. Gets channel values by applying each
   * row to the original channel values (R, G, then B respectively).
   */
  private static final double[][] luma = {
          {0.2126, 0.7152, 0.0722},
          {0.2126, 0.7152, 0.0722},
          {0.2126, 0.7152, 0.0722}
  };


  /**
   * A dispatch method for all image filtering operations, including color transforms, generic
   * filters, and other options. Evaluates if the requested operation is matrix- or kernel-based and
   * calls the appropriate method with the appropriate transformation. Additionally supports
   * dithering and mosaics.
   *
   * @param image  the image to be filtered
   * @param filter the filter to be applied
   * @param args   any additional args needs to apply the filter
   * @return the filtered image
   * @throws IllegalArgumentException if the filter type is not supported by this method
   */
  public static IImage filterDispatch(IImage image, Filter filter, List<String> args)
          throws IllegalArgumentException {

    switch (filter) {
      case BLUR:
        return applyKernel(image, blurKernel);
      case SHARPEN:
        return applyKernel(image, sharpenKernel);
      case SEPIA:
        return applyMatrix(image, sepia);
      case GREYSCALE:
        return applyMatrix(image, luma);
      case DITHER:
        return dither(applyMatrix(image, luma));
      case MOSAIC:
        return mosaic(image, args);
      default:
        throw new IllegalArgumentException("Filter operation not supported: " + filter.toString());
    }
  }

  /**
   * A method that applies a specified filter matrix to a specified image and returns the image.
   * Gets channel values of the new image by applying each row to the original channel values (R, G,
   * then B respectively).
   *
   * @param image  an IImage object, the original image
   * @param filter a specified matrix filter to be applied
   * @return an IImage object, the new image
   */
  private static IImage applyMatrix(IImage image, double[][] filter) {

    // set up variables for new image
    int[][][] originalPixels = image.getPixels();
    int[][][] imageToReturn = new int[originalPixels.length]
            [originalPixels[0].length]
            [3];

    // for each row
    for (int row = 0; row < imageToReturn.length; row++) {
      // for each pixel in the row
      for (int pixel = 0; pixel < imageToReturn[0].length; pixel++) {
        // apply the appropriate filter row to get each channel of new image
        int[] newPixel = new int[3];
        for (int channel = 0; channel < 3; channel++) {
          // apply the filter to each channel to get new values
          newPixel[channel] = (int) (filter[channel][0] * originalPixels[row][pixel][0]
                  + filter[channel][1] * originalPixels[row][pixel][1]
                  + filter[channel][2] * originalPixels[row][pixel][2]);

          // clamp it
          if (newPixel[channel] < 0) {
            newPixel[channel] = 0;
          } else if (newPixel[channel] > MAX_COLOR_VALUE) {
            newPixel[channel] = MAX_COLOR_VALUE;
          }
        }

        // add new RGB values to pixel in imageToReturn
        imageToReturn[row][pixel] = newPixel;
      }
    }

    // convert to an Image and return
    return new Image(imageToReturn);
  }

  /**
   * A method that applies a specified kernel to a specified image. A kernel calculates RGB channel
   * values by evaluating the same channel in surrounding pixels in the original image.
   *
   * @param image  an IImage object, the original image
   * @param kernel a specified kernel to be applied
   * @return an IImage object, the new image
   */
  private static IImage applyKernel(IImage image, double[][] kernel) {
    int[][][] originalPixels = image.getPixels();
    int[][][] newPixels = new int[image.getHeight()][image.getWidth()][3];

    // For each of the 3 channels in every pixel, apply the kernel to that channel
    for (int i = 0; i < image.getHeight(); i++) {
      for (int j = 0; j < image.getWidth(); j++) {
        for (int k = 0; k < 3; k++) {
          int channelVal = applyKernelToChannel(originalPixels, kernel, i, j, k);

          // clamp it
          if (channelVal < 0) {
            channelVal = 0;
          } else if (channelVal > MAX_COLOR_VALUE) {
            channelVal = MAX_COLOR_VALUE;
          }

          // add value to new image's channel
          newPixels[i][j][k] = channelVal;
        }
      }
    }

    return new Image(newPixels);
  }

  /**
   * A helper method for applyKernel. Calculates the value of a specified channel of a specified
   * pixel by multiplying the channel value in surrounding pixels by the coefficients of the
   * kernel.
   *
   * @param originalPixels an int[][][], the original image pixels
   * @param kernel         a specified kernel to be applied
   * @param pixelRow       the row of the pixel to be calculated
   * @param pixelColumn    the column of the pixel to be calculated
   * @param channel        the channel of the pixel to be calculated
   * @return an int, the channel's modified value
   */
  private static int applyKernelToChannel(int[][][] originalPixels, double[][] kernel, int pixelRow,
                                          int pixelColumn, int channel) {
    // Kernel offset is the maximum possible distance from the center of the kernel, when viewed as
    // a matrix.  Used to align values in the kernel with values in the image pixels array.
    int kernelOffset = (kernel.length - 1) / 2;
    double newChannelValue = 0;

    for (int i = -kernelOffset; i <= kernelOffset; i++) {
      for (int j = -kernelOffset; j <= kernelOffset; j++) {
        int matchingPRow = pixelRow + i;
        int matchingPColumn = pixelColumn + j;
        int matchingKRow = kernelOffset + i;
        int matchingKColumn = kernelOffset + j;
        try {
          newChannelValue += originalPixels[matchingPRow][matchingPColumn][channel]
                  * kernel[matchingKRow][matchingKColumn];
        } catch (IndexOutOfBoundsException e) {
          // Do nothing. 0 should be added to the the value here.
        }
      }
    }
    return (int) round(newChannelValue);
  }


  /**
   * A method that dithers a greyscale image. I.e., it converts a greyscale image to one made
   * entirely of black and white dots.
   *
   * @param image an IImage object to be dithered.
   * @return a new Image object, a dithered version of the original
   */
  private static IImage dither(IImage image) {
    // set initial variables, load in image pixels.
    int[][][] pixels = image.getPixels();
    int[][][] ditheredImage = new int[image.getHeight()][image.getWidth()][3];

    // iterate through each pixel, calculating a new value (0 or 255) and pushing the
    // error to surrounding pixels.
    for (int row = 0; row < image.getHeight(); row++) {
      for (int pixel = 0; pixel < image.getWidth(); pixel++) {
        int oldColorVal = pixels[row][pixel][0];
        int newColorVal = toIntExact(round(oldColorVal / 255.0)) * 255;

        // set all color channels to the new value.
        for (int c = 0; c < 3; c++) {
          ditheredImage[row][pixel][c] = newColorVal;
        }

        // push error to other pixels. catch out of bounds exceptions if thrown.
        int error = oldColorVal - newColorVal;
        try {
          pixels[row][pixel + 1][0] += (int) (0.4375 * error);
        } catch (ArrayIndexOutOfBoundsException e) {
          // caught out of bounds
        }
        try {
          pixels[row + 1][pixel - 1][0] += (int) (0.1875 * error);
        } catch (ArrayIndexOutOfBoundsException e) {
          // caught out of bounds
        }
        try {
          pixels[row + 1][pixel][0] += (int) (0.3125 * error);
        } catch (ArrayIndexOutOfBoundsException e) {
          // caught out of bounds
        }
        try {
          pixels[row + 1][pixel + 1][0] += (int) (0.0625 * error);
        } catch (ArrayIndexOutOfBoundsException e) {
          // caught out of bounds
        }
      }
    }

    // convert pixels to an image and return.
    return new Image(ditheredImage);
  }


  /**
   * A method to create a mosaic based on a given image. The user specifies how many seed pixels
   * will be selected. Seed pixels are then selected randomly.
   *
   * @param image an IImage object that will be the basis for the mosaic
   * @param args  a list of arguments passed to the function. args[0] must be an number representing
   *              the user-specified number of seed pixels.
   * @return a new Image, a mosaic of the original.
   * @throws IllegalArgumentException if the user passes an int larger than the total number of
   *                                  pixels or less than 1.
   */
  private static IImage mosaic(IImage image, List<String> args) throws IllegalArgumentException {
    // validate input
    if (args.size() < 1) {
      throw new IllegalArgumentException("Number of seed pixels must be "
              + "specified for a mosaic.");
    }
    int numSeeds;
    try {
      numSeeds = Integer.parseInt(args.get(0));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Desired number of seed pixels must be an integer.");
    }

    // set initial variables, load in image pixels. Ensure number of requested seeds does not
    // exceed number of pixels.
    int[][][] pixels = image.getPixels();
    int w = image.getWidth();
    int h = image.getHeight();
    if (numSeeds > w * h || numSeeds < 1) {
      throw new IllegalArgumentException("Number of seeds may not exceed the number of pixels in"
              + " the image or be less than 1. Number of requested seeds: " + numSeeds + "; "
              + "Number of pixels: " + w * h + ".");
    }

    // get array of pixels to keep track of seeds/nonseeds
    ArrayList<ClusteredPixel> pixelArray = new ArrayList<ClusteredPixel>();
    for (int row = 0; row < h; row++) {
      for (int col = 0; col < w; col++) {
        pixelArray.add(new ClusteredPixel(row, col, pixels[row][col]));
      }
    }

    ArrayList<ArrayList<ClusteredPixel>> seedArray = mapPixels(pixelArray, pixels, numSeeds);

    return new Image(getMosaicPixels(pixels, seedArray));
  }

  /**
   * generates a list of seed pixels and maps all other pixels in the image to the nearest seed
   * pixel. Returns a seed array representing this data.
   *
   * @param pixelArray a List of all pixels in the original image.
   * @param pixels     a 3D integer array representing the data of the original image.
   * @param numSeeds   an int, the user-specified number of seed pixels.
   * @return a List of Lists of pixels, representing the groupings of pixels to their seeds.
   */
  private static ArrayList<ArrayList<ClusteredPixel>> mapPixels(
          ArrayList<ClusteredPixel> pixelArray, int[][][] pixels, int numSeeds) {
    // get seed pixels
    ArrayList<ArrayList<ClusteredPixel>> seedArray = new ArrayList<ArrayList<ClusteredPixel>>();
    Random r = new Random();
    for (int seed = 0; seed < numSeeds; seed++) {
      int randRow = r.nextInt(pixels.length);
      int randCol = r.nextInt(pixels[0].length);
      ClusteredPixel seedPixel = new ClusteredPixel(randRow, randCol, pixels[randRow][randCol]);

      // if pixel hasn't already been chosen as a seed, add it to seedArray
      if (pixelArray.contains(seedPixel)) {
        pixelArray.remove(seedPixel);
        ArrayList<ClusteredPixel> seedRow = new ArrayList<ClusteredPixel>();
        seedRow.add(seedPixel);
        seedArray.add(seedRow);
      } else {
        seed--;
      }
    }

    // map every pixel to a seedpixel
    for (ClusteredPixel p : pixelArray) {
      int prow = p.getRow();
      int pcol = p.getCol();

      double dist = Double.POSITIVE_INFINITY;
      int assignedSeed = -1;
      int index = 0;
      for (ArrayList<ClusteredPixel> s : seedArray) {
        int srow = s.get(0).getRow();
        int scol = s.get(0).getCol();
        double distToCompare = sqrt(pow((srow - prow), 2) + pow((scol - pcol), 2));
        if (distToCompare < dist) {
          dist = distToCompare;
          assignedSeed = index;
        }
        index++;
      }

      seedArray.get(assignedSeed).add(p);
    }

    return seedArray;
  }

  /**
   * Takes the array of seeded pixels and the original image, and produces a new 3D integer array
   * representing the newly mosaic'd image.
   *
   * @param original  a 3D int array, the original image
   * @param seedArray a List of Lists of seedPixels and their matched pixels
   * @return a 3D integer array, the newly generated image based on the seedArray
   */
  private static int[][][] getMosaicPixels(int[][][] original,
                                           ArrayList<ArrayList<ClusteredPixel>> seedArray) {
    int[][][] mosaicImage = new int[original.length][original[0].length][3];
    // color each group into the avg color of all the group's pixels
    for (ArrayList<ClusteredPixel> s : seedArray) {
      int avgRed = 0;
      int avgBlue = 0;
      int avgGreen = 0;

      for (ClusteredPixel pixel : s) {
        avgRed += pixel.getColors()[0];
        avgBlue += pixel.getColors()[1];
        avgGreen += pixel.getColors()[2];
      }

      avgRed = avgRed / s.size();
      avgBlue = avgBlue / s.size();
      avgGreen = avgGreen / s.size();

      // change color channels of modified image
      for (ClusteredPixel pixel : s) {
        mosaicImage[pixel.getRow()][pixel.getCol()][0] = avgRed;
        mosaicImage[pixel.getRow()][pixel.getCol()][1] = avgBlue;
        mosaicImage[pixel.getRow()][pixel.getCol()][2] = avgGreen;
      }
    }

    return mosaicImage;
  }

}
