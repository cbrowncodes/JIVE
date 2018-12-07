package jive.java;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * This class is the user manual for JIVE.
 * 
 * The manual is an HTML document that is
 * displayed in a new stage containing a WebView.
 * 
 * @author Devon
 *
 */
public class UserManual
{
	private String manual = getClass().getResource("/jive/resources/jiveUserManual.html").toExternalForm();
	private Stage stage = new Stage();
	private StackPane pane = new StackPane();
	private WebView webView = new WebView();
	private Scene root = new Scene(pane);
	
	public UserManual()
	{
		stage.setResizable(false);
		stage.setTitle("JIVE - User Manual");
		stage.setScene(root);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/jive/resources/icons/JiveIcon.png")));		
		webView.getEngine().setUserStyleSheetLocation(getClass().getResource("/jive/resources/usermanual.css").toString());
		webView.getEngine().load(manual);
		pane.getChildren().add(webView);
	}
	
	/**
	 * Launches a new stage containing the user manual.
	 * 
	 * If the user manual stage is already open it is
	 * brought to the front of the screen.
	 */
	public void openUserManual()
	{
		if (stage.isShowing())
		{
			stage.setIconified(false);
			stage.toFront();
		}
		else
			stage.show();
	}
}
