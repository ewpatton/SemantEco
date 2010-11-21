/* -*- espresso-indent-level: 2; tab-width: 8; -*- */
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
    showViolatedFacility();

  }
}

function showPollutedWater()
{
  var success = function(data) {
    pollutedwatersource = new Array();
    document.getElementById("spinner").style.display="none";
    $(data).find('result').each(function(){
      var lat="",lng="",sub="",label="";
      $(this).find("binding").each(function(){
        if($(this).attr("name")=="lat")
        {
          lat=($(this).find("literal").text());
        }
        if($(this).attr("name")=="log")
        {
          lng=($(this).find("literal").text());
        }
        if($(this).attr("name")=="label")
        {
          label=($(this).find("literal").text());
        }
        if($(this).attr("name")=="s")
        {
          sub=($(this).find("uri").text());
          pollutedwatersource.push(sub);
        }
      });
      if(lat!=""&&lng!=""){
        var site={'uri':sub,'label':label,'isPolluted':true};
        var blueIcon = new GIcon(G_DEFAULT_ICON,"image/pollutedwater.png");
        blueIcon.iconSize = new GSize(29,34);
        var latlng = new GLatLng(lat ,lng);
        markerOptions = { icon:blueIcon };
        var marker=new GMarker(latlng, markerOptions);
        GEvent.addListener(marker, "click",
            		   function() {
            		     var info = queryForWaterPollution(site,false,marker);
            		     marker.openInfoWindow(info);
            	 	   }
            		  );
        map.addOverlay(marker);
      }
    });
    showCleanWater();
  };
  $.ajax({type: "GET",
          url: "http://was.tw.rpi.edu/water/service/agent", // SPARQL service URI
          data: "session="+window.sessionID+
          "&query="+encodeURIComponent("prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#PollutedWaterSource>. ?s geo:lat ?lat. ?s geo:long ?log. ?s rdfs:label ?label . }"), // query parameter
	  beforeSend: function(xhr) {
	    xhr.setRequestHeader("Accept", "application/sparql-results+xml");
	  },
          dataType: "xml",	  
          error: function(xhr, text, err) {
	    if(xhr.status == 200) {
	      success(xhr.responseXML);
	    }
	  },
          success: success
	 });
}

function showCleanWater()
{
  var success = function(data) {
    showViolatedFacility();
    $(data).find('result').each(function(){
      var lat="",lng="",sub="",label="";var show=true;
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
	if($(this).attr("name")=="label")
	{
	  label=($(this).find("literal").text());
	}
      });			
      if(lat!=""&&lng!=""&&show){
	var thisIcon = new GIcon(G_DEFAULT_ICON,"image/cleanwater2.png");
	thisIcon.iconSize = new GSize(30,34);
	var latlng = new GLatLng(lat ,lng);
	markerOptions = { icon:thisIcon };

	var site={'uri':sub,'label':label,'isPolluted':false};
	var marker=new GMarker(latlng, markerOptions);
	GEvent.addListener(marker, "click",
			   function() {
			     var info=queryForWaterPollution(site,false,marker);
			     marker.openInfoWindowHtml(info);
			   }
			  );
	map.addOverlay(marker);	
      };
    });
  };
  $.ajax({type: "GET",
	  url: "http://was.tw.rpi.edu/water/service/agent", // SPARQL service URI
	  data: "session="+window.sessionID+
          "&query=" + encodeURIComponent("prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type <http://sweet.jpl.nasa.gov/2.1/realmHydroBody.owl#BodyOfWater>. ?s geo:lat ?lat. ?s geo:long ?log. ?s rdfs:label ?label . }"), // query parameter
	  beforeSend: function(xhr) {
	    xhr.setRequestHeader("Accept", "application/sparql-results+xml");
    	  },
    	  dataType: "xml",
    	  error: function(xhr, text, err) {
    	    if(xhr.status == 200) {
    	      success(xhr.responseXML);
    	    }
    	  },
    	  success: success
	 });
}

function showViolatedFacility()
{
  var success = function(data) {
    violatedfacility = new Array();
    $(data).find('result').each(function(){
      var lat="",lng="",sub="",label="";
      $(this).find("binding").each(function(){
        if($(this).attr("name")=="lat")
        {
          lat=($(this).find("literal").text());
        }
        if($(this).attr("name")=="log")
        {
          lng=($(this).find("literal").text());
        }
        if($(this).attr("name")=="label")
        {
          label=($(this).find("literal").text());
        }
        if($(this).attr("name")=="s")
        {
          sub=($(this).find("uri").text());
          violatedfacility.push(sub);
        }
      });
      if(lat!=""&&lng!=""){
        var site={'uri':sub,'label':label,'isPolluted':true};
        var blueIcon = new GIcon(G_DEFAULT_ICON,"image/facilitypollute.jpg");
        blueIcon.iconSize = new GSize(29,34);
        var latlng = new GLatLng(lat ,lng);
        markerOptions = { icon:blueIcon };
        var marker=new GMarker(latlng, markerOptions);
        GEvent.addListener(marker, "click",
            		   function() {
            		     var info = queryForFacilityPollution(site,false,marker);
            		     marker.openInfoWindow(info);
            	 	   }
            		  );
        map.addOverlay(marker);
      }
    });
    showFacility();
  };
  $.ajax({type: "GET",
	  url: "http://was.tw.rpi.edu/water/service/agent", // SPARQL service URI
	  data: "session="+window.sessionID+
	  "&query=" + encodeURIComponent("prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> select * where{?s a this:ViolatingFacility . ?s rdfs:label ?label . ?s geo:lat ?lat . ?s geo:long ?log . }"),
	  beforeSend: function(xhr) {
	    xhr.setRequestHeader("Accept", "application/sparql-results+xml");
	  },
	  dataType: "xml",
	  success: success,
	  error: function(xhr, text, err) {
	    if(xhr.status==200) {
	      success(xhr.responseXML);
	    }
	  }
	 });
}

function showFacility()
{
  var success = function(data) {
    $(data).find('result').each(function(){
      var lat="",lng="",sub="",label="";var show=true;
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
	if($(this).attr("name")=="label")
	{
	  label=($(this).find("literal").text());
	}
      });			
      if(lat!=""&&lng!=""&&show){
	var thisIcon = new GIcon(G_DEFAULT_ICON,"image/facility.jpg");
	thisIcon.iconSize = new GSize(30,34);
	var latlng = new GLatLng(lat ,lng);
	markerOptions = { icon:thisIcon };

	var site={'uri':sub,'label':label,'isPolluted':false};

	var marker=new GMarker(latlng, markerOptions);
	GEvent.addListener(marker, "click",
			   function() {
			     var info=queryForFacilityPollution(site,false,marker);
			     marker.openInfoWindow(info);
			   }
			  );
	map.addOverlay(marker);	
      };
    });
  };
  $.ajax({
    type: "GET",
    url: "http://was.tw.rpi.edu/water/service/agent", // SPARQL service URI
    data: "session="+window.sessionID+
    "&query=" + encodeURIComponent("prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> select * where{?s rdf:type this:Facility . ?s rdfs:label ?label . ?s geo:lat ?lat. ?s geo:long ?log.}"),
    beforeSend: function(xhr) {
      xhr.setRequestHeader("Accept", "application/sparql-results+xml");
    },
    dataType: "xml",
    success: success,
    error: function(xhr, text, err) {
      if(xhr.status == 200) {
	success(xhr.responseXML);
      }
    }
  });
}	  
