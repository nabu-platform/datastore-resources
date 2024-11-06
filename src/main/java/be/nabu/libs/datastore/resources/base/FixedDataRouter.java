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

package be.nabu.libs.datastore.resources.base;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;

import be.nabu.libs.datastore.resources.DataRouter;
import be.nabu.libs.resources.ResourceFactory;
import be.nabu.libs.resources.ResourceUtils;
import be.nabu.libs.resources.api.ManageableContainer;

public class FixedDataRouter implements DataRouter<Object> {

	private ManageableContainer<?> fixed;

	public FixedDataRouter(ManageableContainer<?> fixed) {
		this.fixed = fixed;
	}
	
	@Override
	public ManageableContainer<?> route(Object context, ResourceFactory factory, Principal principal) throws IOException {
		return (ManageableContainer<?>) ResourceUtils.mkdirs(fixed, DataRouterBase.getFormatter().format(new Date()));
	}

	@Override
	public Class<Object> getContextClass() {
		return Object.class;
	}
	
}
