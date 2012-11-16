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


            	$('<input type="submit" class="characterssubmit" />').appendTo(selectscontainer);
				return leftcolumn;
            }

            function rightcolumngenerater(){
            	var rightcolumn=$(document.createElement('div')).addClass("rightcolumn");
            	var specietree=$(document.createElement('div')).addClass("specietree").html('<div ><table cellpadding="0" cellspacing="0"><tr><td colspan="2"></td></tr><tr><td  ><div id="text_map"  ><textarea name ="search" id="search_info_map" style="overflow:hidden;padding:0 ;width:100%;height:100%;border:1;" placeholder="Type message here!" onKeyPress="press1(event)"></textarea></div><td style="width:20%" ><input type=button onClick=" search_node1()" value="search" id="append_map"/></td></td>         </tr><tr><td  style="border-left:1px   solid   #111111;border-bottom:1px   solid   #111111;border-right:1px   solid   #111111;"><div id=show_map></div></td></tr><tr><td colspan="2">       <div id="description_map" style=" border:1px solid #111111; overFlow: auto;  " ><div id="tree_map" class="demo" style="width:120%;height:100px;"></div></div></td></tr></table></div>').appendTo(rightcolumn);
            	testFacet.queryBirdTaxonomy({}, function (data){
    	    		  //jsonHier=JSON.parse(data);
    	    		  initial_hierachy1();	 	          	    		  
    	              });
            	
            	
            	return rightcolumn;
            	
            }

            $($("#infowindowcontrol a").get(0)).click(function(){

				leftcoloumgenerater().appendTo(".lb_content");
				$(".lightbox .lb_container").css({"width":"60%"});
				$(".leftcolumn").css({"width":"100%"});
				SemantAquaUI.lightbox.show();

				$(".characterssubmit").click(function(e){

					$.bbq.pushState({"characteristic":$("#selectforcharacteristic").val()});
					console.log($.bbq.getState("characteristic"));
					$("#lightboxchart").empty();
					$(".lb_loading").show();

					function queryForSiteMeasurementsCallback(data){
						$(".lb_loading").hide();
						console.log("queryForSiteMeasurementsCallback");
						console.log(data);
						data=JSON.parse(data);
						var chartseries1=[];
						var bindings = data.results.bindings;
						var max=0;
						var min=0;
						for(var i=0;i<bindings.length;i++) {
							chartseries1.push([bindings[i].time.value,bindings[i].value.value]);
							if(bindings[i].value.value+100>max && bindings[i].value.value<3000){
								max=bindings[i].value.value+100;
								console.log(max);
							}
							if(bindings[i].value.value-100<min){
								min=bindings[i].value.value-100;
							}
						}
						console.log(chartseries1);
			            var plot1 = $.jqplot("lightboxchart", [chartseries1], {
			            // var plot1 = $.jqplot("lightboxchart", [UITeamUtilities.markerdata[0]["visualize1"]], {
			                title:marker.data.label.value,
			                axes:{
			                    xaxis:{
			                        renderer:$.jqplot.DateAxisRenderer,
			                        tickOptions:{
			                            formatString:'%b&nbsp;%#d'
			                        } 
			                    },
			                    yaxis:{
			                    	max:3000,
			                    	min:min,
			                        tickOptions:{
			                            formatString:'%.0f'
			                        }
			                    }
			                },
			                series:[{label:$("#selectforcharacteristic").html(),lineWidth:4}],
			                highlighter: {
			                    show: true,
			                    sizeAdjust: 7.5
			                },
			                legend: { 
			                	show:true, 
			                	location: 'se'
			                },
			                cursor: {
			                    show: false
			                }
			            });
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
					console.log($.bbq.getState("characteristic"));
					$("#lightboxchart").empty();
					$(".lb_loading").show();

					function queryForSiteMeasurementsCallback(data){
						$(".lb_loading").hide();
						console.log("queryForSiteMeasurementsCallback");
						console.log(data);
						data=JSON.parse(data);
						var chartseries1=[];
						var bindings = data.results.bindings;
						var max=0;
						var min=0;
						for(var i=0;i<bindings.length;i++) {
							chartseries1.push([bindings[i].time.value,bindings[i].value.value]);
							if(bindings[i].value.value+100>max && bindings[i].value.value<3000){
								max=bindings[i].value.value+100;
								console.log(max);
							}
							if(bindings[i].value.value-100<min){
								min=bindings[i].value.value-100;
							}
						}
						console.log(chartseries1);
			            var plot1 = $.jqplot('lightboxchart', [UITeamUtilities.markerdata[0].visualize1,UITeamUtilities.markerdata[0].visualize2], {
			                title:marker.data.label.value,
			                axes:{
			                    xaxis:{
			                        renderer:$.jqplot.DateAxisRenderer,
			                        tickOptions:{
			                            formatString:'%b&nbsp;%#d'
			                        } 
			                    },
			                    yaxis:{
			                        tickOptions:{
			                            formatString:'%.5f'
			                        },
			                    },
			                    y2axis:{
					                autoscale:true, 
					                tickOptions:{showGridline:false}
					            }
			                },
			                legend: { 
			                	show:true, 
			                	location: 'se'
			                },
			                series:[{label:$("#selectforchemical").val(),yaxis:'yaxis',lineWidth:4}, {label:"Aves",yaxis:'y2axis'}],
			                highlighter: {
			                    show: true,
			                    sizeAdjust: 7.5
			                },
			                cursor: {
			                    show: false
			                }
			            });
					}

					testFacet.queryForNearbySpeciesCounts({},queryForSiteMeasurementsCallback);

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
    lightbox.clean=null;

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
	
	
	
	
	var temp_div=document.getElementById('tree_map');
	temp_div.innerHTML="";
	var ul=document.createElement("ul");
	var li=document.createElement("li");
	var a=document.createElement("a");
	a.href="#";
	var text=document.createTextNode(class_hierachy[0][0]);
	var temp_id="map"+"0";
	li.id=temp_id;
	a.appendChild(text); 
	li.appendChild(a); 
	ul.appendChild(li); 
	temp_div.appendChild(ul); 
	document.getElementById('description_map').appendChild(temp_div);
	    //alert("success");
	var i=0+1;
	if (i<class_hierachy.length){
   	for (var i=1;i<class_hierachy.length;i++){ 
			append_node1(i,temp_id);
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
					    var temp=data.rslt.obj.attr("id");
					    var temp_id=parseInt(temp.substring(3));
						alert(class_hierachy[temp_id][0]);
						alert(class_hierachy[temp_id][1]);
				})
					// 2) if not using the UI plugin - the Anchor tags work as expected
					//    so if the anchor has a HREF attirbute - the page will be changed
			//    you can actually prevent the default, etc (normal jquery usage)
					.delegate("a", "click", function (event, data) { event.preventDefault(); })
	});

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
