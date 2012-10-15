$(window).bind("initialize", function() {
	console.log("regulation.js#initialize");
	$(window).bind("get-data", function() {
		console.log("regulation.js#get-data");
		RegulationModule.queryForPollutedSites({}, function(data) {
			SemantAquaUI.hideSpinner();
			var json = JSON.parse(data);
			for(var binding in json.results.bindings) {
				$(window).trigger("create-marker", json.results.bindings[binding]);
			}
		});
	});
	$(window).bind("show-marker-info", function() {
		console.log("regulation.js#show-marker-info");
		RegulationModule.queryForSitePollution({}, function(data){
			var marker = SemantAquaUI.getMarkerForUri($.bbq.getState("uri"));
		});
	});
});