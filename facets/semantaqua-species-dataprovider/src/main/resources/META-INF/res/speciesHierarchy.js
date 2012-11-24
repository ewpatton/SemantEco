 var jsonHier;
 var class_hierachy=new Array();
 var temp_array;
 var class_hierachy_temp=new Array();

 
$(window).bind("initialize", function() {
        	       SpeciesDataProviderModule.queryeBirdTaxonomy({}, function (data){
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
	//alert(jsonHier);
	
	var flag=0;
	for (var i=0;i<jsonHier.length;i++){
		flag=0;
		for (var j=0;j<jsonHier.length;j++){
			temp1=jsonHier[i]["parent"].indexOf("#");
			if(jsonHier[i]["parent"].substring(temp1+1)==jsonHier[j]["label"]){
				//alert(jsonHier[i]);
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
				class_hierachy_temp.push(new Array(jsonHier[i]["parent"].substring(temp1+1),null,jsonHier[i]["id"]));
				class_hierachy.push(new Array(jsonHier[i]["parent"].substring(temp1+1),null,jsonHier[i]["id"]));
				//jsonHier.remove(i);
			}
		}
	}
	//alert(class_hierachy);
	alert(jsonHier.length);
	for (var i=0;i<class_hierachy_temp.length;i++){
		iterative_build(class_hierachy_temp[i][0]);
		//alert(jsonHier.length);
	}
	alert("class hierarchy has"+class_hierachy.length);
	
	
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
		    //alert("success");
		
		var j;
	    for ( j=class_hierachy_temp.length;j<class_hierachy.length;j++){ 
			append_node(j,i);
		}
	    
	}
	
	
	$(function () {
		$("#tree")
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
						//alert(class_hierachy[temp][0]);
						//alert(class_hierachy[temp][1]);
					    $.bbq.pushState({"species":class_hierachy[temp][2]});
				})
				    
				
				
					// 2) if not using the UI plugin - the Anchor tags work as expected
					//    so if the anchor has a HREF attirbute - the page will be changed
			//    you can actually prevent the default, etc (normal jquery usage)
					.delegate("a", "click", function (event, data) { event.preventDefault(); })
	});

}
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
		var i=current+1;
		if (i<class_hierachy.length){
    		for (var i=current+1;i<class_hierachy.length;i++){ 
				append_node(i,current);
    		}
		}
	}
}	


function iterative_build(str){
	 temp_array=new Array();
	 for (var i=0;i<jsonHier.length;i++){
		 	temp=jsonHier[i]["parent"].indexOf("#");		 	
			if(jsonHier[i]["parent"].substring(temp+1)==str){
				class_hierachy.push(new Array(jsonHier[i]["label"],jsonHier[i]["parent"].substring(temp+1),jsonHier[i]["id"]));
				temp_array.push(jsonHier[i]["label"]);
				//jsonHier.remove(i);
			}
	 }
	 for (var i=0;i<temp_array.length;i++){
			 iterative_build(temp_array[i]); 
	 }
	 
}

document.onkeydown = keyDown;

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
function choose(str){
	var temp=str.childNodes[0].nodeValue;
	document.getElementById('search_info').value=temp;
	document.getElementById('show').innerHTML="";
}

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
