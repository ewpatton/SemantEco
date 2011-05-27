/* -*- espresso-indent-level: 2; tab-width: 8; -*- */
var map = null;
var geocoder = null;
var thisserviceagent="http://localhost:14490/agent";
var thiszipagent="http://localhost:14490/zip";
var pollutedwatersource=new Array();
var violatedfacility=new Array();
var pollutedMarkers=new Array();
var cleanMarkers=new Array();
var violatedMarker=new Array();
var facilityMarker=new Array();
var state;
var countyCode;
var start;
var limit;
var stateCode="";

var wqpMarkers = {"pollutedWater":[],"cleanWater":[],"pollutedFacility":[],"facility":[],"flood":[]} 

function showhide(str) {
 var check = document.getElementById(str);
 if(check.checked) {
 for(var i=0;i<window.wqpMarkers[str].length;i++) {
 window.wqpMarkers[str][i].show();
 }
 }
 else {
 for(var i=0;i<window.wqpMarkers[str].length;i++) {
 window.wqpMarkers[str][i].hide();
 }
 }
} 

function initialize() {
  if (GBrowserIsCompatible()) {
	map = new GMap2(document.getElementById("map_canvas"));
	map.setCenter(new GLatLng(37.4419, -122.1419), 10);
	geocoder = new GClientGeocoder();
  }
}

function showAddress(address,tstart,tlimit) {
  var element="http://tw2.tw.rpi.edu/owl/eap.owl#Arsenic";
 // document.getElementById("test2").innerHTML=element.replace("http://tw2.tw.rpi.edu/owl/eap.owl#","");
  var prevpage="";
  var nextpage="";
  start=tstart;
  limit=tlimit;
  document.getElementById("start").innerHTML=1;
  document.getElementById("limit").innerHTML=start+limit;
  
  if(document.getElementById("clear").checked){
  document.getElementById("start").innerHTML=start+1;
  map.clearOverlays();
  
  if(start > 0){
  var thisstart=start-limit;
  
  if(thisstart<0)
    thisstart=0;
	
  prevpage="<a href=\"javascript:showAddress('"+address+"',"+thisstart+","+limit+")\">Preivous "+limit+" triples</a>";
  }
  }
  
  nextpage="<a href=\"javascript:showAddress('"+address+"',"+(start+limit)+","+limit+")\">next "+limit+" triples</a>";
  
  document.getElementById("page").innerHTML=prevpage+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+nextpage;
  var waterquery="";
  var facilityquery="";
  if (geocoder) {
    geocoder.getLatLng(
      address,
      function(point) {
	if (!point) {
	  alert(address + " not found");
	} else {
	  map.setCenter(point, 10);
	}
      }
    );
	
   $.ajax({type: "GET",
          url: thiszipagent, // SPARQL service URI
          data:"code="+address, // query parameter
          dataType: "json",	  
          success: function(data){
		  //document.getElementById("test").innerHTML=data.result.stateAbbr+" "+data.result.stateCode+" "+data.result.countyCode;
		  state=data.result.stateAbbr;
		  thisStateCode=data.result.stateCode;
		  stateCode=thisStateCode.split(":")[1];
		  alert(stateCode);
		  countyCode=data.result.countyCode;
		  countyCode=countyCode.split(":")[2];
		  countyCode=countyCode.replace(/^0+/,"");

		  waterquery="prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type epa:PollutedWaterSource. ?s geo:lat ?lat. ?s geo:long ?long. ?s epa:hasCountyCode \""+countyCode+"\".}";

		  showPollutedWater(waterquery);
		  
		  }
	 });
	 
	 
  }
}
function showFlood(){
  var success = function(data) {
    pollutedwatersource = new Array();
    $(data).find('result').each(function(){
      var lat="",lng="",sub="",label="";
      $(this).find("binding").each(function(){
	 
        if($(this).attr("name")=="lat")
        {
          lat=($(this).find("literal").text());
        }
        if($(this).attr("name")=="long")
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
	  //document.getElementById("test").innerHTML="ready to display";
        var site={'uri':sub,'label':label,'isPolluted':true};
        var blueIcon = new GIcon(G_DEFAULT_ICON,"image/flood.png");
        blueIcon.iconSize = new GSize(29,34);
        var latlng = new GLatLng(lat ,lng);
        markerOptions = { icon:blueIcon };
        var marker=new GMarker(latlng, markerOptions);
        GEvent.addListener(marker, "click",
            		   function() {
            		     var info = queryForFlood(site,false,marker);
            		     marker.openInfoWindow(info);
            	 	   }
            		  );
        map.addOverlay(marker);
		wqpMarkers["flood"].push(marker);
      }
    });
  };
   var query="prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type this:Flood. ?s geo:lat ?lat. ?s geo:long ?long. }"
  var source=null;
  if(data_source["USGS"]==1)
    source="USGS";
  var parameter="data=water&state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query)+"&start="+start+"&limit="+limit+"&source="+source;
  if(regulation!=""){
  parameter+="&regulation="+regulation;
  }
  
  $.ajax({type: "GET",
          url: thisserviceagent, // SPARQL service URI
          data: parameter,//"state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query), // query parameter
          dataType: "xml",	  
          success: success
	 });

}
function showPollutedWater(query)
{
  var success = function(data) {
    pollutedwatersource = new Array();
    $(data).find('result').each(function(){
      var lat="",lng="",sub="",label="";
      $(this).find("binding").each(function(){
	 
        if($(this).attr("name")=="lat")
        {
          lat=($(this).find("literal").text());
        }
        if($(this).attr("name")=="long")
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
	  //document.getElementById("test").innerHTML="ready to display";
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
		wqpMarkers["pollutedWater"].push(marker);
      }
    });
    showCleanWater();
  };
  var source=null;
  if(data_source["USGS"]==1)
    source="USGS";
  var parameter="data=water&state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query)+"&start="+start+"&limit="+limit+"&source="+source;
  if(regulation!=""){
  parameter+="&regulation="+regulation;
  }
  
  $.ajax({type: "GET",
          url: thisserviceagent, // SPARQL service URI
          data: parameter,//"state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query), // query parameter
          dataType: "xml",	  
          success: success
	 });

	 

}

function showCleanWater()
{
  var success = function(data) {

    $(data).find('result').each(function(){
      var lat="",lng="",sub="",label="";var show=true;
      $(this).find("binding").each(function(){

	if($(this).attr("name")=="s")
	{
      //document.getElementById("test").innerHTML+=pollutedwatersource.length;   
	  for(var i=0;i<pollutedwatersource.length;i++)
	  {
	     //document.getElementById("test").innerHTML+=pollutedwatersource[i]+" ";   
	    if($(this).find("uri").text()==pollutedwatersource[i]){
	      show=false;
		  break;
	    }
	  }
	  sub=$(this).find("uri").text();
	}
	if($(this).attr("name")=="lat")
	{
	  lat=($(this).find("literal").text());
	  //document.getElementById("test").innerHTML+=lat;
	}
	if($(this).attr("name")=="long")
	{
	  lng=($(this).find("literal").text());
	  //document.getElementById("test").innerHTML+=lng;
	}
	if($(this).attr("name")=="label")
	{
	  label=($(this).find("literal").text());
	  //document.getElementById("test").innerHTML+=label;
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
    wqpMarkers["cleanWater"].push(marker);	
      };
    });
	var facilityquery="prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type epa:ViolatingFacility. ?s geo:lat ?lat. ?s geo:long ?long.}";
	showViolatedFacility(facilityquery);
  };
  var query="prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type <http://sweet.jpl.nasa.gov/2.1/realmHydroBody.owl#BodyOfWater>. ?s geo:lat ?lat. ?s geo:long ?long. }"
  var source=null;
  if(data_source["USGS"]==1)
    source="USGS";
  var parameter="data=water&state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query)+"&start="+start+"&limit="+limit+"&source="+source;
  if(regulation!=""){
  parameter+="&regulation="+regulation;
  }
  $.ajax({type: "GET",
	  url: thisserviceagent, // SPARQL service URI
	  data: parameter,//"state="+state+"&countyCode="+countyCode+"&query=" + encodeURIComponent(), // query parameter
      dataType: "xml",
      success: success
	 });
}

function showViolatedFacility(query)
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
        if($(this).attr("name")=="long")
        {
          lng=($(this).find("literal").text());
		  if(lng.charAt(0)!="-")
		  {
			lng="-"+lng;
		  }
        }
        if($(this).attr("name")=="s")
        {
          sub=($(this).find("uri").text());
          violatedfacility.push(sub);
        }
      });
      if(lat!=""&&lng!=""){
        var site={'uri':sub,'isPolluted':true};
        var blueIcon = new GIcon(G_DEFAULT_ICON,"image/facilitypollute.png");
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
		wqpMarkers["pollutedFacility"].push(marker);
      }
    });
    showFacility();
  };
   var source=null;
   if(data_source["EPA"]==1)  
      source="EPA";
   var parameter="data=facility&state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query)+"&start="+start+"&limit="+limit+"&type=ViolatingFacility&source="+source;;
  
  $.ajax({type: "GET",
          url: thisserviceagent, // SPARQL service URI
          data: parameter,//"state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query), // query parameter
          dataType: "xml",	  
          success: success
	 }); 
  
  /*
  $.ajax({type: "GET",
	  url: thisserviceagent, // SPARQL service URI
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
	 });*/
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
		  if(lng.charAt(0)!="-")
		  {
			lng="-"+lng;
		  }
	}
	if($(this).attr("name")=="label")
	{
	  label=($(this).find("literal").text());
	}
      });			
      if(lat!=""&&lng!=""&&show){
	 
	var thisIcon = new GIcon(G_DEFAULT_ICON,"image/facility.png");
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
	window.wqpMarkers["facility"].push(marker); 
	
      };
    });
	//showFlood();
  };
   var query="prefix  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> select * where{?s rdf:type this:Facility .  ?s geo:lat ?lat. ?s geo:long ?log.}";
    var source=null;
   if(data_source["EPA"]==1)  
      source="EPA";
   var parameter="data=facility&state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query)+"&start="+start+"&limit="+limit+"&type=facility&source="+source;
  $.ajax({
    type: "GET",
    url: thisserviceagent, // SPARQL service URI
    data: parameter,
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
