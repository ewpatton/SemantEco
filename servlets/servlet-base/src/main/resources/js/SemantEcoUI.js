var SemantEcoUI = {
    //store all reference to markers
    "markers": [],
    "markersByUri": {},

    //initializing the map the element such as infowindow and content containers in infowindow
    "configureMap": function() {
        var mapOptions = {
                center: new google.maps.LatLng(37.6058, -98.8641),
                zoom: 4,
                mapTypeId: google.maps.MapTypeId.ROADMAP,
                zoomControl: true,
                panControl: true
            };
        //create the actuall google map using the configuratin above and reference it in the semantecoUI object
        SemantEcoUI.map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
        SemantEcoUI.geocoder = new google.maps.Geocoder();

         //a set of elements that will be used on infowindow, there are three of them, the infowindowcontent and infowindowcontrol is inside the infowindowcontainer. the infowindowcontainer is the content on the infowindow
        SemantEcoUI.infowindowcontainer = document.createElement('div');
        $(SemantEcoUI.infowindowcontainer).attr("id","infowindowcontainer");
        SemantEcoUI.infowindowcontent = document.createElement('div');
        $(SemantEcoUI.infowindowcontent).attr("id","infowindowcontent");
        $(SemantEcoUI.infowindowcontent).appendTo(SemantEcoUI.infowindowcontainer);
        SemantEcoUI.infowindowcontrol = document.createElement('div');
        $(SemantEcoUI.infowindowcontrol).attr("id","infowindowcontrol");
        $(SemantEcoUI.infowindowcontrol).appendTo(SemantEcoUI.infowindowcontainer);

        //create the infowindow, there will be only one infowindow object. it will be used again and again.
        SemantEcoUI.infowindow=new google.maps.InfoWindow({
            content:SemantEcoUI.infowindowcontainer
        });
        SemantEcoUI.infowindow.addListener("closeclick", function() {
            $.bbq.removeState("uri");
        });

        //the function below is listening on show-marker-info event (after a user clicks on the site), the event is being generated in regulation.js file
        //more than just an event, the trigger also passed a parameter(marker) when it trigger the event
        //also, at this pop-infowindow event, all code for generate elements, such as leftcolumngenerater, rightcolumngenerater, chargenerator, on the lightbox are ready too
        //marker is the object from google maps
        //Evan put some non-google data into the marker: isWater and isAir.
        $(window).on("pop-infowindow",function(event,marker){

            console.log("pop-infowindow");
            
            //open the infowindow, which is created once at the top.
            SemantEcoUI.infowindow.open(SemantEcoUI.map,marker);
            //all container in infowindow is cleared and new data is being put into them everytime a marker is clicked
            $(SemantEcoUI.infowindowcontent).html(marker.tabledata);
            $(SemantEcoUI.infowindowcontrol).html("");
            // if($(".characteristics").length != 0){
            
            //three function pointer will be used later
            leftcoloumgenerater=null;
            rightcolumngenerater=null;
            chartgenerator=null;

            //the processing diverge at here, 
               //for bird data, because the data format is differnt from water/air data, it has a different set of processing code, which is in the else code section
            if(marker.data.isWater || marker.data.isAir){
                if(true){
                    $(SemantEcoUI.infowindowcontrol).html("<a>Chart for all measurements for this site</a><br /><a>Chart for all measurements for this site with nearby species count</a>");
                }
                //left column on the lightbox
                //leftcolumn is the column that contains the dropdown selection and the chart
                leftcoloumgenerater=function(){
                    //show lightbox loading spinner
                    $(".lb_loading").show();
                    //create needed elements
                    var leftcolumn=$(document.createElement('div')).addClass("leftcolumn");
                    var selectscontainer=$(document.createElement('div')).addClass("selectscontainer").appendTo(leftcolumn);
                    $(document.createElement('div')).attr("id","lightboxchart").appendTo(leftcolumn);

                    //this is the select element inside the container, and so is appended
                    var selectforcharacteristic = $('<select id="selectforcharacteristic" class="selects" />').appendTo(selectscontainer);
                    //the default empty characteristic, they have to select a characteristic first manually.
                    $("<option />", {value: "", text: ""}).appendTo(selectforcharacteristic);
                    
                    //get all available characterstic for a certain site by a ajax call
                    CharacteristicsModule.getCharacteristicsForSite({}, function(data) { 
                        $(".lb_loading").hide();
                        data=JSON.parse(data);
                        data=data.results.bindings;

                        //this part of code take out all dupicates (should be done at backend in long term)
                        for(var i=0;i<data.length;i++){
                            var uri=data[i]["element"]["value"];
                            //there was no ontology for characteristic yet, so only the id was passed.
                            var label=uri.substr(uri.indexOf("#")+1).replace(/_/g," ");
                            var ifexist=false;
                            if($(selectforcharacteristic).children().each(function(){
                                if($(this).html()==label){
                                    ifexist=true;
                                }
                            }));
                            //this deals with duplicate characteristics
                            if(!ifexist){
                                $("<option />", {value: uri, text: label}).addClass("characteristics").appendTo(selectforcharacteristic);
                            }
                        }


                    });

                    //this is the selection for the test. this is the same container as you added characteristics selection
                    var selectfortest = $('<select id="selectfortest" class="selects" />').hide().appendTo(selectscontainer);

                    //disable the sumbit button if user didn't select a test if tests exist
                    //appending to select container a disabled submit button.
                    var characteristicssubmit=$('<input type="submit" class="characterssubmit" />').attr("disabled", "disabled").appendTo(selectscontainer);
                    

                    //this section is another ajax call to get all tests for a charecteristic after a user select charecteristic(however, the test is not being used on server side)
                    //so this action is executed as soon as a user finishing choicing one selection of a characteristic from the dropdown menu
                    $(selectforcharacteristic).change(function() 
                    { 
                 
                        var value = $(selectforcharacteristic).val(); 
                        CharacteristicsModule.getTestsForCharacteristic({
                            //Evan's getTestsForCharacteristic method is expecting viaulizeCharacteric object name. (check with evan)
                            "visualizedCharacteristic":$(selectforcharacteristic).val()
                        }, function(d) {
                            //every d=JSON.parse(d); is to parse the json string to json
                            d=JSON.parse(d);
                            console.log(d);
                            //if there are tests for the selected 
                            if (d.length!=0){
                                $(selectfortest).empty().show();
                                for(var i=0;i<d.length;i++){
                                    $("<option />", {value: d[i], text: d[i]}).addClass("tests").appendTo(selectfortest);
                                }
                                 $(characteristicssubmit).removeAttr("disabled");  
                            }
                            else{
                                $(selectfortest).empty().hide();
                                 $(characteristicssubmit).removeAttr("disabled");  
                            }
                         });
                    }); 
                    //return the leftcolumn to caller, who will put the element onto dom
                    //left column is the left holding all the selectors
                    return leftcolumn;
                };

                //this column is for the tree
                rightcolumngenerater=function(){
                    var rightcolumn=$(document.createElement('div')).addClass("rightcolumn");
                    var specietree = $("<div></div>").addClass("hierarchy").appendTo(rightcolumn);
                    SemantEcoUI.HierarchicalFacet.create(specietree, SpeciesDataProviderModule, "queryeBirdTaxonomyHM", "species");
                    /*
                    var specietree=$(document.createElement('div')).addClass("specietree").html('<div class="hierarchy_wrapper"><input type="text" name="search" id="search_info_map" style="overflow:hidden;padding:0;resize:none;" placeholder="Type message here!" autocomplete="off" /><input type=button onClick=" search_node1()" value="search" id="append_map" style="position: relative;"/><div id="show_map"></div><div id="description_map" style=" border:1px solid #111111; overFlow: auto;  " ><div id="tree_map" class="jstree" style="width:100%;height:100px;"></div></div></div>').appendTo(rightcolumn);
                    specietree.keyup(keyUp1);
                    SpeciesDataProviderModule.queryeBirdTaxonomyRoots({}, function (data){
                        jsonHier2=JSON.parse(data);
                        jsonHier2=jsonHier2["data"];
                        initial_hierachy1();                                     
                           });
                    */
                    
                    return rightcolumn;
           
                };

                //this is the chart generater, it gets raw input, which is "binding"s in the returned data, and turn them into data format can be used by jqplot
                chartgenerator=function(measurementData,nearbySpeciesData){
                    var chartdata=[];
                    /*
                    
                    var chartseries1=[];
                    
                    var limitThreshold=[];
                    var limitThresholdValue="";
                    */
                    var unit=measurementData[0].unit.value || "";

                    // aggregate measurements based on URI (e.g. lower and upper
                    // bound generate two entries per measurement)
                    var measurements = {};
                    for(var i=0;i<measurementData.length;i++) {
                        var uri = measurementData[i].measurement.value;
                        if(measurements[uri] == undefined) {
                            measurements[uri] = [];
                        }
                        measurements[uri].push(measurementData[i]);
                    }
                    var limits = [[], []];
                    var values = [];
                    for(var uri in measurements) {
                        // time will be the same in each entry, so use the first
                        var time = measurements[uri][0].time.value.substring(0,10);
                        if(time.length == 8) {
                            time = time.substr(0,4)+"-"+time.substr(4,2)+"-"+time.substr(6,2);
                        }
                        var mLimits = [];
                        for(var i=0; i<measurements[uri].length; i++) {
                            mLimits.push(measurements[uri][i].limit.value);
                        }
                        mLimits.sort();
                        for(var i=0;i<mLimits.length;i++) {
                            limits[i].push([time, Math.round( mLimits[i] * 100 ) / 100]);
                        }
                        values.push([time, Math.round( measurements[uri][0].value.value*100 )/100]);
                    }
                    /*
                    for(var i=0;i<measurementData.length;i++) {
                        var time = measurementData[i].time.value.substring(0,10);
                        if(time.length == 8) {
                            time = time.substr(0,4)+"-"+time.substr(4,2)+"-"+time.substr(6,2);
                        }
                        chartseries1.push([time,Math.round( measurementData[i].value.value*100 )/100]);
                        if(measurementData[i].limit){
                            limitThreshold.push([time,Math.round( measurementData[i].limit.value*100 )/100]);
                        }
                    }
                    */
                    //push the processed data to the chartdata, which a data array will be used as input for jqplot
                    chartdata.push(values);
                    //if exists limit, then push to chartdata to plot limit as an aditional series
                    /*
                    if(limitThreshold.length!=0 && measurementData[0].limit != undefined){
                        limitThresholdValue=measurementData[0].limit.value;
                        chartdata.push(limitThreshold);
                    }*/
                    if(limits[0].length > 0) {
                        chartdata.push(limits[0]);
                    }
                    if(limits[1].length > 0) {
                        chartdata.push(limits[1]);
                    }

                    //aggregating species
                    //the returned data for speices is not agreegated, agregate them by species
                    var speciesnames=[];
                    var speciessobj={};
                    for(var i=0;i<nearbySpeciesData.length;i++){
                        if(!speciessobj[nearbySpeciesData[i]["commonName"]["value"]]){
                            speciessobj[nearbySpeciesData[i]["commonName"]["value"]]=[];
                            speciesnames.push(nearbySpeciesData[i]["commonName"]["value"]);
                        }
                        speciessobj[nearbySpeciesData[i]["commonName"]["value"]].push([nearbySpeciesData[i].date.value,Math.round( nearbySpeciesData[i].count.value )]);
                    }

                    var label = $("#selectforcharacteristic option:selected").html();
                    //the series for the characteristic
                    //after preparing the actually data, start to initial the series
                    var series=[];
                    series.push({
                        label:label
                        ,yaxis:'yaxis'
                    });

                    //the series for the limit for characteristic if limit exists
                    /*
                    if(limitThreshold.length!=0){
                        series.push({
                            label:$("#selectforcharacteristic option:selected").html()+" Threshold Limit ("+limitThresholdValue+")"
                            ,yaxis:'yaxis'
                            ,showMarker: false
                            ,highlighter:{
                                show:false
                                ,bringSeriesToFront:false
                            }
                        });
                    }
                    */
                    for(var i=0;i<limits.length;i++) {
                        if(limits[i].length > 0) {
                            series.push({
                                label:label+" Threshold Limit ("+limits[i][0][1]+")"
                                ,yaxis:'yaxis'
                                ,showMarker: false
                                ,highlighter:{
                                    show:false
                                    ,bringSeriesToFront:false
                                }
                            })
                        }
                    }
                    
                    //based on numbers, dinymically put species series into the series object, which will be used when initializing the chart
                    for(var i=0;i<speciesnames.length;i++){
                        chartdata.push(speciessobj[speciesnames[i]]);
                        series.push({
                                label:speciesnames[i],yaxis:'y2axis'
                            });
                    }

                    //the code above genrated the actual data and controlling information about series, 
                    //now initializing the chart, feeding relative data gnerated above
                    //all detailed configuration can be found at jqplot website
                    var jqplot = $.jqplot("lightboxchart", chartdata, {
                        title:marker.data.label ? marker.data.label.value:""
                        ,seriesDefaults: {
                            lineWidth:2
                            ,markerOptions: {
                                size:7
                            }
                        }
                        ,series:series
                        ,axes:{
                            xaxis:{
                                renderer:$.jqplot.DateAxisRenderer,
                                tickOptions:{
                                    formatString:'%Y-%m-%d'
                                } 
                            },
                            yaxis:{
                                tickOptions:{
                                    //show two decimals
                                    formatString:'%.2f'
                                },
                            },
                            y2axis:{
                                tickOptions:{
                                    //show 1 decimal
                                    formatString:'%.1f'
                                    ,showGridline:false
                                },
                                autoscale:true
                            }
                        }
                        ,legend: { 
                            show:true, 
                            location: 'se',
                            // placement: 'outside',
                            rendererOptions: {numberColumns: 2},
                            marginTop:30
                        }
                        ,highlighter: {
                            show: true,
                            showTooltip:true,
                            bringSeriesToFront:true
                        }
                        
                        ,cursor: {
                          show: true,
                          zoom:true,
                          intersectionThreshold :5,
                          showHorizontalLine:false,
                          showTooltip:true,
                          followMouse:true,
                          // showVerticalLine:true,
                          // tooltipLocation:'sw'
                        }
                    });
                    jqplot.replot( { resetAxes: true } );
                    //resize the chart when the window size changed
                    $(window).resize(function(){
                        jqplot.replot( { resetAxes: true } );
                    });
                };

                //this is when the first link, "chart data got clicked"
                $($("#infowindowcontrol a").get(0)).click(function(){

                    //generate only left part of content
                    leftcoloumgenerater().appendTo(".lb_content");
                    $(".lightbox .lb_container").css({"width":"60%"});
                    $(".leftcolumn").css({"width":"100%"});
                    SemantEcoUI.lightbox.show();

                    //the listener of when submit button is clicked
                    $(".characterssubmit").click(function(e){

                        if($("#selectfortest").val()){
                            $.bbq.pushState({"TestsForCharacteristic":$("#selectfortest").val()});
                        }
                        $.bbq.pushState({"characteristic":$("#selectforcharacteristic").val()});
                        $("#lightboxchart").empty();
                        $(".lb_loading").show();

                        //query for site measurement and push returning data to chartgenerator
                        function queryForSiteMeasurementsCallback(data){
                            $(".lb_loading").hide();
                            console.log("queryForSiteMeasurementsCallback. Data(below):");
                            data=JSON.parse(data);
                            console.log(data);
                            mesurementData=data.results.bindings;
                            chartgenerator(mesurementData,[]);
                        }

                        CharacteristicsModule.queryForSiteMeasurements({},queryForSiteMeasurementsCallback);
                    
                    });

                });

                //this is when the second link "chart data with species data is clicked"
                $($("#infowindowcontrol a").get(1)).click(function(){
                    
                    //genreate both columns, the right one is controling the dropdowns, the left one is for the tree
                    leftcoloumgenerater().appendTo(".lb_content");
                    rightcolumngenerater().appendTo(".lb_content");
                    $(".lightbox .lb_container").css({"width":"70%"});
                    SemantEcoUI.lightbox.show();


                    $(".characterssubmit").click(function(e){

                        //the listener of when characteristic dropdown menu is clicked
                        $.bbq.pushState({"characteristic":$("#selectforcharacteristic").val()});
                        // console.log($.bbq.getState("characteristic"));

                        
                        if(!$.bbq.getState("species")){
                            alert("Please select species on the right");
                            return false;
                        }

                        $("#lightboxchart").empty();
                        $(".lb_loading").show();

                        
                        //when user want to chart data, there are two steps to get all needed data
                        //first, gets site measurement data, then get species data
                        //using javascript's characteristic, the inner function can get outer function's local data without any explicit passing process
                        function queryForSiteMeasurementsCallback(data){
                            console.log("queryForSiteMeasurementsCallback");
                            console.log("data:"+data);
                            data=JSON.parse(data);
                            mesurementData=data.results.bindings;
                            
                            
                        
                            function queryForNearbySpeciesCountsCallback(data2){

                                $(".lb_loading").hide();

                                console.log("queryForNearbySpeciesCountsCallback");

                                if(data2 && (JSON.parse(data2)).results.bindings.length!=0){
                                    data2=JSON.parse(data2);
                                    var nearbySpeciesData=data2.results.bindings;
                                    chartgenerator(mesurementData,nearbySpeciesData);
                                }
                                else{
                                    console.log("Empty data from queryForNearbySpeciesCounts");
                                    
                                    //if the the returning species data is empty, then there is another call if check if sibling species data exists
                                    function queryIfSiblingsExistCallback(data){

                                        data=JSON.parse(data);
                                        data=data.data;
                                        var species=[];
                                        var confirmtext="There are no results for the selected species, would you like to see data for: \n";
                                        if(UITeamUtilities.fakedata){
                                            confirmtext="You attempted to plot bird count data for the species \"Bare-legged Owl\", but none is available. There is  however Bird count data on other species of in family “Owl”, including \"Bare-shanked Screech Owl\" and \"Brown-Fish Owl\"";
                                        }
                                        for(var i=0;i<data.length;i++){
                                            species.push(data[i]["sibling"]);
                                            confirmtext+=data[i]["commonName"]+"\n";
                                        }
                                        console.log("returned sibling species : "+species);
                                        var con=confirm(confirmtext);
                                        if(con){
                                            $.bbq.pushState({"species":species});
                                            console.log("this is getState after push species to state");
                                            console.log($.bbq.getState("species"));

                                            SpeciesDataProviderModule.queryForNearbySpeciesCounts({},queryForNearbySpeciesCountsCallback);
                                            return false;
                                        }
                                        else{
                                            chartgenerator(mesurementData,[]);
                                        }
                                    };
                        //after defining the callback functions, there  are the real calls.
                                    SpeciesDataProviderModule.queryIfSiblingsExist({}, queryIfSiblingsExistCallback);
                                }

                            }

                            SpeciesDataProviderModule.queryForNearbySpeciesCounts({},queryForNearbySpeciesCountsCallback);


                        }

                        CharacteristicsModule.queryForSiteMeasurements({},queryForSiteMeasurementsCallback);

                    });

                
                });
            }
            else if(marker.data.isBird)
            //this part is for the species data, which is different from water/air data, then needs special process
            //the structure is the same as water/air data above, however, it is simpler.
            {

                $(SemantEcoUI.infowindowcontrol).html("<a>Chart for all data for this site</a>");
                
                //there is only one column, no right column
                leftcoloumgenerater=function(){
                    $(".lb_loading").show();
                    var leftcolumn=$(document.createElement('div')).addClass("leftcolumn");
                    $(document.createElement('div')).attr("id","lightboxchart").appendTo(leftcolumn);                

                    return leftcolumn;
                };


                chartgenerator=function(speciesData){
                    var chartdata=[];
                    

                    var speciesnames=[];
                    var speciessobj={};
                    for(var i=0;i<speciesData.length;i++){
                        if(!speciessobj[speciesData[i]["scientific_name"]["value"]]){
                            speciessobj[speciesData[i]["scientific_name"]["value"]]=[];
                            speciesnames.push(speciesData[i]["scientific_name"]["value"]);
                        }
                        var value=Math.round( speciesData[i].count.value );
                        speciessobj[speciesData[i]["scientific_name"]["value"]].push([speciesData[i].date.value,value]);
                    }
                    console.log(speciessobj);
                    console.log(speciesnames);

                    var series=[];
                    for(var i=0;i<speciesnames.length;i++){
                        chartdata.push(speciessobj[speciesnames[i]]);
                        series.push({
                                label:speciesnames[i]
                                ,yaxis:'yaxis'
                            });
                    }

                    console.log(chartdata);

                    var jqplot = $.jqplot("lightboxchart", chartdata, {
                        title:marker.data.label.value
                        ,seriesDefaults: {
                            lineWidth:2
                            ,markerOptions: {
                                size:7
                            }
                        }
                        ,series:series
                        ,axes:{
                            xaxis:{
                                renderer:$.jqplot.DateAxisRenderer,
                                tickOptions:{
                                    formatString:'%Y-%m-%d'
                                } 
                            },
                            yaxis:{
                                tickOptions:{
                                    formatString:'%.1f'
                                    // ,showGridline:false
                                }
                                ,autoscale:true
                            }
                        }
                        ,legend: { 
                            show:true, 
                            location: 'se',
                            placement: 'outside',
                            rendererOptions: {numberColumns: 2},
                            marginTop:30
                        }
                        ,highlighter: {
                            show: true,
                            showTooltip:true,
                            bringSeriesToFront:true
                        }
                        
                        ,cursor: {
                          show: false,
                          intersectionThreshold :5,
                          showHorizontalLine:true,
                          showTooltip:true,
                          followMouse:true,
                        }
                    });
                    jqplot.replot( { resetAxes: true } );
                    $(window).resize(function(){
                        jqplot.replot( { resetAxes: true } );
                    });
                };

                $($("#infowindowcontrol a").get(0)).click(function(){

                    leftcoloumgenerater().appendTo(".lb_content");
                    $(".lightbox .lb_container").css({"width":"60%"});
                    $(".leftcolumn").css({"width":"80%"});
                    SemantEcoUI.lightbox.show();

                    $("#lightboxchart").empty();
                    $(".lb_loading").show();

                    function queryForSpeciesForASiteCallback(data){
                        $(".lb_loading").hide();
                        console.log("queryForSpeciesForASiteCallback. Data(below):");
                        data=JSON.parse(data);
                        console.log(data);
                        speciesData=data.results.bindings;
                        chartgenerator(speciesData,[]);
                    }

                    SpeciesDataProviderModule.queryForSpeciesForASite({},queryForSpeciesForASiteCallback);


                });

            }
        });
    },
    //decode zip code and center the map
    "doGeocode": function(zip) {
        console.trace();
        if(SemantEcoUI.geocoder) {
            SemantEcoUI.geocoder.geocode({"address":zip, "region":"US"},
                function(pt) {
                    console.log(pt);
                    if(!pt) {
                        alert(zip + " not found");
                    }
                    else {
                        SemantEcoUI.map.setCenter(pt[0].geometry.location);
                        SemantEcoUI.map.setZoom(10);
                    }
                });
        }
        else {
            console.log("SemantEco.geocoder is null");
        }
    },
    "getState": function() {
        return $.extend({}, $.bbq.getState(), SemantEcoUI.getFacetParams());
    },

    //these two function below is controlling facet bar on the side by intercting with the bbq
    "getFacetParams": function() {
        var params = {};
        $("div#facets .facet").each(function() {
            var that = $(this);
            if(that.hasClass("unmanaged")) {
                return;
            }
            $("input[name]", that).each(function() {
                var type = this.getAttribute("type").toLowerCase();
                var name = this.getAttribute("name");
                if(type == "checkbox") {
                    if(typeof params[name] == "undefined") {
                        params[name] = [];
                    }
                    if(this.checked) {
                        params[name].push(this.value);
                    }
                }
                else if(type == "radio") {
                    if(this.checked) {
                        params[name] = this.value;
                    }
                }
                else if(type == "text") {
                    params[name] = this.value;
                }
                // else {
                //     console.warn("Facet "+that.getAttribute("id")+
                //             " uses input type "+type+
                //             " which is not supported.");
                // }
            });
            $("select", that).each(function() {
                var name = this.getAttribute("name");
                params[name] = this.value;
            });
        });
        return params;
    },
    "populateFacets": function() {
        var params = $.bbq.getState();
        for(var param in params) {
            var inputs = $("div.facet:not([class ~= 'unmanaged']) *[name='"+param+"']");
            if(inputs.length > 0) {
                if(inputs[0].tagName == "INPUT") {
                    var type = inputs[0].type.toLowerCase();
                    if(type == "checkbox") {
                        for(var i=0;i<inputs.length;i++) {
                            if($.inArray($(inputs[i]).val(), params[param])>=0) {
                                console.debug("Setting "+$(inputs[i]).val()+" to true");
                                inputs[i].checked=true;
                            }
                            else {
                                console.debug("Setting "+$(inputs[i]).val()+" to false");
                                inputs[i].checked=false;
                            }
                        }
                    }
                    else if(type == "radio") {
                        
                    }
                    else if(type == "text") {
                        console.debug("Setting "+$(inputs[i]).attr("name")+" to "+params[param]);
                        inputs[0].value = params[param];
                    }
                }
                else {
                    console.debug("Setting "+$(inputs[0]).attr("name")+" to "+params[param]);
                    inputs[0].value = params[param];
                }
            }
        }
    },
    "initializeFacets": function() {
        var facets = $("div#facets .facet").each(function() {
            var that = this;
            if($(that).hasClass("unmanaged")) {
                return;
            }
            $("input", this).change(function(e) {
                var me = $(this);
                var name = me.attr("name");
                var type = me.attr("type");
                if(type=="checkbox") {
                    var elems = $("input[name='"+name+"']:checked", that);
                    var value = [];
                    elems.each(function() {
                        value.push($(this).val());
                    });
                    var state = {};
                    state[name] = value;
                    $.bbq.pushState(state);
                }
                else if(type=="radio") {
                    var value = $("input[name='"+name+"']:checked", that).val();
                    var state = {};
                    state[name] = value;
                    $.bbq.pushState(state);
                } else if(type=="text") {
                    var value = me.val();
                    var state = {};
                    state[name] = value;
                    $.bbq.pushState(state);
                }
            });
            $("select", this).change(function(e) {
                var me = $(this);
                var name = me.attr("name");
                var value = $("option:selected",this).val();
                var state = {};
                state[name] = value;
                $.bbq.pushState(state);
            });
        });
        $("div#facets").sortable({axis:"y", items: "table", handle:"th",
            placeholder:"ui-state-highlight"}).find("th").disableSelection();
    },
    "createMarker": function(uri, lat, lng, icon, visible, label) {
        var opts = {"clickable": true,
                "icon": new google.maps.MarkerImage(icon, null, null, null, new google.maps.Size(30, 34)),
                "title": label,
                "position": new google.maps.LatLng(lat, lng),
                "visible": visible
                };
        return new google.maps.Marker(opts);
    },
    //add markers to the map that is created in the configure function above
    "addMarker": function(marker) {
        var uri = marker.data["site"].value;
        SemantEcoUI.markers.push(marker);
        SemantEcoUI.markersByUri[uri] = marker;
        marker.setMap(SemantEcoUI.map);

        google.maps.event.addListener(marker, "click",function() {
            SemantEco.action = SemantEcoUI.handleClickedMarker;
            $.bbq.pushState({"uri": uri});
            //trigger the show-marker-info event and pass the coresponding marker to tell where the infowindow should show
            //$(window).trigger('show-marker-info',marker);  
        });
    },

    //simply return all referece of markers stored in the semanteco object itself.
    "getMarkers": function() {
        return SemantEcoUI.markers;
    },
    "getMarkerForUri": function(uri) {
        return SemantEcoUI.markersByUri[uri];
    },
    "clearMarkers": function() {
        for(var i=0;i<SemantEcoUI.markers.length;i++) {
            SemantEcoUI.markers[i].setMap(null);
        }
        SemantEcoUI.markers = [];
    },
    "showMarker": function(marker) {
        marker.setVisible(true);
    },
    "hideMarker": function(marker) {
        marker.setVisible(false);
    },
    "handleClickedMarker": function() {
        SemantEco.action = null;
        $(window).trigger('show-marker-info');
    },
    "focusMap": function() {
        if(SemantEcoUI.markers.length == 0) {
            return;
        }
        var latlng = SemantEcoUI.markers[0].getPosition();
        var bounds = new google.maps.LatLngBounds(latlng, latlng);
        for(var i=1;i<SemantEcoUI.markers.length;i++) {
            bounds.extend(SemantEcoUI.markers[i].getPosition());
        }
        SemantEcoUI.map.fitBounds(bounds);
        if(SemantEcoUI.map.getZoom() > 15) {
            SemantEcoUI.map.setZoom(15);
        }
    },
    "showSpinner": function() {
        $("#spinner").css("display", "block");
    },
    "hideSpinner": function() {
        $("#spinner").css("display", "none");
        $.bbq.removeState("action");
    }
};

//the lightbox system (fake window)
$(document).ready(function(){
    $.globalEval("var lightbox={};");
    //define lightbox as part of semantecoUI
    SemantEcoUI.lightbox={};
    lightbox=SemantEcoUI.lightbox;
    lightbox.clean=function(){
        //when lightbox closes clean the resize bind of the chart
        $(window).unbind("resize");
    };

    lightbox.init=function(){
        $(".lb_content").click(function(e){
            e.stopPropagation();
        });

        $(".lightbox .lb_container").click(function(e){
            e.stopPropagation();
        });

        $(".lightbox .lb_shadow,.lightbox .lb_closebutton").click(function(){
            $(".lb_content").empty();
            if(lightbox.clean){
                lightbox.clean();
            }
            $(".lightbox").fadeOut(300,function(){
                $("body").css({overflow:"auto"});
                $("lb_loading").hide();
            });
        });
    };

    lightbox.show=function(){
        $(".lightbox").show();
        $("body").css({overflow:"hidden"});
        $(".lightbox .lb_container").fadeIn(500,function(){
        });
    };

    lightbox.init();
});

// ewpatton: hacks to fix Chen's code quickly. must be rewritten to satisfy issue #8
var class_hierarchy_temp2 = null;
var class_hierarchy_map2 = null;

function initial_hierachy1(){
	class_hierarchy_temp2 = new Array();
	class_hierarchy_map2 = new Array();
    var flag=0;
    for (var i=0;i<jsonHier2.length;i++){
        flag=0;
        for (var j=0;j<jsonHier2.length;j++){
            temp1=jsonHier2[i]["parent"].indexOf("#");
            if(jsonHier2[i]["parent"].substring(temp1+1)==jsonHier2[j]["label"]){
                flag=1;
                break;
            }
        }
        if(flag==0){
            var flag1=0;
            for (var k=0;k<class_hierarchy_temp2.length;k++){
                if(class_hierarchy_temp2[k][0]==jsonHier2[i]["parent"].substring(temp1+1)){
                    flag1=1;
                    break;
                }
        
            }
            if(flag1==0){
            	class_hierarchy_temp2.push(new Array(jsonHier2[i]["parent"].substring(temp1+1),null,null));
            	class_hierarchy_map2.push(new Array(jsonHier2[i]["parent"].substring(temp1+1),null,null));
            }
        }
    }
    
    for (var j=0;j<class_hierarchy_temp2.length;j++){
         for (var i=0;i<jsonHier2.length;i++){
                 var temp=jsonHier2[i]["parent"].indexOf("#");             
                if(jsonHier2[i]["parent"].substring(temp+1)==class_hierarchy_temp2[j][0]){
                    class_hierarchy_map2.push(new Array(jsonHier2[i]["label"],jsonHier2[i]["parent"].substring(temp+1),jsonHier2[i]["id"]));
                    
                    //jsonHier2.remove(i);
                }
         }
        
    }
    
    
    
    var temp_div=document.getElementById('tree_map');
    temp_div.innerHTML="";
    
    for (var i=0;i<class_hierarchy_temp2.length;i++){
        var ul=document.createElement("ul");
        var li=document.createElement("li");
        var a=document.createElement("a");
        a.href="#";
        var text=document.createTextNode(class_hierarchy_temp2[i][0]);
        var temp_root="map"+i;
        li.id=temp_root;
        a.appendChild(text); 
        li.appendChild(a); 
        ul.appendChild(li); 
        temp_div.appendChild(ul); 
        document.getElementById('description_map').appendChild(temp_div);
            //alert("success");
        
        var j;
        for ( j=class_hierarchy_temp2.length;j<class_hierarchy_map2.length;j++){ 
            
            append_node1(j,temp_root);
        }
        
    }
    
    
    
    
    $(function () {
        $("#tree_map")
            .jstree({
                "themes" : {
                       "theme" : "default",
                       "dots" : true,
                      "icons" : true,
                      "url": "js/jstree/themes/default/style.css"
                    },

                 "plugins" : ["themes","html_data","ui"] })
                    // 1) if using the UI plugin bind to select_node
    
                    .bind("select_node.jstree", function (event, data) { 
                    // `data.rslt.obj` is the jquery extended node that was clicked
                        
                        
                        var temp=data.rslt.obj.attr("id");
                        var temp_id=parseInt(temp.substring(3));
                        //alert(class_hierachy[temp_id][0]);
                        //alert(class_hierachy[temp_id][1]);
                        //$.bbq.pushState({"species":class_hierachy[temp_id][2]});
                        $.bbq.pushState({"queryeBirdTaxonomySubClasses":class_hierarchy_map2[temp_id][2]});
                        ajax_node1();
                        getSelectedValue1();
                })
                    // 2) if not using the UI plugin - the Anchor tags work as expected
                    //    so if the anchor has a HREF attirbute - the page will be changed
            //    you can actually prevent the default, etc (normal jquery usage)
                    .delegate("a", "click", function (event, data) { event.preventDefault(); });
    });

}


/** This method */
function getSelectedValue1() {  
    var nodes = $.jstree._reference($("#tree_map")).get_selected();
    var temp=new Array();
    $.each(nodes, function(i, n) { 
         var temp_id=this.id;
         var temp_id1=parseInt(temp_id.substring(3));
         temp.push(class_hierarchy_map2[temp_id1][2]);
         
    }); 
    $.bbq.pushState({"species":temp});
}  

function append_node1(current, parent){
   var temp_judge=parent;
   temp_judge=parseInt(temp_judge.substring(3));
   if(class_hierarchy_map2[current][1]==class_hierarchy_map2[temp_judge][0]){
        var temp_div=document.getElementById(parent);
        var ul=document.createElement("ul");
        var li=document.createElement("li");
        var a=document.createElement("a");
        a.href="#";
        var text=document.createTextNode(class_hierarchy_map2[current][0]);
        var temp_id="map"+current.toString();
        li.id=temp_id;
        a.appendChild(text); 
        li.appendChild(a); 
        ul.appendChild(li); 
        temp_div.appendChild(ul); 
             //alert("success");
        /*var i=current+1;
        if (i<class_hierachy.length){
           for (var i=current+1;i<class_hierachy.length;i++){ 
                append_node1(i,"map"+current);
        }
           }*/
    }
}    

function ajax_node1() {
    SpeciesDataProviderModule.queryeBirdTaxonomySubClasses({}, function(data) {
        jsonHier2 = JSON.parse(data);
        jsonHier2 = jsonHier2["data"];
        var flag=0;
        var id=0;
        if (jsonHier2.length == 0) {
            //alert("null");
        } else {
            for ( var i = 0; i < jsonHier2.length; i++) {
                for ( var parent = 0; parent < class_hierarchy_map2.length; parent++) {
                    if (jsonHier2[i]["parent"] == class_hierarchy_map2[parent][2]) {
                        id="map"+parent;
                        var temp_div = document.getElementById("map"+parent);
                        var ul = document.createElement("ul");
                        var li = document.createElement("li");
                        var a = document.createElement("a");
                        a.href = "#";
                        var text = document.createTextNode(jsonHier2[i]["label"]);
                        li.id = "map"+class_hierarchy_map2.length;
                        a.appendChild(text);
                        li.appendChild(a);
                        ul.appendChild(li);
                        temp_div.appendChild(ul);
                        // alert("success");
                        class_hierarchy_map2.push(new Array(jsonHier2[i]["label"],jsonHier2[i]["parent"],jsonHier2[i]["id"]));
                        break;
                    }
                }
            }
        }
        var tree = jQuery.jstree._reference("#"+id);
        tree.refresh();
        document.getElementById(id).firstChild.click();
    });
}

function keyUp1(){
    var div1=document.getElementById('show_map');
    var temp =document.getElementById('search_info_map').value;
    var cprStr=temp;
    var show = [];
    if(event.keyCode==8) {
        if(cprStr.length==0) {
            $(div1).css("display", "none");
            return;
        }
    }
    for (i in class_hierarchy_map2) {
        if(cprStr==class_hierarchy_map2[i][0].substring(0,cprStr.length)) {
           show.push(class_hierarchy_map2[i][0]);
       }
    } 
    $(div1).css("display", show.length == 0 ? "none" : "block");
    div1.innerHTML="";
    htmlStr="";
    for (i in show){
        htmlStr+="<a style=\"cursor: pointer;\" onclick=\"choose(this)\">";
        htmlStr+=show[i];
        htmlStr+="</a>";
        htmlStr+="</br>";
    }
    div1.innerHTML+=htmlStr;
}

function choose1(str){
    var temp=str.childNodes[0].nodeValue;
    document.getElementById('search_info_map').value=temp;
    document.getElementById('show_map').innerHTML="";
    search_node1();
}

function search_node1(){
     var temp =document.getElementById('search_info_map').value;
     var flag=0;
     for (var i=0;i<class_hierarchy_map2.length;i++){
         if(temp==class_hierarchy_map2[i][0]){
            flag=1;
            var temp_id="map"+i;
            if(document.all){
                
                document.getElementById(temp_id).firstChild.click();
            }
            else{
                var evt = document.createEvent("MouseEvents");  
                     evt.initEvent("click", true, true);  
                      document.getElementById(temp_id).childNodes[1].dispatchEvent(evt);  
            }
            //alert(" found !");
            break;
        }
     }
     if(flag==0){
         alert(" not found !");
     }
     document.getElementById('search_info_map').value="";
     document.getElementById('show_map').innerHTML="";
     $("#show_map").css("display", "none");
}



function chartTests(){
    lightbox.show();
    var chartdata=[];
    chartdata.push(UITeamUtilities.markerdata[0]["visualize3"]);
    
    var testnames=[];
    var testsobj={};
    for(var i=0;i<UITeamUtilities.species.length;i++){
        if(!testsobj[UITeamUtilities.species[i][0]]){
            testsobj[UITeamUtilities.species[i][0]]=[];
            testnames.push(UITeamUtilities.species[i][0]);
        }
        testsobj[UITeamUtilities.species[i][0]].push([UITeamUtilities.species[i][1],UITeamUtilities.species[i][2]]);
    }
    console.log(testsobj);
    console.log(testnames);

    var series=[];
    series.push({
                label:"abc"
                ,yaxis:'yaxis',lineWidth:4
            });
    
    for(var i=0;i<testnames.length;i++){
        chartdata.push(testsobj[testnames[i]]);
        series.push({
                label:testnames[i],yaxis:'y2axis'
            });
    }

    console.log(chartdata);

    var plot5 = $.jqplot("id_lb_content", chartdata, {
        title:"abc"
        ,series:series
        ,axes:{
            xaxis:{
                renderer:$.jqplot.DateAxisRenderer,
                tickOptions:{
                    formatString:'%Y-%m-%d'
                } 
            },
            yaxis:{
                tickOptions:{
                    formatString:'%d'
                },
            },
            y2axis:{
                autoscale:true, 
                tickOptions:{showGridline:false}
            }
        }
        ,legend: { 
            show:true, 
            location: 'se',
            placement: 'outside',
            rendererOptions: {numberColumns: 2}
        }
        ,highlighter: {
            show: true,
            showTooltip:true
        }
        
        ,cursor: {
          show: false,
          intersectionThreshold :5,
          showHorizontalLine:true,
          showTooltip:true,
          followMouse:true,
          // showVerticalLine:true,
          // tooltipLocation:'sw'
        }
    });
}


