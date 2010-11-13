package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.rpi.tw.eScience.WaterQualityPortal.zip.ZipCodeLookup;

public class ZipCodeDecoder implements HttpHandler {

	@Override
	public void handle(HttpExchange arg0) throws IOException {
		String query = arg0.getRequestURI().getQuery();
		String inputs[] = query.split("&");
		arg0.getResponseHeaders().add("Content-type", "text/plain");
		arg0.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		boolean found=false;
		for(int i=0;i<inputs.length;i++) {
			String split[] = inputs[i].split("=");
			if(split[0].equals("code")) {
				String result=null;
				try {
					ZipCodeLookup zcl = ZipCodeLookup.execute(split[1]);
					result = zcl.toString();
				}
				catch(Exception e) {
					result = "{\"error\":\"Unknown zip code\"}";
				}
				arg0.sendResponseHeaders(200, result.length());
				found=true;
				OutputStream m = arg0.getResponseBody();
				m.write(result.getBytes("UTF-8"));
			}
		}
		if(!found) arg0.sendResponseHeaders(404, 0);
		arg0.getResponseBody().flush();
		arg0.getResponseBody().close();
	}

}
