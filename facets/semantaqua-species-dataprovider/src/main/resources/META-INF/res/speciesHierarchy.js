 /*jsonHier used to accept JSON*/
var jsonHier;
/*class_hierachy used to store nodes information*/
 var class_hierachy=new Array();
 var temp_array;
 var class_hierachy_temp=new Array();
 var class_hierachy_map=new Array();

/*initialization*/
$(window).bind("initialize", function() {
	//input value=birds matches the checkbox for birds, as encoded in SpeciesDataProviderModule.java
	var birdIcon = $("input[value='birds']+img").attr("src");
	var fishIcon = $("input[value='fish']+img").attr("src");

	
	//this is for birds
	
	DataTypeModule.registerVisibilityFunction(function(b) {
		if(b["isBird"] == undefined) {
			return false;
		}
		var bird = b["isBird"]["value"] == "true";
		if(!bird) {
			return false;
		}
		return $("input[value='birds']")[0].checked;
	});
	
	DataTypeModule.registerIconLocator(function(b) {
		if(b["isBird"] == undefined) {
			return null;
		}
		var bird = b["isBird"]["value"] == "true";
		if(!bird) {
			return null;
		}
		return birdIcon;
	});
	
	
	//this is for fish
	//when the server returns the list of sites in the sparql results, it interates over all the binds
	//and the datatype module calls every visibility function and every icon locator (this is in DataTypeModule.js and/or SemantAquaUI.js)
	//registerVisibilityFunction is an array of functions, which are you passing this "anonymous" function into
	//so the sparql results bindings are passing as "b" into the below function
	DataTypeModule.registerVisibilityFunction(function(b) {
		//b is a dictionary where each key is a variable in the sparql
		//each valuable is itself a dictionary. each dicgtionary has up to four keys inside of it.
		// type (uri, literal, or blank node), value (actual uri or actual literal, or actual blank node), datatype (only present if literal and typed literal)
		//if there is no binding to the ?isFish variable then the default is to return false.
		//this will be undefined if you uncheck the both fish and spcies under the domain.
		if(b["isFish"] == undefined) {
			return false;
		}
		//we know this is a fish binding (b/c it could be false if this particular site being iterated on happens not to be a fish site)
		var fish = b["isFish"]["value"] == "true"; 
		if(!fish) {
			return false;
		}
		return $("input[value='fish']")[0].checked; //we use query to grab the checkbox and the value of its "checked" property
	});
	
	//returns as string which is a string to an image if its a relevant image for the visiblity.
	DataTypeModule.registerIconLocator(function(b) {
		if(b["isFish"] == undefined) {
			return null;
		}
		var fish = b["isFish"]["value"] == "true";
		if(!fish) {
			return null;
		}
		return fishIcon;
	});
	
	
	
	
	
	
	/*get the root node by SpeciesDataProviderModule.queryeBirdTaxonomyRoots*/
	//puts the array of root nodes, which are in the "data" array, into jsonHier
	//call initial_hiearchy
					SpeciesDataProviderModule.queryeBirdTaxonomyRoots({}, function (data){
        	    		  jsonHier=JSON.parse(data);
        	    		  jsonHier=jsonHier["data"];
        	    		  initial_hierachy();
        	    		  
        	    		          	    		  
        	       }
        	       );
        	});
 
 //var class_hierachy=[["Aves",null],["Accipiter","Aves"],["Acanthis","Aves"],["Aechmophorus","Aves"],["sharpShinnedHawk","Accipiter"],["commonRedpoll","Acanthis"]];


 var show=new Array();
 var len=0;

 
function initial_hierachy(){
	/*put root node in class_hierachy_temp and class_hierachy*/
	var flag=0;
	for (var i=0;i<jsonHier.length;i++){
		flag=0;
		for (var j=0;j<jsonHier.length;j++){
			//looking in the "parent" value and get the index position of the "#"
			temp1=jsonHier[i]["parent"].indexOf("#");
			//use the temp1, which is the index, to access the string of the parent uri (http://ebird#birdTaxonomy becomes birdTaxonomy) and compare with the label of the current json in jsonHier
			//in the case of birdTaxonomy, which is not one of the Ids, this will never be true.
			if(jsonHier[i]["parent"].substring(temp1+1)==jsonHier[j]["label"]){
				flag=1;
				break;
			}
		}
		if(flag==0){
			var flag1=0
			//at the first iteration class_hierarchty_temp will be empty
			//this loop compares every element in 2-d array class_hierarchy_temp with the parent of each species json
			for (var k=0;k<class_hierachy_temp.length;k++){
				if(class_hierachy_temp[k][0]==jsonHier[i]["parent"].substring(temp1+1)){
					flag1=1;
					break;
				}
		
			}
			//first time interation always true
			//so flag1 will be 1 and the below will not be entered when birdTaxonomy is in the class_hierarchy_temp array
			//so the push below just collects all the parents for the 2nd level and all level (depending on the array).
			if(flag1==0){
				//will put birdTaxonomy (the short name) into class_hierarchy_temp
				//class_hierarchy_temp only includes 1st and 2nd level nodes
				class_hierachy_temp.push(new Array(jsonHier[i]["parent"].substring(temp1+1),null,null));
				//class_hierarchy includes all nodes
				//"parent" here is accessing within the json array and "parent" is accesing into the json array.
				class_hierachy.push(new Array(jsonHier[i]["parent"].substring(temp1+1),null,null));
			}
		}
	}
	

	
	/*iterate nodes in class_hierachy_temp to build tree*/
	for (var j=0;j<class_hierachy_temp.length;j++){ //just birdTaxonomy
		 for (var i=0;i<jsonHier.length;i++){ //all of the second level nodes
			 	var temp=jsonHier[i]["parent"].indexOf("#");		 	
			 	//the below will always be true because each has the parent birdTaxonomy
				if(jsonHier[i]["parent"].substring(temp+1)==class_hierachy_temp[j][0]){
					
					//this creates an array of for example "Struthioniformes", "birdTaxonomy", "http://ebird#Struthioniformes"
					//so the class_hierarchy_temp only has the root-most node
					//the class_hierarchy has the id, label, and parent of all the second level classes
					class_hierachy.push(new Array(jsonHier[i]["label"],jsonHier[i]["parent"].substring(temp+1),jsonHier[i]["id"]));
					
					//jsonHier.remove(i);
				}
		 }
		
	}
	
	//the next thing is to build the tree using class_hierachy_temp for finding the root, and also using class_hierarchy array 
	/*get the nodes information from class_hierachy and then build JStree*/
	//here we are getting the div for the tree
	var temp_div=document.getElementById('tree');
	temp_div.innerHTML="";
	
	//all below here is to help jstree to build the tree
	for (var i=0;i<class_hierachy_temp.length;i++){
		//the jstree uses a ul, li, and a to make one node.
		var ul=document.createElement("ul");
		var li=document.createElement("li");
		var a=document.createElement("a");
		a.href="#";
		//this is a javascript method
		var text=document.createTextNode(class_hierachy_temp[i][0]);
		li.id=i;
		//this builds the structure to use the ul to put into the div.
		a.appendChild(text); 
		li.appendChild(a); 
		ul.appendChild(li); 
		//put the ul into the div
		temp_div.appendChild(ul); 
		//"description" is part of the speciesHierarchy.jsp above the tree div
		document.getElementById('description').appendChild(temp_div);
		
		var j;
		/*append second level nodes to the root level node. append_node is a function below*/
	    for ( j=class_hierachy_temp.length;j<class_hierachy.length;j++){ 
	    	//i is the id of the root level node, which is always 0.
	    	//and the j is 1 (because only one in the parent array) to length of the class hierarchy
			append_node(j,i);
		}
	    
	}
	
	/*JStree function, initialize JStree. make all nodes open at first*/
	$(function () {
		$("#tree")
			.jstree({
				"themes" : {
   					 "theme" : "default",
   					 "dots" : true,
  					 "icons" : true,
			 	    // "url": "themes/default/style.css"
					},
					 
				"core" : { "initially_open" : [ "0" ] },
				
				 "plugins" : ["themes","html_data","ui"] })
					
	                 /*the function used for selecting one node*/
				 //this retrieve the children
					.bind("select_node.jstree", function (event, data) { 
					
						
						//this is a jstree method for getting the id for the node you selected, this id is the index into the array.
					    var temp=data.rslt.obj.attr("id");
					    /*use $.bbq.pushState to put in queryeBirdTaxonomySubClasses's information*/
					    //the [2] index is the id of the species
					    $.bbq.pushState({"queryeBirdTaxonomySubClasses":class_hierachy[temp][2]});
					    /*ajax_node will work on JSON returned from backend*/
						ajax_node();
						getSelectedValue();
				})
				    
				
				
					//    if not using the UI plugin - the Anchor tags work as expected
					//    so if the anchor has a HREF attirbute - the page will be changed
		
					.delegate("a", "click", function (event, data) { event.preventDefault(); })
	});

}

function getSelectedValue() {  
	 /*get every nodes which you selected*/
    var nodes = $.jstree._reference($("#tree")).get_selected();
    var temp=new Array();
    $.each(nodes, function(i, n) {  
    	 /*if one node and its parent node both be selected, parent node will be deselected*/
    	for (var i=0;i< nodes.length;i++){
    		if(class_hierachy[this.id][1]==class_hierachy[nodes[i].id][0]){
    			$.jstree._reference($("#tree")).deselect_node(nodes[i]);
    			break;
    		}
    		
    	};
    
    }); 
    
    /*put all select node's ID in $.bbq*/
    nodes = $.jstree._reference($("#tree")).get_selected();
    $.each(nodes, function(i, n) {  
    	temp.push(class_hierachy[this.id][2]);
    });
    $.bbq.pushState({"species":temp});
}  

/*build second layer nodes*/
//first iteration current is 1 and parent is 0
function append_node(current, parent){
	
	//find the child nodes of one parent node and put them under that id under the parent element (by getElementById)
	// so class_hierachy[parent][0] is always class_hierachy[0][0] and thus always "birdTaxonomy".
	//we use class_hierachy[current][1] because [1] is the parent label so [1] is really ["parent"]
    if(class_hierachy[current][1]==class_hierachy[parent][0]){
		var temp_div=document.getElementById(parent);
		var ul=document.createElement("ul");
		var li=document.createElement("li");
		var a=document.createElement("a");
		a.href="#";
		//when it matches the birdTaxonomy parent, then we take the id, which is the 0 index to create the text node.
		var text=document.createTextNode(class_hierachy[current][0]);
		li.id=current;
		a.appendChild(text); 
		li.appendChild(a); 
		ul.appendChild(li); 
		temp_div.appendChild(ul); 
	 	    //alert("success");
		
	}
	
}	


//this method is called only if a node is selected/clicked AND it builds children nodes.
//if multiple nodes are selected it will be executed once for each node
//the execution finishing depends on whether JSON is returned from queryeBNirdTaxonomySubclasses
function ajax_node() {
	SpeciesDataProviderModule.queryeBirdTaxonomySubClasses({}, function(data) {
		jsonHier = JSON.parse(data);
		jsonHier = jsonHier["data"];
		var flag=0;
		var id=0;
		
		//if the subclassses returned is empty
		if (jsonHier.length == 0) {
			//alert("null");
		} else {
			for ( var parent = 0; parent < class_hierachy.length; parent++) {
				if (jsonHier[0]["id"] == class_hierachy[parent][2]) {
					flag = 1;
					break;
				}
			}
			if (flag == 1) {
				//alert("error");
			} else {
				//alert("success");
				for ( var i = 0; i < jsonHier.length; i++) {
					for ( var parent = 0; parent < class_hierachy.length; parent++) {
						var temp=jsonHier[i]["parent"].indexOf("#");
						//find the parent node for each returned subclass
						//comparing the parent field of the node string with the label of the class nodes label in the class hierarchy.
						if (jsonHier[i]["parent"].substring(temp+1) == class_hierachy[parent][0]) { 
							id=parent;
							var temp_div = document.getElementById(parent);
							var ul = document.createElement("ul");
							var li = document.createElement("li");
							var a = document.createElement("a");
							a.href = "#";
							var text = document.createTextNode(jsonHier[i]["label"]);
							li.id = class_hierachy.length;
							a.appendChild(text);
							li.appendChild(a);
							ul.appendChild(li);
							temp_div.appendChild(ul);
							// alert("success");
							class_hierachy.push(new Array(jsonHier[i]["label"],jsonHier[i]["parent"].substring(temp+1),jsonHier[i]["id"]));
							break;
						}
					}
				}
				 /*unfold children nodes*/
				var tree = jQuery.jstree._reference("#" + id);
		        tree.refresh();
		        document.getElementById(id).firstChild.click();
			}
		}
		
	});
}

/*
 * function iterative_build(str){ temp_array=new Array(); for (var i=0;i<jsonHier.length;i++){
 * temp=jsonHier[i]["parent"].indexOf("#");
 * if(jsonHier[i]["parent"].substring(temp+1)==str){ class_hierachy.push(new
 * Array(jsonHier[i]["label"],jsonHier[i]["parent"].substring(temp+1),jsonHier[i]["id"]));
 * temp_array.push(jsonHier[i]["label"]); //jsonHier.remove(i); } } for (var
 * i=0;i<temp_array.length;i++){ iterative_build(temp_array[i]); }
 *  }
 */

document.onkeydown = keyDown;
/*the funciton handle "delete" key*/
function keyDown(){
     if(event.keyCode==8){
     	var temp =document.getElementById('search_info').value;
     
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
	  var div1=document.getElementById('show');
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

/*handle other key except "delete" key*/
function press(event){
 var e=event.srcElement;
     if(event.keyCode!=13){
         if(event.keyCode!=8){
     var realkey = String.fromCharCode(event.keyCode);
         match(realkey);
	     return false;
         }
         
     }
 }

/*find the match string*/
function match(str){
     //alert(document.getElementById('textarea2').value);
     var temp =document.getElementById('search_info').value;
     
     
     var cprStr=temp+str;

     show=[];
     len=0;
     for (i in class_hierachy){
     	if(cprStr==class_hierachy[i][0].substring(0,cprStr.length)){
    	    len=show.push(class_hierachy[i][0]);
        }
     } 
     //alert("Have "+len+" compatible records");
 var div1=document.getElementById('show');
     div1.innerHTML="";
     htmlStr=""
     for (i in show){
	 	htmlStr+="<a style=\"cursor: pointer;\" onclick=\"choose(this)\">"
        htmlStr+=show[i];
		htmlStr+="</a>"
        htmlStr+="</br>";
     }
 div1.innerHTML+=htmlStr;
 }

/*when you find a match string. choose it.*/
function choose(str){
	var temp=str.childNodes[0].nodeValue;
	document.getElementById('search_info').value=temp;
	document.getElementById('show').innerHTML="";
}

/*find where this node is*/
function search_node(){
	 var temp =document.getElementById('search_info').value;
	 var flag=0;
	 for (var i=0;i<class_hierachy.length;i++){
	 	if(temp==class_hierachy[i][0]){
			flag=1;
			if(document.all){
				document.getElementById(i).firstChild.click();
			}
			else{
				var evt = document.createEvent("MouseEvents");  
	 				evt.initEvent("click", true, true);  
			  	    document.getElementById(i).childNodes[1].dispatchEvent(evt);  
			}
			//alert(" found !");
			break;
		}
	 }
	 if(flag==0){
	 	alert(" not found !")
	 }
	 document.getElementById('search_info').value="";
	 document.getElementById('show').innerHTML="";
}
