 var jsonHier_ch;
 var class_hierachy_ch=new Array();

 var class_hierachy_ch_temp=new Array();

 
$(window).bind("initialize", function() {
					CharacteristicsModule.queryCharacteristicTaxonomy({}, function (data){
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
			temp1=jsonHier_ch[i]["parent"].indexOf("#");
			if(jsonHier_ch[i]["parent"].substring(temp1+1)==jsonHier_ch[j]["label"]){
				//alert(jsonHier_ch[i]);
				flag=1;
				break;
			}
		}
		if(flag==0){
			var flag1=0
			for (var k=0;k<class_hierachy_ch_temp.length;k++){
				if(class_hierachy_ch_temp[k][0]==jsonHier_ch[i]["parent"].substring(temp1+1)){
					flag1=1;
					break;
				}
		
			}
			if(flag1==0){
				class_hierachy_ch_temp.push(new Array(jsonHier_ch[i]["parent"].substring(temp1+1),null,jsonHier_ch[i]["id"]));
				class_hierachy_ch.push(new Array(jsonHier_ch[i]["parent"].substring(temp1+1),null,jsonHier_ch[i]["id"]));
				//jsonHier_ch.remove(i);
			}
		}
	}
	//alert(class_hierachy_ch);
	//alert(jsonHier_ch.length);
	for (var i=0;i<class_hierachy_ch_temp.length;i++){
		iterative_build_ch(class_hierachy_ch_temp[i][0]);
		//alert(jsonHier_ch.length);
	}
	//alert("class hierarchy has"+class_hierachy_ch.length);
	
	
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
			 	     "url": "themes/default/style.css"
					},

				 "plugins" : ["themes","html_data","ui"] })
					// 1) if using the UI plugin bind to select_node
	
					.bind("select_node.jstree", function (event, data) { 
					// `data.rslt.obj` is the jquery extended node that was clicked
						getSelectedValue_ch();
					    //var temp=data.rslt.obj.attr("id");
						//alert(class_hierachy_ch[temp][0]);
						//alert(class_hierachy_ch[temp][1]);
					    //$.bbq.pushState({"species":class_hierachy_ch[temp][2]});
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
    	 var temp_id=this.id;
	     var temp_id1=parseInt(temp_id.substring(0,1));
         temp.push(class_hierachy_ch[temp_id1][2]);
         
    }); 
    $.bbq.pushState({"characteristic":temp});
}  


function append_node_ch(current, parent){
	var temp_judge=parent;
	temp_judge=parseInt(temp_judge.substring(0,1));
    if(class_hierachy_ch[current][1]==class_hierachy_ch[temp_judge][0]){
		var temp_div=document.getElementById(parent);
		var ul=document.createElement("ul");
		var li=document.createElement("li");
		var a=document.createElement("a");
		a.href="#";
		var text=document.createTextNode(class_hierachy_ch[current][0]);
		var temp_id=current.toString()+"_ch";
		li.id=temp_id;
		a.appendChild(text); 
		li.appendChild(a); 
		ul.appendChild(li); 
		temp_div.appendChild(ul); 
	 	    //alert("success");
		var i=current+1;
		if (i<class_hierachy_ch.length){
    		for (var i=current+1;i<class_hierachy_ch.length;i++){ 
				append_node_ch(i,current+"_ch");
    		}
		}
	}
}	


function iterative_build_ch(str){
	 var temp_array=new Array();
	 for (var i=0;i<jsonHier_ch.length;i++){
		 	temp=jsonHier_ch[i]["parent"].indexOf("#");		 	
			if(jsonHier_ch[i]["parent"].substring(temp+1)==str){
				class_hierachy_ch.push(new Array(jsonHier_ch[i]["label"],jsonHier_ch[i]["parent"].substring(temp+1),jsonHier_ch[i]["id"]));
				temp_array.push(jsonHier_ch[i]["label"]);
				//jsonHier_ch.remove(i);
			}
	 }
	 for (var i=0;i<temp_array.length;i++){
			 iterative_build_ch(temp_array[i]); 
	 }
	 
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
