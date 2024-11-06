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

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;

import be.nabu.libs.datastore.DatastoreOutputStream;
import be.nabu.libs.datastore.api.Datastore;
import be.nabu.libs.resources.api.WritableResource;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.WritableContainer;

public class WritableDataResource extends DatastoreResource implements WritableResource, Closeable {

	private WritableContainer<ByteBuffer> output;

	public WritableDataResource(Datastore datastore, DatastoreOutputStream output) {
		super(datastore, output.getURI());
		this.output = IOUtils.wrap(new BufferedOutputStream(output));
	}
	
	@Override
	public WritableContainer<ByteBuffer> getWritable() throws IOException {
		return output;
	}

	@Override
	public void close() throws IOException {
		output.close();
	}
}
