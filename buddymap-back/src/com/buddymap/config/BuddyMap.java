package com.buddymap.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import com.buddymap.resources.filters.RequestFilter;
import com.buddymap.resources.filters.ResponseFilter;

@ApplicationPath("/")
public class BuddyMap extends ResourceConfig {
	 
	    public BuddyMap() {
	        // Register resources and providers using package-scanning.
	        packages("com.buddymap.resources");
	        register(AuthenticationRequired.class);
	        register(RequestFilter.class);
	        register(ResponseFilter.class);
	    }
}
