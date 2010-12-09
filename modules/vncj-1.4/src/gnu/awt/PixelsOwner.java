package gnu.awt;

/**
* Access to a rectangular pixel raster as an array. Pixel arrays are composed of integers,
* with each integer corresponding to a pixel. The arrangement is left to right, top to
* bottom.
**/

public interface PixelsOwner
{
	public int[] getPixels();
	public void setPixelArray( int[] pixelArray, int pixelWidth, int pixelHeight );
	public int getPixelWidth();
	public int getPixelHeight();
}
