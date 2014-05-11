package hu.sanyi.demo.todolist.oauth.engine;

import hu.sanyi.demo.todolist.service.OAuthCallResult;
import hu.sanyi.demo.todolist.service.OAuthCallback;
import hu.sanyi.demo.todolist.service.OAuthServiceInterface;

/**
 * This object represents a working unit of an OAuth-enabled service call.
 * The object is created by the service interface implementations and passed
 * to the OAuth engine.
 * 
 * @author Sanyi
 *
 */
public class OAuthJob {

	private Class<? extends OAuthServiceInterface> serviceInterface;
	private String method;
	private OAuthCallback callback;
	
	public OAuthJob(Class<? extends OAuthServiceInterface> serviceInterface, String method,
			OAuthCallback callback) {
		this.serviceInterface = serviceInterface;
		this.method = method;
		this.callback = callback;
	}

	public Class<? extends OAuthServiceInterface> getServiceInterface() {
		return serviceInterface;
	}

	public String getMethod() {
		return method;
	}

	public OAuthCallback getCallback() {
		return callback;
	}
	
	public void callCallbackWithException(Exception e) {
		try {
			callback.callCompleted(new OAuthCallResult(OAuthCallResult.Status.FAILURE, e));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void callCallback(OAuthCallResult result) {
		try {
			callback.callCompleted(result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
