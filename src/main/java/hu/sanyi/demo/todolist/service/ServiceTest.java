package hu.sanyi.demo.todolist.service;

import hu.sanyi.demo.todolist.oauth.OAuthContext;
import hu.sanyi.demo.todolist.oauth.OAuthContext.OAuthContextBuilder;
import hu.sanyi.demo.todolist.oauth.google.GoogleOAuthService;
import hu.sanyi.demo.todolist.service.todoservice.TodoListService;
import hu.sanyi.demo.todolist.service.todoservice.TodoListServiceClient;

import java.io.IOException;
import java.util.Properties;

public class ServiceTest {
	
	public static void main(String[] args) throws Exception  {	
		
		//-------------- Create context
		OAuthContext context = createInitialContext();
		System.out.println(context.getAuthorizationUrl());
		
		
        System.out.println(context);
        
        //-------------- Create service
        TodoListService service = createService(context);
        
        //-------------- Authorize
        
        context.setAuthorizationCode("4/JxJHx9Y5HftkybDacwJT1Ia5xbfi.AqejCfNSiP8ROl05ti8ZT3bbNI3WiwI");
        context.authorize();
        System.out.println(context);
        context.refreshAccessToken();
        System.out.println(context);
    
	}

	private static OAuthContext createInitialContext() {
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
        final OAuthContext context = builder.build();
        
        return context;
	}
	
	private static TodoListService createService(OAuthContext context) {
/*		TodoListService impl null;
		TodoListService wrapper = new TodoListOAuthWrapper(context, impl);*/
		return null;
	}
	
}
