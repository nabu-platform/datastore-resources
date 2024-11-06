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

/**
 * Optionally add the capability of "chaining" the original resource
 * So on first read > original (+ copy to datastore)
 * On second read > flush (!) the datastore output, retrieve datastore as input and chain the original (+ further copy to datastore)
 * 		> the chain should switch to the original only after the datastore is done reading so any new data should not be read twice
 */
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
	public URI getUri() {
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
