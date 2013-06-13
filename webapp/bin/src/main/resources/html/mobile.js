function findUser(pos) {
  $.getJSON("http://api.geonames.org/findNearbyPostalCodesJSON",
	    {"lat":pos.coords.latitude,
	     "lng":pos.coords.longitude,
	     "maxRows":"1",
	     "username":"twcswqp"},
	    function(data,status,xhr) {
	      var x = null;
	      if(typeof(data.postalCodes.length)!==undefined &&
		 data.postalCodes.length > 0)
		x = data.postalCodes[0];
	      if(!x) return;

	      window.console.log(x);
	    });
}

if(Modernizer.geolocation) {
  navigation.geolcation.getCurrentPosition(findUser);
}
