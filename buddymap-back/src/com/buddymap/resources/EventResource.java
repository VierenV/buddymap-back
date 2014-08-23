package com.buddymap.resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.buddymap.config.AuthenticationRequired;
import com.buddymap.dao.EventDAO;
import com.buddymap.model.Event;
import com.buddymap.model.User;

@AuthenticationRequired
@Path("/events")
public class EventResource {
	
	@Context
	protected ContainerRequestContext request;
	
	private EventDAO eventDAO = new EventDAO();
	private static Logger logger = Logger.getRootLogger();
	
	@GET
	@Path("/{idEvent : [0-9a-z]+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEvent(@PathParam("idEvent") String idEvent) throws JsonParseException, JsonMappingException, IOException{
		Event event = eventDAO.find(idEvent);
		if(event == null){
			return Response.status(404).build();
		}
		//else if(!(event.getGuestList()).contains((User)request.getProperty("connectedUser")) && !event.getIdUser().equals(((User)request.getProperty("connectedUser")).getId())){
			//return Response.status(401).build();
		//}
		else{
			return Response.ok(event).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEventsByCriteria(@QueryParam(value="idUser") String idUser){
		if(idUser == null || !idUser.matches("[0-9a-z]+")){
			return Response.status(400).entity("Wrong format : idUser").build();
		}
		//if(!idUser.equals(((User)request.getProperty("connectedUser")).getId())){
			//return Response.status(401).build();
		//}
		List<Event> eventList = eventDAO.findByUser(idUser);
		if(eventList.isEmpty()){
			return Response.status(404).build();
		}
		return Response.ok(eventList).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEvent(Event event, @Context UriInfo uriInfo) throws URISyntaxException {
		if (event == null) {
			return Response.status(400).entity("Syntax error : Event").build();
		}else{
			if(event.getTitle() == null || !event.getTitle().matches("[a-zA-Z 0-9-<>\\.]+")){
				return Response.status(400).entity("Wrong format : Title").build();
			}
			if(event.getEventType() == null || !event.getEventType().matches("[a-zA-Z]+")){
				return Response.status(400).entity("Wrong format : Event type").build();
			}
			if(event.getDetails() == null || !event.getDetails().matches("[a-zA-Z \\'0-9-éàèêëîïôö#\\.!]+")){
				return Response.status(400).entity("Wrong format : details").build();
			}
			if(event.getDate() == null || !event.getDate().matches("^[0-9]{4}[/-]((0[1-9])|(1[0-2]))[/-][0-3][0-9]$")){
				return Response.status(400).entity("Wrong format : Date (YYYY/MM/DD)").build();
			}
			String nbRes = eventDAO.create(event);
			if(nbRes == null){
				return Response.status(400).entity("Unable to add event").build();
			}else{
				return Response.created(new URI(uriInfo.getAbsolutePath()+ "/"+nbRes)).build();
			}
		}
	}
	
	@DELETE
	@Path("/{idEvent:[0-9a-z]+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEvent(@PathParam(value="idEvent") String idEvent){
		Event event = eventDAO.find(idEvent);
		if(event == null){
			return Response.status(404).entity("Unable to delete event").build();
		}
		//if(!event.getIdUser().equals(((User)request.getProperty("connectedUser")).getId())){
			//return Response.status(401).build();
		//}
		int nbRes = eventDAO.delete(idEvent);
		if(nbRes < 1){
			return Response.status(404).entity("Unable to delete event").build();
		}else{
			return Response.noContent().build();
		}
	}
	
	@PUT
	@Path("/{idEvent:[0-9a-z]+}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateEvent(@PathParam(value="idEvent")String idEvent, Event event){
		if (event == null) {
			return Response.status(400)
					.entity("Syntax error : event").build();
		} else {
			Event eventStored = eventDAO.find(idEvent);
			if(eventStored == null){
				return Response.status(404).entity("Unable to delete event").build();
			}
			if(!eventStored.getIdUser().equals(((User)request.getProperty("connectedUser")).getId())){
				return Response.status(401).build();
			}
			if(!event.getTitle().matches("[a-zA-Z 0-9-<>\\.]+")){
				return Response.status(400).entity("Wrong format : Title").build();
			}
			if(!event.getEventType().matches("[a-zA-Z]+")){
				return Response.status(400).entity("Wrong format : Event type").build();
			}
			if(!event.getDetails().matches("[a-zA-Z 0-9-éàèêëîïôö\\.!]+")){
				return Response.status(400).entity("Wrong format : details").build();
			}
			if(!event.getDate().matches("^[0-9]{4}[/-]((0[1-9])|(1[0-2]))[/-][0-3][0-9]$")){
				return Response.status(400).entity("Wrong format : Date (YYYY/MM/DD)").build();
			}
			event.setIdEvent(idEvent);
			int nb = eventDAO.update(event);
			if (nb == 1) {
				return Response.ok(event).build();
			} else{
				return Response.status(404).build();
			} 
		}
	}
}
