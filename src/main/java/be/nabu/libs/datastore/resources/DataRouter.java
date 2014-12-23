package be.nabu.libs.datastore.resources;

import java.io.IOException;
import java.security.Principal;

import be.nabu.libs.resources.ResourceFactory;
import be.nabu.libs.resources.api.ManageableContainer;

public interface DataRouter<T> {
	/**
	 * The context can be "null", this should not result in a npe but instead fall back to a default
	 * If such a default is not possible, return null
	 */
	public ManageableContainer<?> route(T context, ResourceFactory factory, Principal principal) throws IOException;
	
	public Class<T> getContextClass();
	
}
