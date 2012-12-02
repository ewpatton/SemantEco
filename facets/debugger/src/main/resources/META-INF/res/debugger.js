$(window).bind("initialize",function() {
	var debug = $("div#display").append("<div id=\"debugger\"></div>").find("div#debugger");
	
	// css
	debug.css("position","absolute");
	debug.css("top","0").css("left","0");
	debug.css("width","100%").css("height","100%");
	debug.css("display","none");
	debug.css("background-color","white");
	
	// controls
	debug.append("Query:");
	debug.append("<br />");
	debug.append("<textarea id=\"query\" rows=\"25\" cols=\"80\"></textarea>");
	debug.append("<br />");
	debug.append("Reason?");
	debug.append("<input type=\"checkbox\" id=\"reason\" />");
	debug.append("<br />");
	debug.append("<input type=\"button\" id=\"debug\" value=\"Query\" />");
	debug.append("<br />");
	debug.append("Results:");
	debug.append("<br />");
	debug.append("<textarea id=\"results\" rows=\"25\" cols=\"80\"></textarea>");
	
	$("#debug", debug).click(function() {
		Debugger.sparql({"query":$("#query").val(), "reason":$("#reason")[0].checked}, function(results){
			$("#results").val(results);
		});
	});
	
	Debugger["open"] = function() {
		$("div#debugger").css("display","block");
	};
	Debugger["close"] = function() {
		$("div#debugger").css("display","none");
	}
});
