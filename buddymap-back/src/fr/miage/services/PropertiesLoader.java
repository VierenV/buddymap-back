package fr.miage.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesLoader {
	private static volatile PropertiesLoader propertiesLoader;
	private Properties prop;
	private Logger logger = Logger.getRootLogger();
	private static String salt;
	private static String AESKey;
	
	private PropertiesLoader() {
		try {
			InputStream inputStream  = getClass().getClassLoader().getResourceAsStream("pathworld_local.properties");
			prop = new Properties();
			prop.load(inputStream);
			salt = prop.getProperty("salt");
			AESKey = prop.getProperty("AESKey");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return this.salt;
	}
	
	public String getAESKey(){
		return this.AESKey;
	}
}
