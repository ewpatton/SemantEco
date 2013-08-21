
$(window).bind("initialize", function() {
/*
	$("body").replaceWith('<div id="hierarchy"><h2>New heading</h2></div>');

	check_value = new Array()
	check_value[0] = "I work at home"
	check_value[1] = "Train/Subway"
	check_value[2] = "Walk"
	check_value[3] = "Bicycle"
	var parentElement = document.getElementById('hierarchy');

	for(count in check_value)
	{
		var ptworkinfo=document.createElement('input');
		ptworkinfo.type="checkbox";
		ptworkinfo.id="ptworkinfo" + count;

		parentElement.appendChild(ptworkinfo);
		parentElement.appendChild(document.createTextNode(check_value[count]));

	}
*/

	//accessService
	//var a=$.extend(SemantEcoUI.getState(),args);
	/*
	var a="";
	var b=$.ajax(SemantEco.restBaseUrl+"DataoneModule/accessService",{"data":SemantEco.prepareArgs(a)});
    if(success)
    	b.done(success);
    	//document.write(b);
    if(error)
      b.fail(error);
	 */

//	var jsonString  = DataoneModule.accessService({}, function(d){console.debug(d)});



	

	//$(document).ready(function(){
		
		DataoneModule.accessService({}, function (data) {
			var table='<table border="1">';
			table+= '<tr><td>Title</td><td>Abstract</td><td>Keywords</td></tr>';
			//table+= '<tr><td>Title2</td><td>Abstract</td><td>Keywords</td></tr>';
			//table+= '<tr><td>Title3</td><td>Abstract</td><td>Keywords</td></tr>';

			data = jQuery.parseJSON(data);
			//table+= '<tr><td>Title4</td><td>Abstract</td><td>Keywords</td></tr>';

			console.debug(data);

			//data = data.response.docs;
			//data = data.response;

			//console.debug(data);
			//table+= '<tr><td>Title5</td><td>Abstract</td><td>Keywords</td></tr>';

			$.each( data.response.docs, function( index, item){
				//table+='<tr><td>'+'88'+'</td><td>' +
				//'88' + '</td><td> ' +
				//'99' +  '</td></tr>';	table+= '<tr><td>Title4</td><td>Abstract</td><td>Keywords</td></tr>';

				
				table+='<tr><td>'+item.title+'</td><td>' +
				item.abstract + '</td><td> ' +
				item.keywords +  '</td></tr>';
			});
			//table+= '<tr><td>Title6</td><td>Abstract</td><td>Keywords</td></tr>';

			//table+= '<tr><td>Title7</td><td>Abstract</td><td>Keywords</td></tr>';
			table+='</table>';

			$("body").replaceWith(table);

	//		$('#outTable').replaceWith(table);
		});
//	});

	//document.body.appendChild(table);



});






