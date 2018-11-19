package jive.java;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;

/**
 * Project consists of methods to edit BufferedImage objects and manage editing projects.
 * 
 * Image editing functions are extended from the ImageEditor class.
 * All editing functions return a newly-edited BufferedImage object.
 * 
 * Edited images can be saved to file or converted to different raster file formats.
 * 
 * Undo and redo functionality can be implemented using the stateHistory and undoHistory
 * variables to store BufferedImage objects in memory until a new project is opened or
 * the ImageEditor instance is destroyed.
 * 
 * @author Devon Hunter
 * @author Craig Vandeventer
 * @author Casey Brown
 *
 */
public class Project extends ImageEditor
{
	private File imageFile;
	private String fileExtension;
	private Stack<BufferedImage> stateHistory;
	private Stack<BufferedImage> undoHistory;	
	private boolean hasUnsavedChanges;
	
	/**
	 * Initializes state history, undo history, and hasUnsavedChanges
	 * @param A File object of the image to create a project from
	 */
	public Project(File imageFile) throws IOException
	{
		super(ImageIO.read(imageFile));
		
		this.imageFile = imageFile;
		
		fileExtension = findFileExtension(imageFile);				
		stateHistory = new Stack<BufferedImage>();
		undoHistory = new Stack<BufferedImage>();
		hasUnsavedChanges = false;
	}
	
	/**
	 * Saves the Project's BufferedImage object to disk
	 * This function overwrites the bufferedImage's original file.
	 * @return True if successful, false otherwise
	 */
	public boolean save()
	{
		try
		{
			ImageIO.write(bufferedImage, fileExtension, imageFile);
			hasUnsavedChanges = false;
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
	 * @param newFile The destination file of the bufferedImage
	 * @return True if successful, false otherwise.
	 */
	public boolean saveAs(File newFile)
	{
		String newFileExtension = findFileExtension(newFile);
		boolean hasAlphaChannel = bufferedImage.getColorModel().hasAlpha();
		
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
	 * This method reverts the bufferedImage object to its previous state.
	 * @return A javaFX Image of the previous state
	 */
	public BufferedImage undo()
	{
		undoHistory.push(bufferedImage);
		bufferedImage = stateHistory.pop();
		return bufferedImage;
	}
	
	/**
	 * This method reverts the bufferedImage object to its previous state.
	 * @return A javaFX Image of the previous state
	 */
	public BufferedImage redo()
	{
		stateHistory.push(bufferedImage);
		bufferedImage = undoHistory.pop();
		return bufferedImage;
	}
	
	/**
	 * Pushes the ImageEditor's current BufferedImage object onto a stack of previous states.
	 * This method should be called before every editing function for undo/redo functionality.
	 */
	public void storeState()
	{
		stateHistory.push(bufferedImage);
	}
	
	/**
	 * Checks if there are BufferedImage objects in the undoHistory stack
	 * @return True if the stack is empty, false otherwise.
	 */
	public boolean undoHistoryIsEmpty()
	{
		return undoHistory.isEmpty();
	}
	
	/**
	 * Checks if there are BufferedImage objects in the stateHistory stack
	 * @return True if the stack is empty, false otherwise.
	 */
	public boolean stateHistoryIsEmpty()
	{
		return stateHistory.isEmpty();
	}
	
	/**
	 * Empties the undoHistory stack
	 */
	public void clearUndoHistory()
	{
		undoHistory.clear();
	}
	
	/**
	 * Check if the project has unsaved changes
	 * @return true if it has unsaved changes, false otherwise
	 */
	public boolean hasUnsavedChanges()
	{
		return hasUnsavedChanges;
	}
	
	/**
	 * Get the name of the file of the current project
	 * @return The file name as a string
	 */
	public String getName()
	{
		return imageFile.getName();
	}
	
	/**
	 * Toggle the hasUnsavedChanges variable
	 * @param unsavedChanges
	 */
	public void setHasUnsavedChanges(boolean unsavedChanges)
	{
		hasUnsavedChanges = unsavedChanges;
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
	 * Gets the file extension of a file
	 * @param file A File object
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
