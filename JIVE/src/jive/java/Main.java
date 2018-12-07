package jive.java;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * This is the entry-point of the Java Image Viewer and Editor application
 * 
 * @author Devon Hunter
 * @author Craig Vandeventer
 * @author Casey Brown
 *
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try 
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/jive/resources/view.fxml"));
			Parent root = (Parent)loader.load();
			Controller gui = (Controller) loader.getController();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/jive/resources/view.css").toExternalForm());
			primaryStage.setTitle("JIVE");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/jive/resources/icons/JiveIcon.png")));
			primaryStage.setScene(scene);
			primaryStage.setMinWidth(800);
			primaryStage.setMinHeight(325);
			gui.setUp(primaryStage);
			primaryStage.show();
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
