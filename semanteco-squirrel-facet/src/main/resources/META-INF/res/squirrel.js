SemantEcoUI.overlays = new Object(); // Put in SemantEco.js?

function drawRadiusExtendedPolygon(radius, aName) {
    //Get drawn polygon coordinates (latlngs)
    var coords = $.bbq.getState("UserDrawnMapPolygon");
    
    // Check arguments first
    if (coords == undefined) { return false; }

	// Generate extended polygon overlay name
    if (aName == undefined) {
        aName = Math.random().toString(36).slice(2); // Generate a random alphanumeric string
    }

	//Get the user drawn polygon and find it's center
    var drawnPolygon = SemantEcoUI.overlays[$.bbq.getState("PolygonID")];
    var center = drawnPolygon.getBounds().getCenter();
    
    //Calculate coords for the new polygon 
    //Headiing is the angle/direction from the center(origin) to a point on the drawn polygon
    //Offset is a distance extended in this direction
    //Don't use Euclidean math, use google.maps.geometry.spherical methods
    var newPolygon;
    var extCoords = [];
    //Get radial extended latlngs
    for(var a = 0; a < coords.length; a++) {
        //console.log(coords[a], radius, center, drawnPolygon.getPath().getAt(a), google.maps.geometry.spherical.computeHeading(center, drawnPolygon.getPath().getAt(a)));
    	//extCoords.push(google.maps.geometry.spherical.computeHeading(center, drawnPolygon.getPath().getAt(a)).computeOffset(radius));
        var heading = google.maps.geometry.spherical.computeHeading(center, drawnPolygon.getPath().getAt(a));
        var cleanCoords = coords[a].replace("(", "").replace(")", "").split(",");
        var lat = parseFloat(cleanCoords[0]);
        var lng = parseFloat(cleanCoords[1]);
        var newLatLng = new google.maps.LatLng(lat, lng);
        var aCoord = google.maps.geometry.spherical.computeOffset(newLatLng, radius, heading);
        //console.log(coords[a], newLatLng, radius, heading, aCoord);
        extCoords.push(aCoord);

    }
    
    // Build extended polygon
    newPolygon = new google.maps.Polygon({
        paths: extCoords,
        strokeColor: '#FF0000',
        strokeOpacity: 0.8,
        strokeWeight: 3,
        fillColor: '#FF0000',
        fillOpacity: 0.35,
        editable: true,
        draggable: true
    });

    // Connect polygon to the existing Google Map
    newPolygon.setMap(SemantEcoUI.map);

    // Store reference to this overlay. This is crucial if the user ever wants to remove the overlay
    if (SemantEcoUI.overlays[aName] == undefined) {
        // Name is not already taken
        SemantEcoUI.overlays[aName] = newPolygon;
    } else { return false; }

	// Serialize polygon path to string to push to state
    var path = newPolygon.getPath();
    var latlngarray = [];
    for (var i = 0; i < path.getLength(); i++) {
        latlngarray.push(path.getAt(i).toString());
    }

    // Publish final polygon path to $.bbq state
    $.bbq.pushState({"ExtendedUserDrawnPolygon": latlngarray});
    // Return success
    return true;
}

function printPolygons(WKTString)
{
 var WKTs = WKTString.split("\n");
 for(var a = 0; a < WKTs.length; a++)
  console.log("WKT PRINTED HERE\n", WKTs[a] );
}

function drawPolygons(WKTString) 
{
 var WKTs = WKTString.split("\n");
 for(var a = 0; a < WKTs.length; a++)
 {
  if(WKTs[a].indexOf("POLYGON") != -1)
  {
   //Format - escape quotes
   var drawMe = "\\";
   drawMe += WKTs[a];
   drawMe = drawMe.substring(0, drawMe.length-1);
   drawMe = drawMe + "\\\"";
   //drawPolygonFromWKT(drawMe);
   setTimeout(drawPolygonFromWKT(drawMe), 5000);
   console.log(drawMe);
  }
  else if(WKTs[a].indexOf("LINESTRING") != -1 || WKTs[a].indexOf("POINT") != -1)
   drawLinestringFromWKT(WKTs[a]);
 }
}
 
function checkString(str)
{
    var drawMe = "\\";
    drawMe += str;
    drawMe = drawMe.substring(0, drawMe.length-1);
    drawMe = drawMe + "\\\"";
    //drawPolygonFromWKT(drawMe);
    console.log(drawMe);
}

function checkLinestring(wktLiteral)
{
 //var wktLiteral = wktLiteral.replace(/[\s,]+/, " ");
 //var start = wktLiteral.indexOf("(");
 //var end = wktLiteral.indexOf(")");
 //var cleanLiteralList = wktLiteral.substring(start+1, end);
 console.log(wktLiteral);
 //console.log(cleanLiteralList);
}

function drawLinestringFromWKT(wktLiteral)
{
 var wktLiteral = wktLiteral.replace(/[\s,]+/, " ");
 var start = wktLiteral.indexOf("(");
 var end = wktLiteral.indexOf(")");
 var cleanLiteralList = wktLiteral.substring(start+1, end);
 cleanLiteralList = cleanLiteralList.split(",");

    // Build polyline
    var polyOptions = {
        strokeColor: '#FF0000',
        strokeOpacity: 0.8,
        strokeWeight: 3,
        editable: true
    };
    var newPolyline = new google.maps.Polyline(polyOptions);
    
    // Connect polylines to the existing Google Map
    newPolyline.setMap(SemantEcoUI.map);

	for(var i = 0; i < cleanLiteralList.length; i++)
 	{
        var lng = parseFloat($.trim(cleanLiteralList[i]).split(/[\s,]+/)[1]);
        var lat = parseFloat($.trim(cleanLiteralList[i]).split(/[\s,]+/)[0]);
        console.log(cleanLiteralList[i], lat, lng);
        //Switched coordinates here b/c geosparql polygon takes long lat : "LINESTRING((long1 lat1, long2...
        newPolyline.getPath().push(new google.maps.LatLng(lng, lat));
    }
    
    // Return success
    return true;
}

function drawPolygonFromWKT(wktLiteral, aName) {   
    // Check arguments first
    if (wktLiteral == undefined) { console.log("Bad Input, no WKTLiteral passed"); return false; }

	// Generate names
    if (aName == undefined) 
        aName = Math.random().toString(36).slice(2); // Generate a random alphanumeric string
    
    //Extract coords from geosparql POLYGON
    var cleanLiteralList = wktLiteral.replace(/[^\d.\-, ]/g, '').split(",");

    //EX INPUT: "POLYGON((45.336702 -81.587906, 39.774769 -81.148453, 39.30029   -69.964371, 45.58329 -70.403824, 45.336702 -81.587906))"^^geo:wktLiteral))

    //Create lat lng arrays from lng lat points for each polygon
    newPolygonLatLngList = [];
    console.log(cleanLiteralList);
    for(var i = 0; i < cleanLiteralList.length; i++) {
        var lng = parseFloat($.trim(cleanLiteralList[i]).split(/[\s,]+/)[1]);
        var lat = parseFloat($.trim(cleanLiteralList[i]).split(/[\s,]+/)[0]);
        console.log(cleanLiteralList[i], lat, lng);
        //Switched coordinates here b/c geosparql polygon takes long lat : "POLYGON((long1 lat1, long2...
        newPolygonLatLngList.push(new google.maps.LatLng(lng, lat));
    }

    // Build polygon
    var newPolygon = new google.maps.Polygon
    ({
        paths: newPolygonLatLngList,
        strokeColor: '#FF0000',
        strokeOpacity: 0.8,
        strokeWeight: 3,
        fillColor: '#FF0000',
        fillOpacity: 0.35,
        editable: true,
        draggable: true
    });
    
    // Connect polygons to the existing Google Map
    newPolygon.setMap(SemantEcoUI.map);
	
    // Store reference to the overlay of polygon1
    if (SemantEcoUI.overlays[aName] == undefined) {
        // Name is not already taken
        SemantEcoUI.overlays[aName] = newPolygon;
    } else { console.log("Cannot make polygon, name is taken:", aName); return false; }

    // Return success
    return true;
}

//Get bounds of a polygon (existed in google maps v2, can be substituted like this for v3)
//http://stackoverflow.com/questions/3081021/how-to-get-the-center-of-a-polygon-in-google-maps-v3
google.maps.Polygon.prototype.getBounds=function()
{
    var bounds = new google.maps.LatLngBounds()
    this.getPath().forEach(function(element,index){bounds.extend(element)})
    return bounds
}