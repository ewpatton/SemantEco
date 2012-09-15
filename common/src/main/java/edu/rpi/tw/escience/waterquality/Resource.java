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

	String getPath();
	InputStream open() throws IOException;
	boolean isJspResource();
	Module getOwner();

}
