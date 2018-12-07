package jive.java;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * ImageEditor encompasses image editing functions. 
 * <br><br>
 * All editing functions require a BufferedImage object as an argument
 * and return newly edited BufferedImage object.
 * <br><br>
 * Images may be converted to different types as necessary to performing editing
 * operations. Colors and quality are preserved as much as possible.
 * 
 * @author Devon Hunter
 *
 */
public class ImageEditor
{	
	/**
	 * Uses an AffineTransform to rotate a BufferedImage 90 degrees clockwise.
	 * This function converts TYPE_BYTE_INDEXED images to TYPE_INT_ARGB
	 * (Requirement 2.1.1)
	 * 
	 * @return A rotated BufferedImage
	 */
	public BufferedImage rotateRight(BufferedImage bufferedImage)
	{
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		
		AffineTransform rotateTransform = new AffineTransform();
				
		rotateTransform.translate(height / 2, width / 2);	//Images with a height and/or width not divisible by 2 are rounded down to the nearest even number
		rotateTransform.rotate(Math.PI / 2);
		rotateTransform.translate(width / -2, height / -2);
		
		BufferedImage newImage;
		int imageType = bufferedImage.getType();
		
		if (imageType == BufferedImage.TYPE_BYTE_INDEXED)	//Indexed images are converted to TYPE_INT_ARGB to preserve transparency and colors
			newImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
		else
			newImage = new BufferedImage(height, width, imageType);
		
		AffineTransformOp rotateOp = new AffineTransformOp(rotateTransform, AffineTransformOp.TYPE_BILINEAR);
		rotateOp.filter(bufferedImage, newImage);
		
		return newImage;
	}
	
	/**
	 * Uses an AffineTransform to rotate a BufferedImage 90 degrees counter-clockwise.
	 * This function converts TYPE_BYTE_INDEXED images to TYPE_INT_ARGB
	 * (Requirement 2.1.2)
	 * 
	 * @return A rotated BufferedImage
	 */
	public BufferedImage rotateLeft(BufferedImage bufferedImage)
	{
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		
		AffineTransform rotateTransform = new AffineTransform();
		
		rotateTransform.translate(height / 2, width / 2);
		rotateTransform.rotate(Math.PI / -2);
		rotateTransform.translate(width / -2, height / -2);
		
		BufferedImage newImage;
		int imageType = bufferedImage.getType();
		
		if (imageType == BufferedImage.TYPE_BYTE_INDEXED)
			newImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
		else
			newImage = new BufferedImage(height, width, imageType);
		
		AffineTransformOp rotateOp = new AffineTransformOp(rotateTransform, AffineTransformOp.TYPE_BILINEAR);
		rotateOp.filter(bufferedImage, newImage);
		
		return newImage;
	}
	
	/**
	 * Mirrors a BufferedImage horizontally using an AffineTransform
	 * (Requirement 2.2.1)
	 * 
	 * @return A horizontally flipped BufferedImage
	 */
	public BufferedImage flipHorizontal(BufferedImage bufferedImage)
	{
		AffineTransform flipTransform = AffineTransform.getScaleInstance(-1, 1);
		flipTransform.translate(-bufferedImage.getWidth(), 0);
		
		AffineTransformOp flipOp = new AffineTransformOp(flipTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		bufferedImage = flipOp.filter(bufferedImage, null);
		return bufferedImage;
		
	}
	
	/**
	 * Mirrors a BufferedImage vertically using an AffineTransform
	 * (Requirement 2.2.2)
	 * 
	 * @return A vertically flipped BufferedImage
	 */
	public BufferedImage flipVertical(BufferedImage bufferedImage)
	{
		AffineTransform flipTransform = AffineTransform.getScaleInstance(1, -1);
		flipTransform.translate(0, -bufferedImage.getHeight());
		
		AffineTransformOp flipOp = new AffineTransformOp(flipTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		bufferedImage = flipOp.filter(bufferedImage, null);
		return bufferedImage;
	}

	/**
	 * Crops a BufferedImage using the specified coordinates and dimensions
	 * (Requirement 2.3.5)
	 * 
	 * @param x - The X coordinate of the upper-left corner of the crop area
	 * @param y - The Y coordinate of the upper-left corner of the crop area
	 * @param width - The width of the crop area
	 * @param height - The height of the crop area
	 * @return A cropped BufferedImage
	 */
	public BufferedImage crop(BufferedImage bufferedImage, int x, int y, int width, int height)
	{
		bufferedImage = bufferedImage.getSubimage(x, y, width, height);
		return bufferedImage;
	}
	
	/**
	 * Resizes a BufferedImage by the given factor
	 * (Requirement 2.4.1)
	 * 
	 * @param scaleFactor - The percent to scale by, between 0.0 and 1.0
	 * @return A resized BufferedImage
	 */
	public BufferedImage resize(BufferedImage bufferedImage, double scaleFactor)
	{
		int newWidth = (int) (bufferedImage.getWidth() * scaleFactor);
		int newHeight = (int) (bufferedImage.getHeight() * scaleFactor);
		
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(scaleFactor, scaleFactor);
		
		BufferedImage newImage;
		int imageType = bufferedImage.getType();
		
		if (imageType == BufferedImage.TYPE_BYTE_INDEXED)
			newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		else
			newImage = new BufferedImage(newWidth, newHeight, imageType);
		
		AffineTransformOp scaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
		scaleOp.filter(bufferedImage, newImage);

		return newImage;
	}
	
	/**
	 * Adjusts the brightness and contrast of a BufferedImage using RescaleOp.
	 * RescaleOp uses a version of the following algorithm to adjust brightness and contrast:
	 * 
	 * <br><br>
	 * newPixelColor = scaleFactor(currentPixelColor) + offset
	 * <br><br>
	 * 
	 * Contrast is affected by scaleFactor and brightness is affected by offset.
	 * (Requirements 2.5.1/2.5.2)
	 * 
	 * @param bufferedImage - the image to adjust
	 * @param brightnessAdjustment - the offset to apply to each pixel (-100.0 to 100.0)
	 * @param contrastAdjustment - the value to scale each pixel by (0.0 to 2.0)
	 * @return a BufferedImage with the appropriate brightness and contrast adjustments
	 */
	public BufferedImage adjustBrightnessContrast(BufferedImage bufferedImage, double brightnessAdjustment, double contrastAdjustment)
	{			
		//RescaleOp doesn't support images with indexed color
		//Images with indexed color (PNGs and GIFs, typically) are converted to ARGB
		if (bufferedImage.getType() == BufferedImage.TYPE_BYTE_INDEXED)
		{
			BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
			newImage.createGraphics().drawImage(bufferedImage, 0, 0, null);
			bufferedImage = newImage;
		}
		
		RescaleOp brightnessOp;
		float offset = (float) brightnessAdjustment;
		float scaleFactor = (float) contrastAdjustment;
		
		if (bufferedImage.getColorModel().hasAlpha())
		{
			float[] scaleFactors = {scaleFactor, scaleFactor, scaleFactor, 1}; //Alpha channel is not adjusted
			float[] offsets = {offset, offset, offset, 0};
			brightnessOp = new RescaleOp(scaleFactors, offsets, null);
		}
		else
		{
			brightnessOp = new RescaleOp(scaleFactor, offset, null);
		}
				
		bufferedImage = brightnessOp.filter(bufferedImage, null);
		
		return bufferedImage;
	}
}
