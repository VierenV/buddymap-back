package com.buddymap.dao;

import com.mongodb.DBCollection;


public abstract class AbstractDAO<T> {
	DBCollection collec;
	
	public DBCollection getCollec() {
		return collec;
	}
	public void setCollec(DBCollection collec) {
		this.collec = collec;
	}
	public abstract int delete(String id);
	public abstract int update(T object);
	public abstract String create(T object);
	public abstract T find(String id);
}
