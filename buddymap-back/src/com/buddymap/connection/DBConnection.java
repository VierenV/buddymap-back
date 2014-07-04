package com.buddymap.connection;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.MongoClient;


public final class DBConnection {
	private static volatile DBConnection connec;
	private static DB db;
	private Logger logger = Logger.getRootLogger();
	
	private DBConnection() {
			MongoClient mongoClient;
			try {
				mongoClient = new MongoClient();
	        	DB dbase = mongoClient.getDB("buddyMap");
	        	db = dbase;
			} catch (UnknownHostException e) {
				logger.error("Fail to connect to db", e);
			}
	}

	public static DBConnection getInstance(){
		synchronized(DBConnection.class){
			if(connec==null){
				connec = new DBConnection();
			}
		}
		return connec;
	}

	public DB getDB() {
		return db;
	}
}
