package com.buddymap.dao;

import java.util.Map;

import org.bson.types.ObjectId;

import com.buddymap.connection.DBConnection;
import com.buddymap.model.Event;
import com.mongodb.BasicDBObject;
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
		newDocument.put("guests", event.getGuestMap());
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
		newDocument.put("guests", event.getGuestMap());
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
			event.setGuestMap(res.get("guestMap") != null ? (Map<String, String>) res.get("guestMap") : null);
			
			return event;
		}else{
			return null;
		}
	}

}
