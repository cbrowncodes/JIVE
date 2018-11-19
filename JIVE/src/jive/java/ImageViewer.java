package jive.java;

import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

/**
 * ImageViewer displays JavaFX images in a BorderPane.
 * Images are centered and resized if necessary.
 * The ImageView binding is updated dynamically during window resizing.
 * Image ratio is preserved during resizing.
 * 
 * @author Devon Hunter
 * @author Craig Vandeventer
 * @author Casey Brown
 *
 */
public class ImageViewer extends BorderPane
{
	Image image;
	ImageView imageView;
	
	//The resizeListener dynamically binds or unbinds imageView's fit properties according to window size
	ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
		if (image != null)
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
	};
	
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
	 * 
	 * @param newImage The image to show in the ImageViewer
	 */
	public void update(Image newImage)
	{
		image = newImage;
		
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
		
		imageView.setImage(image);
		this.setCenter(imageView);
	}
}
