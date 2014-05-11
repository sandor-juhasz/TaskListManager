package hu.sanyi.demo.todolist.service.todoservice;

import hu.sanyi.demo.todolist.oauth.engine.OAuthEngine;
import hu.sanyi.demo.todolist.oauth.engine.OAuthEngineQueueFullException;
import hu.sanyi.demo.todolist.oauth.engine.OAuthJob;
import hu.sanyi.demo.todolist.service.OAuthCallback;

public class TodoListServiceWrapper implements TodoListService {

	private OAuthEngine engine;
	
	public TodoListServiceWrapper(OAuthEngine engine) {
		this.engine = engine;
	}
	
	@Override
	public void getMyTodoLists(OAuthCallback callback) {
		OAuthJob job = new OAuthJob(TodoListService.class, "getMyTodoLists", callback);
		try {
			engine.addJob(job);
		} catch (OAuthEngineQueueFullException e) {
			e.printStackTrace();
		}
	}

}
