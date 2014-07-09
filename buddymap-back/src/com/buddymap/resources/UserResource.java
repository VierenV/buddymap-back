package com.buddymap.resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.buddymap.dao.AuthenticationDAO;
import com.buddymap.dao.UserDAO;
import com.buddymap.model.Authentication;
import com.buddymap.model.User;


@Path("/users")
public class UserResource {

	@Context
	protected ContainerRequestContext request;
	private UserDAO userDAO = new UserDAO();
	private AuthenticationDAO authentDAO = new AuthenticationDAO();
	private static Logger logger = Logger.getRootLogger();
	
	@GET
	@Path("/{idUser : [0-9a-z]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("idUser") String idUser) throws JsonParseException, JsonMappingException, IOException{
		User user = userDAO.find(idUser);
		ObjectMapper mapper = new ObjectMapper();
		if(user == null){
			return Response.status(404).build();
		}else if(!user.getId().equals(((User)request.getProperty("connectedUser")).getId())){
			return Response.status(401).build();
		}else{
			String fluxJson;
			try {
				fluxJson = mapper.writeValueAsString(user);
				return Response.ok(fluxJson).build();
			} catch (JsonGenerationException e) {
				logger.error("Error while parsing json", e);
				return Response.status(500).build();
			} catch (JsonMappingException e) {
				logger.error("Error while parsing json", e);
				return Response.status(500).build();
			} catch (IOException e) {
				logger.error("Error while parsing json", e);
				return Response.status(500).build();
			}
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addUser(User user, @Context UriInfo uriInfo) throws URISyntaxException {
		if (user == null) {
			return Response.status(400).entity("Syntax error : User").build();
		}else{
			if(!user.getMail().matches("^[A-Za-z0-9\\._-]+@[A-Za-z0-9\\.-_]+.[a-zA-Z]{2,4}$")){
				return Response.status(400).entity("Wrong format : Mail").build();
			}
			if(!user.getPseudo().matches("[a-zA-Z 0-9]+")){
				return Response.status(400).entity("Wrong format : pseudo").build();
			}
			if(!user.getPassword().matches("[a-zA-Z 0-9-+\\*éàèêëîïôö\\.!]+")){
				return Response.status(400).entity("Wrong format : password").build();
			}
			String nbRes = userDAO.create(user);
			if(nbRes == null){
				return Response.status(409).entity("Conflict : Unable to add user").build();
			}else{
				Authentication authent = new Authentication();
				authent.setMail(user.getMail());
				authent.setServerTimestamp((new Date()).getTime());
				if(authentDAO.create(authent)==null){
					userDAO.delete(nbRes);
					return Response.status(500).entity("Unable to create authent after the user").build();
				}else{
					return Response.created(new URI(uriInfo.getAbsolutePath()+ "/"+nbRes)).build();
				}
			}
		}
	}
	
	@DELETE
	@Path("/{idUser:[0-9a-z]+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(@PathParam(value="idUser") String idUser){
		User user = userDAO.find(idUser);
		if(user == null){
			return Response.status(404).entity("Unable to delete user").build();
		}
		if(!user.getId().equals(((User)request.getProperty("connectedUser")).getId())){
			return Response.status(401).build();
		}
		int nbRes = userDAO.delete(idUser);
		if(nbRes < 1){
			return Response.status(404).entity("Unable to delete user").build();
		}else{
			return Response.noContent().build();
		}
	}
	
	@PUT
	@Path("/{idUser:[0-9a-z]+}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(@PathParam(value="idUser")String idUser, User user){
		if (user == null) {
			return Response.status(400)
					.entity("Syntax error : user").build();
		} else if(!idUser.equals(((User)request.getProperty("connectedUser")).getId())){
			return Response.status(401).build();
		}else {
			if(!user.getMail().matches("^[A-Za-z0-9\\._-]+@[A-Za-z0-9\\.-_]+.[a-zA-Z]{2,4}$")){
				return Response.status(400).entity("Wrong format : Mail").build();
			}
			if(!user.getPseudo().matches("[a-zA-Z 0-9]+")){
				return Response.status(400).entity("Wrong format : pseudo").build();
			}
			if(!user.getPassword().matches("[a-zA-Z 0-9-+\\*éàèêëîïôö\\.!]+")){
				return Response.status(400).entity("Wrong format : password").build();
			}
			user.setId(idUser);
			int nb = userDAO.update(user);
			if (nb == 1) {
				ObjectMapper mapper = new ObjectMapper();
				String fluxJson;
				try {
					fluxJson = mapper.writeValueAsString(user);
					return Response.ok(fluxJson).build();
				} catch (JsonGenerationException e) {
					logger.error("Error while parsing json", e);
					return Response.status(500).build();
				} catch (JsonMappingException e) {
					logger.error("Error while parsing json", e);
					return Response.status(500).build();
				} catch (IOException e) {
					logger.error("Error while parsing json", e);
					return Response.status(500).build();
				}
			} else{
				return Response.status(404).build();
			} 
		}
	}
}
