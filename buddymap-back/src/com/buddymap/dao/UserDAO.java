package com.buddymap.dao;

import java.util.List;

import org.bson.types.ObjectId;

import com.buddymap.connection.DBConnection;
import com.buddymap.model.User;
import com.buddymap.services.CryptographyService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;


public class UserDAO extends AbstractDAO<User>{
	
	private static final String COLLECTION_NAME = "users";
	
	public UserDAO(){
		this.setCollec(DBConnection.getInstance().getDB().getCollection(COLLECTION_NAME));
	}

	@Override
	public int delete(String idUser) {
		BasicDBObject query = new BasicDBObject();
		query.append("_id", new ObjectId(idUser));
		WriteResult res = this.getCollec().remove(query);
		return res.getN();
	}

	@Override
	public int update(User user) {
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put("mail", user.getMail());
		newDocument.put("pseudo", user.getPseudo());
		newDocument.put("currentLatitude", user.getCurrentLatitude());
		newDocument.put("currentLongitude", user.getCurrentLongitude());
		newDocument.put("friendsList", user.getFriendsList());
		newDocument.put("houseLatitude", user.getHouseLatitude());
		newDocument.put("houseLongitude", user.getHouseLongitude());
		newDocument.put("lastRefresh", user.getLastRefresh());
		newDocument.put("pwd", user.getPassword());
		
		BasicDBObject searchQuery = new BasicDBObject().append("_id", new ObjectId(user.getId()));
		WriteResult res = this.getCollec().update(searchQuery, newDocument);
		return res.getN();
	}

	@Override
	public String create(User user) {
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put("mail", user.getMail());
		newDocument.put("pseudo", user.getPseudo());
		newDocument.put("currentLatitude", user.getCurrentLatitude());
		newDocument.put("currentLongitude", user.getCurrentLongitude());
		newDocument.put("friendsList", user.getFriendsList());
		newDocument.put("houseLatitude", user.getHouseLatitude());
		newDocument.put("houseLongitude", user.getHouseLongitude());
		newDocument.put("lastRefresh", user.getLastRefresh());
		newDocument.put("pwd", CryptographyService.hashSaltedSHA512(user.getPassword()+user.getMail()));
		
		BasicDBObject query = new BasicDBObject();
		query.put("mail", user.getMail());
		BasicDBObject setOnInsert = new BasicDBObject();
		setOnInsert.put("$setOnInsert", newDocument);
		
		WriteResult res = this.getCollec().update(query, setOnInsert, true, false);
		boolean updated = (Boolean) res.getField("updatedExisting");
		if(!updated){
			ObjectId id = (ObjectId) res.getField("upserted");
			return id.toString();
		}else{
			return null;
		}
	}

	@Override
	public User find(String idUser){
		BasicDBObject searchQuery = new BasicDBObject().append("_id", new ObjectId(idUser));
		DBObject res = this.getCollec().findOne(searchQuery);
		if(res != null){
			User user = new User();
			user.setId(res.get("_id").toString());
			user.setCurrentLatitude((Double) res.get("currentLatitude"));
			user.setCurrentLongitude((Double) res.get("currentLongitude"));
			user.setHouseLatitude((Double) res.get("houseLatitude"));
			user.setHouseLongitude((Double) res.get("houseLongitude"));
			user.setLastRefresh(res.get("lastRefresh") != null ? res.get("lastRefresh").toString() : null);
			user.setMail(res.get("mail") != null ? res.get("mail").toString() : null);
			user.setPhone(res.get("phone") != null ? res.get("phone").toString() : null);
			user.setPseudo(res.get("pseudo") != null ? res.get("pseudo").toString() : null);
			user.setPassword(res.get("pwd") != null ? res.get("pwd").toString() : null);
			user.setFriendsList(res.get("friendsList") != null ? (List<String>) res.get("friendsList") : null);
			
			return user;
		}else{
			return null;
		}
	}

	public User findByMail(String mail){
		BasicDBObject searchQuery = new BasicDBObject().append("mail", mail);
		DBObject res = this.getCollec().findOne(searchQuery);
		if(res != null){
			User user = new User();
			user.setId(res.get("_id").toString());
			user.setCurrentLatitude((Double) res.get("currentLatitude"));
			user.setCurrentLongitude((Double) res.get("currentLongitude"));
			user.setHouseLatitude((Double) res.get("houseLatitude"));
			user.setHouseLongitude((Double) res.get("houseLongitude"));
			user.setLastRefresh(res.get("lastRefresh") != null ? res.get("lastRefresh").toString() : null);
			user.setMail(res.get("mail") != null ? res.get("mail").toString() : null);
			user.setPhone(res.get("phone") != null ? res.get("phone").toString() : null);
			user.setPseudo(res.get("pseudo") != null ? res.get("pseudo").toString() : null);
			user.setPassword(res.get("pwd") != null ? res.get("pwd").toString() : null);
			user.setFriendsList(res.get("friendsList") != null ? (List<String>) res.get("friendsList") : null);
			
			return user;
		}else{
			return null;
		}
	}
}
