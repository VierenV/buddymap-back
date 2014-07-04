package com.buddymap.resources.filters;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Date;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.buddymap.dao.AuthenticationDAO;
import com.buddymap.dao.UserDAO;
import com.buddymap.model.Authentication;
import com.buddymap.model.User;
import com.buddymap.services.CryptographyService;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

 
public class RequestFilter implements ContainerRequestFilter {
	private static final long TOKEN_VALIDITY = 600000;
	private static Logger logger = Logger.getRootLogger();

	public ContainerRequest filter(ContainerRequest request) {
		String path = request.getPath(true);
		//Cases where we don't need authentication
		//If we want to create an user
		if("users".equals(path) && "POST".equals(request.getMethod())){
			return request;
		}
		//If we want to login
		if(path != null && path.startsWith("authentication/") && "PUT".equals(request.getMethod())){
			return request;
		}
		
		String authorizationHeader = request.getHeaderValue("Authorization");
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
						throw new WebApplicationException(Status.UNAUTHORIZED);
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
								request.getProperties().put("encryptedToken", newAuthent.getEncryptedToken());
							}
							UserDAO userDAO = new UserDAO();
							User user = userDAO.findByMail(authent.getMail());
							if(user == null){
								logger.error("Unable to find user "+authent.getMail());
								throw new WebApplicationException(Status.UNAUTHORIZED);
							}else{
								request.getProperties().put("connectedUser", user);
							}
						}else{
							logger.error("Unable to store updated authent "+newAuthent.toString());
							throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
						}
					}else{
						logger.error("Unacceptable signature "+authent.getMail()+":"+authent.getHashSignature());
						throw new WebApplicationException(Status.UNAUTHORIZED);
					}
				}else{
					throw new WebApplicationException(Status.UNAUTHORIZED);
				}
			} catch (JsonParseException e) {
				throw new WebApplicationException(Status.UNAUTHORIZED);
			} catch (JsonMappingException e) {
				throw new WebApplicationException(Status.UNAUTHORIZED);
			} catch (IOException e) {
				throw new WebApplicationException(Status.UNAUTHORIZED);
			} catch (SignatureException e) {
				throw new WebApplicationException(Status.UNAUTHORIZED);
			}
			
		}else{
			throw new WebApplicationException(Status.UNAUTHORIZED);
		}
		return request;
	}
 
}