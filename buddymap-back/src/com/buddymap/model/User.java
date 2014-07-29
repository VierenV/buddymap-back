package com.buddymap.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class User{
	@JsonProperty("_id")
	private String id;
	private String pseudo;
	private String mail;
	private String password;
	private String phone;
	private double houseLatitude;
	private double houseLongitude;
	private double currentLatitude;
	private double currentLongitude;
	private String lastRefresh;
	private List<String> friendsList = new ArrayList<String>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public double getHouseLatitude() {
		return houseLatitude;
	}
	public void setHouseLatitude(double houseLatitude) {
		this.houseLatitude = houseLatitude;
	}
	public double getHouseLongitude() {
		return houseLongitude;
	}
	public void setHouseLongitude(double houseLongitude) {
		this.houseLongitude = houseLongitude;
	}
	public double getCurrentLatitude() {
		return currentLatitude;
	}
	public void setCurrentLatitude(double currentLatitude) {
		this.currentLatitude = currentLatitude;
	}
	public double getCurrentLongitude() {
		return currentLongitude;
	}
	public void setCurrentLongitude(double currentLongitude) {
		this.currentLongitude = currentLongitude;
	}
	public String getLastRefresh() {
		return lastRefresh;
	}
	public void setLastRefresh(String lastRefresh) {
		this.lastRefresh = lastRefresh;
	}
	public List<String> getFriendsList() {
		return friendsList;
	}
	public void setFriendsList(List<String> friendsList) {
		this.friendsList = friendsList;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if(getClass() != obj.getClass()){
			return false;
		}
		User other = (User) obj;
		if(this.id.equals(other.getId())){
			return true;
		}
		return false;
	}
	
	
}
