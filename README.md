# Resource Datastore

This package contains an implementation for the datastore api that uses the resource system in the back. The resource system is a low level VFS and can plug into many different protocols.

The resource datastore also implements the "ContextualDynamicResourceProvider" interface which allows you to plug it in into other libraries like the mime parsing (and by extension the http server/client), the type binding framework,...

## Configuration

By default, the resource datastore will try to find a file called "datastore.xml" using the context classloader.
Alternatively you can also provide your own configuration. In both these cases however the resource datastore uses a String context.

An example of the configuration file:

```xml
<datastore>
	<defaultLocation>file:/tmp/datastore</defaultLocation>
	<contexts>
		<name>myContext</name>
		<location>smb://remote/path/to/something</location>
	</contexts>
	<contexts>
		<name>temporary</name>
		<location>memory:/path/to/tmp</location>
	</contexts>
</datastore>
```

You can add a context entry for every situation that you want to cover and if no context matches are found, it will use the default location. As you can see the scheme is to identify the protocol that will be used. Any protocol-specific settings in the URI (e.g. how to add authentication) should be checked with the respective resource implementations.

Note that the string context uses a dot-based notation for specificity. For example given this config, if you store something to the context `myContext.subcontext` it will end up in the context `myContext` since it's seen as a child context. The most specific match wins so if you want to route a specific subcontext somewhere else, just define it in the configuration.

If you want more advanced (non-string) contexts, you can provide your own DataRouter implementation which is used in the background to decide where data should be routed.

## Folder Structure

When you store something to the resource datastore, it will actually create a folder structure on the target location that has the date in it. For instance suppose we save a file to the default location in the above configuration, it would actually end up in `file:/tmp/datastore/2013/12/13". The reason for this is twofold:

- If you just dump all the data into one big folder, it will get ever bigger. Scanning said folder (e.g. for archiving) or opening it in a GUI will get slower and slower
- This specific structure makes it easy to find data but also to archive it at a specific level. Suppose you want to archive at day level, just zip the entire day folder, month level? Zip the month folder.

## Unique Naming

The datastore obviously has to make sure there are no naming collisions when storing the file to the target folder (the resources API mandates a name be unique within a container). Additionally it needs to retain the original name but also the mime type but seeing that the mime type is usually deduced from the extension and the extension is usually part of the name, this could make things difficult if the actual mimetype does not match the original extension.

Suppose you store a file called "test.txt" in the datastore but you know (due to analysis, convention,...) that it actually contains "application/xml", the datastore will create this file in the backend: `file:/tmp/datastore/2013/12/13/test.txt.uuid.xml` where the uuid is obviously a generated UUID. This retains all the original information, is unique for the target file system and should you venture in that directory with a file explorer, it will pick up the correct extension.