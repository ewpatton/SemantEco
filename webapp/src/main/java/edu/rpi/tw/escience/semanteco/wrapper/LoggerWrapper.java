package edu.rpi.tw.escience.semanteco.wrapper;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * LoggerWrapper extends the Logger class
 * and allows for an alternate channel
 * for transmitting logging information.
 * @author ewpatton
 *
 */
public class LoggerWrapper extends Logger {
	private Logger log;
	
	/**
	 * Constructs a new Logger given a
	 * fully-qualified class name.
	 * @param name
	 */
	public LoggerWrapper(String name) {
		super(name);
	}
	
	protected final void setLogger(Logger log) {
		this.log = log;
	}
	
	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}
	
	@Override
	public void trace(Object message) {
		log(Level.TRACE, message);
	}
	
	@Override
	public void trace(Object message, Throwable t) {
		log(Level.TRACE, message, t);
	}
	
	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}
	
	@Override
	public boolean isEnabledFor(Priority level) {
		return log.isEnabledFor(level);
	}
	
	@Override
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}
	
	@Override
	public void debug(Object message) {
		log(Level.DEBUG, message);
	}
	
	@Override
	public void debug(Object message, Throwable t) {
		log(Level.DEBUG, message, t);
	}
	
	@Override
	public void error(Object message) {
		log(Level.ERROR, message);
	}
	
	@Override
	public void error(Object message, Throwable t) {
		log(Level.ERROR, message, t);
	}
	
	@Override
	public void fatal(Object message) {
		log(Level.FATAL, message);
	}
	
	@Override
	public void fatal(Object message, Throwable t) {
		log(Level.FATAL, message, t);
	}
	
	@Override
	public void info(Object message) {
		log(Level.INFO, message);
	}
	
	@Override
	public void info(Object message, Throwable t) {
		log(Level.INFO, message, t);
	}
	
	@Override
	public void warn(Object message) {
		log(Level.WARN, message);
	}
	
	@Override
	public void warn(Object message, Throwable t) {
		log(Level.WARN, message, t);
	}
	
	@Override
	public void log(Priority priority, Object message) {
		log.log(priority, message);
	}
	
	@Override
	public void log(Priority priority, Object message, Throwable t) {
		log.log(priority, message, t);
	}
	
	@Override
	public void log(String callerFQCN, Priority level, Object message, Throwable t) {
		log.log(callerFQCN, level, message, t);
	}

}
