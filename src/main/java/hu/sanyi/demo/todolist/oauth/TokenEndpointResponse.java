package hu.sanyi.demo.todolist.oauth;

public class TokenEndpointResponse {

	private String access_token;
	private String token_type;
	private long expires_in;
	private String refresh_token;
	private String scope;
	private String state;
	
	public String getScope() {
		return scope;
	}
	public String getState() {
		return state;
	}
	public String getAccess_token() {
		return access_token;
	}
	public String getToken_type() {
		return token_type;
	}
	public long getExpires_in() {
		return expires_in;
	}
	public String getRefresh_token() {
		return refresh_token;
	}

	@Override
	public String toString() {
		return String.format("Token endpoint response [access_token: \"%s\",  token type: \"%s\",  expires in: %d, refresh token: \"%s\"", access_token, token_type, expires_in, refresh_token);
	}
	
}
