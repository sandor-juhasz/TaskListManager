package hu.sanyi.demo.todolist.oauth;

import java.util.Properties;

public class OAuthClientCredentials {

	private String clientId;
	private String clientSecret;
	private String redirectUrl;
	
	private String CLIENT_ID_PROPERTY="client_id";
	private String CLIENT_SECRET_PROPERTY="client_secret";
	private String REDIRECT_URL_PROPERTY="redirect_url";
	
	public OAuthClientCredentials(String clientId, String clientSecret,
			String redirectUrl) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUrl = redirectUrl;
	}

	public OAuthClientCredentials(final Properties properties) {
		if (properties == null)
			throw new NullPointerException("Properties parameter is null.");
		
		clientId=getProperty(CLIENT_ID_PROPERTY, properties);
		clientSecret = getProperty(CLIENT_SECRET_PROPERTY, properties);
		redirectUrl = getProperty(REDIRECT_URL_PROPERTY, properties);
	}
	
	private String getProperty(String key, Properties properties) {
		String value = properties.getProperty(key);
		if (value == null)
			throw new IllegalArgumentException("Property "+key+" was not found.");
		return value;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public String getClientSecret() {
		return clientSecret;
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}
}
