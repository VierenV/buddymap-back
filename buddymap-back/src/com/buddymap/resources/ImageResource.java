package com.buddymap.resources;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.buddymap.config.AuthenticationRequired;
import com.buddymap.dao.ImageDAO;
import com.buddymap.model.Image;

@AuthenticationRequired
@Path("/images")
public class ImageResource {
	
	@Context
	protected ContainerRequestContext request;
	private ImageDAO imageDAO = new ImageDAO();
	private static Logger logger = Logger.getRootLogger();
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @Context UriInfo uriInfo) throws URISyntaxException {
 
		if(uploadedInputStream == null || fileDetail == null){
			return Response.status(400).build();
		}
		if(fileDetail.getSize()>4096){
			return Response.status(400).entity("The file size is limited to 4MB").build();
		}
		Image img = new Image();
		img.setUploadedInputStream(uploadedInputStream);
		img.setFileDetail(fileDetail);
		
		String id = imageDAO.create(img);
		
		return Response.created(new URI(uriInfo.getAbsolutePath()+ "/"+id)).build();
	}
	
	@GET
	@Path("/{idImage : [0-9a-z]+}")
	@Produces("image/png")
	public Response getImage(@PathParam("idImage") String idImage) throws IOException{
		Image img = imageDAO.find(idImage);
		if(img == null){
			return Response.status(404).build();
		}
		else{
			BufferedInputStream in = new BufferedInputStream(img.getUploadedInputStream());
			BufferedImage image = ImageIO.read(in);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ImageIO.write(image, "png", baos);
		    byte[] imageData = baos.toByteArray();
			return Response.ok(imageData).build();
		}
	}
}
