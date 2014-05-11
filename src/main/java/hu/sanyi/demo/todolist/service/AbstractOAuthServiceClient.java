package hu.sanyi.demo.todolist.service;

public abstract class AbstractOAuthServiceClient implements OAuthServiceClient {

	protected HeaderAppender headerAppender;
	
	public void registerHeaderAppender(HeaderAppender headerAppender) {
		this.headerAppender = headerAppender;
	}
	
}
