/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package org.coursera.symptom.client;

import org.coursera.symptom.SymptomValues;
import org.coursera.symptom.activity.LoginActivity;
import org.coursera.symptom.client.oauth.SecuredRestBuilder;
import org.coursera.symptom.client.EasyHttpClient;
import org.coursera.symptom.client.SymptomSvcApi;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import android.content.Context;
import android.content.Intent;

/**
 * This class store a static SymptomSvcApi object to access to the server. 
 * It will be used by all activities and services
 * 
 */
public class SymptomSvc {
	
	private static SymptomSvcApi clientSvc;
	private static SecuredRestBuilder securedRestBuilder;

	/**
	 * Get a SymptomSvcApi object and if it is null, starts Login Activity to log in.
	 * 
	 * @param ctx Activity or Service that calls this method
	 * @param service a boolean that indicates if a Service calls this method. In this case,
	 * the LoginActivity will not be shown. The true value is never used in this Project
	 * @return a SymptomSvcApi object
	 */
	public static synchronized SymptomSvcApi getOrShowLogin(Context ctx, boolean service) {
		if (clientSvc != null) {
			return clientSvc;
		} else {
			if (!service){
				Intent i = new Intent(ctx, LoginActivity.class);
				ctx.startActivity(i);				
			}
			return null;
		}
	}

	/**
	 * This method initializes a SymptomSvcApi object to access server and store it in this object
	 * 
	 * @param server Server URL where SymptomServer is hosted
	 * @param user a String with the user name
	 * @param pass a String with the password
	 * @param oauth a boolean. A true value indicates that OAUTH mechanism is needed. A false
	 * value indicate that a simple authentication mechanism will be used. 
	 * @return a SymptomSvcApi object
	 */
	public static synchronized SymptomSvcApi init(String server, String user,
			String pass, boolean oauth) {

		if (oauth){
			securedRestBuilder = new SecuredRestBuilder();
			clientSvc = securedRestBuilder
			.setClient(new ApacheClient(new EasyHttpClient()))
//			.setConverter(converter)
//			.setErrorHandler(errorHandler)
			.setLoginEndpoint(server + SymptomSvcApi.TOKEN_PATH)
			.setUsername(user)
			.setPassword(pass)
			.setClientId(SymptomValues.CLIENT_ID)
			.setEndpoint(server)
			.setLogLevel(LogLevel.FULL).build().create(SymptomSvcApi.class);
		}else{
			clientSvc = new RestAdapter.Builder()
            .setClient(new ApacheClient(new EasyHttpClient()))
            .setEndpoint(server)
            .setLogLevel(LogLevel.FULL).build()
            .create(SymptomSvcApi.class);
		}
		return clientSvc;
	}
	
	/**
	 * This method initializes a SymptomSvcApi object to access to the server
	 * 
	 * @param server Server URL where SymptomServer is hosted
	 * @param token a String with the token to use to make all http calls to the server
	 * 
	 * @return the SymptomSvcApi object initializes
	 */
	public static synchronized SymptomSvcApi init(String server, String token){
		SecuredRestBuilder securedRestBuilder = new SecuredRestBuilder();
		SymptomSvcApi clientSvc = securedRestBuilder
			.setClient(new ApacheClient(new EasyHttpClient()))
			.setEndpoint(server)
			.setToken(token)
			.setLogLevel(LogLevel.FULL).build()
			.create(SymptomSvcApi.class);
		
		return clientSvc;		
	}
	
	/**
	 * Returns OAuth token saved in internal object
	 * 
	 * @return a String with OAuth token used for communication with the server
	 */
	public static synchronized String getOAuthToken(){
		if (securedRestBuilder != null){
			return securedRestBuilder.getAccessToken();
		}
		return "";
	}
		
}