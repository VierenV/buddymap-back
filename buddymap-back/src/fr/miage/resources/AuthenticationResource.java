package fr.miage.resources;

import java.net.URISyntaxException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.HttpContext;

import fr.miage.dao.AuthenticationDAO;
import fr.miage.model.Authentication;
import fr.miage.services.AuthenticationService;
import fr.miage.services.CryptographyService;

@Path("/authentication")
public class AuthenticationResource {

	@Context
	protected HttpContext request;
	private AuthenticationDAO authentDAO = new AuthenticationDAO();
	
	@PUT
	@Path("/{mail}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAuthentication(Authentication authent, @Context UriInfo uriInfo) throws URISyntaxException {
		if (authent == null) {
			return Response.status(400).entity("Syntax error : Authentication").build();
		}else{
			if(!authent.getMail().matches("(^[A-Za-z0-9\\._-]+@[A-Za-z0-9\\.-_]+.[a-zA-Z]{2,4}$)||([a-zA-Z 0-9]+)")){
				return Response.status(400).entity("Wrong format : Login").build();
			}
			if(!authent.getPassword().matches("[a-zA-Z 0-9-+\\*éàèêëîïôö\\.!]+")){
				return Response.status(400).entity("Wrong format : Password").build();
			}
			if(authent.getClientTimestamp() == 0){
				return Response.status(400).entity("Wrong value : Timestamp").build();
			}
			
			//Si le password donné est incorrect pour le login donné
			if(!AuthenticationService.checkUserPassword(authent)){
				return Response.status(404).build();
			}
			
			Authentication storedAuthent = authentDAO.find(authent.getMail());
			if(storedAuthent == null){
				return Response.status(404).build();
			}
			if(storedAuthent.getClientTimestamp()>authent.getClientTimestamp()){
				return Response.status(400).entity("Wrong value : ClientTimestamp").build();
			}
			String token = CryptographyService.generateCryptedToken(authent.getMail());
			authent.setEncryptedToken(token);
			authent.setServerTimestamp((new Date()).getTime());
			int nbRes = authentDAO.update(authent);
			if(nbRes == 0){
				return Response.status(400).entity("Unable to update authentication").build();
			}else{
				return Response.ok(token).build();
			}
		}
	}
}
