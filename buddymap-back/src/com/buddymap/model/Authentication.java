package com.buddymap.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class Authentication {
	private String mail;
	private long serverTimestamp;
	private long clientTimestamp;
	private String password;
	private String encryptedToken;
	private String hashSignature;
	private String hashAlgorythm;
	
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public long getServerTimestamp() {
		return serverTimestamp;
	}
	public void setServerTimestamp(long serverTimestamp) {
		this.serverTimestamp = serverTimestamp;
	}
	public long getClientTimestamp() {
		return clientTimestamp;
	}
	public void setClientTimestamp(long clientTimestamp) {
		this.clientTimestamp = clientTimestamp;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEncryptedToken() {
		return encryptedToken;
	}
	public void setEncryptedToken(String encryptedToken) {
		this.encryptedToken = encryptedToken;
	}
	public String getHashSignature() {
		return hashSignature;
	}
	public void setHashSignature(String hashSignature) {
		this.hashSignature = hashSignature;
	}
	public String getHashAlgorythm() {
		return hashAlgorythm;
	}
	public void setHashAlgorythm(String hashAlgorythm) {
		this.hashAlgorythm = hashAlgorythm;
	}
	
}
