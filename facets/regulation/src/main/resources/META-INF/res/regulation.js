/**
 * @module edu.rpi.tw.escience.semanteco.regulation
 */
/**
 * @file
 * regulation.js provides client-side scripts for interfacing
 * with the regulation module.
 */
/**
 * @class SemantEcoUI
 */
/**
 * The regulation module needs to respond to two different events in the client
 *    1) Retrieve site data when requested by the interface
 *    2) Retrieve the set of polluted measurements when an icon is clicked in the interface
 * Here we bind to the initialize event (provided by jQuery) to handle configuring the
 * module's responses to the events described above.
 * @class RegulationModule
 */
/**
 * Allows modules to respond to when the user requests data
 * @event SemantEcoUI#get-data
 * @type {object}
 */
/**
 * Allows modules to respond to when a user clicks on a site marker
 * @event SemantEcoUI#show-marker-info
 * @type {object}
 */
/**
 * Allows modules to create a marker
 * @event SemantEcoUI#create-marker
 * @type {object}
 * @param {object} binding A SPARQL JSON results binding
 */
/**
 * Gets site data based on the current application state.
 * @function RegulationModule#getData
 * @fires SemantEcoUI#create-marker
 * @handles SemantEcoUI#get-data
 * @public
 */
RegulationModule.getData = function() {
	console.log("regulation.js#get-data");
	
	// call the auto-generated AJAX method to get the set of polluted sites
	RegulationModule.queryForSites({"uri":null}, function(data) {
		// hide the spinner in case of an error
		// (otherwise the application looks like it's locked up)
		SemantEcoUI.hideSpinner();
		// clear the map
		SemantEcoUI.clearMarkers();
		// parse the JSON response
		var json = JSON.parse(data);
		
		// for each binding in the SPARQL results raise the "create-marker" event
		for(var binding in json.results.bindings) {
			var b = json.results.bindings[binding];
			// follow if statement temporarily addresses issue #21.
			if(b.lng != undefined && parseFloat(b.lng.value) > 0.0) {
				b.lng.value = (-parseFloat(b.lng.value)).toString();
			}
			$(window).trigger("create-marker", json.results.bindings[binding]);
		}
		SemantEcoUI.focusMap();
	});
};

/**
 * Shows a marker
 * @function RegulationModule#showMarkerInfo
 * @fires SemantEcoUI#pop-infowindow
 * @handles SemantEcoUI#show-marker-info
 * @function 
 */
RegulationModule.showMarkerInfo = function(event, marker) {
	console.log("regulation.js#show-marker-info");
	var marker=SemantEcoUI.getMarkerForUri($.bbq.getState("uri"));
	
	$("#spinner").show();
	// call the auto-generated AJAX method to get the polluted measurements

	//////////////////////////////////////////////////////////////////

	if((marker.data.isWater != undefined && marker.data.isWater.value == "true")||
		(marker.data.isAir != undefined && marker.data.isAir.value == "true")){
		RegulationModule.queryForSitePollution({}, function(data){
			$("#spinner").hide();
			data=JSON.parse(data);
			var contents = "";
		    if(marker.data.label && marker.data.label.value != '')
		      contents += "<div class='top'>Site: "+marker.data.label.value+"</div>";
		    var bindings = data.results.bindings;
		    if(bindings.length==0) {
		      contents += "<div class='bottom'>This site has no known pollution based on the regulation you selected.</div>";
		      marker.tabledata=contents;
		    }
		    else{
		        contents += "<div class=\"table-wrapper\"><table border=\"1\"><tr><th>Characteristic</th><th>Measured Value</th><th>Limit Value</th><th>Time</th></tr>";
			    for(var i=0;i<bindings.length;i++) {
						var result = bindings[i];
						var element = result["element"].value;
						var label = element.substr(element.indexOf("#")+1).replace(/_/g," ");
						var time = bindings[i].time.value;
						var value = result["value"].value;
						var unit = result["unit"].value;
						if(unit.indexOf("http://")>=0) {
						  unit = unit.substr(unit.lastIndexOf("/")+1);
						}
						var op = result["op"].value;
						var limit = result["limit"].value;
						contents += "<tr class=\""+(i%2==0?"even":"odd")+"\"><td class='characteristics' data-value='"+ element +"'>";
						contents += label+"</td><td>"+value+" "+unit+"</td><td>";
						contents += op+" "+limit+" "+unit+"</td><td>"+time+"</td>";
						//var curSpecies=result["species"].value;
						var curSpecies=result["species"];
						//alert(curSpecies)
						var first = true;
			    }
			    contents += "</tr></table></div>";
			    marker.tabledata=contents;
		    }
			$(window).trigger('pop-infowindow',marker);
		});
	}
	else{
		SpeciesDataProviderModule.queryForSpeciesForASite({},function(data){
			$("#spinner").hide();
			console.log("data retrieved in queryForSpeciesForASite. Data(below):");
			console.log(data);
			data=JSON.parse(data);
			console.log("marker:(below):");
			console.log(marker);
			var contents = "";
		    if(marker.data.label.value != '')
		      contents += "<div class='top'>Site: "+marker.data.label.value+"</div>";
		  	// console.log(data.results);
		    

	    	contents += "<div class=\"table-wrapper\"><table border=\"1\"><tr><th>Scientific Name</th><th>Date</th><th>Count</th></tr>";
		    var bindings = data.results.bindings;
		    for(var i=0;i<bindings.length;i++) {
					var result = bindings[i];
					var scientific_name = result["scientific_name"].value;
					var date = result.date.value;
					var count = result["count"].value;
					contents += "<tr class=\""+(i%2==0?"even":"odd")+"\"><td>";
					contents += scientific_name+"</td><td>"+date+"</td><td>"+count+"</td></tr>";
		    }
		    contents += "</table></div>";
		    marker.tabledata=contents;
		    
			$(window).trigger('pop-infowindow',marker);
		});
	}

};

$(window).bind("initialize", function() {
	console.log("regulation.js#initialize");

    $("div.search").bind("click", function() {
        $(window).trigger("get-data");
    });

	// set the action for when another module or the UI raises a "get-data" event
	$(window).bind("get-data", RegulationModule.getData);
	
	// set the action for when the UI raises the "show-marker-info" event
	$(window).bind("show-marker-info", RegulationModule.showMarkerInfo);
});
