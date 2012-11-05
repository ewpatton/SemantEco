package edu.rpi.tw.escience.waterquality;

import java.util.List;

public interface ProvidesDomain {
	List<Domain> getDomains(Request request);
}
