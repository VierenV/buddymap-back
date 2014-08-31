package com.buddymap.dao;

import org.bson.types.ObjectId;

import com.buddymap.connection.DBConnection;
import com.buddymap.model.Image;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class ImageDAO extends AbstractDAO<Image> {
	
	private static GridFS gfs;
	
	static{
		gfs = new GridFS(DBConnection.getInstance().getDB(), "images");
	}
	
	@Override
	public int delete(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Image object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String create(Image image) {
		GridFSInputFile gfsFile = gfs.createFile(image.getUploadedInputStream());
		gfsFile.setFilename(image.getFileDetail().getFileName());
		gfsFile.save();
		ObjectId id = (ObjectId)gfsFile.getId();
		return id.toString();
	}

	@Override
	public Image find(String id) {
		GridFSDBFile file = gfs.findOne(new ObjectId(id));
		Image image = null;
		if(file != null){
			image = new Image();
			image.setUploadedInputStream(file.getInputStream());
		}
		return image;
	}

}
