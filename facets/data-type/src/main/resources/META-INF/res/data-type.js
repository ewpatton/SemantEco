$(document).ready(function() {
	if(window["DataTypeModule"] === undefined) {
		window["DataTypeModule"] = {};
	}
	//
	DataTypeModule.visibilityFunctions = [];
	DataTypeModule.iconLocators = [];
	DataTypeModule.registerVisibilityFunction = function(func) {
		//this registerVisiblityFunction is pushed onto array visibityFunctions
		DataTypeModule.visibilityFunctions.push(func);
	};
	DataTypeModule.registerIconLocator = function(func) {
		DataTypeModule.iconLocators.push(func);
	};
	
	//when we're checking whether a marker should be visible,
	//we have a binding for a specific site
	//do any of the visibility functions believe whether this site
	//should be visible?
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
		var marker = SemantAquaUI.createMarker(uri, lat, lng, icon, visible, label);
		marker.data = binding;
		$(window).trigger("render-marker", marker);
	};
	
	DataTypeModule.refreshMapIcons = function() {
		var markers = SemantAquaUI.getMarkers();
		for(var i=0;i<markers.length;i++) {
			var m = markers[i];
			if(DataTypeModule.shouldBeVisible(m.data)) {
				SemantAquaUI.showMarker(m);
			}
			else {
				SemantAquaUI.hideMarker(m);
			}
		}
	};
	
	$(window).bind("create-marker", DataTypeModule.createMarker);
	$(window).bind("render-marker", function(e, marker) {
		SemantAquaUI.addMarker(marker);
	});
	$("#DataTypeFacet input[name='type']").change(DataTypeModule.refreshMapIcons);
});