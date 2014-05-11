package hu.sanyi.demo.todolist.service.todoservice;

import hu.sanyi.demo.todolist.service.OAuthCallback;
import hu.sanyi.demo.todolist.service.OAuthServiceInterface;

/**
 * This is the interface the Desktop application will see.
 * 
 * @author Sanyi
 *
 */
public interface TodoListService extends OAuthServiceInterface {

	public void getMyTodoLists(OAuthCallback callback);
	
}
