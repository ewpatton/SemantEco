package test;

import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.dataprovider.DataModelBuilder;

public class PublicDataModelBuilder extends DataModelBuilder {

	public PublicDataModelBuilder(Request request, ModuleConfiguration config) {
		super(request, config);
	}

}
