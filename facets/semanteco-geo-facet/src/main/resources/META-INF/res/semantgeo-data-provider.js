/*
$(window).bind("initialize", function() {
	var icons = [];
	icons[0] = [];
	icons[1] = [];
	icons[0][0] = $("input[value='clean-air']+img").attr("src");
	icons[0][1] = $("input[value='polluted-air']+img").attr("src");
	icons[1][0] = $("input[value='clean-air-facility']+img").attr("src");
	icons[1][1] = $("input[value='polluted-air-facility']+img").attr("src");
	
	DataTypeModule.registerVisibilityFunction(function(b) {
		if(b["isAir"] == undefined) {
			return false;
		}
		var air = b["isAir"]["value"] == "true";
		if(!air) {
			return false;
		}
		var facility = b["facility"]["value"]=="true";
		var polluted = b["polluted"]["value"]=="true";
		var str="";
		if(polluted) {
			str = "polluted";
		}
		else {
			str = "clean";
		}
		str += "-air";
		if(facility) {
			str += "-facility";
		}
		return $("input[value='"+str+"']")[0].checked;
	});
	
	DataTypeModule.registerIconLocator(function(b) {
		if(b["isAir"] == undefined) {
			return null;
		}
		var air = b["isAir"]["value"] == "true";
		if(!air) {
			return null;
		}
		var facility = b["facility"]["value"]=="true" ? 1 : 0;
		var polluted = b["polluted"]["value"]=="true" ? 1 : 0;
		return icons[facility][polluted];
	});
});
*/
