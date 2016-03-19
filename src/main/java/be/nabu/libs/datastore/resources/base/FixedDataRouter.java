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
