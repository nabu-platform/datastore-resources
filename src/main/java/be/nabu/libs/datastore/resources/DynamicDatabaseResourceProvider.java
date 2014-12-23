package be.nabu.libs.datastore.resources;

import java.io.IOException;

import be.nabu.libs.datastore.api.ContextualStreamableDatastore;
import be.nabu.libs.resources.api.ContextualDynamicResourceProvider;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;

public class DynamicDatabaseResourceProvider<T> implements ContextualDynamicResourceProvider<T> {

	private ContextualStreamableDatastore<T> datastore;

	public DynamicDatabaseResourceProvider(ContextualStreamableDatastore<T> datastore) {
		this.datastore = datastore;
	}
	
	@Override
	public ReadableResource createDynamicResource(ReadableContainer<ByteBuffer> originalContent, String name, String contentType, boolean shouldClose) throws IOException {
		return new DynamicDatastoreResource<T>(datastore, null, name, contentType, originalContent, shouldClose);
	}

	@Override
	public ReadableResource createDynamicResource(T context, ReadableContainer<ByteBuffer> originalContent, String name, String contentType, boolean shouldClose) throws IOException {
		return new DynamicDatastoreResource<T>(datastore, context, name, contentType, originalContent, shouldClose);
	}
}
