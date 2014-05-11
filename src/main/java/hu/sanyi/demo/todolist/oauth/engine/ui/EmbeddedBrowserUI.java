package hu.sanyi.demo.todolist.oauth.engine.ui;

import hu.sanyi.demo.todolist.oauth.engine.OAuthUI;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EmbeddedBrowserUI extends OAuthUI {

	String authorizationCode; 
	
	@Override
	public void authorize() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
			      final Stage dialog = new Stage();

			      dialog.setTitle("Enter Missing Text");
			      dialog.initStyle(StageStyle.UTILITY);
			      dialog.initModality(Modality.WINDOW_MODAL);

			        WebView browser = new WebView();
			        final WebEngine webEngine = browser.getEngine();
			        webEngine.load(engine.getAuthorizationUrl());
			        webEngine.getLoadWorker().stateProperty().addListener(
			                new ChangeListener<State>() {
			                    @Override
			                    public void changed(ObservableValue<? extends State> ov,
			                        State oldState, State newState) {
			                            if (newState == State.SUCCEEDED) {
			                            	String title = webEngine.getTitle();
			                            	if (title != null && title.matches("^Success code=.*")) {
			                            		authorizationCode = title.substring(13);
			                            		dialog.close();
			                            	}
			                            }
			                        }
			                }

			            );        
			        StackPane root = new StackPane();
			        root.getChildren().add(browser);
			        
			      dialog.setScene(new Scene(root, 600, 400));
			      dialog.showAndWait();

			      System.out.println("AuthorizationCode");
			      engine.publishAuthorizationCode(authorizationCode);
			  }
		});
	}


}
