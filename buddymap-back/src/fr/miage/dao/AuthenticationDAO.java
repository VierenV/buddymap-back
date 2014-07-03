package fr.miage.dao;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import fr.miage.connexion.ConnexionBD;
import fr.miage.model.Authentication;

public class AuthenticationDAO extends AbstractDAO<Authentication>{

	private static final String COLLECTION_NAME = "Authentication";
	
	public AuthenticationDAO(){
		this.setCollec(ConnexionBD.getInstance().getDB().getCollection(COLLECTION_NAME));
	}
	
	@Override
	public int delete(String id) {
		BasicDBObject query = new BasicDBObject();
		query.append("mail", new ObjectId(id));
		WriteResult res = this.getCollec().remove(query);
		return res.getN();
	}

	@Override
	public int update(Authentication authent) {
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put("mail", authent.getMail());
		newDocument.put("encryptedToken", authent.getEncryptedToken());
		newDocument.put("clientTimestamp", authent.getClientTimestamp());
		newDocument.put("serverTimestamp", authent.getServerTimestamp());
		
		BasicDBObject searchQuery = new BasicDBObject().append("mail", authent.getMail());
		WriteResult res = this.getCollec().update(searchQuery, newDocument);
		return res.getN();
	}

	@Override
	public String create(Authentication authent) {
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put("mail", authent.getMail());
		newDocument.put("encryptedToken", authent.getEncryptedToken());
		newDocument.put("clientTimestamp", authent.getClientTimestamp());
		newDocument.put("serverTimestamp", authent.getServerTimestamp());
		
		WriteResult res = this.getCollec().insert(newDocument);
		return ((Double) res.getField("ok")).intValue() == 1 ? authent.getMail() : null;
	}

	@Override
	public Authentication find(String id) {
		BasicDBObject searchQuery = new BasicDBObject().append("mail", id);
		DBObject res = this.getCollec().findOne(searchQuery);
		if(res != null){
			Authentication authent = new Authentication();
			authent.setClientTimestamp((Long) res.get("clientTimestamp"));
			authent.setEncryptedToken(res.get("encryptedToken") != null ? res.get("encryptedToken").toString() : null);
			authent.setMail(id);
			authent.setServerTimestamp((Long) res.get("serverTimestamp"));
			
			return authent;
		}else{
			return null;
		}
	}

}
