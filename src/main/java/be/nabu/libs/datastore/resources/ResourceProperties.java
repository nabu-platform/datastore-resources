/*
* Copyright (C) 2014 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

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
