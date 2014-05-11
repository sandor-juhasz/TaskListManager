package hu.sanyi.demo.todolist.service;

import hu.sanyi.demo.todolist.oauth.OAuthException;

import java.util.Map;

public interface HeaderAppender {

	Map<String, String> getHeaders() throws OAuthException ;
	
}
