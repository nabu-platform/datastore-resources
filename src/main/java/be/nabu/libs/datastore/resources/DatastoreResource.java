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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;

import be.nabu.libs.datastore.api.DataProperties;
import be.nabu.libs.datastore.api.Datastore;
import be.nabu.libs.resources.api.LocatableResource;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;

public class DatastoreResource implements ReadableResource, LocatableResource {

	private Datastore datastore;
	private URI uri;
	private DataProperties properties;

	public DatastoreResource(Datastore datastore, URI uri) {
		this.datastore = datastore;
		this.uri = uri;
	}

	@Override
	public String getContentType() {
		return getProperties().getContentType();
	}

	@Override
	public String getName() {
		return getProperties().getName();
	}

	@Override
	public ResourceContainer<?> getParent() {
		return null;
	}

	@Override
	public ReadableContainer<ByteBuffer> getReadable() throws IOException {
		return IOUtils.wrap(new BufferedInputStream(datastore.retrieve(uri)));
	}
	
	private DataProperties getProperties() {
		if (properties == null) {
			try {
				properties = datastore.getProperties(uri);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return properties;
	}

	@Override
	public URI getUri() {
		return uri;
	}

}
