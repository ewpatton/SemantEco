var SemantAqua = {
	"limit": 5,
	"initialize": function() {
		SemantAqua.configureConsole();
		var mapOptions = {
			center: new google.maps.LatLng(37.4419, -122.1419),
			zoom: 8,
			mapTypeId: google.maps.MapTypeId.ROADMAP,
			zoomControl: true,
			panControl: true
		};
		SemantAqua.map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
		SemantAqua.geocode = new google.maps.Geocoder();
		$(window).bind('hashchange', SemantAqua.handleStateChange);
		SemantAqua.configureConsole();
		SemantAqua.configureWebSockets();
	},
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
	"showAddress": function(zip) {
		zip = zip || $("#zip").val();
		if(zip == null || zip == "") {
			return;
		}
		if(zip.length != 5) {
			alert("The input zip code is not valid! Please check and input again.")
			return;
		}
		$.bbq.pushState({"zip": zip});
		SemantAqua.handleStateChange();
		return false;
	},
	"handleStateChange": function() {
		var zip = $.bbq.getState("zip");
		if(zip == null) {
			SemantAqua.hideSpinner();
			return;
		}
		SemantAqua.pagedData = [];
		SemantAqua.curPage = 0;
		SemantAqua.doGeocode(zip);
		SemantAqua.showSpinner();
		ZipCodeModule.decodeZipCode({}, SemantAqua.processZipCode);
	},
	"processZipCode": function(response) {
		var data = JSON.parse(response);
		console.log(response);
		var stateCode = data.result.stateCode;
		if(stateCode.indexOf(":")!=-1) {
			stateCode = stateCode.split(":")[1];
		}
		$.bbq.pushState({"state":data.result.stateAbbr,
			"stateCode":stateCode, "countyCode":countyCode,
			"lat":data.result.lat, "lng":data.result.lng})
	},
	"doGeocode": function(zip) {
		if(SemantAqua.geocoder) {
			SemantAqua.geocoder.getLatLng(zip, function(pt) {
				if(!point) {
					alert(zip + " not found");
				}
				else {
					map.setCenter(point);
					map.setZoom(10);
				}
			});
		}
	},
	"showReportSites": function() {
		
	},
	"showSpinner": function() {
		$("#spinner").css("display", "block");
	},
	"hideSpinner": function() {
		$("#spinner").css("display", "none");
	},
	"triggerUpdate": function() {
		SemantAqua.showAddress($("#zip").val());
	}
};