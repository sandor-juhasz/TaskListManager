package hu.sanyi.demo.todolist.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class GetTaskListView extends BorderPane {

	Button loginButton = new Button();
	
	public GetTaskListView() {
		loginButton.setText("GetTaskList...");
		setCenter(loginButton);
	}

	public void addActionEventHandler(EventHandler<ActionEvent> eventHandler) {
		loginButton.setOnAction(eventHandler);		
	}
}
