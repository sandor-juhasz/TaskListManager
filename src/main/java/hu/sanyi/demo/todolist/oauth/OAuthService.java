package hu.sanyi.demo.todolist.oauth;

import java.net.URI;

public interface OAuthService {

	/**
	 * Returns the URI used for the authorization code grant flow. The system
	 * browser will be needed to be opened with this URL. This method does not
	 * perform any network calls.
	 *
	 * @param clientCredentials The object containing the client credentials.
	 * @param scope The scope of the request. Scopes are defined by the OAuth service.
	 * @return The URI which can be opened to request an authorization code. 
	 */
	URI getAuthorizationURI(OAuthClientCredentials clientCredentials, String scope);
	
	/**
	 * Exchanges the authorization code for tokens.
	 */
	TokenEndpointResponse getTokens(OAuthClientCredentials clientCredentials, String authorizationCode) throws OAuthException ;
	
	TokenEndpointResponse refreshAccessToken(
			OAuthClientCredentials clientCredentials, String refreshToken) throws OAuthException;	
	/*
	 * The authorization header is added to the web service calls. 
	 */
	
	String getAuthorizationHeaderName();
	
	String getAurhorizationHeaderValue(String tokenType, String accessToken);
	
}
