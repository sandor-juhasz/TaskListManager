package hu.sanyi.demo.todolist.service;

/**
 * This class represents the outcome of an oauth call. This is the parameter 
 * which is passed to the asynchronous callbacks.
 * w
 * @author Sanyi
 *
 */
public class OAuthCallResult {

	private Status status;
	
	public static enum Status {
		SUCCESS,
		FAILURE
	}
	
	private Object payload;

	public OAuthCallResult(Status status, Object payload) {
		this.status=status;
		this.payload = payload;
	}
	
	public Object getPayload() {
		return payload;
	}

	public Status getStatus() {
		return status;
	}
	
}
