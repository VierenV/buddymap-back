package com.buddymap.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.buddymap.connection.DBConnection;
import com.buddymap.model.Event;
import com.buddymap.model.User;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;


public class EventDAO extends AbstractDAO<Event> {

	private static final String COLLECTION_NAME = "events";
	
	public EventDAO(){
		this.setCollec(DBConnection.getInstance().getDB().getCollection(COLLECTION_NAME));
	}
	
	@Override
	public int delete(String idEvent) {
		BasicDBObject query = new BasicDBObject();
		query.append("_id", new ObjectId(idEvent));
		WriteResult res = this.getCollec().remove(query);
		return res.getN();
	}

	@Override
	public int update(Event event) {
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put("title", event.getTitle());
		newDocument.put("details", event.getDetails());
		newDocument.put("latitude", event.getLatitude());
		newDocument.put("longitude", event.getLongitude());
		newDocument.put("idUser", event.getIdUser());
		newDocument.put("eventType", event.getEventType());
		newDocument.put("guests", event.getGuestList());
		newDocument.put("date", event.getDate());
		
		BasicDBObject searchQuery = new BasicDBObject().append("_id", new ObjectId(event.getIdEvent()));
		WriteResult res = this.getCollec().update(searchQuery, newDocument);
		return res.getN();
	}

	@Override
	public String create(Event event) {
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put("title", event.getTitle());
		newDocument.put("details", event.getDetails());
		newDocument.put("latitude", event.getLatitude());
		newDocument.put("longitude", event.getLongitude());
		newDocument.put("idUser", event.getIdUser());
		newDocument.put("eventType", event.getEventType());
		BasicDBList guestList = new BasicDBList();
		for(User user : event.getGuestList()){
			guestList.add(new BasicDBObject("id", user.getId()).append("pseudo", user.getPseudo()).append("mail", user.getMail()));
		}
		newDocument.put("guests", guestList);
		newDocument.put("date", event.getDate());
		
		WriteResult res = this.getCollec().insert(newDocument);
		ObjectId id = (ObjectId)newDocument.get("_id");
		return ((Double) res.getField("ok")).intValue() == 1 ? id.toString() : null;
	}


	@Override
	public Event find(String idEvent){
		BasicDBObject searchQuery = new BasicDBObject().append("_id", new ObjectId(idEvent));
		DBObject res = this.getCollec().findOne(searchQuery);
		if(res != null){
			Event event = new Event();
			event.setIdEvent(res.get("_id").toString());
			event.setDate(res.get("date") != null ? res.get("date").toString() : null);
			event.setDetails(res.get("details") != null ? res.get("details").toString() : null);
			event.setEventType(res.get("eventType") != null ? res.get("eventType").toString() : null);
			event.setIdUser(res.get("idUser") != null ? res.get("idUser").toString() : null);
			event.setLatitude((Double) res.get("latitude"));
			event.setLongitude((Double) res.get("longitude"));
			event.setTitle(res.get("title") != null ? res.get("title").toString() : null);
			BasicDBList guests = (BasicDBList) res.get("guests");
			List<User> listeGuests = new ArrayList<User>();
			for(Object guest : guests){
				User user = new User();
				user.setId(((DBObject) guest).get("id") != null ? ((DBObject) guest).get("id").toString() : null);
				user.setPseudo(((DBObject) guest).get("pseudo") != null ? ((DBObject) guest).get("pseudo").toString() : null);
				user.setMail(((DBObject) guest).get("mail") != null ? ((DBObject) guest).get("mail").toString() : null);
				
				listeGuests.add(user);
			}
			event.setGuestList(listeGuests);
			
			return event;
		}else{
			return null;
		}
	}

	public List<Event> findByUser(String idUser) {
		BasicDBObject searchQuery = new BasicDBObject().append("id", idUser);
		BasicDBObject elemMatch = new BasicDBObject("$elemMatch", searchQuery);
		BasicDBObject globalQuery = new BasicDBObject().append("guests", elemMatch);
		DBCursor cursor = this.getCollec().find(globalQuery);
		List<Event> eventList = new ArrayList<Event>();
		while(cursor.hasNext()){
			DBObject res = cursor.next();
			Event event = new Event();
			event.setIdEvent(res.get("_id").toString());
			event.setDate(res.get("date") != null ? res.get("date").toString() : null);
			event.setDetails(res.get("details") != null ? res.get("details").toString() : null);
			event.setEventType(res.get("eventType") != null ? res.get("eventType").toString() : null);
			event.setIdUser(res.get("idUser") != null ? res.get("idUser").toString() : null);
			event.setLatitude((Double) res.get("latitude"));
			event.setLongitude((Double) res.get("longitude"));
			event.setTitle(res.get("title") != null ? res.get("title").toString() : null);
			BasicDBList guests = (BasicDBList) res.get("guests");
			List<User> listeGuests = new ArrayList<User>();
			for(Object guest : guests){
				User user = new User();
				user.setId(((DBObject) guest).get("id") != null ? ((DBObject) guest).get("id").toString() : null);
				user.setPseudo(((DBObject) guest).get("pseudo") != null ? ((DBObject) guest).get("pseudo").toString() : null);
				user.setMail(((DBObject) guest).get("mail") != null ? ((DBObject) guest).get("mail").toString() : null);
				
				listeGuests.add(user);
			}
			event.setGuestList(listeGuests);
			
			eventList.add(event);
		}
		return eventList;
	}

}
