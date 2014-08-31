package com.buddymap.model;

import java.io.InputStream;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class Image {
	
	private InputStream uploadedInputStream;
	private FormDataContentDisposition fileDetail;
	
	public InputStream getUploadedInputStream() {
		return uploadedInputStream;
	}
	public void setUploadedInputStream(InputStream uploadedInputStream) {
		this.uploadedInputStream = uploadedInputStream;
	}
	public FormDataContentDisposition getFileDetail() {
		return fileDetail;
	}
	public void setFileDetail(FormDataContentDisposition fileDetail) {
		this.fileDetail = fileDetail;
	}
}
