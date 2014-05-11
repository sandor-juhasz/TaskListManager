package hu.sanyi.demo.todolist.oauth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

/**
 * This class contains all the information known of the current OAuth2 context.
 * This class can be used to obtain an access token.
 * @author Sanyi
 *
 */
public class OAuthContext {

	/**
	 * The OAuth2 service endpoint API
	 */
	private OAuthService service;
	
	/**
	 * The Client-related OAuth2 credentials of the application 
	 */
	private OAuthClientCredentials clientCredentials;

	private String scope;

	private OAuthContextState state = OAuthContextState.EMPTY;
	
	/**
	 * The different tokens used to make OAuth calls.
	 */
	private String authorizationCode;
	private String accessToken;
	private String refreshToken;
	private String tokenType;
	private Date tokenExpirationDate;
	
	
	public void setAuthorizationCode(String authorizationCode) {
		System.out.println("Authoirzation code: "+authorizationCode);
		this.authorizationCode = authorizationCode;
		state=OAuthContextState.AUTHORIZATION_CODE_KNOWN;
		accessToken=null;
		refreshToken=null;
		tokenType=null;
		tokenExpirationDate=null;
	}
	
	public void authorize() throws OAuthException {
		try {
			TokenEndpointResponse resp = service.getTokens(clientCredentials, authorizationCode);
			System.out.println(resp);
			this.accessToken=resp.getAccess_token();
			this.refreshToken=resp.getRefresh_token();
			this.tokenType = resp.getToken_type();
			if (resp.getScope() != null) {
				this.scope = resp.getScope();
				System.out.println("Warning: scope is narrower than requested.");
			}
			Calendar c = GregorianCalendar.getInstance();
			c.add(Calendar.SECOND, (int)resp.getExpires_in());
			tokenExpirationDate = new Date(c.getTimeInMillis());
			
			authorizationCode = null;
			state=OAuthContextState.TOKENS_PRESENT;
			persist();
		} catch (OAuthException e) {
			throw new OAuthException("Authorization failed.", e);
		}
	}
	
	/**
	 * Returns the current access token. If the access token seems to be 
	 * expired, it will renew the access token.
	 * 
	 * @return	The access token.
	 * @throws OAuthException if any error occurs.
	 */
	public String getAccessToken() throws OAuthException {
		if (accessToken == null && refreshToken == null) {
			throw new IllegalStateException("Set authorization code first.");
		}
		if (accessToken == null || (tokenExpirationDate != null && tokenExpirationDate.before(new Date()))) {
			refreshAccessToken();
		}
		return accessToken;
	}

	public void refreshAccessToken() throws OAuthException {
		try {
			TokenEndpointResponse resp = service.refreshAccessToken(clientCredentials, refreshToken);
			System.out.println(resp);
			this.accessToken=resp.getAccess_token();
			this.tokenType = resp.getToken_type();
			if (resp.getScope() != null) {
				this.scope = resp.getScope();
				System.out.println("Warning: scope is narrower than requested.");
			}
			Calendar c = GregorianCalendar.getInstance();
			c.add(Calendar.SECOND, (int)resp.getExpires_in());
			tokenExpirationDate = new Date(c.getTimeInMillis());
			
			authorizationCode = null;
			state=OAuthContextState.TOKENS_PRESENT;
		} catch (Exception e) {
			throw new OAuthException("Error while refreshing access token",e);
		}
	}
	
	public static class OAuthContextBuilder {
		private OAuthService oauthService;
		private String clientId;
		private String clientSecret;
		private String redirectUrl;
		private Properties p;
		private String scope;
		
		private OAuthContextBuilder() {}

		public void setOauthService(OAuthService oauthService) {
			this.oauthService = oauthService;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}

		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		};
		
		public void setOAuthClientCrendentialsProperties(Properties p) {
			this.p=p;
		}
		
		
		public void setScope(String scope) {
			this.scope = scope;
		}

		public OAuthContext build() {
			OAuthContext context = new OAuthContext();
			context.service = oauthService;
			if (p != null) {
				context.clientCredentials = new OAuthClientCredentials(p);
			} else {
				context.clientCredentials = new OAuthClientCredentials(clientId,  clientSecret, redirectUrl);
			}
			context.scope=scope;
			context.state = OAuthContextState.EMPTY;
			return context;
		}
	}
	
	public static OAuthContextBuilder builder() {
		return new OAuthContextBuilder();
	}
	
	public String getAuthorizationUrl() {
		return service.getAuthorizationURI(clientCredentials, scope).toString();		
	}

	public String getTokenType() {
		return tokenType;
	}

	public String getScope() {
		return scope;
	}

	public Date getTokenExpirationDate() {
		return tokenExpirationDate;
	}
	
	@Override
	public String toString() {
		return String.format("OAuth context [\n" +
				"   OAuth service: %s\n"+
				"   state: %s\n"+
				"   authorization code: %s\n" +
				"   access token: %s\n" +
				"   refreshToken: %s\n" +
				"   expires in: %s\n",
				service.getClass().getName(),
				state,
				authorizationCode,
				accessToken,
				refreshToken,
				tokenExpirationDate);
	}

	public String getAuthorizationHeaderName() {
		return service.getAuthorizationHeaderName();
	}

	public String getAuthorizationHeaderValue() throws OAuthException {
		String accessToken = getAccessToken();
		return service.getAurhorizationHeaderValue(tokenType, accessToken);
	}

	public OAuthContextState getState() {
		return state;
	}
	
	public boolean isAuthorized() {
		return state == OAuthContextState.AUTHORIZATION_CODE_KNOWN || state == OAuthContextState.TOKENS_PRESENT || state==OAuthContextState.REFRESH_TOKEN_PRESENT;
	}
	
	public void persist() {
		String path = System.getProperty("user.home")+"\\todolist.config";
		if (refreshToken != null) {
			Properties p = new Properties();
			p.put("refresh_token", refreshToken);
			p.put("scope", scope);
			try (FileOutputStream out = new FileOutputStream(path)) {
				p.store(out, "Refresh token");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
		}
	}
	
	public void restore() {
		String path = System.getProperty("user.home")+"\\todolist.config";
		Properties p = new Properties();
		try (FileInputStream in = new FileInputStream(path)) {
			p.load(in);
			accessToken = null;
			tokenExpirationDate=null;
			tokenType=null;
			scope=null;
			state=null;
			
			refreshToken = (String)p.get("refresh_token");
			if (refreshToken != null) {
				scope=(String)p.get("scope");
				state=OAuthContextState.REFRESH_TOKEN_PRESENT;
			}
		} catch(FileNotFoundException e) {
			System.out.println("No config file yet.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void deauthorize() {
		accessToken = null;
		authorizationCode = null;
		refreshToken = null;
		state = OAuthContextState.EMPTY;
		tokenExpirationDate=null;
		tokenType = null;
		persist();
	}
}
