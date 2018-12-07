package jive.java;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * PhotoReel loads all image files from a directory into an array
 * and keeps track of the current position within the directory.
 * 
 * Photos are sorted alphabetically on the reel.
 * 
 * (Requirement 1.4.0)
 * 
 * @author Devon Hunter
 *
 */
public class PhotoReel
{
	private final List<String> COMPATIBLE_FORMATS = Arrays.asList(".jpg", ".png", ".bmp", ".gif");
	private File[] directoryImages;
	private int currentPosition;
	
	/**
	 * Gets every image file in imageFile's directory, stores the files in alphabetical order,
	 * and finds the position of the supplied imageFile in the array.
	 * @param imageFile - the image file to create a photo reel from
	 */
	public PhotoReel(File imageFile)
	{
		File parentDirectory = new File(imageFile.getParent());
		
		directoryImages = parentDirectory.listFiles(pathname ->
		{
			String fileName = pathname.getName();
			for (String format : COMPATIBLE_FORMATS)
			{
				if (fileName.endsWith(format))
					return true;
			}
			return false;
		});
		
		Arrays.sort(directoryImages);
		
		int position = 0;
		for (File file : directoryImages)
		{
			if (file.getName().equals(imageFile.getName()))
			{
				currentPosition = position;
				break;
			}
			++position;
		}
	}
	
	/**
	 * Gets the next image in the photo reel and updates the current position.
	 * (Requirement 1.4.1)
	 * 
	 * @return The next image file in the directory or null if the current position is
	 * the last file in the directory.
	 */
	public File getNext()
	{
		int newPosition = currentPosition + 1;
		
		if (newPosition < directoryImages.length)
		{
			currentPosition = newPosition;
			return directoryImages[newPosition];
		}
		else
			return null;
	}
	
	/**
	 * Gets the previous image in the photo reel and updates the current position.
	 * (Requirement 1.4.2)
	 * 
	 * @return The previous image file in the directory of null if the current position is
	 * the beginning of the directory.
	 */
	public File getPrevious()
	{
		int newPosition = currentPosition - 1;
		
		if (newPosition >= 0)
		{
			currentPosition = newPosition;
			return directoryImages[newPosition];
		}
		else
			return null;
	}
	
	/**
	 * Check if the current position is at the end of the photo reel
	 * (Requirement 1.4.3)
	 * 
	 * @return True if there is a next image to return, false otherwise
	 */
	public boolean hasNext()
	{
		if (currentPosition + 1 < directoryImages.length)
			return true;
		else
			return false;
	}
	
	/**
	 * Check if the current position is at the beginning of the photo reel
	 * (Requirement 1.4.4)
	 * 
	 * @return True if there is a previous image to return, false otherwise
	 */
	public boolean hasPrevious()
	{
		if (currentPosition - 1 >= 0)
			return true;
		else
			return false;
	}
}
