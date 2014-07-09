package com.buddymap.resources.filters;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Date;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.buddymap.config.AuthenticationNeeded;
import com.buddymap.dao.AuthenticationDAO;
import com.buddymap.dao.UserDAO;
import com.buddymap.model.Authentication;
import com.buddymap.model.User;
import com.buddymap.services.CryptographyService;

@AuthenticationNeeded
public class RequestFilter implements ContainerRequestFilter {
	private static final long TOKEN_VALIDITY = 600000;
	private static Logger logger = Logger.getRootLogger();

	public void filter(ContainerRequestContext request) {
		String authorizationHeader = request.getHeaderString("Authorization");
		if(authorizationHeader != null){
			ObjectMapper mapper = new ObjectMapper();
			Authentication authent;
			try {
				authent = mapper.readValue(authorizationHeader, Authentication.class);
				if(authent.getMail() != null && authent.getClientTimestamp() > 0 && authent.getHashAlgorythm() != null && authent.getHashSignature() != null){
					AuthenticationDAO authentDAO = new AuthenticationDAO();
					Authentication storedAuthent = authentDAO.find(authent.getMail());
					if(storedAuthent == null){
						logger.error("Stored authent not found "+authent.getMail());
						request.abortWith(Response
				                    .status(Response.Status.UNAUTHORIZED)
				                    .entity("User cannot access the resource.")
				                    .build());
						return;
					}
					if(CryptographyService.checkSignature(authent, storedAuthent)){
						Authentication newAuthent = new Authentication();
						boolean newTokenGenerated = false;
						newAuthent.setClientTimestamp(authent.getClientTimestamp());
						newAuthent.setMail(authent.getMail());
						newAuthent.setServerTimestamp((new Date()).getTime());
						
						if((new Date()).getTime()-storedAuthent.getServerTimestamp()> TOKEN_VALIDITY){
							logger.info("invalid server timestamp for token, new token to create "+authent.getMail()+":"+authent.getEncryptedToken());
							newAuthent.setEncryptedToken(CryptographyService.generateCryptedToken(authent.getMail()));
							newTokenGenerated = true;
						}else{
							newAuthent.setEncryptedToken(storedAuthent.getEncryptedToken());
						}
						int nbRes = authentDAO.update(newAuthent);
						if(nbRes == 1){
							if(newTokenGenerated){
								request.setProperty("encryptedToken", newAuthent.getEncryptedToken());
							}
							UserDAO userDAO = new UserDAO();
							User user = userDAO.findByMail(authent.getMail());
							if(user == null){
								logger.error("Unable to find user "+authent.getMail());
								request.abortWith(Response
					                    .status(Response.Status.UNAUTHORIZED)
					                    .entity("User cannot access the resource.")
					                    .build());
								return;
							}else{
								request.setProperty("connectedUser", user);
							}
						}else{
							logger.error("Unable to store updated authent "+newAuthent.toString());
							request.abortWith(Response
				                    .status(Response.Status.INTERNAL_SERVER_ERROR)
				                    .build());
							return;
						}
					}else{
						logger.error("Unacceptable signature "+authent.getMail()+":"+authent.getHashSignature());
						request.abortWith(Response
			                    .status(Response.Status.UNAUTHORIZED)
			                    .entity("User cannot access the resource.")
			                    .build());
						return;
					}
				}else{
					request.abortWith(Response
		                    .status(Response.Status.UNAUTHORIZED)
		                    .entity("User cannot access the resource.")
		                    .build());
					return;
				}
			} catch (JsonParseException e) {
				logger.error(e);
				request.abortWith(Response
	                    .status(Response.Status.UNAUTHORIZED)
	                    .entity("User cannot access the resource.")
	                    .build());
				return;
			} catch (JsonMappingException e) {
				logger.error(e);
				request.abortWith(Response
	                    .status(Response.Status.UNAUTHORIZED)
	                    .entity("User cannot access the resource.")
	                    .build());
				return;
			} catch (IOException e) {
				logger.error(e);
				request.abortWith(Response
	                    .status(Response.Status.UNAUTHORIZED)
	                    .entity("User cannot access the resource.")
	                    .build());
				return;
			} catch (SignatureException e) {
				logger.error(e);
				request.abortWith(Response
	                    .status(Response.Status.UNAUTHORIZED)
	                    .entity("User cannot access the resource.")
	                    .build());
				return;
			}
			
		}else{
			request.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("User cannot access the resource.")
                    .build());
			return;
		}
	}
 
}
