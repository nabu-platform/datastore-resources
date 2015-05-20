package be.nabu.libs.datastore.resources;


import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.Principal;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import be.nabu.libs.datastore.DatastoreOutputStream;
import be.nabu.libs.datastore.api.ContextualStreamableDatastore;
import be.nabu.libs.datastore.api.DataProperties;
import be.nabu.libs.datastore.api.URNManager;
import be.nabu.libs.datastore.resources.base.DataRouterBase;
import be.nabu.libs.datastore.resources.context.DatastoreConfiguration;
import be.nabu.libs.datastore.resources.context.StringContextRouter;
import be.nabu.libs.resources.ResourceFactory;
import be.nabu.libs.resources.ResourceReadableContainer;
import be.nabu.libs.resources.ResourceUtils;
import be.nabu.libs.resources.ResourceWritableContainer;
import be.nabu.libs.resources.api.DynamicResourceProvider;
import be.nabu.libs.resources.api.FiniteResource;
import be.nabu.libs.resources.api.ManageableContainer;
import be.nabu.libs.resources.api.ReadableResource;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.TimestampedResource;
import be.nabu.libs.resources.api.WritableResource;
import be.nabu.utils.io.ContentTypeMap;
import be.nabu.utils.io.IOUtils;

public class ResourceDatastore<T> implements ContextualStreamableDatastore<T> {

	private URNManager urnManager;
	private DataRouter<T> dataRouter;
	private ResourceFactory resourceFactory;
	
	private Principal principal;
	
	@SuppressWarnings("unchecked")
	public ResourceDatastore() {
		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("datastore.xml");
		if (input == null) {
			this.dataRouter = (DataRouter<T>) new DataRouterBase();
		}
		else {
			try {
				this.dataRouter = (DataRouter<T>) new StringContextRouter(DatastoreConfiguration.getConfiguration(input));
			}
			catch (JAXBException e) {
				throw new RuntimeException(e);
			}
			finally {
				try {
					input.close();
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public ResourceDatastore(DatastoreConfiguration configuration) {
		this.dataRouter = (DataRouter<T>) new StringContextRouter(configuration);
	}
	
	public ResourceDatastore(DataRouter<T> dataRouter) {
		this.dataRouter = dataRouter;
	}
	
	private URI resolve(URI uri) {
		// if urn, resolve
		if (uri.getScheme().equals("urn")) {
			if (urnManager == null) {
				throw new IllegalArgumentException("Can not resolve URN's, no urn manager is set");
			}
			uri = urnManager.resolve(uri);
		}
		return uri;
	}
	
	private Resource getResource(URI uri) throws IOException {
		Resource resource = getResourceFactory().resolve(uri, principal);
		if (resource == null) {
			throw new IOException("Could not find the resource at " + uri);
		}
		return resource;
	}
	
	@Override
	public InputStream retrieve(URI uri) throws IOException {
		uri = resolve(uri);
		Resource resource = getResource(uri);
		if (!(resource instanceof ReadableResource)) {
			throw new IOException("The resource located at " + uri + " is not readable");
		}
		return IOUtils.toInputStream(new ResourceReadableContainer((ReadableResource) resource));
	}

	@Override
	public DataProperties getProperties(URI uri) throws IOException {
		Resource resource = getResource(resolve(uri));
		try {
			return new ResourceProperties(
				getActualName(resource.getName()), 
				resource.getContentType(), 
				resource instanceof FiniteResource ? ((FiniteResource) resource).getSize() : -1,
				resource instanceof TimestampedResource ? ((TimestampedResource) resource).getLastModified() : new Date()
			);
		}
		finally {
			if (resource instanceof Closeable) {
				((Closeable) resource).close();
			}
		}
	}

	/**
	 * The name in a resource container must be unique
	 * However the user must be able to store multiple resources with the same name which may end up in the same folder
	 */
	private static String generateUniqueName(String name, String contentType) {
		return name.replace('/', '_') + "." + UUID.randomUUID().toString() + "." + ContentTypeMap.getInstance().getExtensionFor(contentType);
	}
	
	/**
	 * This strips the uniquely generated part from the name
	 */
	private static String getActualName(String uniqueName) {
		return uniqueName.replaceAll("\\.[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}\\.[a-z0-9.]+$", "");
	}
	
	@Override
	public URI store(T context, InputStream input, String name, String contentType) throws IOException {
		DatastoreOutputStream stream = stream(context, name, contentType);
		try {
			IOUtils.copyBytes(IOUtils.wrap(input), IOUtils.wrap(stream));
		}
		finally {
			stream.close();
		}
		return urnManager == null ? stream.getURI() : urnManager.map(stream.getURI());
	}

	@Override
	public Class<T> getContextClass() {
		return dataRouter.getContextClass();
	}

	public URNManager getUrnManager() {
		return urnManager;
	}

	public void setUrnManager(URNManager urnManager) {
		this.urnManager = urnManager;
	}

	public ResourceFactory getResourceFactory() {
		if (resourceFactory == null) {
			resourceFactory = ResourceFactory.getInstance();
		}
		return resourceFactory;
	}

	public void setResourceFactory(ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	@Override
	public URI store(InputStream input, String name, String contentType) throws IOException {
		return store(null, input, name, contentType);
	}

	@Override
	public DatastoreOutputStream stream(T context, String name, String contentType) throws IOException {
		ManageableContainer<?> target = dataRouter.route(context, getResourceFactory(), principal);
		if (target == null) {
			throw new IOException("Can not store the resource with the given context parameters");
		}
		Resource resource = target.create(generateUniqueName(name, contentType), contentType);
		if (!(resource instanceof WritableResource)) {
			throw new IOException("Can not write to the generated resource");
		}
		URI uri = ResourceUtils.getURI(resource);
		return new DatastoreOutputStream(uri, IOUtils.toOutputStream(new ResourceWritableContainer((WritableResource) resource)));
	}
	
	public DynamicResourceProvider getDynamicResourceProvider() {
		return new DynamicDatabaseResourceProvider<T>(this);
	}

	@Override
	public DatastoreOutputStream stream(String name, String contentType) throws IOException {
		return stream(null, name, contentType);
	}
}
