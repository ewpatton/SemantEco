 /*jsonHier used to accept JSON*/
var jsonHier;
/*class_hierachy used to store nodes information*/
 var class_hierachy=new Array();
 var temp_array;
 var class_hierachy_temp=new Array();
 var class_hierachy_map=new Array();

/*initialization*/
$(window).bind("initialize", function() {
	var birdIcon = $("input[value='birds']+img").attr("src");
	
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
	/*get the root node by SpeciesDataProviderModule.queryeBirdTaxonomyRoots*/
	
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
			temp1=jsonHier[i]["parent"].indexOf("#");
			if(jsonHier[i]["parent"].substring(temp1+1)==jsonHier[j]["label"]){
				flag=1;
				break;
			}
		}
		if(flag==0){
			var flag1=0
			for (var k=0;k<class_hierachy_temp.length;k++){
				if(class_hierachy_temp[k][0]==jsonHier[i]["parent"].substring(temp1+1)){
					flag1=1;
					break;
				}
		
			}
			if(flag1==0){
				class_hierachy_temp.push(new Array(jsonHier[i]["parent"].substring(temp1+1),null,null));
				class_hierachy.push(new Array(jsonHier[i]["parent"].substring(temp1+1),null,null));
			}
		}
	}
	
	/*iterate nodes in class_hierachy_temp to build tree*/
	for (var j=0;j<class_hierachy_temp.length;j++){
		 for (var i=0;i<jsonHier.length;i++){
			 	var temp=jsonHier[i]["parent"].indexOf("#");		 	
				if(jsonHier[i]["parent"].substring(temp+1)==class_hierachy_temp[j][0]){
					class_hierachy.push(new Array(jsonHier[i]["label"],jsonHier[i]["parent"].substring(temp+1),jsonHier[i]["id"]));
					
					//jsonHier.remove(i);
				}
		 }
		
	}
	
	
	/*get the nodes information from class_hierachy_temp and then build JStree*/
	var temp_div=document.getElementById('tree');
	temp_div.innerHTML="";
	
	for (var i=0;i<class_hierachy_temp.length;i++){
		var ul=document.createElement("ul");
		var li=document.createElement("li");
		var a=document.createElement("a");
		a.href="#";
		var text=document.createTextNode(class_hierachy_temp[i][0]);
		li.id=i;
		a.appendChild(text); 
		li.appendChild(a); 
		ul.appendChild(li); 
		temp_div.appendChild(ul); 
		document.getElementById('description').appendChild(temp_div);
		
		var j;
		/*append second layer nodes*/
	    for ( j=class_hierachy_temp.length;j<class_hierachy.length;j++){ 
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
			 	     "url": "themes/default/style.css"
					},
					 
				"core" : { "initially_open" : [ "0" ] },
				
				 "plugins" : ["themes","html_data","ui"] })
					
	                 /*the function used for selecting one node*/
					.bind("select_node.jstree", function (event, data) { 
					
						
						
					    var temp=data.rslt.obj.attr("id");
					    /*use $.bbq.pushState to put in queryeBirdTaxonomySubClasses's information*/
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
function append_node(current, parent){
    if(class_hierachy[current][1]==class_hierachy[parent][0]){
		var temp_div=document.getElementById(parent);
		var ul=document.createElement("ul");
		var li=document.createElement("li");
		var a=document.createElement("a");
		a.href="#";
		var text=document.createTextNode(class_hierachy[current][0]);
		li.id=current;
		a.appendChild(text); 
		li.appendChild(a); 
		ul.appendChild(li); 
		temp_div.appendChild(ul); 
	 	    //alert("success");
		
	}
	
}	


/*depend on JSON returned from backend when user click on one node to build children nodes*/
function ajax_node() {
	SpeciesDataProviderModule.queryeBirdTaxonomySubClasses({}, function(data) {
		jsonHier = JSON.parse(data);
		jsonHier = jsonHier["data"];
		var flag=0;
		var id=0;
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
