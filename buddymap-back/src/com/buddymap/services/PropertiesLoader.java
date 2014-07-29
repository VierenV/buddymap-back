package com.buddymap.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesLoader {
	private static volatile PropertiesLoader propertiesLoader;
	private static final String fileName = "buddymap.properties";
	private Properties prop;
	private Logger logger = Logger.getRootLogger();
	private String salt;
	private String AESKey;
	private String DBName;
	
	private PropertiesLoader() {
		try {
			InputStream inputStream  = getClass().getClassLoader().getResourceAsStream(fileName);
			prop = new Properties();
			prop.load(inputStream);
			salt = prop.getProperty("salt");
			AESKey = prop.getProperty("AESKey");
			DBName = prop.getProperty("DBName");
		} catch (IOException e) {
			logger.error("Error while loading properties file "+fileName, e);
		}
	}
	
	public static PropertiesLoader getInstance(){
		synchronized(PropertiesLoader.class){
			if(propertiesLoader==null){
				propertiesLoader = new PropertiesLoader();
			}
		}
		return propertiesLoader;
	}
	
	public String getSalt(){
		return salt;
	}
	
	public String getAESKey(){
		return AESKey;
	}

	public String getDBName() {
		return DBName;
	}
}
