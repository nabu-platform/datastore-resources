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
