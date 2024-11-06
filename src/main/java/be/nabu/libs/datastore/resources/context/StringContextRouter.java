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

package be.nabu.libs.datastore.resources.context;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import be.nabu.libs.datastore.resources.DataRouter;
import be.nabu.libs.datastore.resources.context.DatastoreConfiguration.DatastoreContext;
import be.nabu.libs.resources.ResourceFactory;
import be.nabu.libs.resources.ResourceUtils;
import be.nabu.libs.resources.api.ManageableContainer;

/**
 * You can separate your context with "." to be more specific, for example:
 * - register a context for "test"
 * - save something with context "test.something", it will be saved in test
 * - save something with context "something.else", it will be saved in the default 
 */
public class StringContextRouter implements DataRouter<String> {
	
	private static ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>();
	
	private DatastoreConfiguration configuration;
	private Map<String, ManageableContainer<?>> containers = new HashMap<String, ManageableContainer<?>>();
	
	public StringContextRouter(DatastoreConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public ManageableContainer<?> route(String context, ResourceFactory factory, Principal principal) throws IOException {
		if (context != null) {
			context = context.toLowerCase();
		}
		if (!containers.containsKey(context)) {
			synchronized (containers) {
				if (!containers.containsKey(context)) {
					DatastoreContext datastoreContext = null;
					if (context != null) {
						String contextToFind = context;
						while(datastoreContext == null) {
							for (DatastoreContext possibleContext : configuration.getContexts()) {
								if (contextToFind.equalsIgnoreCase(possibleContext.getName())) {
									datastoreContext = possibleContext;
									break;
								}
							}
							int index = contextToFind.lastIndexOf('.');
							if (index <= 0) {
								break;
							}
							contextToFind = contextToFind.substring(0, index);
						}
					}
					URI uri = datastoreContext == null ? configuration.getDefaultLocation() : datastoreContext.getLocation();
					ManageableContainer<?> container = (ManageableContainer<?>) ResourceUtils.mkdir(uri, null);
					containers.put(context, container);
				}
			}
		}
		return (ManageableContainer<?>) ResourceUtils.mkdirs(containers.get(context), getFormatter().format(new Date()));
	}

	@Override
	public Class<String> getContextClass() {
		return String.class;
	}
	
	protected SimpleDateFormat getFormatter() {
		if (formatter.get() == null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
			formatter.set(simpleDateFormat);
		}
		return formatter.get();
	}

	public DatastoreConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(DatastoreConfiguration configuration) {
		// need to reset the containers if the configuration has changed
		synchronized (containers) {
			this.configuration = configuration;
			containers.clear();
		}
	}

}
