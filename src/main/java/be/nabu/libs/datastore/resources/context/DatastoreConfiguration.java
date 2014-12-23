package be.nabu.libs.datastore.resources.context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import be.nabu.libs.resources.ResourceUtils;
import be.nabu.utils.io.IOUtils;
import be.nabu.utils.io.api.ByteBuffer;
import be.nabu.utils.io.api.ReadableContainer;
import be.nabu.utils.io.api.WritableContainer;

@XmlRootElement(name = "datastore")
public class DatastoreConfiguration {

	private URI defaultLocation;
	private List<DatastoreContext> contexts = new ArrayList<DatastoreContext>();
	
	public URI getDefaultLocation() {
		return defaultLocation;
	}
	public void setDefaultLocation(URI defaultLocation) {
		this.defaultLocation = defaultLocation;
	}

	public List<DatastoreContext> getContexts() {
		return contexts;
	}
	public void setContexts(List<DatastoreContext> contexts) {
		this.contexts = contexts;
	}
	
	public static class DatastoreContext {
		private String name;
		private URI location;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public URI getLocation() {
			return location;
		}
		public void setLocation(URI location) {
			this.location = location;
		}
	}
	
	public void store(OutputStream output) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(DatastoreConfiguration.class);
		context.createMarshaller().marshal(this, output);
	}
	
	public void store(URI uri) throws JAXBException, IOException {
		WritableContainer<ByteBuffer> container = ResourceUtils.toWritableContainer(uri, null);
		try {
			store(IOUtils.toOutputStream(container));
		}
		finally {
			container.close();
		}
	}
	
	public static DatastoreConfiguration getConfiguration(URI uri) throws JAXBException, IOException {
		ReadableContainer<ByteBuffer> container = ResourceUtils.toReadableContainer(uri, null);
		if (container == null) {
			throw new IllegalArgumentException("Can not find a datastore configuration located at: " + uri);
		}
		try {
			return getConfiguration(IOUtils.toInputStream(container));
		}
		finally {
			container.close();
		}
	}
	
	public static DatastoreConfiguration getConfiguration(String name) throws JAXBException, IOException {
		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		if (input == null) {
			throw new IllegalArgumentException("Can not find a datastore configuration with the name " + name + " on the classpath");
		}
		try {
			return getConfiguration(new BufferedInputStream(input));
		}
		finally {
			input.close();
		}
	}
	
	public static DatastoreConfiguration getConfiguration(File file) throws JAXBException, IOException {
		InputStream input = new BufferedInputStream(new FileInputStream(file));
		try {
			return getConfiguration(input);
		}
		finally {
			input.close();
		}
	}
	
	public static DatastoreConfiguration getConfiguration(InputStream input) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(DatastoreConfiguration.class);
		return (DatastoreConfiguration) context.createUnmarshaller().unmarshal(input);
	}
}
