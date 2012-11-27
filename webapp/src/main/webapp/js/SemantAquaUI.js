var SemantAquaUI = {
	//store all reference to markers
	"markers": [],
	"markersByUri": {},

	//initializing the map the element such as infowindow and content containers in infowindow
	"configureMap": function() {
		var mapOptions = {
				center: new google.maps.LatLng(37.4419, -122.1419),
				zoom: 8,
				mapTypeId: google.maps.MapTypeId.ROADMAP,
				zoomControl: true,
				panControl: true
			};
		SemantAquaUI.map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
		SemantAquaUI.geocoder = new google.maps.Geocoder();

 
		SemantAquaUI.infowindowcontainer = document.createElement('div');
        $(SemantAquaUI.infowindowcontainer).attr("id","infowindowcontainer");
        SemantAquaUI.infowindowcontent = document.createElement('div');
        $(SemantAquaUI.infowindowcontent).attr("id","infowindowcontent");
        $(SemantAquaUI.infowindowcontent).appendTo(SemantAquaUI.infowindowcontainer);
        SemantAquaUI.infowindowcontrol = document.createElement('div');
        $(SemantAquaUI.infowindowcontrol).attr("id","infowindowcontrol");
        $(SemantAquaUI.infowindowcontrol).appendTo(SemantAquaUI.infowindowcontainer);
		SemantAquaUI.infowindow=new google.maps.InfoWindow({
            content:SemantAquaUI.infowindowcontainer
        });

		//the function below is listening on show-marker-info event,
		//more than just an event, the trigger also passed a parameter(marker) when it trigger the event
		//this is a really good jquery function
        $(window).on("pop-infowindow",function(event,marker){

        	console.log("pop-infowindow");
        	//copy user-data from marker to infowindow itself
        	//the user-data on marker of also get from somewhere else
            SemantAquaUI.infowindow.open(SemantAquaUI.map,marker);
            //all container in infowindow is cleared and new data is being put into them everytime a marker is clicked
            $(SemantAquaUI.infowindowcontent).html(marker.tabledata);
            $(SemantAquaUI.infowindowcontrol).html("");
            console.log($(".characteristics").length);
            if($(".characteristics").length != 0){
            	$(SemantAquaUI.infowindowcontrol).html("<a>Chart for all measurements for this site</a><br /><a>Chart for all measurements for this site with nearby species count</a>");
            }

            function leftcoloumgenerater(){
            	var leftcolumn=$(document.createElement('div')).addClass("leftcolumn");
            	var selectscontainer=$(document.createElement('div')).addClass("selectscontainer").appendTo(leftcolumn);
            	$(document.createElement('div')).attr("id","lightboxchart").appendTo(leftcolumn);

            	var selectforcharacteristic = $('<select id="selectforcharacteristic" class="selects" />').appendTo(selectscontainer);
    			$("<option />", {value: "", text: ""}).addClass("characteristics").appendTo(selectforcharacteristic);
            	$(".characteristics").each(function(){
            		var ifexist=false;
            		var self=this;
            		if($(selectforcharacteristic).children().each(function(){
            			if($(this).html()==$(self).html()){
            				ifexist=true;
            			}
            		}));
            		if(!ifexist){
            			$("<option />", {value: $(this).data("value"), text: $(this).html()}).addClass("characteristics").appendTo(selectforcharacteristic);
            		}
            	});
            	var selectfortest = $('<select id="selectfortest" class="selects" />').hide().appendTo(selectscontainer);

            	var characteristicssubmit=$('<input type="submit" class="characterssubmit" />').attr("disabled", "disabled").appendTo(selectscontainer);
            	
            	$(selectforcharacteristic).change(function() 
			    { 
			 
			        var value = $(selectforcharacteristic).val(); 
			        CharacteristicsModule.getTestsForCharacteristic({
			        	"visualizedCharacteristic":$(selectforcharacteristic).val()
			        }, function(d) {
			        	d=JSON.parse(d);
			        	console.log(d);
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

				return leftcolumn;
            };

            function rightcolumngenerater(){
            	var rightcolumn=$(document.createElement('div')).addClass("rightcolumn");
            	var specietree=$(document.createElement('div')).addClass("specietree").html('<div ><table cellpadding="0" cellspacing="0"><tr><td colspan="2"></td></tr><tr><td  ><div id="text_map"  ><textarea name ="search" id="search_info_map" style="overflow:hidden;padding:0 ;width:100px;height:25px;resize: none;"  placeholder="Type message here!" onKeyPress="press1(event)"></textarea></div><td style="width:20%" ><input type=button onClick=" search_node1()" value="search" id="append_map" style="position: relative;top: -10px;"/></td></td>         </tr><tr><td colspan="2" style="border-left:1px   solid   #111111;border-bottom:1px   solid   #111111;border-right:1px   solid   #111111;"><div id="show_map"></div></td></tr><tr><td colspan="2">       <div id="description_map" style=" border:1px solid #111111; overFlow: auto;  " ><div id="tree_map" class="demo" style="width:100%;height:100px;"></div></div></td></tr></table></div>').appendTo(rightcolumn);
            	SpeciesDataProviderModule.queryeBirdTaxonomy({}, function (data){
    	    		  // //jsonHier=JSON.parse(data);
            		initial_hierachy1();	 	          	    		  
    	               });
            	
            	
            	return rightcolumn;
       
            };

            function chartgenerator(mesurementData,nearbySpeciesData){
            	var chartdata=[];
            	
            	var chartseries1=[];
				for(var i=0;i<mesurementData.length;i++) {
					chartseries1.push([mesurementData[i].time.value,Math.round( mesurementData[i].value.value )]);
				}
				chartdata.push(chartseries1);

				var speciesnames=[];
				var speciessobj={};
				for(var i=0;i<nearbySpeciesData.length;i++){
					if(!speciessobj[nearbySpeciesData[i]["commonName"]["value"]]){
						speciessobj[nearbySpeciesData[i]["commonName"]["value"]]=[];
						speciesnames.push(nearbySpeciesData[i]["commonName"]["value"]);
					}
					speciessobj[nearbySpeciesData[i]["commonName"]["value"]].push([nearbySpeciesData[i].date.value,Math.round( nearbySpeciesData[i].count.value )]);
				}
				console.log(speciessobj);
				console.log(speciesnames);

				var series=[];
				series.push({
							label:$("#selectforcharacteristic option:selected").html()
							,yaxis:'yaxis',lineWidth:4
						});
				
				for(var i=0;i<speciesnames.length;i++){
					chartdata.push(speciessobj[speciesnames[i]]);
					series.push({
							label:speciesnames[i],yaxis:'y2axis'
						})
				}

				console.log(chartdata);

				var jqplot = $.jqplot("lightboxchart", chartdata, {
			        title:marker.data.label.value
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


				jqplot.replot( { resetAxes: true } );
				
				$(window).resize(function(){
	                jqplot.replot( { resetAxes: true } );
	            });
            }

            $($("#infowindowcontrol a").get(0)).click(function(){

				leftcoloumgenerater().appendTo(".lb_content");
				$(".lightbox .lb_container").css({"width":"60%"});
				$(".leftcolumn").css({"width":"100%"});
				SemantAquaUI.lightbox.show();

				$(".characterssubmit").click(function(e){

					if($("#selectfortest").val()){
						$.bbq.pushState({"TestsForCharacteristic":$("#selectfortest").val()});
					}
					$.bbq.pushState({"characteristic":$("#selectforcharacteristic").val()});
					$("#lightboxchart").empty();
					$(".lb_loading").show();

					function queryForSiteMeasurementsCallback(data){
						$(".lb_loading").hide();
						console.log("queryForSiteMeasurementsCallback");
						data=JSON.parse(data);
						console.log(data);
						mesurementData=data.results.bindings;
						chartgenerator(mesurementData,[]);
					}

					CharacteristicsModule.queryForSiteMeasurements({},queryForSiteMeasurementsCallback);
				
				});

            });

			$($("#infowindowcontrol a").get(1)).click(function(){
				
            	leftcoloumgenerater().appendTo(".lb_content");
            	rightcolumngenerater().appendTo(".lb_content");
            	$(".lightbox .lb_container").css({"width":"70%"});
				SemantAquaUI.lightbox.show();

				$(".characterssubmit").click(function(e){

					$.bbq.pushState({"characteristic":$("#selectforcharacteristic").val()});
					// console.log($.bbq.getState("characteristic"));

					
					if(!$.bbq.getState("species")){
						alert("Please select species on the left");
						return false;
					}

					$("#lightboxchart").empty();
					$(".lb_loading").show();

					

					function queryForSiteMeasurementsCallback(data){
						console.log("queryForSiteMeasurementsCallback");
						console.log("data:"+data);
						data=JSON.parse(data);
						mesurementData=data.results.bindings;
			            
			            //this part is only for development
			            if(UITeamUtilities.exampleSpecies){
			            	var tempcounty=$.bbq.getState("county");
			            	var tempstate=$.bbq.getState("state");
			            	var tempspecies=$.bbq.getState("species");
							$.bbq.pushState({"county":"019"});
							$.bbq.pushState({"state": "MD"});
							$.bbq.pushState({"species":["http://ebird#Megascops_asio","http://ebird#Strigidae"]});
			            }
						//
					
						function queryForNearbySpeciesCountsCallback(data2){

							//this part is only for development
							if(UITeamUtilities.exampleSpecies){
								$.bbq.pushState({"county":tempcounty});
								$.bbq.pushState({"state": tempstate});
								$.bbq.pushState({"species":tempspecies});
							}
							//

							$(".lb_loading").hide();

							console.log("queryForNearbySpeciesCountsCallback");
							console.log("data:"+data2);


							if(data2 && (JSON.parse(data2)).results.bindings.length!=0){
								data2=JSON.parse(data2);
								var nearbySpeciesData=data2.results.bindings;
								chartgenerator(mesurementData,nearbySpeciesData);
							}
							else{
								console.log("Empty data from queryForNearbySpeciesCounts");
								
								//to getting siblings we need to push this particular species
								$.bbq.pushState({"species":["http://ebird#Megascops_asio","http://ebird#Strigidae"]});
								
								console.log("states before calling queryIfSiblingsExist")
								console.log($.bbq.getState("state"));
								console.log($.bbq.getState("county"));
								console.log($.bbq.getState("species"));

								function queryIfSiblingsExistCallback(data){
									data=JSON.parse(data);
									console.log("returned species from queryIfSiblingsExist : "+data);
									console.log(data);
									data=data.data;
									var species=[];
									var confirmtext="No result for the selected species, would you want to see data for\n";
									for(var i=0;i<data.length;i++){
										species.push(data[i]["sibling"]);
										confirmtext+=data[i]["sibling"]+"\n";
									}

									console.log(species);
									if(species.length!=0){
										chartgenerator(mesurementData,[]);
										return false;
									}
									var con=confirm(confirmtext);
									if(con){
										console.log("yes I would");
										$.bbq.pushState({"species":species});
										console.log("this is after pushState");
										console.log($.bbq.getState("species"));
										SpeciesDataProviderModule.queryForNearbySpeciesCounts({},queryForNearbySpeciesCountsCallback);
										return false;
									}
									else{
										chartgenerator(mesurementData,[]);
									}
								};
								
								SpeciesDataProviderModule.queryIfSiblingsExist({}, queryIfSiblingsExistCallback);
							}

						}

						SpeciesDataProviderModule.queryForNearbySpeciesCounts({},queryForNearbySpeciesCountsCallback);


					}

					CharacteristicsModule.queryForSiteMeasurements({},queryForSiteMeasurementsCallback);

				});

	        
            });
            
        })
	},
	//decode zip code and center the map
	"doGeocode": function(zip) {
		console.trace();
		if(SemantAquaUI.geocoder) {
			SemantAquaUI.geocoder.geocode({"address":zip, "region":"US"},
				function(pt) {
					console.log(pt);
					if(!pt) {
						alert(zip + " not found");
					}
					else {
						SemantAquaUI.map.setCenter(pt[0].geometry.location);
						SemantAquaUI.map.setZoom(10);
					}
				});
		}
		else {
			console.log("SemantAqua.geocoder is null");
		}
	},
	"getState": function() {
		return $.extend({}, $.bbq.getState(), SemantAquaUI.getFacetParams());
	},

	//these two function below is controlling facet bar on the side by intercting with the bbq
	"getFacetParams": function() {
		var params = {};
		$("div#facets .facet").each(function() {
			var that = $(this);
			$("input", that).each(function() {
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
				// 	console.warn("Facet "+that.getAttribute("id")+
				// 			" uses input type "+type+
				// 			" which is not supported.");
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
			var inputs = $("*[name='"+param+"']");
			if(inputs.length > 0) {
				if(inputs[0].tagName == "INPUT") {
					var type = inputs[0].type.toLowerCase();
					if(type == "checkbox") {
						for(var i=0;i<inputs.length;i++) {
							if($.inArray($(inputs[i]).val(), params[param])>=0) {
								console.debug("Setting "+$(inputs[i]).val()+" to true")
								inputs[i].checked=true;
							}
							else {
								console.debug("Setting "+$(inputs[i]).val()+" to false")
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
		SemantAquaUI.markers.push(marker);
		SemantAquaUI.markersByUri[uri] = marker;
		marker.setMap(SemantAquaUI.map);

		google.maps.event.addListener(marker, "click",function() {
			SemantAqua.action = SemantAquaUI.handleClickedMarker;
			$.bbq.pushState({"uri": uri});
			//trigger the show-marker-info event and pass the coresponding marker to tell where the infowindow should show
			$(window).trigger('show-marker-info',marker);  
		});
	},

	//simply return all referece of markers stored in the semantAqua object itself.
	"getMarkers": function() {
		return SemantAquaUI.markers;
	},
	"getMarkerForUri": function(uri) {
		return SemantAquaUI.markersByUri[uri];
	},
	"clearMarkers": function() {
		for(var i=0;i<SemantAquaUI.markers.length;i++) {
			SemantAquaUI.markers[i].setMap(null);
		}
		SemantAquaUI.markers = [];
	},
	"showMarker": function(marker) {
		marker.setVisible(true);
	},
	"hideMarker": function(marker) {
		marker.setVisible(false);
	},
	"handleClickedMarker": function() {
		SemantAqua.action = null;
		$(window).trigger('show-marker-info');
	},
	"showSpinner": function() {
		$("#spinner").css("display", "block");
	},
	"hideSpinner": function() {
		$("#spinner").css("display", "none");
		$.bbq.removeState("action");
	}
};


$(document).ready(function(){
    $.globalEval("var lightbox={};");
    SemantAquaUI.lightbox={};
    lightbox=SemantAquaUI.lightbox;
    lightbox.clean=function(){
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
    }

    lightbox.show=function(){
        $(".lightbox").show();
        $("body").css({overflow:"hidden"});
        $(".lightbox .lb_container").fadeIn(500,function(){
        });
    };

    lightbox.init();
});






function initial_hierachy1(){
	//alert(class_hierachy);
	//alert("class hierarchy has"+class_hierachy.length);
	
	
	
	var temp_div=document.getElementById('tree_map');
	temp_div.innerHTML="";
	
	for (var i=0;i<class_hierachy_temp.length;i++){
		var ul=document.createElement("ul");
		var li=document.createElement("li");
		var a=document.createElement("a");
		a.href="#";
		var text=document.createTextNode(class_hierachy_temp[i][0]);
		var temp_root="map"+i;
		li.id=temp_root;
		a.appendChild(text); 
		li.appendChild(a); 
		ul.appendChild(li); 
		temp_div.appendChild(ul); 
		document.getElementById('description_map').appendChild(temp_div);
		    //alert("success");
		
		var j;
	    for ( j=class_hierachy_temp.length;j<class_hierachy.length;j++){ 
	    	
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
			 	     "url": "themes/default/style.css"
					},

				 "plugins" : ["themes","html_data","ui"] })
					// 1) if using the UI plugin bind to select_node
	
					.bind("select_node.jstree", function (event, data) { 
					// `data.rslt.obj` is the jquery extended node that was clicked
						getSelectedValue1()
						//var temp=data.rslt.obj.attr("id");
					    //var temp_id=parseInt(temp.substring(3));
						//alert(class_hierachy[temp_id][0]);
						//alert(class_hierachy[temp_id][1]);
						//$.bbq.pushState({"species":class_hierachy[temp_id][2]});
				})
					// 2) if not using the UI plugin - the Anchor tags work as expected
					//    so if the anchor has a HREF attirbute - the page will be changed
			//    you can actually prevent the default, etc (normal jquery usage)
					.delegate("a", "click", function (event, data) { event.preventDefault(); })
	});

}

function getSelectedValue1() {  
    var nodes = $.jstree._reference($("#tree_map")).get_selected();
    var temp=new Array();
    $.each(nodes, function(i, n) { 
    	 var temp_id=this.id;
	     var temp_id1=parseInt(temp_id.substring(3));
         temp.push(class_hierachy[temp_id1][2]);
         
    }); 
    $.bbq.pushState({"species":temp});
}  

function append_node1(current, parent){
   var temp_judge=parent;
   temp_judge=parseInt(temp_judge.substring(3));
   if(class_hierachy[current][1]==class_hierachy[temp_judge][0]){
		var temp_div=document.getElementById(parent);
		var ul=document.createElement("ul");
		var li=document.createElement("li");
		var a=document.createElement("a");
		a.href="#";
		var text=document.createTextNode(class_hierachy[current][0]);
		var temp_id="map"+current.toString();
		li.id=temp_id;
		a.appendChild(text); 
		li.appendChild(a); 
		ul.appendChild(li); 
		temp_div.appendChild(ul); 
	 	    //alert("success");
		var i=current+1;
		if (i<class_hierachy.length){
   		for (var i=current+1;i<class_hierachy.length;i++){ 
				append_node1(i,"map"+current);
		}
   }
	}
}	



document.onkeydown = keyDown1;

function keyDown1(){
    if(event.keyCode==8){
    	var temp =document.getElementById('search_info_map').value;
    
   	var cprStr=temp;
    	if(cprStr.length!=1&&cprStr.length!=0){             
    		cprStr=cprStr.substring(0,cprStr.length-1);
    
   		show=[];
   		len=0;
   		for (i in class_hierachy){
    			if(cprStr==class_hierachy[i][0].substring(0,cprStr.length)){
   	    		len=show.push(class_hierachy[i][0]);
       		}
    		} 
   	 }
	 else{
	show=[]
        }
   	 //alert("Have "+len+" compatible records");
	  var div1=document.getElementById('show_map');
  	  div1.innerHTML="";
  	  htmlStr=""
  	  for (i in show){
     	  htmlStr+="<a style=\"cursor: pointer;\" onclick=\"choose(this)\">"
      	  htmlStr+=show[i];
		  htmlStr+="</a>"
         htmlStr+="</br>";
    }
	  div1.innerHTML+=htmlStr;
   	  //alert(show);
    }
    
}


function press1(event){
var e=event.srcElement;
    if(event.keyCode!=13){
        if(event.keyCode!=8){
    var realkey = String.fromCharCode(event.keyCode);
        match1(realkey);
	     return false;
        }
        
    }
}

function match1(str){
    //alert(document.getElementById('textarea2').value);
    var temp =document.getElementById('search_info_map').value;
    
    
    var cprStr=temp+str;

    show=[];
    len=0;
    for (i in class_hierachy){
    	if(cprStr==class_hierachy[i][0].substring(0,cprStr.length)){
   	    len=show.push(class_hierachy[i][0]);
       }
    } 
    //alert("Have "+len+" compatible records");
var div1=document.getElementById('show_map');
    div1.innerHTML="";
    htmlStr=""
    for (i in show){
	 	htmlStr+="<a style=\"cursor: pointer;\" onclick=\"choose1(this)\">"
       htmlStr+=show[i];
		htmlStr+="</a>"
       htmlStr+="</br>";
    }
div1.innerHTML+=htmlStr;
}
function choose1(str){
	var temp=str.childNodes[0].nodeValue;
	document.getElementById('search_info_map').value=temp;
	document.getElementById('show_map').innerHTML="";
}

function search_node1(){
	 var temp =document.getElementById('search_info_map').value;
	 var flag=0;
	 for (var i=0;i<class_hierachy.length;i++){
	 	if(temp==class_hierachy[i][0]){
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
	 	alert(" not found !")
	 }
	 document.getElementById('search_info_map').value="";
	 document.getElementById('show_map').innerHTML="";
}



function m(){
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
			})
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


