package hu.sanyi.demo.todolist.oauth.engine;

import javafx.application.Platform;

/**
 * This class implements the UI for the engine.
 * 
 * @author Sanyi
 *
 */
public abstract class OAuthUI {

	protected OAuthEngine engine;
	
	public void setOAuthEngine(OAuthEngine engine) {
		this.engine = engine;
	}
	
	/**
	 * This method is called from the OAuthEngine's Job processor thread when 
	 * an authorization is required.
	 */
	public abstract void authorize();
	
}
