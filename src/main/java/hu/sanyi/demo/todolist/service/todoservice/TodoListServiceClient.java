package hu.sanyi.demo.todolist.service.todoservice;

import hu.sanyi.demo.todolist.service.AbstractOAuthServiceClient;
import hu.sanyi.demo.todolist.service.OAuthCallResult;
import hu.sanyi.demo.todolist.service.OAuthCallResult.Status;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Implements the todo service client. The desktop application never calls
 * this class directly. It is invoked by the OAuthEngine.
 * <p>
 * All methods which implement an OAuthServiceClient should have service methods
 * with the following characteristics:
 * <ul>
 *   <li>The service method must return an OAuthCallResult object this object
 *       cannot be null.
 *   </li>
 *   <li>The service method name must match the service method defined in the
 *       asynchronous interface.
 *   </li>
 *   <li>
 *       The signature
 *   </li>
 * </ul> 
 * The Service client must not have mutable state. 
 * 
 * TODO:
 *   * The clients and service interfaces could use annotations
 * @author Sanyi
 *
 */
public class TodoListServiceClient extends AbstractOAuthServiceClient {
	
	public OAuthCallResult getMyTodoLists() {
		List<String> taskLists = new ArrayList<String>();
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet("https://www.googleapis.com/tasks/v1/users/@me/lists");
			if (headerAppender != null) {
				Map<String, String> headers = headerAppender.getHeaders();
				for (String key : headers.keySet()) {
					httpGet.addHeader(key, headers.get(key));
				}				
			}
			
			CloseableHttpResponse response1 = httpclient.execute(httpGet);
			// The underlying HTTP connection is still held by the response object
			// to allow the response content to be streamed directly from the network socket.
			// In order to ensure correct deallocation of system resources
			// the user MUST either fully consume the response content  or abort request
			// execution by calling CloseableHttpResponse#close().
	
			try {
			    System.out.println(response1.getStatusLine());
			    HttpEntity entity1 = response1.getEntity();
			    try (BufferedReader in = new BufferedReader(new InputStreamReader(entity1.getContent(), "UTF-8"))) {
			        for (String line = in.readLine(); line != null; line=in.readLine()) {
			        	System.out.println(line);
			        	if (line.contains("\"title\":")) {
			        		taskLists.add(line.substring(13, line.length()-2));
			        	}
			        }
			    }
			    // do something useful with the response body
			    // and ensure it is fully consumed
			    EntityUtils.consume(entity1);
			} finally {
			    response1.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new OAuthCallResult(Status.SUCCESS, taskLists);
	}
}
