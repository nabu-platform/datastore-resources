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
