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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.nabu.libs.datastore.resources.DataRouter;
import be.nabu.libs.resources.ResourceFactory;
import be.nabu.libs.resources.ResourceUtils;
import be.nabu.libs.resources.URIUtils;
import be.nabu.libs.resources.api.ManageableContainer;

public class StringContextBaseRouter implements DataRouter<String> {

	private URI base;
	
	private static ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>();
	
	public StringContextBaseRouter() {
		this(new File("datastore").toURI());
	}
	
	public StringContextBaseRouter(URI base) {
		this.base = base;
	}
	
	@Override
	public ManageableContainer<?> route(String context, ResourceFactory factory, Principal principal) throws IOException {
		URI target = context == null ? base : URIUtils.getChild(base, context.replace('.', '/'));
		ManageableContainer<?> root = (ManageableContainer<?>) factory.resolve(target, principal);
		if (root == null) {
			try {
				root = (ManageableContainer<?>) ResourceUtils.mkdir(target, principal);
			}
			catch (IOException e) {
				// try to resolve again, someone may have created it in the mean time
				root = (ManageableContainer<?>) factory.resolve(target, principal);
				if (root == null) {
					throw e;
				}
			}
		}
		if (root == null) {
			throw new FileNotFoundException("Can not locate nor create the root of the datastore context: " + target);
		}
		String path = getFormatter().format(new Date());
		return (ManageableContainer<?>) ResourceUtils.mkdirs(root, path);
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

	public URI getBase() {
		return base;
	}

}
