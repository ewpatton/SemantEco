$(window).bind("initialize", function() {
	$(window).bind("get-data", function() {
		RegulationModule.queryForPollutedSites({}, function(data) {
			SemantAquaUI.hideSpinner();
			var json = JSON.parse(data);
			for(var binding in json.results.bindings) {
				$(window).trigger("render-marker", {"binding": binding});
			}
		});
	});
});