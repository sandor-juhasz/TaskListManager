package hu.sanyi.demo.todolist.ui;

import hu.sanyi.demo.todolist.oauth.OAuthContext;
import hu.sanyi.demo.todolist.oauth.OAuthContext.OAuthContextBuilder;
import hu.sanyi.demo.todolist.oauth.engine.OAuthEngine;
import hu.sanyi.demo.todolist.oauth.engine.ui.EmbeddedBrowserUI;
import hu.sanyi.demo.todolist.oauth.google.GoogleOAuthService;
import hu.sanyi.demo.todolist.service.todoservice.TodoListService;
import hu.sanyi.demo.todolist.service.todoservice.TodoListServiceClient;
import hu.sanyi.demo.todolist.service.todoservice.TodoListServiceWrapper;

import java.io.IOException;
import java.util.Properties;

public class ServiceLocator {

	private static TodoListService todoListService;
	private static OAuthContext oauthContext;
	private static OAuthEngine oauthEngine;
	
	static {
        Properties p = new Properties();
        try {
        	p.load(p.getClass().getResourceAsStream("/client_credentials.properties"));
        } catch (IOException e) {
        	System.out.println("Cannot load clientCredentials.properties.");
        }
      
        OAuthContextBuilder builder = OAuthContext.builder();
        builder.setOauthService(new GoogleOAuthService());
        builder.setOAuthClientCrendentialsProperties(p);
        builder.setScope(GoogleOAuthService.SCOPE_TASK_API);
        oauthContext = builder.build();
        oauthContext.restore();
        System.out.println(oauthContext);
        
        oauthEngine = new OAuthEngine(oauthContext);
        oauthEngine.setAuthUI(new EmbeddedBrowserUI());
        oauthEngine.registerServiceClient(TodoListService.class, new TodoListServiceClient());
        oauthEngine.start();
        
        todoListService = new TodoListServiceWrapper(oauthEngine);
	}
	
	
	public static TodoListService getTodoListService() {
		return todoListService;
	}
	
	public static OAuthEngine getOAuthEngine() {
		return oauthEngine;
	}
}
