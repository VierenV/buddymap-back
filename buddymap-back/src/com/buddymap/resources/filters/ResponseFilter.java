package com.buddymap.resources.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ContainerResponseContext;
 
public class ResponseFilter implements ContainerResponseFilter {


	public void filter(ContainerRequestContext  request,
			ContainerResponseContext response) {
		if(request.getProperty("encryptedToken") != null){
			response.getHeaders().add("Authorization", request.getProperty("encryptedToken"));
		}
		response.getHeaders().add("Access-Control-Allow-Origin", "*");
		response.getHeaders().add("Access-Control-Allow-Referer", "*");
		response.getHeaders().add("Access-Control-Allow-Headers", "accept, origin, referer, content-type");
		response.getHeaders().add("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE");
		response.getHeaders().add("Access-Control-Expose-Headers", "Location");
	}
 
}