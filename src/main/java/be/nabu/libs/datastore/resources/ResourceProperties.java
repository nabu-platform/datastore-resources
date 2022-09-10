package be.nabu.libs.datastore.resources;

import java.util.Date;

import be.nabu.libs.datastore.api.DataProperties;

public class ResourceProperties implements DataProperties {

	private String name;
	private String contentType;
	private Long size;
	private Date date;
	
	public ResourceProperties() {
		// autoconstruct
	}
	
	public ResourceProperties(String name, String contentType, Long size, Date date) {
		this.name = name;
		this.contentType = contentType;
		this.size = size;
		this.date = date;
	}
	
	@Override
	public Long getSize() {
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

	public void setLastModified(Date date) {
		this.date = date;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public void setSize(Long size) {
		this.size = size;
	}

}
