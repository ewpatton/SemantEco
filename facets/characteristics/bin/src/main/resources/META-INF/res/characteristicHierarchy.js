 var jsonHier_ch;
 var class_hierachy_ch=new Array();

 var class_hierachy_ch_temp=new Array();

 
$(window).bind("initialize", function() {
			CharacteristicsModule.queryCharacteristicsTaxonomyRoots({}, function (data){
						
        	    		  jsonHier_ch=JSON.parse(data);
        	    		  jsonHier_ch=jsonHier_ch["data"];
        	    		  initial_hierachy_ch();
        	    		  
        	    		          	    		  
        	       }
        	       );
        	});
 
 //var class_hierachy_ch=[["Aves",null],["Accipiter","Aves"],["Acanthis","Aves"],["Aechmophorus","Aves"],["sharpShinnedHawk","Accipiter"],["commonRedpoll","Acanthis"]];


 var show_ch=new Array();
 var len_ch=0;

 
function initial_hierachy_ch(){
	//alert(jsonHier_ch);
	//alert("aaa");
	var flag=0;
	for (var i=0;i<jsonHier_ch.length;i++){
		flag=0;
		for (var j=0;j<jsonHier_ch.length;j++){
			
			if(jsonHier_ch[i]["parent"]==jsonHier_ch[j]["id"]){
				//alert(jsonHier_ch[i]);
				flag=1;
				break;
			}
		}
		if(flag==0){
			var flag1=0
			for (var k=0;k<class_hierachy_ch_temp.length;k++){
				if(class_hierachy_ch_temp[k][2]==jsonHier_ch[i]["parent"]){
					flag1=1;
					break;
				}
		
			}
			if(flag1==0){
				var temp1=jsonHier_ch[i]["parent"].indexOf("#");
				class_hierachy_ch_temp.push(new Array(jsonHier_ch[i]["parent"].substring(temp1+1),null,jsonHier_ch[i]["parent"]));
				class_hierachy_ch.push(new Array(jsonHier_ch[i]["parent"].substring(temp1+1),null,jsonHier_ch[i]["parent"]));
				//jsonHier_ch.remove(i);
			}
		}
	}
	
	
	for (var j=0;j<class_hierachy_ch_temp.length;j++){
		 for (var i=0;i<jsonHier_ch.length;i++){
			 		 	
				if(jsonHier_ch[i]["parent"]==class_hierachy_ch_temp[j][2]){
					class_hierachy_ch.push(new Array(jsonHier_ch[i]["label"],jsonHier_ch[i]["parent"],jsonHier_ch[i]["id"]));
					
					//jsonHier.remove(i);
				}
		 }
		
	}
	
	
	var temp_div=document.getElementById('tree_ch');
	temp_div.innerHTML="";
	
	for (var i=0;i<class_hierachy_ch_temp.length;i++){
		var ul=document.createElement("ul");
		var li=document.createElement("li");
		var a=document.createElement("a");
		a.href="#";
		var text=document.createTextNode(class_hierachy_ch_temp[i][0]);
		var temp_root=i+"_ch";
		li.id=temp_root;
		a.appendChild(text); 
		li.appendChild(a); 
		ul.appendChild(li); 
		temp_div.appendChild(ul); 
		document.getElementById('description_ch').appendChild(temp_div);
		    //alert("success");
		
		var j;
	    for ( j=class_hierachy_ch_temp.length;j<class_hierachy_ch.length;j++){ 
			append_node_ch(j,temp_root);
		}
	    
	}
	
	
	$(function () {
		$("#tree_ch")
			.jstree({
				"themes" : {
   					 "theme" : "default",
   					 "dots" : true,
  					 "icons" : true,
			 	  //   "url": "themes/default/style.css"
					},
				 "core" : { "initially_open" : [ "0_ch" ] },
				 
				 "plugins" : ["themes","html_data","ui"] })
					// 1) if using the UI plugin bind to select_node
	
					.bind("select_node.jstree", function (event, data) { 
					// `data.rslt.obj` is the jquery extended node that was clicked
						 var temp=data.rslt.obj.attr("id");
							//alert(class_hierachy[temp][0]);
							//alert(class_hierachy[temp][1]);
						  var index=temp.indexOf("_");
						  temp=temp.substring(0,index);
						  //alert(temp);
						  $.bbq.pushState({"queryCharacteristicsTaxonomySubClasses":class_hierachy_ch[temp][2]});
					      ajax_node_ch();
						  getSelectedValue_ch();
				})
				    
				
				
					// 2) if not using the UI plugin - the Anchor tags work as expected
					//    so if the anchor has a HREF attirbute - the page will be changed
			//    you can actually prevent the default, etc (normal jquery usage)
					.delegate("a", "click", function (event, data) { event.preventDefault(); })
	});

}

function getSelectedValue_ch() {  
    var nodes = $.jstree._reference($("#tree_ch")).get_selected();
    var temp=new Array();
    $.each(nodes, function(i, n) {  
    	
    	for (var i=0;i< nodes.length;i++){
    		var temp_id=this.id;
    		var index=temp_id.indexOf("_");
   	     	var temp_id1=parseInt(temp_id.substring(0,index));
   	     	var temp_id2=nodes[i].id;
   	        index=temp_id2.indexOf("_");
	        var temp_id3=parseInt(temp_id2.substring(0,index));
    		if(class_hierachy_ch[temp_id1][1]==class_hierachy_ch[temp_id3][2]){
    			$.jstree._reference($("#tree_ch")).deselect_node(nodes[i]);
    			//alert(nodes[i].id);
    			break;
    		}
    		
    	};
    
    }); 
    
    nodes = $.jstree._reference($("#tree_ch")).get_selected();
    $.each(nodes, function(i, n) {  
    	 var temp_id=this.id;
    	 var index=temp_id.indexOf("_");
	     var temp_id1=parseInt(temp_id.substring(0,index));
         temp.push(class_hierachy_ch[temp_id1][2]);
         
    }); 
    $.bbq.pushState({"characteristic":temp});
}  


function append_node_ch(current, parent){
	var temp_judge=parent;
	var index=temp_judge.indexOf("_");
	temp_judge=parseInt(temp_judge.substring(0,index));
    if(class_hierachy_ch[current][1]==class_hierachy_ch[temp_judge][2]){
		var temp_div=document.getElementById(parent);
		var ul=document.createElement("ul");
		var li=document.createElement("li");
		var a=document.createElement("a");
		a.href="#";
		var text=document.createTextNode(class_hierachy_ch[current][0]);
		var temp_id=current.toString()+"_ch";
		//cannot use id variable because id is used by species, and the li needs to be distinct, so we have 0 for species and we have 0_ch for charactatericcs.
		li.id=temp_id;
		a.appendChild(text); 
		li.appendChild(a); 
		ul.appendChild(li); 
		temp_div.appendChild(ul); 
	 	    //alert("success");
		
	}
}	


function ajax_node_ch() {
	CharacteristicsModule.queryCharacteristicsTaxonomySubClasses({}, function(data) {
		jsonHier_ch = JSON.parse(data);
		jsonHier_ch = jsonHier_ch["data"];
		var flag=0;
		var id=0;
		if (jsonHier_ch.length == 0) {
			//alert("null");
		} else {
			for ( var parent = 0; parent < class_hierachy_ch.length; parent++) {
				if (jsonHier_ch[0]["id"] == class_hierachy_ch[parent][2]) {
					flag = 1;
					break;
				}
			}
			if (flag == 1) {
				//alert("error");
			} else {
				//alert("success");
				for ( var i = 0; i < jsonHier_ch.length; i++) {
					for ( var parent = 0; parent < class_hierachy_ch.length; parent++) {
						
						if (jsonHier_ch[i]["parent"] == class_hierachy_ch[parent][2]) {
							id=parent;
							parent=parent+"_ch";
							var temp_div = document.getElementById(parent);
							var ul = document.createElement("ul");
							var li = document.createElement("li");
							var a = document.createElement("a");
							a.href = "#";
							var text = document.createTextNode(jsonHier_ch[i]["label"]);
							li.id = class_hierachy_ch.length+"_ch";
							a.appendChild(text);
							li.appendChild(a);
							ul.appendChild(li);
							temp_div.appendChild(ul);
							// alert("success");
							class_hierachy_ch.push(new Array(jsonHier_ch[i]["label"],jsonHier_ch[i]["parent"],jsonHier_ch[i]["id"]));
							break;
						}
					}
				}
				var tree = jQuery.jstree._reference("#" + id+"_ch");
		        tree.refresh();
		        document.getElementById(id+"_ch").firstChild.click();
			}
		}
		
	});
}

document.onkeydown = keyDown_ch;

function keyDown_ch(){
     if(event.keyCode==8){
     	var temp =document.getElementById('search_info_ch').value;
     
    	var cprStr=temp;
     	if(cprStr.length!=1&&cprStr.length!=0){             
     		cprStr=cprStr.substring(0,cprStr.length-1);
     
    		show_ch=[];
    		len_ch=0;
    		for (i in class_hierachy_ch){
     			if(cprStr==class_hierachy_ch[i][0].substring(0,cprStr.length)){
    	    		len_ch=show.push(class_hierachy_ch[i][0]);
        		}
     		} 
    	 }
 	 else{
 	show_ch=[]
         }
    	 //alert("Have "+len+" compatible records");
	  var div1=document.getElementById('show_ch');
   	  div1.innerHTML="";
   	  htmlStr=""
   	  for (i in show_ch){
      	  htmlStr+="<a style=\"cursor: pointer;\" onclick=\"choose_ch(this)\">"
       	  htmlStr+=show_ch[i];
		  htmlStr+="</a>"
          htmlStr+="</br>";
     }
	  div1.innerHTML+=htmlStr;
    	  //alert(show);
     }
     
 }


function press_ch(event){
 var e=event.srcElement;
     if(event.keyCode!=13){
         if(event.keyCode!=8){
     var realkey = String.fromCharCode(event.keyCode);
         match_ch(realkey);
	     return false;
         }
         
     }
 }

function match_ch(str){
     //alert(document.getElementById('textarea2').value);
     var temp =document.getElementById('search_info').value;
     
     
     var cprStr=temp+str;

     show_ch=[];
     len_ch=0;
     for (i in class_hierachy_ch){
     	if(cprStr==class_hierachy_ch[i][0].substring(0,cprStr.length)){
    	    len_ch=show_ch.push(class_hierachy_ch[i][0]);
        }
     } 
     //alert("Have "+len+" compatible records");
 var div1=document.getElementById('show_ch');
     div1.innerHTML="";
     htmlStr=""
     for (i in show_ch){
	 	htmlStr+="<a style=\"cursor: pointer;\" onclick=\"choose_ch(this)\">"
        htmlStr+=show_ch[i];
		htmlStr+="</a>"
        htmlStr+="</br>";
     }
 div1.innerHTML+=htmlStr;
 }
function choose_ch(str){
	var temp=str.childNodes[0].nodeValue;
	document.getElementById('search_info_ch').value=temp;
	document.getElementById('show_ch').innerHTML="";
}

function search_node_ch(){
	 var temp =document.getElementById('search_info_ch').value;
	 var flag=0;
	 for (var i=0;i<class_hierachy_ch.length;i++){
	 	if(temp==class_hierachy_ch[i][0]){
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
	 document.getElementById('search_info_ch').value="";
	 document.getElementById('show_ch').innerHTML="";
}
