package jive.java;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

//TODO: implement metadata editing functions

/**
 * ImageEditor encompasses all image editing functions. 
 * The functions operate on the bufferedImage attribute and return edited BufferedImage objects
 * 
 * @author Devon Hunter
 * @author Craig Vandeventer
 * @author Casey Brown
 *
 */
public class ImageEditor
{
	protected BufferedImage bufferedImage;
	private int imageType;
	
	public ImageEditor(BufferedImage image)
	{
		bufferedImage = image;
		imageType = bufferedImage.getType();
	}
	
	/**
	 * Uses an AffineTransform to rotate a BufferedImage 90 degrees clockwise.
	 * This function converts GIFs to TYPE_INT_ARGB to preserve transparency
	 * @return A rotated BufferedImage
	 */
	public BufferedImage rotateRight()
	{
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		
		AffineTransform rotateTransform = new AffineTransform();
				
		rotateTransform.translate(height / 2, width / 2);	//Images with a height and/or width not divisible by 2 are rounded down to the nearest even number
		rotateTransform.rotate(Math.PI / 2);
		rotateTransform.translate(width / -2, height / -2);
		
		BufferedImage newImage;
		
		if (imageType == BufferedImage.TYPE_BYTE_INDEXED)	//GIFs are converted to TYPE_INT_ARGB to preserve transparency and colors
			newImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
		else
			newImage = new BufferedImage(height, width, imageType);
		
		AffineTransformOp rotateOp = new AffineTransformOp(rotateTransform, AffineTransformOp.TYPE_BILINEAR);
		rotateOp.filter(bufferedImage, newImage);
		bufferedImage = newImage;
		newImage.flush();
		
		return bufferedImage;
	}
	
	/**
	 * Uses an AffineTransform to rotate a BufferedImage 90 degrees counter-clockwise.
	 * This function converts GIFs to TYPE_INT_ARGB to preserve transparency
	 * @return A rotated BufferedImage
	 */
	public BufferedImage rotateLeft()
	{
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		
		AffineTransform rotateTransform = new AffineTransform();
		
		rotateTransform.translate(height / 2, width / 2);
		rotateTransform.rotate(Math.PI / -2);
		rotateTransform.translate(width / -2, height / -2);
		
		BufferedImage newImage;
		
		if (imageType == BufferedImage.TYPE_BYTE_INDEXED)
			newImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
		else
			newImage = new BufferedImage(height, width, imageType);
		
		AffineTransformOp rotateOp = new AffineTransformOp(rotateTransform, AffineTransformOp.TYPE_BILINEAR);
		rotateOp.filter(bufferedImage, newImage);
		bufferedImage = newImage;
		newImage.flush();
		return bufferedImage;
	}
	
	/**
	 * Mirrors the bufferedImage horizontally
	 * @return A flipped BufferedImage
	 */
	public BufferedImage flipHorizontal()
	{
		AffineTransform flipTransform = AffineTransform.getScaleInstance(-1, 1);
		flipTransform.translate(-bufferedImage.getWidth(), 0);
		
		AffineTransformOp flipOp = new AffineTransformOp(flipTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		bufferedImage = flipOp.filter(bufferedImage, null);
		return bufferedImage;
		
	}
	
	/**
	 * Mirrors the bufferedImage vertically
	 * @return A flipped BufferedImage
	 */
	public BufferedImage flipVertical()
	{
		AffineTransform flipTransform = AffineTransform.getScaleInstance(1, -1);
		flipTransform.translate(0, -bufferedImage.getHeight());
		
		AffineTransformOp flipOp = new AffineTransformOp(flipTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		bufferedImage = flipOp.filter(bufferedImage, null);
		return bufferedImage;
	}

	/**
	 * Crops the bufferedImage using the specified coordinates and dimensions
	 * 
	 * @param x - The X coordinate of the upper-left corner of the crop area
	 * @param y - The Y coordinate of the upper-left corner of the crop area
	 * @param width - The width of the crop area
	 * @param height - The height of the crop area
	 * @return A cropped BufferedImage
	 */
	public BufferedImage crop(int x, int y, int width, int height)
	{
		bufferedImage = bufferedImage.getSubimage(x, y, width, height);
		return bufferedImage;
	}
	
	/**
	 * Resizes the bufferedImage by the given factor
	 * @param scaleFactor - The percent to scale by, between 0.0 and 1.0
	 * @return A resized BufferedImage
	 */
	public BufferedImage resize(double scaleFactor)
	{
		int newWidth = (int) (bufferedImage.getWidth() * scaleFactor);
		int newHeight = (int) (bufferedImage.getHeight() * scaleFactor);
		
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(scaleFactor, scaleFactor);
		
		BufferedImage newImage;
		if (imageType == BufferedImage.TYPE_BYTE_INDEXED)
			newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		else
			newImage = new BufferedImage(newWidth, newHeight, imageType);
		
		AffineTransformOp scaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
		scaleOp.filter(bufferedImage, newImage);
		bufferedImage = newImage;
		newImage.flush();
		return bufferedImage;
	}
	
	//TODO:
	public BufferedImage editMetadata()
	{
		return null;
	}
	
	/**
	 * @return The height of the class's BufferedImage attribute
	 */
	public int getHeight()
	{
		return bufferedImage.getHeight();
	}
	
	/**
	 * @return The width of the class's BufferedImage attribute
	 */
	public int getWidth()
	{
		return bufferedImage.getWidth();
	}
}
