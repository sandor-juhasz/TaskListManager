package hu.sanyi.demo.todolist.oauth.engine;

import hu.sanyi.demo.todolist.oauth.OAuthContext;
import hu.sanyi.demo.todolist.oauth.OAuthException;
import hu.sanyi.demo.todolist.oauth.engine.ui.EmbeddedBrowserUI;
import hu.sanyi.demo.todolist.oauth.engine.ui.OAuthAuthorizationCodeInputUI;
import hu.sanyi.demo.todolist.service.HeaderAppender;
import hu.sanyi.demo.todolist.service.OAuthCallResult;
import hu.sanyi.demo.todolist.service.OAuthServiceClient;
import hu.sanyi.demo.todolist.service.OAuthServiceInterface;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is the OAuth executor engine. Every OAath call wrapped into a
 * OAuthJob and passed to the OAuthEngine for execution.
 * <p>
 * The OAuthEngine can implement various strategies for service execution.
 * This reference implementation implements a single job queue and sequential
 * execution. The queue is limited to 20 waiting jobs. The authorization UI
 * uses JavaFX to display the embedded browser. The context of the execution
 * engine is fixed, all incoming calls will be executed with the same
 * OAuthContext.
 * 
 * @author Sanyi
 *
 */
public class OAuthEngine implements Runnable, HeaderAppender {

	private Map<Class<? extends OAuthServiceInterface>, OAuthServiceClient> clients = new HashMap<Class<? extends OAuthServiceInterface>, OAuthServiceClient>();
	
	/**
	 * The current OAuth context used to make calls.
	 */
	private OAuthContext context;
	
	/**
	 * The current job being processed.
	 */
	private OAuthJob currentJob;
	private Object currentJobLock = new Integer(2);
	
	private OAuthUI oauthUI;

	private Object authorizationCodeLock=new Integer(1);
	private String authorizationCode;
	
	public OAuthEngine(OAuthContext context) {
		this.context = context;
	}
	
	/**
	 * Adds an OAuthJob to the queue of the OAuthEngine.
	 * 
	 * @param job	The job to be added to the engine's queue.
	 * @throws NulPointerException if the job is null.
	 * @throws OAuthEngineQueueFullException if the queue is full.
	 */
	public void addJob(final OAuthJob job) throws OAuthEngineQueueFullException {
		processJob(job);
	}
	
	/**
	 * Registers a service client. Service clients will be used to serve the
	 * incoming client requests. Calling the method multiple times does not
	 * register the service client multiple times.
	 * 
	 * @param serviceClient The service client to be registered.
	 */
	public void registerServiceClient(Class<? extends OAuthServiceInterface> clientInterface ,final OAuthServiceClient serviceClient) {
		clients.put(clientInterface, serviceClient);
		serviceClient.registerHeaderAppender(this);
	}

	/**
	 * Sets the job to be processed. This method may block if the is
	 * a block to be processed.
	 * 
	 * @param job
	 */
	public void processJob(final OAuthJob job) throws OAuthEngineQueueFullException {
		synchronized(currentJobLock) {
			if (currentJob != null) 
				throw new OAuthEngineQueueFullException();
			
			this.currentJob = job;
			currentJobLock.notify();
		}
	}
	
	/**
	 * This is the main job processor of the engine. It implements a simple 
	 * strategy to make oauth calls. 
	 */
	protected void processCurrentJob() {
		if (currentJob == null)
			return;
		
		if (!context.isAuthorized()) {
			String authorizationCode = waitForAuthorizationCode();
			this.authorizationCode=null;
			context.setAuthorizationCode(authorizationCode);
			try {
				context.authorize();
			} catch (OAuthException e) {
				currentJob.callCallbackWithException(e);
				return;
			}
		}
		
		OAuthServiceClient serviceClient = clients.get(currentJob.getServiceInterface());
		if (serviceClient == null) {
			currentJob.callCallbackWithException(new IllegalStateException(currentJob.getServiceInterface()+" is not registered."));
			return;
		}
		try {
			Object o = serviceClient.getClass().getMethod(currentJob.getMethod()).invoke(serviceClient);
			OAuthCallResult result = (OAuthCallResult)o;
			currentJob.callCallback(result);
		} catch (NoSuchMethodException e) {
			currentJob.callCallbackWithException(e);
			return;
		} catch (InvocationTargetException e) {
			currentJob.callCallbackWithException(e);
			return;
		} catch (IllegalAccessException e) {
			currentJob.callCallbackWithException(e);
			return;
		}
		// TODO: handle cases when the service client returns with invalid authorization exception. Refresh the tokens.
	}

	public String getAuthorizationUrl() {
		return context.getAuthorizationUrl();
	}

	public String waitForAuthorizationCode() {
		synchronized(authorizationCodeLock) {
			if (authorizationCode == null) {
				oauthUI.authorize();
				try {
					authorizationCodeLock.wait();
				} catch (InterruptedException e){}
			}
			return authorizationCode;
		}
	}
	
	public void publishAuthorizationCode(String authorizationCode) {
		synchronized(this.authorizationCodeLock) {
			this.authorizationCode = authorizationCode;
			authorizationCodeLock.notify();
		}
	}

	/*
	 * The main event loop of the OAuthEngine 
	 */
	
	private volatile boolean running = true;

	@Override
	public void run() {
		System.out.println("JobProcessorThread: started.");
		while (running) {
			synchronized (currentJobLock) {
				if (currentJob == null) {
					try {
						System.out.println("JobProcessorThread: waiting for new job.");
						currentJobLock.wait();
						System.out.println("JobProcessorThread: woken up.");
					} catch (InterruptedException e) {}
				}
				System.out.println("JobProcessorThread: starting to process current job");
				processCurrentJob();
				System.out.println("JobProcessorThread: job is processed.");
				currentJob=null;
			}
		}
		System.out.println("JobProcessorThread: finished.");
	}

	public void stop() {
		synchronized(currentJobLock) {
			running = false;
			currentJobLock.notify();
		}
	}
	
	public void start() {
		new Thread(this).start();
	}

	@Override
	public Map<String, String> getHeaders() throws OAuthException {
		Map<String, String> map = new HashMap<String, String>();
		map.put(context.getAuthorizationHeaderName(), context.getAuthorizationHeaderValue());
		return map;
	}

	public void setAuthUI(OAuthUI oauthUI) {
		this.oauthUI = oauthUI;
		oauthUI.setOAuthEngine(this);
	}
	
	public boolean isAuthorized() {
		return context.isAuthorized();
	}

	public void deauthorize() {
		context.deauthorize();
	}
}
