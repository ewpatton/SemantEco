$(document).ready(function() {
	if(window["DataTypeModule"] === undefined) {
		window["DataTypeModule"] = {};
	}
	//
	DataTypeModule.visibilityFunctions = [];
	DataTypeModule.iconLocators = [];
	DataTypeModule.registerVisibilityFunction = function(func) {
		DataTypeModule.visibilityFunctions.push(func);
	};
	DataTypeModule.registerIconLocator = function(func) {
		DataTypeModule.iconLocators.push(func);
	};
	
	DataTypeModule.shouldBeVisible = function(binding) {
		for(var i=0;i<DataTypeModule.visibilityFunctions.length;i++) {
			var func = DataTypeModule.visibilityFunctions[i];
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
	$(window).bind("create-marker", DataTypeModule.createMarker);
	$(window).bind("render-marker", function(e, marker) {
		SemantAquaUI.addMarker(marker);
	});
});