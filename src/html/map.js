    var map = null;
    var geocoder = null;
var pollutedwatersource=new Array();
var violatedfacility=new Array();
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
	      GEvent.addListener(marker, "click",
				 function() {
				     marker.openInfoWindowHtml(address);
				 }
				 );
              map.addOverlay(marker);
              marker.openInfoWindowHtml(address);
            }
          }
        );
	showPollutedWater();
	showCleanWater();
 	showViolatedFacility();
	showFacility();
      }
    }
   
   function showPollutedWater()
   {
   	 $.ajax({
        type: "GET",
        url: "http://was.tw.rpi.edu:14490/agent", // SPARQL service URI
       data: "query=" + encodeURIComponent("prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#PollutedWaterSource>. ?s this:hasLocation ?loc. ?loc geo:latitude ?lat. ?loc geo:longitude ?log.}"), // query parameter
       beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/sparql-results+xml");
	    },
        dataType: "xml",
        success: function(data) {

		     $(data).find('result').each(function(){
			     var lat="",lng="",sub="";
			$(this).find("binding").each(function(){
				
				if($(this).attr("name")=="lat")
				{
					lat=($(this).find("literal").text());
				}
				if($(this).attr("name")=="log")
				{
					lng=($(this).find("literal").text());
				}
				if($(this).attr("name")=="s")
				{
					sub=($(this).find("uri").text());
					pollutedwatersource.push(sub);
				}
			});			
 			if(lat!=""&&lng!=""){
        		var blueIcon = new GIcon(G_DEFAULT_ICON);
        		blueIcon.image = "http://tw2.tw.rpi.edu/zhengj3/demo/pollutedwater.jpg";

			var latlng = new GLatLng(lat ,lng);
			markerOptions = { icon:blueIcon };
			var marker=new GMarker(latlng, markerOptions);
			GEvent.addListener(marker, "click",
			function() {
				marker.openInfoWindowHtml(sub);
				}
			);
         		map.addOverlay(marker);	
			}			
			});
        }
	});
	}
   function showCleanWater()
   {
   	 $.ajax({
        type: "GET",
        url: "http://was.tw.rpi.edu:14490/agent", // SPARQL service URI
       data: "query=" + encodeURIComponent("prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#WaterSource>. ?s this:hasLocation ?loc. ?loc geo:latitude ?lat. ?loc geo:longitude ?log.}"), // query parameter
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/sparql-results+xml");
        },
        dataType: "xml",
        success: function(data) {
		$(data).find('result').each(function(){
			var lat="",lng="",sub="";var show=true;
			$(this).find("binding").each(function(){
				if($(this).attr("name")=="s")
				    {					
					for(var i=0;i<pollutedwatersource.length;i++)
					    {
						if($(this).find("uri").text()==pollutedwatersource[i]){
						    show=false;
						}
					    }
					sub=$(this).find("uri").text();
				    }
				if($(this).attr("name")=="lat")
				{
					lat=($(this).find("literal").text());
				}
				if($(this).attr("name")=="log")
				{
					lng=($(this).find("literal").text());
				}
				

			});			
 			if(lat!=""&&lng!=""&&show){
        		var thisIcon = new GIcon(G_DEFAULT_ICON);
        		thisIcon.image = "http://tw2.tw.rpi.edu/zhengj3/demo/cleanwater2.jpg";

			var latlng = new GLatLng(lat ,lng);
			markerOptions = { icon:thisIcon };
			var marker=new GMarker(latlng, markerOptions);
			GEvent.addListener(marker, "click",
			function() {
				marker.openInfoWindowHtml(sub);
				}
			);
         		map.addOverlay(marker);	
			}			
		});
        }
    });
}
   function showViolatedFacility()
   {
   	 $.ajax({
		 type: "GET",
		     url: "http://was.tw.rpi.edu:14490/agent", // SPARQL service URI
		     data: "query=" + encodeURIComponent("prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s this:hasMeasurement ?o. ?o rdf:type this:Violation. ?s this:hasLocation ?loc. ?loc geo:latitude ?lat. ?loc geo:longitude ?log.}"),
		     beforeSend: function(xhr) {
		     xhr.setRequestHeader("Accept", "application/sparql-results+xml");
		 },
		     dataType: "xml",
		     success: function(data) {
		     $(data).find('result').each(function(){
			     var lat="",lng="",sub;
			     $(this).find("binding").each(function(){
				     
				     if($(this).attr("name")=="lat")
					 {
					     lat=($(this).find("literal").text());
					 }
				     if($(this).attr("name")=="log")
					 {
					     lng=($(this).find("literal").text());
					 }
				     if($(this).attr("name")=="s")
					 {
					     sub=($(this).find("uri").text());
					     violatedfacility.push(sub);
					 }				     				     
				 });			
			     if(lat!=""&&lng!=""){
				 var blueIcon = new GIcon(G_DEFAULT_ICON);
				 blueIcon.image = "http://tw2.tw.rpi.edu/zhengj3/demo/facilitypollute.jpg";
				 
				 var latlng = new GLatLng(lat ,lng);
				 markerOptions = { icon:blueIcon };
				 var marker=new GMarker(latlng, markerOptions);
				 GEvent.addListener(marker, "click",
						    function() {
							marker.openInfoWindowHtml(sub);
						    }
						    );
				 map.addOverlay(marker);			
			     }			
			 });
		 }
	     });
   }
function showFacility()
   {
       $.ajax({
	       type: "GET",
		   url: "http://was.tw.rpi.edu:14490/agent", // SPARQL service URI
		   data: "query=" + encodeURIComponent("prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type this:Facility. ?s this:hasLocation ?loc. ?loc geo:latitude ?lat. ?loc geo:longitude ?log.}"),
	       beforeSend: function(xhr) {
		   xhr.setRequestHeader("Accept", "application/sparql-results+xml");
	       },
		   dataType: "xml",
		   success: function(data) {
		$(data).find('result').each(function(){
			var lat="",lng="",sub="";var show=true;
			$(this).find("binding").each(function(){
				if($(this).attr("name")=="s")
				    {					
					for(var i=0;i<violatedfacility.length;i++)
					    {
						if($(this).find("uri").text()==violatedfacility[i]){
						    show=false;
						}
					    }
					sub=$(this).find("uri").text();
				    }
				if($(this).attr("name")=="lat")
				{
					lat=($(this).find("literal").text());
				}
				if($(this).attr("name")=="log")
				{
					lng=($(this).find("literal").text());
				}
				

			});			
 			if(lat!=""&&lng!=""&&show){
        		var thisIcon = new GIcon(G_DEFAULT_ICON);
        		thisIcon.image = "http://tw2.tw.rpi.edu/zhengj3/demo/facility.jpg";

			var latlng = new GLatLng(lat ,lng);
			markerOptions = { icon:thisIcon };
			var marker=new GMarker(latlng, markerOptions);
			GEvent.addListener(marker, "click",
			function() {
				marker.openInfoWindowHtml(sub);
				}
			);
         		map.addOverlay(marker);	
			}			
		});
	       }
	   });
   }	  

		       
		       
    