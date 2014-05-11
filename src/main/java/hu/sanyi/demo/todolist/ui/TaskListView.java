package hu.sanyi.demo.todolist.ui;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class TaskListView extends BorderPane {

	Button logoutButton = new Button();
	ListView<String> list = new ListView<>();
	ObservableList<String> items =FXCollections.observableArrayList (
            "Single", "Double", "Suite", "Family App");
	Button refreshButton = new Button();
	
	public TaskListView() {
		refreshButton.setText("Refresh");
		logoutButton.setText("Logout");
		FlowPane buttonPane = new FlowPane();
		buttonPane.getChildren().add(refreshButton);
		buttonPane.getChildren().add(logoutButton);
	
		list.setItems(items);
        setBottom(buttonPane);
        setCenter(list);
	}
	
	public void updateTaskList(List<String> taskLists) {
		items.clear();
		items.addAll(taskLists);
	}

	public void addLogoutButtonEventHandler(EventHandler<ActionEvent> eventHandler) {
		logoutButton.setOnAction(eventHandler);
	}

	public void addRefreshButtonEventHandler(
			EventHandler<ActionEvent> eventHandler) {
		refreshButton.setOnAction(eventHandler);
	}
	
}
