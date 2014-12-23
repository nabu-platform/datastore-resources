package be.nabu.libs.datastore.resources;

import java.util.Date;

import be.nabu.libs.datastore.api.DataProperties;

public class ResourceProperties implements DataProperties {

	private String name;
	private String contentType;
	private long size;
	private Date date;
	
	public ResourceProperties(String name, String contentType, long size, Date date) {
		this.name = name;
		this.contentType = contentType;
		this.size = size;
		this.date = date;
	}
	
	@Override
	public long getSize() {
		return size;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public String toString() {
		return name + "; " + contentType + " [" + size + "]";
	}

	@Override
	public Date getLastModified() {
		return date;
	}
	
}
