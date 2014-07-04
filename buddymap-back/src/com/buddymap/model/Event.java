package com.buddymap.model;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class Event {
	private String title;
	private String date;
	private String eventType;
	private String details;
	@JsonProperty("_id")
	private String idEvent;
	private double latitude;
	private double longitude;
	private String idUser;
	private Map<String, String> guestMap = new HashMap<String, String>();
		
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	/**
	 * @return the idEvent
	 */
	public String getIdEvent() {
		return idEvent;
	}
	/**
	 * @param idEvent the idEvent to set
	 */
	public void setIdEvent(String idEvent) {
		this.idEvent = idEvent;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getIdUser() {
		return idUser;
	}
	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}
	public Map<String, String> getGuestMap() {
		return guestMap;
	}
	public void setGuestMap(Map<String, String> guestMap) {
		this.guestMap = guestMap;
	}
}
