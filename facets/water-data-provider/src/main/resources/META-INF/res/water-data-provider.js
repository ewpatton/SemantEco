$(window).bind("initialize", function() {
	var icons = [];
	icons[0] = [];
	icons[0][0] = $("input[value='clean-water']+img").attr("src");
	icons[0][1] = $("input[value='polluted-water']+img").attr("src");
	icons[1] = [];
	icons[1][0] = $("input[value='clean-facility']+img").attr("src");
	icons[1][1] = $("input[value='polluted-facility']+img").attr("src");
	
	DataTypeModule.registerVisibilityFunction(function(b) {
		if(b["isWater"] == undefined) {
			return false;
		}
		var water = b["isWater"]["value"] == "true";
		if(!water) {
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
		str += "-";
		if(facility) {
			str += "facility";
		}
		else {
			str += "water";
		}
		return $("input[value='"+str+"']")[0].checked;
	});
	
	DataTypeModule.registerIconLocator(function(b) {
		if(b["isWater"] == undefined) {
			return null;
		}
		var water = b["isWater"]["value"] == "true";
		if(!water) {
			return null;
		}
		var facility = b["facility"]["value"]=="true" ? 1 : 0;
		var polluted = b["polluted"]["value"]=="true" ? 1 : 0;
		return icons[facility][polluted];
	});
});