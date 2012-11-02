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
		RegulationModule.queryForPollutedSites({}, function(data) {
			// hide the spinner in case of an error
			// (otherwise the application looks like it's locked up)
			SemantAquaUI.hideSpinner();
			// parse the JSON response
			var json = JSON.parse(data);
			
			// for each binding in the SPARQL results raise the "create-marker" event
			for(var binding in json.results.bindings) {
				$(window).trigger("create-marker", json.results.bindings[binding]);
			}
		});
	});
	
	// set the action for when the UI raises the "show-marker-info" event
	$(window).bind("show-marker-info", function() {
		console.log("regulation.js#show-marker-info");
		
		// call the auto-generated AJAX method to get the polluted measurements
		RegulationModule.queryForSitePollution({}, function(data){
			
			// retrieve the marker for the currently selected URI
			// (to use as the source of the info box)
			var marker = SemantAquaUI.getMarkerForUri($.bbq.getState("uri"));
			if(marker == undefined) {
				return;
			}
			// TODO process SPARQL results here and create the table and info window
			// also consider charts and other forms of presentation
		});
	});
});