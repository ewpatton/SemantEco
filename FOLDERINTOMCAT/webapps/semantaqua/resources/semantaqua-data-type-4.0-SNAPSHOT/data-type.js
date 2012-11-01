//this function is facilitated by jquery, the $(document).ready(function() will make sure anything inside will run after all element on dom is ready.

$(document).ready(function() {

	//the icons is a object store icon resourse for diffiernt markers
	var icons = {};
	var facet = $("#DataTypeFacet");
	icons["cleanwater"] = $("input[value='cleanwater'] + img", facet).attr("src");
	icons["facility"] = $("input[value='facility'] + img",facet).attr("src");
	icons["pollutedwater"] = $("input[value='pollutedwater'] + img", facet).attr("src");
	icons["pollutedfacility"] = $("input[value='pollutedfacility'] + img", facet).attr("src");

	icons["airnotviolated"]="images/airnotviolated.png";
	icons["airviolated"]="images/airviolated.png";

	//craete a ojbect called DataTypeModule at window level,
	//the reason to create it at windows level will make sure the object can be accessed everywhere
	window["DataTypeModule"] = {};

	//the ojbect contains many functions
	//this one is to control visivlity of markers
	DataTypeModule.shouldBeVisible = function(binding) {
		if(binding['type']!=undefined && binding["type"].value=="air"){
			return true;
		}


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


		
		return false;
	};

	//this one will get proper icon for marker based on data it get from the server (or fake load)
	DataTypeModule.getIcon = function(binding) {
		console.log(binding);
		if(binding['type']!=undefined && binding['type'].value=="air"){
			if((binding['RegulationViolation'])=="yes"){
				return icons['airviolated'];
			}
			else{
				return icons['airnotviolated'];
			}
		}

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

		return null;
	};

	//this function will gether all neccessary information to feed the coresponding createmarker function in semantaquaUI object
	//this one gether information, the function in semantaquaUI will talk to google maps api directly
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