package be.nabu.libs.datastore.resources;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

import be.nabu.libs.datastore.DatastoreOutputStream;
import be.nabu.libs.datastore.api.ContextualStreamableDatastore;
import be.nabu.libs.resources.api.LocatableResource;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;
import be.nabu.utils.io.containers.ReadableContainerDuplicator;

public class DynamicDatastoreResource<T> implements ReadableResource, LocatableResource, Closeable {

	private String name;
	private String contentType;
	private boolean alreadyRequested = false;
	private boolean shouldClose;
	private ReadableContainer<ByteBuffer> originalContent;
	private URI uri;
	private DatastoreOutputStream stream;
	private ContextualStreamableDatastore<T> datastore;
	
	@SuppressWarnings("unchecked")
	public DynamicDatastoreResource(ContextualStreamableDatastore<T> datastore, T context, String name, String contentType, ReadableContainer<ByteBuffer> originalContent, boolean shouldClose) throws IOException {
		this.datastore = datastore;
		this.name = name;
		this.contentType = contentType;
		this.originalContent = originalContent;
		this.shouldClose = shouldClose;
		this.stream = datastore.stream(context, name, contentType);
		this.uri = stream.getURI();
		this.originalContent = new ReadableContainerDuplicator<ByteBuffer>(originalContent, IOUtils.wrap(stream));
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ResourceContainer<?> getParent() {
		return null;
	}

	@Override
	public URI getURI() {
		return uri;
	}

	@Override
	public ReadableContainer<ByteBuffer> getReadable() throws IOException {
		if (!alreadyRequested) {
			alreadyRequested = true;
			return new PossiblyCloseableReadableContainer(stream, originalContent, shouldClose);
		}
		else {
			// make sure the datastore stream is closed first
			stream.close();
			return IOUtils.wrap(datastore.retrieve(uri));
		}
	}

	public static class PossiblyCloseableReadableContainer implements ReadableContainer<ByteBuffer> {
		private ReadableContainer<ByteBuffer> parent;
		private Closeable closeable;
		private boolean closeParent;

		public PossiblyCloseableReadableContainer(Closeable closeable, ReadableContainer<ByteBuffer> parent, boolean closeParent) {
			this.closeable = closeable;
			this.parent = parent;
			this.closeParent = closeParent;
		}

		/**
		 * If you close the original data stream, definitely close the datastore one
		 */
		@Override
		public void close() throws IOException {
			try {
				closeable.close();
			}
			finally {
				if (closeParent) {
					parent.close();
				}
			}
		}

		@Override
		public long read(ByteBuffer buffer) throws IOException {
			return parent.read(buffer);
		}
	}

	/**
	 * Make sure everything is closed
	 */
	@Override
	public void close() throws IOException {
		try {
			stream.close();
		}
		finally {
			if (shouldClose) {
				originalContent.close();
			}
		}
	}
}
