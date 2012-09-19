package edu.rpi.tw.escience.waterquality;

import org.apache.log4j.Logger;

public interface Request {
	String[] getParam(String key);
	Logger getLogger();
}
