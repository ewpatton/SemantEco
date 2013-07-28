SemantEcoUI.overlays = new Object(); // Put in SemantEco.js?

function drawRadiusExtendedPolygon(radius, aName)
{
    //Get drawn polygon coordinates (latlngs)
    var coords = $.bbq.getState("UserDrawnMapPolygon");
    
    // Check arguments first
    if (typeof coords == undefined) { return false; }

	// Generate extended polygon overlay name
    if (typeof aName == undefined) {
        aName = Math.random().toString(36).slice(2); // Generate a random alphanumeric string
    }

	//Get the user drawn polygon and find it's center
    var drawnPolygon = SemantEcoUI.overlays[$bbq.getState("PolygonID")];
    var center = drawnPolygon.getBounds().getCenter();
    
    //Calculate coords for the new polygon 
    //Headiing is the angle/direction from the center(origin) to a point on the drawn polygon
    //Offset is a distance extended in this direction
    //Don't use Euclidean math, use google.maps.geometry.spherical methods
    var newPolygon;
    var extCoords = [];
    //Get radial extended latlngs
    for(var a = 0; a < coords.length; a++)
    	extCoords.push(google.maps.geometry.spherical.computeHeading(center, drawnPolygon.getPath().getAt(a)).computeOffset(radius));
    
    // Build extended polygon
    newPolygon = new google.maps.Polygon
    ({
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

function drawTwoPolygons(polygon1WKT, polygon2WKT, aName, bName)
{   
    // Check arguments first
    if (typeof polygon1Coords == undefined || typeof polygon2Coords == undefined) { return false; }

	// Generate names
    if (typeof aName == undefined) 
        aName = Math.random().toString(36).slice(2); // Generate a random alphanumeric string
    if (typeof bName == undefined) 
        bName = Math.random().toString(36).slice(2); // Generate a random alphanumeric string
    
    //Extract coords from geosparql POLYGON
    //EX INPUT: "POLYGON((45.336702 -81.587906, 39.774769 -81.148453, 39.30029   -69.964371, 45.58329 -70.403824, 45.336702 -81.587906))"^^geo:wktLiteral))

    //PARSING: Parse out letters parentheses and commas
    //Create long lat array (not lat long yet b/c geosparql polygon format uses long lat)
    var polygon1Coords = (polygon1WKT.substring(0, polygo1nWKT.length-21)).substring(9);
    polygon1Coords = polygon1Coords.replace(",", "");
	var polygon12Coords = (polygon2WKT.substring(0, polygon2WKT.length-21)).substring(9);
    polygon2Coords = polygon2Coords.replace(",", "");
    var polygon1latlng = [];
    var polygon1LONGLAT = polygon1Coords.split(","); 
    var polygon2latlng = [];
    var polygon2LONGLAT = polygon2Coords.split(",");
    
    //Create lat lng arrays from lng lat points for each polygon
    for(var a = 0; a < polygon1LONGLAT.length; i++)
        polygon1latlng.push(new google.maps.LatLng((polygon1LONGLAT[a].split(/[\s,]+/))[1], (polygon1LONGLAT[a].split(/[\s,]+/))[0]);
    for(var a = 0; a < polygon2LONGLAT.length; a++)
        polygon1latlng.push(new google.maps.LatLng((polygon2LONGLAT[a].split(/[\s,]+/))[1], (polygon2LONGLAT[a].split(/[\s,]+/))[0]);

    // Build polygon1
    var polygon1 = new google.maps.Polygon
    ({
        paths: polygon1latlng,
        strokeColor: '#FF0000',
        strokeOpacity: 0.8,
        strokeWeight: 3,
        fillColor: '#FF0000',
        fillOpacity: 0.35,
        editable: true,
        draggable: true
    });

    // Build polygon2
    var polygon2 = new google.maps.Polygon
    ({
        paths: polygon2latlng,
        strokeColor: '#FF0000',
        strokeOpacity: 0.8,
        strokeWeight: 3,
        fillColor: '#FF0000',
        fillOpacity: 0.35,
        editable: true,
        draggable: true
    });
    
    // Connect polygons to the existing Google Map
    polygon1.setMap(SemantEcoUI.map);
	polygon2.setMap(SemantEcoUI.map);
	
    // Store reference to the overlay of polygon1
    if (SemantEcoUI.overlays[aName] == undefined) {
        // Name is not already taken
        SemantEcoUI.overlays[aName] = polygon1;
    } else { return false; }
    
    //Store reference to the overlay of polygon2
    if (SemantEcoUI.overlays[bName] == undefined) {
        // Name is not already taken
        SemantEcoUI.overlays[bName] = polygon2;
    } else { return false; }

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