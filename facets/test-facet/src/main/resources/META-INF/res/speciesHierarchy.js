 $(window).bind("initialize", function() {
        	       testFacet.queryBirdTaxonomy({}, function (data){
        	    		   alert(data);
        	       }
        	       );
        	});
 
 var class_hierachy=[["Aves",null],["Accipiter","Aves"],["Acanthis","Aves"],["Aechmophorus","Aves"],["sharpShinnedHawk","Accipiter"],["commonRedpoll","Acanthis"]];
 var show=new Array();
 var len=0;
 
function initial_hierachy(){
	var temp_div=document.getElementById('tree');
	temp_div.innerHTML="";
	var ul=document.createElement("ul");
	var li=document.createElement("li");
	var a=document.createElement("a");
	a.href="#";
	var text=document.createTextNode(class_hierachy[0][0]);
	li.id=0;
	a.appendChild(text); 
	li.appendChild(a); 
	ul.appendChild(li); 
	temp_div.appendChild(ul); 
	document.getElementById('description').appendChild(temp_div);
	    //alert("success");
	var i=0+1;
	if (i<class_hierachy.length){
    	for (var i=1;i<class_hierachy.length;i++){ 
			append_node(i,0);
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
						alert(class_hierachy[temp][0]);
						alert(class_hierachy[temp][1]);
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
