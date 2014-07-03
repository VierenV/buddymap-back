package fr.miage.connexion;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.MongoClient;


public final class ConnexionBD {
	private static volatile ConnexionBD connec;
	private static DB db;
	private Logger logger = Logger.getRootLogger();
	
	private ConnexionBD() {
			MongoClient mongoClient;
			try {
				mongoClient = new MongoClient();
	        	DB dbase = mongoClient.getDB("project");
	        	db = dbase;
			} catch (UnknownHostException e) {
				logger.error("Fail to connect to db", e);
			}
	}

	public static ConnexionBD getInstance(){
		synchronized(ConnexionBD.class){
			if(connec==null){
				connec = new ConnexionBD();
			}
		}
		return connec;
	}

	public DB getDB() {
		return db;
	}
}
