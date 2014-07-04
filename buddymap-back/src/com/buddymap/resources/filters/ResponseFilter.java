package com.buddymap.resources.filters;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
 
public class ResponseFilter implements ContainerResponseFilter {


	public ContainerResponse filter(ContainerRequest request,
			ContainerResponse response) {
		if(request.getProperties().get("encryptedToken") != null){
			response.getHttpHeaders().add("Authorization", request.getProperties().get("encryptedToken"));
		}
		response.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
		response.getHttpHeaders().add("Access-Control-Allow-Referer", "*");
		response.getHttpHeaders().add("Access-Control-Allow-Headers", "accept, origin, referer, content-type");
		response.getHttpHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		response.getHttpHeaders().add("Access-Control-Expose-Headers", "Location");
		return response;
	}
 
}