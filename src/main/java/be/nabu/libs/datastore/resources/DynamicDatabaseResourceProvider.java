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
