/* The regulation module needs to respond to two different events in the client
 *    1) Retrieve site data when requested by the interface
 *    2) Retrieve the set of polluted measurements when an icon is clicked in the interface
 * Here we bind to the initialize event (provided by jQuery) to handle configuring the
 * module's responses to the events described above.
 */
$(window).bind("initialize", function() {
	console.log("regulation.js#initialize");
	
	// set the action for when another module or the UI raises a "get-data" event
	$(window).bind("get-data", function() {
		console.log("regulation.js#get-data");
		
		// call the auto-generated AJAX method to get the set of polluted sites
		RegulationModule.queryForSites({}, function(data) {
			// hide the spinner in case of an error
			// (otherwise the application looks like it's locked up)
			SemantAquaUI.hideSpinner();
			// clear the map
			SemantAquaUI.clearMarkers();
			// parse the JSON response
			var json = JSON.parse(data);
			
			// for each binding in the SPARQL results raise the "create-marker" event
			for(var binding in json.results.bindings) {
				$(window).trigger("create-marker", json.results.bindings[binding]);
			}
		});
	});
	
	// set the action for when the UI raises the "show-marker-info" event
	$(window).bind("show-marker-info", function(event,marker) {
		console.log("regulation.js#show-marker-info");
		var marker=marker;
		
		$("#spinner").show();
		RegulationModule.queryForSitePollution({}, function(data){
			$("#spinner").hide();
			console.log("data retrieved in queryForSitePollution");
			console.log(data);

			// call the auto-generated AJAX method to get the polluted measurements

			data="<div>THE LOADED DATA DOES NOT CONTAIN ENOUGH DATA TO GENERATE A FORM, USE FAKE DATA STILL</div><table border=1><tr><td>MeasurementId</td><td>monitorSite</td><td>Chemical</td><td>hasValue</td><td>hasUnit</td><td>DateCollected</td><td>RegulationViolation</td></tr><tr><td>AirMeasurement_1a</td><td>01-073-0023</td><td class='chemicals'>SO2</td><td>0.00076</td><td>ppm</td><td>1/1/2012</td><td>no</td></tr><tr><td>AirMeasurement_1b</td><td>01-073-0023</td><td class='chemicals'>CO2</td><td>0.00096</td><td>ppm</td><td>2/1/2012</td><td>yes</td></tr><tr><td>AirMeasurement_1c</td><td>01-073-0023</td><td class='chemicals'>HCl</td><td>0.00086</td><td>ppm</td><td>3/1/2012</td><td>no</td></tr><tr><td>AirMeasurement_1c</td><td>01-073-0023</td><td class='chemicals'>Hg</td><td>0.00096</td><td>ppm</td><td>4/1/2012</td><td>no</td></tr><tr><td>AirMeasurement_1c</td><td>01-073-0023</td><td class='chemicals'>Cl2</td><td>0.00086</td><td>ppm</td><td>5/1/2012</td><td>no</td></tr></table>";

			marker.tabledata=data;
			$(window).trigger('pop-infowindow',marker);
			//THE LOADED DATA DOES NOT CONTAIN ENOUGH DATA TO GENERATE A FORM, USE FAKE DATA STILL
			

			// retrieve the marker for the currently selected URI
			// (to use as the source of the info box)

			//no need to receive the marker, the event click trigger will pass it to listener
			// var marker = SemantAquaUI.getMarkerForUri($.bbq.getState("uri"));
			// if(marker == undefined) {
			// 	return;
			// }
			// TODO process SPARQL results here and create the table and info window
			// also consider charts and other forms of presentation
		});

	});
});