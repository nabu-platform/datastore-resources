package be.nabu.libs.datastore.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;
import be.nabu.libs.datastore.DatastoreFactory;
import be.nabu.libs.datastore.api.DataProperties;
import be.nabu.libs.datastore.api.Datastore;
import be.nabu.libs.datastore.api.WritableDatastore;
import be.nabu.libs.datastore.resources.context.DatastoreConfiguration;
import be.nabu.libs.datastore.resources.context.DatastoreConfiguration.DatastoreContext;
import be.nabu.libs.datastore.resources.context.StringContextRouter;
import be.nabu.utils.io.IOUtils;

public class TestDatastore extends TestCase {
	
	public void testDefaultDatastore() throws IOException {
		Datastore datastore = DatastoreFactory.getInstance().getDatastore();
		assertTrue("Datastore is not writable", datastore instanceof WritableDatastore);
		
		WritableDatastore writableDatastore = (WritableDatastore) datastore;
	
		String content = "test";
		URI resource = writableDatastore.store(new ByteArrayInputStream(content.getBytes()), "test.txt", "text/plain");
		System.out.println("Created: " + resource);
		assertNotNull(resource);
		
		assertEquals(content, new String(IOUtils.toBytes(IOUtils.wrap(datastore.retrieve(resource))), Charset.defaultCharset()));
		
		DataProperties properties = datastore.getProperties(resource);
		assertEquals("test.txt", properties.getName());
		assertEquals("text/plain", properties.getContentType());
		assertEquals(Long.valueOf(4), properties.getSize());
	}
	
	public void testWithConfig() throws URISyntaxException, JAXBException, IOException {
		DatastoreConfiguration configuration = new DatastoreConfiguration();
		configuration.setDefaultLocation(new URI("memory:/default"));
		DatastoreContext context = new DatastoreContext();
		context.setLocation(new URI("memory:/somewhere"));
		context.setName("somewhere");
		configuration.getContexts().add(context);
		configuration.store(new URI("memory:/configuration.xml"));
		
		ResourceDatastore<String> datastore = new ResourceDatastore<String>(new StringContextRouter(DatastoreConfiguration.getConfiguration(new URI("memory:/configuration.xml"))));
		
		URI first = datastore.store("somewhere", new ByteArrayInputStream("test".getBytes()), "test.txt", "text/plain");
		assertTrue(first.toString().startsWith("memory:/somewhere/"));
		
		URI second = datastore.store("else", new ByteArrayInputStream("test".getBytes()), "test.txt", "text/plain");
		assertTrue(second.toString().startsWith("memory:/default/"));
		
		URI third = datastore.store(new ByteArrayInputStream("test".getBytes()), "test.txt", "text/plain");
		assertTrue(third.toString().startsWith("memory:/default/"));
		
		URI fourth = datastore.store("somewhere.specific", new ByteArrayInputStream("test".getBytes()), "test.txt", "text/plain");
		assertTrue(fourth.toString().startsWith("memory:/somewhere/"));
	}
}
