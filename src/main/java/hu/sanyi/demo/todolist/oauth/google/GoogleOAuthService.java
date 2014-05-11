package hu.sanyi.demo.todolist.oauth.google;

import hu.sanyi.demo.todolist.oauth.OAuthClientCredentials;
import hu.sanyi.demo.todolist.oauth.OAuthException;
import hu.sanyi.demo.todolist.oauth.OAuthService;
import hu.sanyi.demo.todolist.oauth.TokenEndpointResponse;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GoogleOAuthService implements OAuthService {

	private String HOST_NAME = "accounts.google.com";
	private String AUTHORIZATION_ENDPOINT_PATH = "/o/oauth2/auth";
	private String TOKEN_ENDPOINT_PATH = "/o/oauth2/token"; 
	
	public static final String SCOPE_TASK_API = "https://www.googleapis.com/auth/tasks";
	
	private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

	@Override
	public URI getAuthorizationURI(final OAuthClientCredentials clientCredentials, final String scope) {
		try {
			URI uri = new URIBuilder()
				.setScheme("https")
				.setHost(HOST_NAME)
				.setPath(AUTHORIZATION_ENDPOINT_PATH)
				.setParameter("redirect_uri", clientCredentials.getRedirectUrl())
				.setParameter("response_type", "code")
				.setParameter("client_id", clientCredentials.getClientId())
				.setParameter("scope", scope)
				.build();
			return uri;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Error while creating the URI: ", e);
		}
	}

	@Override
	public TokenEndpointResponse getTokens(
			OAuthClientCredentials clientCredentials, String authorizationCode) throws OAuthException {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			URI uri = new URIBuilder()
				.setScheme("https")
				.setHost(HOST_NAME)
				.setPath(TOKEN_ENDPOINT_PATH)
				.build();
			
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("grant_type", "authorization_code"));           
			formparams.add(new BasicNameValuePair("code", authorizationCode));
			formparams.add(new BasicNameValuePair("client_id", clientCredentials.getClientId()));
			formparams.add(new BasicNameValuePair("client_secret", clientCredentials.getClientSecret()));
			formparams.add(new BasicNameValuePair("redirect_uri", clientCredentials.getRedirectUrl()));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
						
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(entity);
			CloseableHttpResponse response1 = httpclient.execute(httpPost);			
			try (Reader reader = new InputStreamReader(response1.getEntity().getContent(), "UTF-8")) {
				Gson gson = new GsonBuilder().create();
				TokenEndpointResponse resp = gson.fromJson(reader, TokenEndpointResponse.class);
				return resp;
			}			
		} catch (Exception e) {
			throw new OAuthException("OAuthException", e);
		}
	}
	
	public TokenEndpointResponse refreshAccessToken(
			OAuthClientCredentials clientCredentials, String refreshToken) throws OAuthException {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			URI uri = new URIBuilder()
				.setScheme("https")
				.setHost(HOST_NAME)
				.setPath(TOKEN_ENDPOINT_PATH)
				.build();
			
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("grant_type", "refresh_token"));           
			formparams.add(new BasicNameValuePair("refresh_token", refreshToken));
			formparams.add(new BasicNameValuePair("client_id", clientCredentials.getClientId()));
			formparams.add(new BasicNameValuePair("client_secret", clientCredentials.getClientSecret()));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
						
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(entity);
			CloseableHttpResponse response1 = httpclient.execute(httpPost);			
			try (Reader reader = new InputStreamReader(response1.getEntity().getContent(), "UTF-8")) {
				Gson gson = new GsonBuilder().create();
				TokenEndpointResponse resp = gson.fromJson(reader, TokenEndpointResponse.class);
				return resp;
			}			
		} catch (Exception e) {
			throw new OAuthException("OAuthException", e);
		}		
	}

	@Override
	public String getAuthorizationHeaderName() {
		return AUTHORIZATION_HEADER_NAME;
	}

	@Override
	public String getAurhorizationHeaderValue(String tokenType, String accessToken) {
		return tokenType+" "+accessToken;
	}
}
