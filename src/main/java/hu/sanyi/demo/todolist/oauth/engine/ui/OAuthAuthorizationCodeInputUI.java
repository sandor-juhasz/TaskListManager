package hu.sanyi.demo.todolist.oauth.engine.ui;

import hu.sanyi.demo.todolist.oauth.engine.OAuthUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class OAuthAuthorizationCodeInputUI extends OAuthUI {

	@Override
	public void authorize() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
			      final Stage dialog = new Stage();

			      dialog.setTitle("Enter Missing Text");
			      dialog.initStyle(StageStyle.UTILITY);
			      dialog.initModality(Modality.WINDOW_MODAL);

			      final TextField textField = new TextField();
			      final Button submitButton = new Button("Submit");
			      submitButton.setDefaultButton(true);
			      submitButton.setOnAction(new EventHandler<ActionEvent>() {
			        @Override public void handle(ActionEvent t) {
			          dialog.close();
			        }
			      });
			      textField.setMinHeight(TextField.USE_PREF_SIZE);

			      final VBox layout = new VBox(10);
			      layout.setAlignment(Pos.CENTER_RIGHT);
			      layout.setStyle("-fx-background-color: azure; -fx-padding: 10;");
			      layout.getChildren().setAll(
			        textField, 
			        submitButton
			      );

			      dialog.setScene(new Scene(layout));
			      dialog.showAndWait();

			      String result = textField.getText();
			      engine.publishAuthorizationCode(result);
			  }
		});
	}

}
