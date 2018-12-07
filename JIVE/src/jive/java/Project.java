package jive.java;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;

/**
 * Project consists of methods to edit BufferedImage objects and manage editing projects.
 * <br><br>
 * Actual image editing is performed by the ImageEditor class and the
 * changes are applied to the Project's bufferedImage attribute.
 * <br><br>
 * Images can be saved to file or converted to different raster file formats (JPEG, PNG, BMP, and GIF).
 * <br><br>
 * Undo and redo functionality are implemented using the stateHistory and undoHistory
 * stacks to store BufferedImage objects in memory until a new project is opened or
 * the ImageEditor instance is destroyed.
 * 
 * (Requirement 3.0.0)
 * 
 * @author Devon Hunter
 * @author Casey Brown
 *
 */
public class Project
{
	private final List<String> COMPATIBLE_WRITE_FORMATS = getWriteFileTypes();

	private ImageEditor imageEditor;
	private BufferedImage bufferedImage;
	private File imageFile;
	private String fileExtension;
	private Stack<BufferedImage> stateHistory;
	private Stack<BufferedImage> undoHistory;	
	private int changesSinceSave;
	
	public Project(File imageFile) throws IOException
	{
		imageEditor = new ImageEditor();
		bufferedImage = ImageIO.read(imageFile);		
		this.imageFile = imageFile;
		fileExtension = findFileExtension(imageFile);				
		stateHistory = new Stack<BufferedImage>();
		undoHistory = new Stack<BufferedImage>();
		changesSinceSave = 0;
	}
	
	/**
	 * Saves the Project's BufferedImage object to disk.
	 * This function overwrites the bufferedImage's original file.
	 * (Requirement 3.1.1)
	 * 
	 * @return True if successful, false otherwise
	 */
	public List<String> getWriteFileTypes() {
		String writeTypes[] = ImageIO.getWriterFormatNames();
		for(int i=0;i<writeTypes.length;i++) {
			writeTypes[i]="*."+writeTypes[i];
		}
		return new ArrayList<String>(Arrays.asList(writeTypes));
		
	}
	public boolean save()
	{
		try
		{
			ImageIO.write(bufferedImage, fileExtension, imageFile);
			changesSinceSave = 0;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Saves the Project's BufferedImage object to a new file.
	 * Images can be converted between JPG, PNG, BMP, and GIF.
	 * 
	 * The alpha channel is removed and the image is drawn to
	 * a white background if the new file type doesn't support transparency.
	 * 
	 * (Requirements 3.1.2/3.2.1)
	 * 
	 * @param newFile The destination file of the bufferedImage
	 * @return True if successful, false otherwise.
	 */
	public boolean saveAs(File newFile)
	{
		String newFileExtension = findFileExtension(newFile);
		boolean hasAlphaChannel = bufferedImage.getColorModel().hasAlpha();
				
		if (!COMPATIBLE_WRITE_FORMATS.contains("*." + newFileExtension)||newFileExtension.equals("WBMP")||newFileExtension.equals("wbmp"))
			return false;
		
		try
		{
			if (hasAlphaChannel && (newFileExtension.equals("jpg") | newFileExtension.equals("bmp")))
			{				
				int width = bufferedImage.getWidth();
				int height = bufferedImage.getHeight();
				BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				newImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
				
				ImageIO.write(newImage, newFileExtension, newFile);
				newImage.flush();
			}
			else
			{
				ImageIO.write(bufferedImage, newFileExtension, newFile);
			}
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Undo the most recent editing function.
	 * Reverts the bufferedImage to its previous state.
	 * (Requirement 3.4.0)
	 */
	public void undo()
	{
		undoHistory.push(bufferedImage);
		bufferedImage = stateHistory.pop();
		--changesSinceSave;
	}
	
	/**
	 * Redo the most recent undone action.
	 * Reapplies the change that was undone.
	 * (Requirement 3.5.0)
	 */
	public void redo()
	{
		stateHistory.push(bufferedImage);
		bufferedImage = undoHistory.pop();
		++changesSinceSave;
	}
	
	/**
	 * Rotates the bufferedImage 90* clockwise and updates relevant
	 * project attributes.
	 * (Requirement 2.1.1)
	 * 
	 * @see ImageEditor#rotateRight(BufferedImage)
	 */
	public void rotateRight()
	{
		stateHistory.push(bufferedImage);
		bufferedImage = imageEditor.rotateRight(bufferedImage);
		undoHistory.clear();
		++changesSinceSave;
	}
	
	/**
	 * Rotates the bufferedImage 90* counter-clockwise and
	 * updates relevant project attributes.
	 * (Requirement 2.1.2)
	 * 
	 * @see ImageEditor#rotateLeft(BufferedImage)
	 */
	public void rotateLeft()
	{
		stateHistory.push(bufferedImage);
		bufferedImage = imageEditor.rotateLeft(bufferedImage);
		undoHistory.clear();
		++changesSinceSave;
	}
	
	/**
	 * Mirrors the bufferedImage horizontally and updates
	 * relevant project attributes.
	 * (Requirement 2.2.1)
	 * 
	 * @see ImageEditor#flipHorizontal(BufferedImage)
	 */
	public void flipHorizontal()
	{
		stateHistory.push(bufferedImage);
		bufferedImage = imageEditor.flipHorizontal(bufferedImage);
		undoHistory.clear();
		++changesSinceSave;
	}
	
	/**
	 * Mirrors the bufferedImage vertically and updates
	 * relevant project attributes.
	 * (Requirement 2.2.2)
	 * 
	 * @see ImageEditor#flipVertical(BufferedImage)
	 */
	public void flipVertical()
	{
		stateHistory.push(bufferedImage);
		bufferedImage = imageEditor.flipVertical(bufferedImage);
		undoHistory.clear();
		++changesSinceSave;
	}
	
	/**
	 * Crops the bufferedImage using the specified coordinates and dimensions
	 * and updates relevant project attributes.
	 * (Requirement 2.3.5)
	 * 
	 * @param x - The X coordinate of the upper-left corner of the crop area
	 * @param y - The Y coordinate of the upper-left corner of the crop area
	 * @param width - The width of the crop area
	 * @param height - The height of the crop area
	 * 
	 * @see ImageEditor#crop(BufferedImage, int, int, int, int)
	 */
	public void crop(int x, int y, int width, int height)
	{
		stateHistory.push(bufferedImage);
		bufferedImage = imageEditor.crop(bufferedImage, x, y, width, height);
		undoHistory.clear();
		++changesSinceSave;
	}
	
	/**
	 * Resizes the bufferedImage by the specified factor and
	 * updates relevant project attributes.
	 * (Requirement 2.4.1)
	 * 
	 * @param scaleFactor - the percent to resize by (0.0 to 1.0)
	 * @see ImageEditor#resize(BufferedImage, double)
	 */
	public void resize(double scaleFactor)
	{
		stateHistory.push(bufferedImage);
		bufferedImage = imageEditor.resize(bufferedImage, scaleFactor);
		undoHistory.clear();
		++changesSinceSave;
	}
	
	/**
	 * Applies brightness and contrast adjustments to the bufferedImage. The bufferedImage attribute is
	 * updated to reflect the adjustments and relevant project attributes are updated.
	 * <br><br>
	 * To preview an adjustment without actually making changes to the project,
	 * use the previewBrightnessContrast() function.
	 * (Requirement 2.5.0)
	 * 
	 * @param brightnessAdjustment - the offset to be applied to each pixel (-100.0 to 100.0)
	 * @param contrastAdjustment - the value to scale the pixel by (0.0 to 2.0)
	 * 
	 * @see ImageEditor#adjustBrightnessContrast(BufferedImage, double, double)
	 * @see #previewBrightnessContrast(double, double)
	 */
	public void adjustBrightnessContrast(double brightnessAdjustment, double contrastAdjustment)
	{
		stateHistory.push(bufferedImage);
		bufferedImage = imageEditor.adjustBrightnessContrast(bufferedImage, brightnessAdjustment, contrastAdjustment);
		undoHistory.clear();
		++changesSinceSave;
	}
	
	/**
	 * Applies brightness and contrast adjustments to the bufferedImage but
	 * doesn't store the result or modify any project attributes.
	 * (Requirement 2.5.3)
	 * 
	 * @param brightnessAdjustment - the offset to be applied to each pixel (-100.0 to 100.0)
	 * @param contrastAdjustment - the value to scale each pixel by (0.0 to 2.0)
	 * @return the current bufferedImage with brightness and contrast adjustments applied (but not stored)
	 * 
	 * @see ImageEditor#adjustBrightnessContrast(BufferedImage, double, double)
	 */
	public BufferedImage previewBrightnessContrast(double brightnessAdjustment, double contrastAdjustment)
	{
		BufferedImage previewImage = imageEditor.adjustBrightnessContrast(bufferedImage, brightnessAdjustment, contrastAdjustment);
		return previewImage;
	}
	
	
	/**
	 * Checks if there are BufferedImage objects in the undoHistory stack
	 * (Requirement 3.5.1)
	 * 
	 * @return True if the stack is empty, false otherwise.
	 */
	public boolean isRedoAvailable()
	{
		return !undoHistory.isEmpty();
	}
	
	/**
	 * Checks if there are BufferedImage objects in the stateHistory stack
	 * (Requirement 3.4.1)
	 * 
	 * @return True if the stack is empty, false otherwise.
	 */
	public boolean isUndoAvailable()
	{
		return !stateHistory.isEmpty();
	}
	
	/**
	 * Check if the project has unsaved changes
	 * @return true if it has unsaved changes, false otherwise
	 */
	public boolean hasUnsavedChanges()
	{
		return changesSinceSave != 0;
	}
	
	
	/**
	 * Get the name of the file of the current project
	 * (Requirement 1.3.0)
	 * 
	 * @return The file name as a string
	 */
	public String getName()
	{
		return imageFile.getName();
	}
	
	/**
	 * Get the file extension as a String
	 * @return The file extension of the current project
	 */
	public String getFileExtension()
	{
		return fileExtension;
	}
	
	/**
	 * @return a reference to the bufferedImage attribute
	 */
	public BufferedImage getImage()
	{
		return bufferedImage;
	}
	
	/**
	 * (Requirement 1.3.1)
	 * @return The height of the class's BufferedImage attribute
	 */
	public int getHeight()
	{
		return bufferedImage.getHeight();
	}
	
	/**
	 * (Requirement 1.3.1)
	 * @return The width of the class's BufferedImage attribute
	 */
	public int getWidth()
	{
		return bufferedImage.getWidth();
	}
	
	/**
	 * Gets the file extension of a file
	 * @param file - A File object
	 * @return The extension of the file
	 */
	private String findFileExtension(File file)
	{
		String fileName = file.getName();
		int extensionIndex = fileName.lastIndexOf(".");
		String extension = fileName.substring(extensionIndex + 1);
		return extension;
	}
}
