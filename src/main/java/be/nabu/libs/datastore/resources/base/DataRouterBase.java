package be.nabu.libs.datastore.resources.base;

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
import be.nabu.libs.resources.api.ManageableContainer;

public class DataRouterBase implements DataRouter<Object> {

	private URI base;
	
	private static ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>();
	
	public DataRouterBase() {
		this(new File("datastore").toURI());
	}
	
	public DataRouterBase(URI base) {
		this.base = base;
	}
	
	@Override
	public ManageableContainer<?> route(Object context, ResourceFactory factory, Principal principal) throws IOException {
		ManageableContainer<?> root = (ManageableContainer<?>) factory.resolve(base, principal);
		if (root == null) {
			root = (ManageableContainer<?>) ResourceUtils.mkdir(base, principal);
		}
		if (root == null) {
			throw new FileNotFoundException("Can not locate the root of the datastore: " + base);
		}
		String path = getFormatter().format(new Date());
		return (ManageableContainer<?>) ResourceUtils.mkdirs(root, path);
	}

	@Override
	public Class<Object> getContextClass() {
		return Object.class;
	}

	protected SimpleDateFormat getFormatter() {
		if (formatter.get() == null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
			formatter.set(simpleDateFormat);
		}
		return formatter.get();
	}
}
