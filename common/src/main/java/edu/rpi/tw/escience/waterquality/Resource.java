package edu.rpi.tw.escience.waterquality;

import java.io.IOException;
import java.io.InputStream;

/**
 * The Resource interface is used to represent
 * resources belonging to a module that come from 
 * its JAR file. These are used to pass resources
 * from server to client as part of the UIVisitor
 * mechanism.
 * @author ewpatton
 *
 */
public interface Resource {

	/**
	 * Gets the internal path for this Resource
	 * @return
	 */
	String getPath();
	
	/**
	 * Opens the resource as a stream; actual behavior may be
	 * dependent on the underlying location of the resource
	 * (e\.g\. in a JAR vs on the file system).
	 * @return
	 * @throws IOException
	 */
	InputStream open() throws IOException;
	
	/**
	 * Returns whether this resource represents a JSP file
	 * that needs to be processed by the JSP engine.
	 * @return
	 */
	boolean isJspResource();
	
	/**
	 * Gets the Module that owns this resource.
	 * @return
	 */
	Module getOwner();

}
