    var map = null;
    var geocoder = null;

    function initialize() {
      if (GBrowserIsCompatible()) {
        map = new GMap2(document.getElementById("map_canvas"));
        map.setCenter(new GLatLng(37.4419, -122.1419), 13);
        geocoder = new GClientGeocoder();
      }
    }

    function showAddress(address) {
      if (geocoder) {
        geocoder.getLatLng(
          address,
          function(point) {
            if (!point) {
              alert(address + " not found");
            } else {
              map.setCenter(point, 13);
              var marker = new GMarker(point);
              map.addOverlay(marker);
              marker.openInfoWindowHtml(address);
            }
          }
        );
	showPollutedWater();
	//showCleanWater();
 	//showViolatedFacility();
	//showFacility();
      }
    }
   
   function showPollutedWater()
   {
   	 $.ajax({
        type: "GET",
        url: "http://tw2.tw.rpi.edu/zhengj3/demo/wresult.xml", // SPARQL service URL
	 data: "",
       // data: "query=" + encodeURIComponent("prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"+
	//	"select * where{?s rdf:type <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#PollutedWaterSource>. ?s this:hasLocation ?loc. ?loc geo:latitude ?lat. ?loc geo:longitude ?log.}"), // query parameter
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/sparql-results+xml");
        },
        dataType: "xml",
        success: function(data) {
		$(data).find('result').each(function(){
			var lat="",lng="";
			$(this).find("binding").each(function(){
				
				if($(this).attr("name")=="lat")
				{
					lat=($(this).find("literal").text());
				}
				if($(this).attr("name")=="log")
				{
					lng=($(this).find("literal").text());
				}
				

			});			
 			if(lat!=""&&lng!=""){
        		var blueIcon = new GIcon(G_DEFAULT_ICON);
        		blueIcon.image = "http://gmaps-samples.googlecode.com/svn/trunk/markers/blue/blank.png";

			var latlng = new GLatLng(lat ,lng);
			markerOptions = { icon:blueIcon };
         		map.addOverlay(new GMarker(latlng, markerOptions));
			alert("added");
			}			
		});
        }
    });
	}
   function showCleanWater()
   {
   	 $.ajax({
        type: "GET",
        url: "http://tw2.tw.rpi.edu/zhengj3/demo/wresult.xml", // SPARQL service URL
	 data: "",
       // data: "query=" + encodeURIComponent("prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"+
	//	"select * where{?s rdf:type <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#PollutedWaterSource>. ?s this:hasLocation ?loc. ?loc geo:latitude ?lat. ?loc geo:longitude ?log.}"), // query parameter
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/sparql-results+xml");
        },
        dataType: "xml",
        success: function(data) {
		$(data).find('result').each(function(){
			var lat="",lng="";
			$(this).find("binding").each(function(){
				
				if($(this).attr("name")=="lat")
				{
					lat=($(this).find("literal").text());
				}
				if($(this).attr("name")=="log")
				{
					lng=($(this).find("literal").text());
				}
				

			});			
 			if(lat!=""&&lng!=""){
        		var blueIcon = new GIcon(G_DEFAULT_ICON);
        		blueIcon.image = "http://gmaps-samples.googlecode.com/svn/trunk/markers/blue/blank.png";

			var latlng = new GLatLng(lat ,lng);
			markerOptions = { icon:blueIcon };
         		map.addOverlay(new GMarker(latlng, markerOptions));
			alert("added");
			}			
		});
        }
    });
}
   function showViolatedFacility()
   {
   	 $.ajax({
        type: "GET",
        url: "http://tw2.tw.rpi.edu/zhengj3/demo/wresult.xml", // SPARQL service URL
	 data: "",
       // data: "query=" + encodeURIComponent("prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"+
	//	"select * where{?s rdf:type <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#PollutedWaterSource>. ?s this:hasLocation ?loc. ?loc geo:latitude ?lat. ?loc geo:longitude ?log.}"), // query parameter
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/sparql-results+xml");
        },
        dataType: "xml",
        success: function(data) {
		$(data).find('result').each(function(){
			var lat="",lng="";
			$(this).find("binding").each(function(){
				
				if($(this).attr("name")=="lat")
				{
					lat=($(this).find("literal").text());
				}
				if($(this).attr("name")=="log")
				{
					lng=($(this).find("literal").text());
				}
				

			});			
 			if(lat!=""&&lng!=""){
        		var blueIcon = new GIcon(G_DEFAULT_ICON);
        		blueIcon.image = "http://gmaps-samples.googlecode.com/svn/trunk/markers/blue/blank.png";

			var latlng = new GLatLng(lat ,lng);
			markerOptions = { icon:blueIcon };
         		map.addOverlay(new GMarker(latlng, markerOptions));
			alert("added");
			}			
		});
        }
    });
	}
   function showFacility()
   {
   	 $.ajax({
        type: "GET",
        url: "http://tw2.tw.rpi.edu/zhengj3/demo/wresult.xml", // SPARQL service URL
	 data: "",
       // data: "query=" + encodeURIComponent("prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"+
	//	"select * where{?s rdf:type <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#PollutedWaterSource>. ?s this:hasLocation ?loc. ?loc geo:latitude ?lat. ?loc geo:longitude ?log.}"), // query parameter
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/sparql-results+xml");
        },
        dataType: "xml",
        success: function(data) {
		$(data).find('result').each(function(){
			var lat="",lng="";
			$(this).find("binding").each(function(){
				
				if($(this).attr("name")=="lat")
				{
					lat=($(this).find("literal").text());
				}
				if($(this).attr("name")=="log")
				{
					lng=($(this).find("literal").text());
				}
				

			});			
 			if(lat!=""&&lng!=""){
        		var blueIcon = new GIcon(G_DEFAULT_ICON);
        		blueIcon.image = "http://gmaps-samples.googlecode.com/svn/trunk/markers/blue/blank.png";

			var latlng = new GLatLng(lat ,lng);
			markerOptions = { icon:blueIcon };
         		map.addOverlay(new GMarker(latlng, markerOptions));
			alert("added");
			}			
		});
        }
    });
	}