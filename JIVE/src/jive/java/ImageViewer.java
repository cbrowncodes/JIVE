package jive.java;


import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

/**
 * ImageViewer displays JavaFX images in a BorderPane.
 * Images are centered and resized as necessary.
 * The ImageView binding is updated dynamically during window resizing.
 * Aspect ratio is always preserved during resizing.
 * 
 * (Requirement 1.2.0)
 * 
 * @author Devon Hunter
 *
 */
public class ImageViewer extends BorderPane
{
	private Image image;
	private ImageView imageView;
	
	public ImageViewer()
	{
		imageView = new ImageView();
		imageView.setPreserveRatio(true);
		this.heightProperty().addListener(resizeListener);
		this.widthProperty().addListener(resizeListener);
	}
	
	/**
	 * Set a new image to be displayed in the ImageViewer.
	 * Images larger than the pane will be resized to fit, smaller images are shown true-to-size.
	 * (Requirements 1.2.1/1.2.2)
	 * 
	 * @param newImage The image to show in the ImageViewer
	 */
	public void update(Image newImage)
	{
		image = newImage;
		adjustImageBinding();
		imageView.setImage(image);
		this.setCenter(imageView);
	}
	
	/**
	 * Gets the ImageViewer's ImageView attribute
	 * @return the current ImageView
	 */
	public ImageView getImageView()
	{
		return imageView;
	}
	
	/**
	 * This function binds or unbinds the imageView's fit properties as necessary
	 */
	private void adjustImageBinding()
	{
		if (image.getHeight() > this.getHeight() || image.getWidth() > this.getWidth())
		{
			imageView.fitHeightProperty().bind(this.heightProperty());
			imageView.fitWidthProperty().bind(this.widthProperty());
		}
		else
		{
			imageView.fitHeightProperty().unbind();
			imageView.fitWidthProperty().unbind();
			imageView.setFitHeight(image.getHeight());
			imageView.setFitWidth(image.getWidth());
		}
	}
	
	/**
	 * Dynamically binds or unbinds imageView's fit properties according to window size
	 * (Requirement 1.2.3)
	 */
	ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
		if (image != null)
		{
			adjustImageBinding();
		}
	};
}
