$(document).ready(function() {
	var icons = {};
	var facet = $("#DataTypeFacet");
	icons["cleanwater"] = $("input[value='cleanwater'] + img", facet).attr("src");
	icons["facility"] = $("input[value='facility'] + img",facet).attr("src");
	icons["pollutedwater"] = $("input[value='pollutedwater'] + img", facet).attr("src");
	icons["pollutedfacility"] = $("input[value='pollutedfacility'] + img", facet).attr("src");

	icons["airnotviolated"]="images/airnotviolated.png";
	icons["airviolated"]="images/airviolated.png";


	window["DataTypeModule"] = {};
	DataTypeModule.shouldBeVisible = function(binding) {
		if(binding["polluted"].value == "true") {
			if(binding["facility"].value == "true") {
				return $.inArray("pollutedfacility", $.bbq.getState("type"))>=0;
			}
			else {
				return $.inArray("pollutedwater", $.bbq.getState("type"))>=0;
			}
		}
		else {
			if(binding["facility"].value == "true") {
				return $.inArray("facility", $.bbq.getState("type"))>=0;
			}
			else {
				return $.inArray("cleanwater", $.bbq.getState("type"))>=0;
			}
		}


		if(binding["type"]=="air"){
			return true;
		}
		return false;
	};
	DataTypeModule.getIcon = function(binding) {
		if(binding["polluted"].value == "true") {
			if(binding["facility"].value == "true") {
				return icons["pollutedfacility"];
			}
			else {
				return icons["pollutedwater"];
			}
		}
		else {
			if(binding["facility"].value == "true") {
				return icons["facility"];
			}
			else {
				return icons["cleanwater"];
			}
		}

		if(binding['type'].value=="air"){
			if((binding['RegulationViolation'])=="yes"){
				return icon['airviolated'];
			}
			else{
				return icon['airnotviolated'];
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