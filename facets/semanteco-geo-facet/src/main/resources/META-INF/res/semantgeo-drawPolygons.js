// Code by Brendan Ashby
// Need to maintain a reference to all created overlays
// This is what google said we should do when working with multiple overlays
SemantEcoUI.overlays = new Object(); // TODO: This should probably be in a init function and here

function addPolygonToMap(coords, aName){
    // Call to add a polygon overlay to the SemantEcoUI Google Map View 
    // coords: a list/array of latitiude longitute "tuples"
    // e.g. [[alat, along], [alat, along], ...]
    // returns True on success, or false on error

    // Check arguments first
    if (coords == undefined) { return false; }

    // We are OK if a name was not given, we can generate one
    if (aName == undefined) {
        aName = Math.random().toString(36).slice(2); // Generate a random alphanumeric string
    }

    //var polygonCoords = [];
    var newPolygon;

    // rebuild coords into google LatLng object list
    //for(var i = 0; i < coords.length; i++){
    //    polygonCoords.push(new google.maps.LatLng(coords[i][0], coords[i][1]));
    //}
    
    // Build out polygon (using variable defaults from google's documentation)
    newPolygon = new google.maps.Polygon({
        paths: coords,
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

    console.log("PUSHING TO BBQ STATE", aName);

    // Publish final polygon path to $.bbq state
    $.bbq.pushState({"UserDrawnMapPolygon": latlngarray});
    

    //Push the name of the polygon
    $.bbq.pushState({"PolygonID": aName});

    // Return success
    return true;
}

function removeOverlayFromMap(aName){
    // Called when we want to remove an overlay. Arrays are stored against a name they were created with.
     if (SemantEcoUI.overlays[aName] != undefined) {
        // Name exists
        SemantEcoUI.overlays[aName].setMap(null); // Hides overlay from map
        SemantEcoUI.overlays[aName] = undefined; // Deletes overlay
    }
}

function hideAllOverlaysOnMap(){
    // Sets all overlays to hidden
    SemantEcoUI.overlays.forEach(function(overlay) {
        overlay.setMap(null);
    });
}

function showAllOverlaysOnMap(){
    // Sets all overlays to visible
    SemantEcoUI.overlays.forEach(function(overlay) {
        overlay.setMap(SemantEcoUI.map);
    });
}

function addPolygonDrawListeners(aPolyline){

    // Reference to a doubleclick across listeners
    SemantEcoUI.userHasDoubleClicked = false;

    // During editing, disable doubleClickToZoom on the overall map
    SemantEcoUI.map.set("disableDoubleClickZoom", true);

    // Created listeners needed when user is drawing a polygon 
    var polyLineClickListener = google.maps.event.addListener(aPolyline, 'click', function(event) {
        console.log("SINGLE CLICK POLYLINE");
        // Logic to make sure this is the first point
        if (aPolyline.getPath().getAt(0).equals(event.latLng)) {
            // We are done editing, create our final polygon, and remove listeners for edting
            addPolygonToMap(aPolyline.getPath());
            removePolygonDrawListeners();    
        }
    });

    var polyLineMapDoubleClickListener = google.maps.event.addListener(SemantEcoUI.map, 'dblclick', function(event) {
        // We have double clicked, set the flag for later
        console.log("DOUBLE CLICK MAP FIRED");
        SemantEcoUI.userHasDoubleClicked = true;
        // TEST: Prevent the doubleclick event from propagating
        // See: https://code.google.com/p/gmaps-api-issues/issues/detail?id=2172
        // See: https://code.google.com/p/google-maps-utility-library-v3/source/browse/trunk/markerclustererplus/src/markerclusterer.js?r=362
        // See: http://stackoverflow.com/questions/10020239/how-to-get-the-event-to-use-event-stoppropagation
        // Prevent event propagation to the map:
        console.log(event);
        event.stop();
        event.cancelBubble = true;
        if (event.stopPropagation) {
            event.stopPropagation();
        }
        if (event.preventDefault) {
            event.preventDefault(); 
        } else {
            event.returnValue = false;  
        }
    });

    var polyLineUpdatePathListener = google.maps.event.addListener(SemantEcoUI.map, 'click', function(event) {
        console.log("SINGLE CLICK MAP FIRED");
        // We assume user has only single clicked, but if double clicked this will be overidden
        SemantEcoUI.userHasDoubleClicked = false;
        setTimeout(function() {
            handleSingleDoubleDrawingClickEvent(event, aPolyline)}, 250);
    });

    // Store references to our listeners so they can be removed later
    SemantEcoUI.polyLineClickListener = polyLineClickListener;
    SemantEcoUI.polyLineMapDoubleClickListener = polyLineMapDoubleClickListener;
    SemantEcoUI.polyLineUpdatePathListener = polyLineUpdatePathListener;
}

function handleSingleDoubleDrawingClickEvent(event, polyline) {
    // Contains all logic for a single our double click event

    if (SemantEcoUI.userHasDoubleClicked) {
        // We are done editing, create our final polygon, and remove listeners for edting
        if (aPolyline.getPath().getLength() > 0) {
            addPolygonToMap(aPolyline.getPath());
        }
        removePolygonDrawListeners();
    } else {
        // Single click, add to path
        var path = aPolyline.getPath();

        // Because path is an MVCArray, we can simply append a new coordinate
        // and it will automatically appear
        path.push(event.latLng);
    }
}

function removePolygonDrawListeners(){
    // Called when a user finished drawing a polygon, removes listeners created for drawing
    google.maps.event.removeListener(SemantEcoUI.polyLineClickListener);
    google.maps.event.removeListener(SemantEcoUI.polyLineMapDoubleClickListener);
    google.maps.event.removeListener(SemantEcoUI.polyLineUpdatePathListener);

    // revert map behavior to default now that we are not editing anymore
    // NOTE: If some other function has set this to true, we will be overriding it. Hmm. Food for thought.
    SemantEcoUI.map.set("disableDoubleClickZoom", false);
}

function startDrawingPolygon(){
    // Called when a User begins to draw a polygon into the map

    var polyOptions = {
        strokeColor: '#FF0000',
        strokeOpacity: 0.8,
        strokeWeight: 3,
        editable: true
    };

    aPolyline = new google.maps.Polyline(polyOptions);
    aPolyline.setMap(SemantEcoUI.map);

    // Build listners for our polyline
    addPolygonDrawListeners(aPolyline);
}


// Brendan Edit:  Wait for the dom to load 
$(window).bind('initialize', function() {
    //Button Code by Irene Khan
    //Draw polygon button        
    var drawPolygonButtonDiv = document.createElement('div');
    var drawPolygonButton = new DrawPolygonButton(drawPolygonButtonDiv, SemantEcoUI.map);
    drawPolygonButtonDiv.index = 1;
    SemantEcoUI.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(drawPolygonButtonDiv);
});

function DrawPolygonButton(buttonDiv, map) {
    var drawMode = false;
    
    //CSS for div, border, and content
    buttonDiv.style.padding = '5px';
    buttonDiv.style.width = '100px';
    var buttonUI = document.createElement('div');
    buttonUI.style.borderRadius = '10px';
    buttonUI.style.backgroundColor = 'white';
    buttonUI.style.borderStyle = 'solid';
    buttonUI.style.borderWidth = '2px';
    buttonUI.style.cursor = 'pointer';
    buttonUI.style.textAlign = 'center';
    buttonUI.title = 'Click to draw a polygon';
    buttonDiv.appendChild(buttonUI);
    var buttonText = document.createElement('div');
    buttonText.style.fontFamily = 'Arial,sans-serif';
    buttonText.style.fontSize = '12px';
    buttonText.style.padding = '4px';

    buttonText.innerHTML = '<b>Draw&nbsp;Polygon</b>';
    buttonUI.appendChild(buttonText);

    //Click event listener - on click, call startDrawingPolygon()
    google.maps.event.addDomListener(buttonUI, 'click', function() {
        if ( !drawMode ) {
            drawMode = true;
            startDrawingPolygon()
            buttonUI.style.backgroundColor = 'lightgray';
            buttonText.innerHTML = '<b>Stop&nbsp;Drawing</b>';
            buttonUI.appendChild(buttonText);
        } else {
            buttonUI.style.backgroundColor = 'white';
            removePolygonDrawListeners();
            aPolyline.setMap(null);
            drawMode = false;
            buttonText.innerHTML = '<b>Draw&nbsp;Polygon</b>';
            buttonUI.appendChild(buttonText);
        }
    });
}

