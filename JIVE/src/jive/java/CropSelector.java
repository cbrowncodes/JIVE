package jive.java;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

/**
 * This class creates and manages a crop selector tool using mouse events. 
 * It allows the user to draw a rectangle over an ImageView to select an area to crop.
 * 
 * The crop area is limited to the bounds of the ImageView in its parent node.
 * 
 * When a crop area is drawn, the CropSelector calculates the X and Y coordinates of the
 * area relative to the bounds of the ImageView. 
 * 
 * The X, Y, width, and height of the crop area are corrected if the ImageView is scaled
 * to fit in its parent node.
 * 
 * This class also enables and disables the confirmCropButton as appropriate.
 * 
 * @author Devon Hunter
 *
 */
public class CropSelector
{
    private Rectangle rectangle;
    private Pane pane;
    private ImageView image;
    private Button confirmButton;
    private Bounds bounds;
    
    private boolean illegalStart;
    private double startX;
    private double startY;
    private double rectangleMinBoundX;
    private double rectangleMinBoundY;
    private double rectangleMaxBoundX;
    private double rectangleMaxBoundY;
    private double imageWidth;
    private double imageHeight;
    private double displayWidth;
    private double displayHeight;
    private double aspectRatio;
    private int cropX;
    private int cropY;
    private int cropWidth;
    private int cropHeight;

    public CropSelector(Pane pane, ImageView image, Button cropConfirmButton)
    {
        this.pane = pane;
        this.image = image;
        confirmButton = cropConfirmButton;
        confirmButton.setDisable(true);
        
        pane.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressEvent);
        pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragEvent);
        pane.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleaseEvent);
        pane.widthProperty().addListener(listener);
        pane.heightProperty().addListener(listener);
        
        rectangle = new Rectangle();
        rectangle.setStroke(Color.ROYALBLUE);
        rectangle.setStrokeWidth(1);
        rectangle.setStrokeLineCap(StrokeLineCap.ROUND);
        rectangle.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.5));
        
        bounds = image.getBoundsInParent();
        rectangleMinBoundX = bounds.getMinX();
        rectangleMinBoundY = bounds.getMinY();
        rectangleMaxBoundX = bounds.getMaxX();
        rectangleMaxBoundY = bounds.getMaxY();
        
        imageWidth = image.getImage().getWidth();
        imageHeight = image.getImage().getHeight();
        aspectRatio = imageWidth/imageHeight;
        displayWidth = Math.min(image.getFitWidth(), image.getFitHeight() * aspectRatio);
        displayHeight = Math.min(image.getFitHeight(), image.getFitWidth() / aspectRatio);
    }
    
    EventHandler<MouseEvent> mousePressEvent = new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent event)
        {
        	pane.getChildren().remove(rectangle);
        	
        	confirmButton.setDisable(true);
        	
            if (event.isSecondaryButtonDown() )
                return;
            
            bounds = image.getBoundsInParent();
            
            if (!bounds.contains(event.getX(), event.getY()))
            {
            	illegalStart = true;	//illegalStart is used to stop mouseDragEvent from drawing a rectangle
            	rectangle.setWidth(0);
            	rectangle.setHeight(0);
            	return;
            }
            else
            {
            	illegalStart = false;
            }
            
            if (rectangleMinBoundX != bounds.getMinX() || rectangleMinBoundY != bounds.getMinY())
            {	
            	rectangleMinBoundX = bounds.getMinX();
            	rectangleMaxBoundX = bounds.getMaxX();
            	rectangleMinBoundY = bounds.getMinY();
            	rectangleMaxBoundY = bounds.getMaxY();
            }

            startX = event.getX();
            startY = event.getY();

            rectangle.setX(startX);
            rectangle.setY(startY);
            rectangle.setWidth(0);
            rectangle.setHeight(0);
            
            pane.getChildren().add(rectangle);
        }
    };

    EventHandler<MouseEvent> mouseDragEvent = new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent event)
        {
            if (event.isSecondaryButtonDown())
                return;
            
            if (illegalStart)
            	return;
            
            double newX = Math.min(startX, event.getX());
            double newY = Math.min(startY, event.getY());
            double width = Math.abs(event.getX() - startX);
            double height = Math.abs(event.getY() - startY);
            double maxWidth = rectangleMaxBoundX - newX;
            double maxHeight = rectangleMaxBoundY - newY;

            if (newX < rectangleMinBoundX)
            {
            	rectangle.setX(rectangleMinBoundX);
            	width = rectangle.getWidth();
            }
            else
            	rectangle.setX(newX);
            
            if (newY < rectangleMinBoundY)
            {
            	rectangle.setY(rectangleMinBoundY);
            	height = rectangle.getHeight();
            }
            else
            	rectangle.setY(newY);
            
            if (event.getX() > rectangleMaxBoundX)
            {
            	rectangle.setWidth(maxWidth);
            }
            else
            	rectangle.setWidth(width);
            
            if (event.getY() > rectangleMaxBoundY)
            	rectangle.setHeight(maxHeight);
            else
            	rectangle.setHeight(height);
        }
    };

    EventHandler<MouseEvent> mouseReleaseEvent = new EventHandler<MouseEvent>()
    {
        @Override
        public void handle(MouseEvent event)
        {
            if (event.isSecondaryButtonDown())
                return;

            if (rectangle.getWidth() == 0 || rectangle.getHeight() == 0)
            {
            	pane.getChildren().remove(rectangle);
            	return;
            }
          
            //Calculate the crop area
            Bounds cropBounds = rectangle.getBoundsInParent();
            cropX = (int) (cropBounds.getMinX() - rectangleMinBoundX);
            cropY = (int) (cropBounds.getMinY() - rectangleMinBoundY);
            cropWidth = (int) rectangle.getWidth();
            cropHeight = (int) rectangle.getHeight();
            
            //Correct for scaled images, if necessary
            if (displayWidth < imageWidth)
            {
            	double correctionFactor = imageWidth / displayWidth;
            	cropX = (int) (cropX * correctionFactor);
            	cropWidth = (int) (cropWidth * correctionFactor);
            }
            
            if (displayHeight < imageHeight)
            {
            	double correctionFactor = imageHeight / displayHeight;
            	cropY = (int) (cropY * correctionFactor);
            	cropHeight = (int) (cropHeight * correctionFactor);
            }
            
            confirmButton.setDisable(false);
        }
    };
    
    ChangeListener<Number> listener = (observable, oldValue, newValue) -> 
    {
    	pane.getChildren().remove(rectangle);
    	confirmButton.setDisable(true);
    	displayWidth = Math.min(image.getFitWidth(), image.getFitHeight() * aspectRatio);
    	displayHeight = Math.min(image.getFitHeight(), image.getFitWidth() / aspectRatio);
	};
    
	/**
	 * Removes all aspects of the CropSelector from the parent node
	 */
    public void remove()
    {
    	pane.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressEvent);
        pane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragEvent);
        pane.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleaseEvent);        
        pane.getChildren().remove(rectangle);
    }
    
    /**
     * Gets the X coordinate of the crop area.
     * 
     * The value of cropX is corrected if images are scaled to fit
     * in the ImageView.
     * 
     * @return The X coordinate of the rectangular crop area
     */
    public int getCropX()
    {    	
    	return cropX;
    }
    
    /**
     * Gets the Y coordinate of the crop area.
     * 
     * The value of cropY is corrected if images are scaled to fit
     * in the ImageView.
     * 
     * @return The Y coordinate of the rectangular crop area
     */
    public int getCropY()
    {
    	return cropY;
    }
    
    /**
     * Gets the width of the crop area.
     * 
     * The value of cropWidth is corrected if images are scaled to fit
     * in the ImageView.
     * 
     * @return The width of the rectangular crop area
     */
    public int getCropWidth()
    {	
    	return cropWidth;
    }
    
    /**
     * Gets the height of the crop area.
     * 
     * The value of cropHeight is corrected if images are scaled to fit
     * in the ImageView.
     * 
     * @return The height of the rectangular crop area
     */
    public int getCropHeight()
    {
    	return cropHeight;
    }
}