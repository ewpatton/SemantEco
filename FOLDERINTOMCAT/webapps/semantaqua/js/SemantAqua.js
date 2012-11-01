
//beacause this object is not warpped within a $(ducument).ready(function(){}) or $(function(){}) (this two are samething),
//this object will be at window level automatically when it is being created, so it can be refered anywhere
var SemantAqua = {
	"limit": 5,
	"action": null,

	"initialize": function() {
		SemantAqua.configureConsole();
		SemantAquaUI.configureMap();

		//this line of code is facilited by jquery
		//it will send out an "initialize" event, whoever have bind(on) (means listen) will be triggered.
		$(window).trigger("initialize");
		
		// set up facets
		$.bbq.pushState(SemantAquaUI.getFacetParams(),1);
		SemantAquaUI.populateFacets();
		 
		// bind handle state change
		//this line means the handleStateChange function in semantauqa object will be runned whenever it lisened a haschange event
		$(window).bind('hashchange', SemantAqua.handleStateChange);
		SemantAqua.configureWebSockets();
		if($.bbq.getState("zip") != null) {
			SemantAqua.decodeZipCode();
		}
	},
	//this part is to create the console object if the browser don't have one(because the broaswer is old)
	"configureConsole": function() {
		if(typeof window.console == "undefined") {
			window.console = {};
		}
		if(typeof window.console.__proto__.debug == "undefined") {
			window.console.__proto__.debug = function() { }
		}
		if(typeof window.console.__proto__.info == "undefined") {
			window.console.__proto__.info = function() { }
		}
		if(typeof window.console.__proto__.warn == "undefined") {
			window.console.__proto__.warn = function() { }
		}
		if(typeof window.console.__proto__.error == "undefined") {
			window.console.__proto__.error = function() { }
		}
	},
	//this part utilize websocket technology in html5 standard to read write log to the server
	//however, it is not a core function.
	"configureWebSockets": function() {
		SemantAqua.socket = null;
		var host = null;
		if(window.location.protocol == "http:") {
			host = "ws://"+window.location.host+"/semantaqua/log"
		}
		else {
			host = "wss://"+window.location.host+"/semantaqua/log";
		}
		if('WebSocket' in window) {
			SemantAqua.socket = new WebSocket(host);
		}
		else if('MozWebSocket' in window) {
			SemantAqua.socket = new MozWebSocket(host);
		}
		else {
			$(window).trigger('hashchange');
			return;
		}
		SemantAqua.socket.onopen = function() {
			console.log("Connection to server opened");
			SemantAqua.socket.send("getId");
		};
		SemantAqua.socket.onclose = function() {
			console.log("Connection to server lost");
		};
		SemantAqua.socket.onmessage = function(msg) {
			var obj = JSON.parse(msg.data);
			if(obj.socketId) {
				SemantAqua.socketId = obj.socketId;
				$.cookie("socketId", obj.socketId);
				$(window).trigger('hashchange');
			}
			if(obj.level == "DEBUG") {
				console.debug("[DEBUG] "+obj.message);
				if(obj.error) {
					console.debug("[DEBUG] "+obj.error);
				}
			}
			else if(obj.level == "INFO") {
				console.info("[INFO ] "+obj.message);
				if(obj.error) {
					console.info("[INFO ] "+obj.error);
				}
			}
			else if(obj.level == "WARN") {
				console.warn("[WARN ] "+obj.message);
				if(obj.error) {
					console.warn("[WARN ] "+obj.error);
				}
			}
			else if(obj.level == "ERROR") {
				console.error("[ERROR] "+obj.message);
				if(obj.error) {
					console.error("[ERROR] "+obj.error);
				}
			}
			else if(obj.level == "FATAL") {
				console.error("[FATAL] "+obj.message);
				if(obj.error) {
					console.error("[FATAL] "+obj.error);
				}
			}
		};
	},
	//this function get zip code from the input then push it to url
	"showAddress": function(zip) {
		zip = zip || $("#zip").val();
		if(zip == null || zip == "") {
			return;
		}
		if(zip.length != 5) {
			alert("The input zip code is not valid! Please check and input again.")
			return;
		}
		//the semantAqua.action is used all over the place
		//it appears as a string, but it can be called as a function
		SemantAqua.action = "decodeZipCode";
		$.bbq.pushState({"zip": zip});
		return false;
	},
	"handleStateChange": function() {
		var action = SemantAqua.action;
		if(action == null) {
			return;
		}
		if(typeof action == "function") {
			action.call(window);
		}
		if(typeof SemantAqua[action] == "function") {
			SemantAqua[action].call(SemantAqua);
		}
	},
	"decodeZipCode": function() {
		var zip = $.bbq.getState("zip");
		if(zip == null) {
			SemantAqua.hideSpinner();
			return;
		}
		SemantAqua.pagedData = [];
		SemantAqua.curPage = 0;
		SemantAquaUI.doGeocode(zip);
		SemantAquaUI.showSpinner();
		ZipCodeModule.decodeZipCode({}, SemantAqua.processZipCode);
	},

	//variables in front end are stored in the url and are currently controlled by bbq, a jquery exntension that has the ability to change url after # (pound).
	//using bbq will facilitate back button in modern browsers. because the front end page never really freashed, without bbq, any touch of back button will result in leaving the page instead of go back to previous state of the page
	"processZipCode": function(response) {
		var data = JSON.parse(response);
		console.log(response);
		SemantAqua.action = "getLimitData";
		$.bbq.pushState({"state":data.result.stateAbbr,
			"county":data.result.countyCode,
			"lat":data.result.lat, "lng":data.result.lng});
	},
	"getLimitData": function() {
		DataSourceModule.getSiteCounts({}, SemantAqua.processLimitData);
	},
	"processLimitData": function(response) {
		var data = JSON.parse(response);
		console.log(response);
		var limits = {"site": {}, "facility": {}};
		limits.site["offset"] = 0;
		limits.facility["offset"] = 0;
		limits.site["count"] = parseInt(data.site);
		limits.facility["count"] = parseInt(data.facility);
		if(data.facility > data.site) {
			limits.site["limit"] = Math.min(SemantAqua.limit, limits.site.count);
			limits.facility["limit"] = Math.min(limits.facility.count,
					2*SemantAqua.limit - limits.site.limit);
		}
		else {
			limits.facility["limit"] = Math.min(SemantAqua.limit, limits.facility.count);
			limits.site["limit"] = Math.min(limits.site.count,
					2*SemantAqua.limit - limits.facility.limit);
		}
		SemantAqua.action = null;
		$.bbq.pushState({"limits": limits});
		SemantAqua.generatePaging();
		SemantAqua.getData();
	},

	//this section is controlling the pagination below map.
	"generatePaging": function() {
		var limits = $.bbq.getState("limits");
		var div = $("#page");
		div.empty();
		div.append("Page: ");
		var offset = parseInt(limits.facility.offset) + parseInt(limits.site.offset);
		var limit = parseInt(limits.facility.count) + parseInt(limits.site.count);
		var page = Math.floor(offset / (2 * SemantAqua.limit)) + 1;
		var pages = Math.floor(limit / (2 * SemantAqua.limit)) + 1;
		for ( var i = 1; i < page; i++) {
			var el = document.createElement("a");
			div.append(el);
			$(el).click(generatePagingCallback(i));
			$(el).attr("href", "#");
			$(el).text(i.toString());
			div.append(" ");
		}
		var el = document.createElement("a");
		div.append(el);
		$(el).click(SemantAqua.generatePagingCallback(page));
		$(el).attr("href", "#");
		$(el).toggleClass("selected");
		$(el).text(page.toString());
		div.append(" ");
		for ( var i = page + 1; i <= pages; i++) {
			var el = document.createElement("a");
			div.append(el);
			$(el).click(SemantAqua.generatePagingCallback(i));
			$(el).attr("href", "#");
			$(el).text(i.toString());
			div.append(" ");
		}
	},
	"generatePagingCallback": function(i) {
		return function() {
			changePage(i);
			return false;
		};
	},
	//after pushing information to url,whoever listening to "get-data" event will try to get data
	"getData": function() {
		console.log("SemantAqua.getData");
		$(window).trigger("get-data");
		//SemantAquaUI.hideSpinner();
	},
	"showReportSites": function() {
		SemantAquaUI.hideSpinner();
	},
	"triggerUpdate": function() {
		SemantAqua.showAddress($("#zip").val());
	},
	"prepareArgs": function(args) {
		var result = {};
		for(var i in args) {
			var div = $("div.facet:has(input[name='"+i+"'], select[name='"+i+"'])");
			if(div.hasClass("no-rest")) {
				continue;
			}
			if(typeof args[i] == "object") {
				result[i] = JSON.stringify(args[i]);
			}
			else {
				result[i] = args[i];
			}
		}
		return result;
	}
};


//this object is used by the UIteam, to store fake data and bypass certain function and push data directly to map
var UITeamUtilities={
	init:function(){
		d3.csv("a.txt", function(rows) {
		    
		});

		UITeamUtilities.markerdata=[];
		UITeamUtilities.markerdata[0]={
			"type":{
				value:"air"
			},
			"site":{
				value:"gooogle.com"
			},
			"label":{
				value:"01-073-0023"
			},
			"lat":{
				value:"33.553056"
			},
			"lng":{
				value:"-86.815"
			},
			"RegulationViolation":"no",
			"infowindowcontent":"<table border=1><tr><td>MeasurementId</td><td>monitorSite</td><td>hasCounty</td><td>hasValue</td><td>hasUnit</td><td>DateCollected</td><td>RegulationViolation</td></tr><tr><td>AirMeasurement_1a</td><td>01-073-0023</td><td>-</td><td>0.00076</td><td>ppm</td><td>1/1/2012</td><td>no</td></tr><tr><td>AirMeasurement_1b</td><td>01-073-0023</td><td>-</td><td>0.00096</td><td>ppm</td><td>2/1/2012</td><td>yes</td></tr><tr><td>AirMeasurement_1c</td><td>01-073-0023</td><td>-</td><td>0.00086</td><td>ppm</td><td>3/1/2012</td><td>no</td></tr><tr><td>AirMeasurement_1c</td><td>01-073-0023</td><td>-</td><td>0.00096</td><td>ppm</td><td>4/1/2012</td><td>no</td></tr><tr><td>AirMeasurement_1c</td><td>01-073-0023</td><td>-</td><td>0.00086</td><td>ppm</td><td>5/1/2012</td><td>no</td></tr></table>",
			"visualize1":[['1/1/2012', 0.00076], ['2/1/2012', 0.00096], ['3/1/2012', 0.00086], ['4/1/2012', 0.00096],['5/1/2012', 0.00086]],
			"visualize2":[['3/1/2012', 15],['3/19/2012',12],['3/22/2012',8]]
		};

		UITeamUtilities.markerdata[1]={
			"type":{
				value:"air"
			},
			"site":{
				value:"gooogle.com"
			},
			"label":{
				value:"01-073-0025"
			},
			"lat":{
				value:"33.545556"
			},
			"lng":{
				value:"-86.782778"
			},
			"RegulationViolation":"yes",
			"infowindowcontent":"no content"
		};

		$("#fakeload").click(function(){
			SemantAqua.pagedData = [];
			SemantAqua.curPage = 0;
			SemantAquaUI.doGeocode("35207");

			for(var i=0;i<UITeamUtilities.markerdata.length;i++){
				DataTypeModule.createMarker(null,UITeamUtilities.markerdata[i])
			}
			
		});
	},
	markerdata:null,
	infowindowdata:null
}

$(function(){
	UITeamUtilities.init();
});