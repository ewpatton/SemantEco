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
			data=JSON.parse(data);
			console.log(data);
			console.log(marker);
			// call the auto-generated AJAX method to get the polluted measurements

			//////////////////////////////////////////////////////////////////
		    var contents = "";
		    if(marker.data.label.value != '')
		      contents += "<div class='top'>Site: "+marker.data.label.value+"</div>";
		  	console.log(data.results);
		    var bindings = data.results.bindings;
		    var found = {};
		    var effects = {};
		    var effectURLs = {};
		    if(bindings.length==0) {
		      contents += "<div class='bottom'>This site has no known pollution based on the regulation you selected.</div>";
		      marker.tabledata=contents;
		    }
		    else{
		    	contents += "<div class=\"table-wrapper\"><table border=\"1\"><tr><th>Pollutant</th><th>Measured Value</th><th>Limit Value</th><th>Time</th><th>Species</th><th>Health Effects</th></tr>";
			    var table = $(document.createElement("table"));
			    for(var i=0;i<bindings.length;i++) {
			      	// try {
						var result = bindings[i];
						var element = result["element"].value;
						var label = element.substr(element.indexOf("#")+1).replace(/_/g," ");
						var time = "2012";
						// if(found[label+time]) 
						// 	continue;
						// found[label+time] = true;
						var value = result["value"].value;
						var unit = result["unit"].value;
						if(unit.indexOf("http://")>=0) {
						  unit = unit.substr(unit.lastIndexOf("/")+1);
						}
						var op = result["op"].value;
						var limit = result["limit"].value;
						contents += "<tr class=\""+(i%2==0?"even":"odd")+"\"><td class='characteristics' data-value='"+ element +"'>";
						contents += label+"</td><td>"+value+" "+unit+"<a href=\"javascript:openProvWindow(\'"+element.substring(element.indexOf('#')+1)+"\',\'"+value+"\',\'"+unit+"\',"+ false+","+marker.data.facility.value+",\'"+encodeURIComponent(marker.data.site.value)+"\')\">?</a></td><td>";
						contents += op+" "+limit+" "+unit+"<a href=\"javascript:openProvWindow(\'"+element.substring(element.indexOf('#')+1)+"\',\'"+limit+"\',\'"+unit+"\',"+ true+")\">?</a></td><td>"+time+"</td>";
						//var curSpecies=result["species"].value;
						var curSpecies=result["species"];
						//alert(curSpecies)
						contents += "<td>"+curSpecies+"</td>";
						contents += "<td>";
						var first = true;
						// var healthBds= result["health"].results.bindings;
						/*for(var effect in effects[label]) {
						  if(!first) contents += ",<br/>";
						  if(effectURLs[label][effect])
						  	contents += "<a href=\""+effectURLs[label][effect]+"\">" + effects[label][effect]+"</a>";
						  else
						  	contents += effects[label][effect];
						  first = false;
						}*/
				  //   	for(var i=0;i<healthBds.length;i++) {
				  //   	 if(!first) contents += ",<br/>";
				  //   	 else
				  //   	   first = false;
				  //   	  var healthRes = healthBds[i];
				  //   	  var effect=healthRes["effect"].value;
				  //   	  effect=effect.substr(effect.indexOf("#")+1).replace(/_/g," ");
				  //   	  var effectURL=healthRes["effectURL"].value;
				  //   	  if(effectURL==null || effectURL.length==0)
				  //   	    contents += effect;
				  //   	  else
				  //   	    contents += "<a href=\""+effectURL+"\">" + effect+"</a>";
						// }

			      	// }//end of try
			      	// catch(e) {
			      	// 	console.log("catch"); 
			      	// 	console.log(e);
			      	// }
			    }
			    contents += "</td></tr></table></div>";
			    // contents += "<div class='bottom'><a href='"+(marker.data.facility.value?"trend/epaTrend.html":"trend/usgsTrend.html")+"?state="+state+"&county="+countyCode+"&site="+encodeURIComponent(marker.siteData.uri)+"' target='_new'>Visualize Characteristics</a></div><div class='bottom'></div>";
			    // contents += "<div class='bottom'></div><div class='bottom'></div>";
			    marker.tabledata=contents;
		    }
		    
			////////////////////////////////////////////////////////////


			// data="<div>THE LOADED DATA DOES NOT CONTAIN ENOUGH DATA TO GENERATE A FORM, USE FAKE DATA STILL</div><table border=1><tr><td>MeasurementId</td><td>monitorSite</td><td>Chemical</td><td>hasValue</td><td>hasUnit</td><td>DateCollected</td><td>RegulationViolation</td></tr><tr><td>AirMeasurement_1a</td><td>01-073-0023</td><td class='chemicals'>SO2</td><td>0.00076</td><td>ppm</td><td>1/1/2012</td><td>no</td></tr><tr><td>AirMeasurement_1b</td><td>01-073-0023</td><td class='chemicals'>CO2</td><td>0.00096</td><td>ppm</td><td>2/1/2012</td><td>yes</td></tr><tr><td>AirMeasurement_1c</td><td>01-073-0023</td><td class='chemicals'>HCl</td><td>0.00086</td><td>ppm</td><td>3/1/2012</td><td>no</td></tr><tr><td>AirMeasurement_1c</td><td>01-073-0023</td><td class='chemicals'>Hg</td><td>0.00096</td><td>ppm</td><td>4/1/2012</td><td>no</td></tr><tr><td>AirMeasurement_1c</td><td>01-073-0023</td><td class='chemicals'>Cl2</td><td>0.00086</td><td>ppm</td><td>5/1/2012</td><td>no</td></tr></table>";


			// marker.tabledata=data;
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