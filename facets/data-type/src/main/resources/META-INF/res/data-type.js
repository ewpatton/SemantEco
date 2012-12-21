$(document).ready(function() {
	// depending on what order code is fired, the DataTypeModule may
	// not be defined yet so we define it here...
	if(window["DataTypeModule"] === undefined) {
		window["DataTypeModule"] = {};
	}
	
	// holds references to the registered visibility functions
	DataTypeModule.visibilityFunctions = [];
	// holds references to the registered icon locator functions
	DataTypeModule.iconLocators = [];
	
	/**
	 * Allows a domain provider to register a function by which it
	 * can determine whether the marker for a particular SPARQL binding
	 * should appear on the map or not.
	 * @param func A function that takes a JSON object representing a SPARQL
	 * binding and returns a boolean if a marker for that binding should be visible.
	 */
	DataTypeModule.registerVisibilityFunction = function(func) {
		//this registerVisiblityFunction is pushed onto array visibityFunctions
		DataTypeModule.visibilityFunctions.push(func);
	};
	
	/**
	 * Allows a domain provider to register a function by which it
	 * can provide a marker icon for a particular SPARQL binding.
	 * @param func A function that takes a JSON object representing a SPARQL
	 * binding and returns a URL to an icon or null if the function cannot
	 * provide an icon for the binding.
	 */
	DataTypeModule.registerIconLocator = function(func) {
		DataTypeModule.iconLocators.push(func);
	};
	
	/**
	 * Determines whether the marker for a binding should be visible or not
	 * by passing a binding to the registered visibility functions. If any
	 * of the visibility functions returns true then the marker generated
	 * for the binding will be rendered.
	 * @param binding A SPARQL JSON results binding
	 * @returns true if a visibility function returns true for binding, otherwise false
	 */
	DataTypeModule.shouldBeVisible = function(binding) {
		for(var i=0;i<DataTypeModule.visibilityFunctions.length;i++) {
			var func = DataTypeModule.visibilityFunctions[i];
			//call is used to call the actual function that was registered
			//you're calling the function of the function object
			//the first arg is bound to the this keyword
			//and the remaining are mapped to the arguments
			//
			if(func.call(window, binding) == true) {
				return true;
			}
		}
		return false;
	};
	
	/**
	 * Gets a URL to an icon for a particular SPARQL results variable binding.
	 * @param binding SPARQL JSON results binding
	 * @returns A URL if a registered icon locator returned a URL, otherwise null
	 */
	DataTypeModule.getIcon = function(binding) {
		for(var i=0;i<DataTypeModule.iconLocators.length;i++) {
			var func = DataTypeModule.iconLocators[i];
			var icon = func.call(window, binding);
			if(icon != null) {
				return icon;
			}
		}
		return null;
	};
	
	/**
	 * Creates a marker in response to the 'create-marker' event
	 * and renders it via the 'render-marker' event.
	 * @param e A jQuery event object
	 * @param binding The SPARQL binding as per the SPARQL JSON results format
	 */
	DataTypeModule.createMarker = function(e, binding) {
		var uri = binding["site"].value;
		var lat = parseFloat(binding["lat"].value);
		var lng = parseFloat(binding["lng"].value);
		var icon = DataTypeModule.getIcon(binding);
		var visible = DataTypeModule.shouldBeVisible(binding);
		var label = null;
		if(binding["label"] != undefined) {
			label = binding["label"].value;
		}
		var marker = SemantEcoUI.createMarker(uri, lat, lng, icon, visible, label);
		marker.data = binding;
		$(window).trigger("render-marker", marker);
	};
	
	/**
	 * Refreshes the visible map icons due to changes in the
	 * application state.
	 */
	DataTypeModule.refreshMapIcons = function() {
		var markers = SemantEcoUI.getMarkers();
		for(var i=0;i<markers.length;i++) {
			var m = markers[i];
			if(DataTypeModule.shouldBeVisible(m.data)) {
				SemantEcoUI.showMarker(m);
			}
			else {
				SemantEcoUI.hideMarker(m);
			}
		}
	};
	
	// bind DataTypeModule to the create-marker and render-marker events
	$(window).bind("create-marker", DataTypeModule.createMarker);
	$(window).bind("render-marker", function(e, marker) {
		SemantEcoUI.addMarker(marker);
	});
	// handle user changing type selection after map is rendered
	$("#DataTypeFacet input[name='type']").change(DataTypeModule.refreshMapIcons);
});