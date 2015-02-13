package be.nabu.libs.datastore.resources;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;

import be.nabu.libs.datastore.api.DataProperties;
import be.nabu.libs.datastore.api.Datastore;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;

public class DatastoreResource implements ReadableResource {

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
}
