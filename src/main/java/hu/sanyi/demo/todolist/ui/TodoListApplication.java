package hu.sanyi.demo.todolist.ui;

import hu.sanyi.demo.todolist.service.OAuthCallResult;
import hu.sanyi.demo.todolist.service.OAuthCallResult.Status;
import hu.sanyi.demo.todolist.service.OAuthCallback;
import hu.sanyi.demo.todolist.service.todoservice.TodoListService;

import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TodoListApplication extends Application {

	Scene loadingScene;
    Scene taskListScene;
    Scene loginScene;
    GetTaskListView loginButtonView;
    TaskListView taskListView;
    Stage primaryStage;
    
    public static void main(String[] args) {
        launch(args);
    }	
	
    private void initComponents(Stage primaryStage) {
    	this.primaryStage = primaryStage;
		primaryStage.setTitle("OAuthEngine demo");
		primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
		      public void handle(WindowEvent event) {
		    	  mainWindowClosed();
		      }
		    });		

        loginButtonView = new GetTaskListView();
        loginButtonView.addActionEventHandler(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	getTaskListClicked();
            }
        });
        
        taskListView = new TaskListView();
        taskListView.addLogoutButtonEventHandler(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				logoutButtonClicked();
			}
        });
        taskListView.addRefreshButtonEventHandler(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				refreshButtonClicked();
			}
        });
		
        taskListScene = new Scene(taskListView, 300, 250);
        loginScene = new Scene(loginButtonView, 300,250);
        
        BorderPane loadingPane = new BorderPane();
        loadingPane.setCenter(new Label("Loading..."));
        loadingScene = new Scene(loadingPane, 300,250);
    }
    
    /*
     * Event handler methods
     */
    
    private void mainWindowClosed() {
  	    ServiceLocator.getOAuthEngine().stop();    	
    }
    
    private void getTaskListClicked() {
    	loadTasks();
    }

	private void logoutButtonClicked() {
		ServiceLocator.getOAuthEngine().deauthorize();
		primaryStage.setScene(loginScene);
	}

	private void refreshButtonClicked() {
		loadTasks();
	}
	
    private void loadTasks() {
    	primaryStage.setScene(loadingScene);
    	TodoListService todoService = ServiceLocator.getTodoListService();
    	todoService.getMyTodoLists(new OAuthCallback() {
			@Override
			public void callCompleted(final OAuthCallResult result) {
				if (result.getStatus() == Status.SUCCESS) {					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							taskListView.updateTaskList((List<String>)result.getPayload());
							primaryStage.setScene(taskListScene);
						}});
				} else {
					System.out.println(result.getStatus());
					System.out.println(result.getPayload());					
				}
			}
    	});   	    	
    }
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		initComponents(primaryStage);
		        
        if (ServiceLocator.getOAuthEngine().isAuthorized()) {
        	loadTasks();
        	primaryStage.setScene(loadingScene);
        } else {
        	primaryStage.setScene(loginScene);
        }        
        primaryStage.show();
	}

}
