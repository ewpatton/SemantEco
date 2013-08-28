$(window).bind("initialize", function() {
$("input[type=button]").click(function(e) {
	vocab();
	e.preventDefault();
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
		
		DataoneSolrModule.accessService({}, function (data) {
			var table=$("<table><tbody> <tr><th>Title</th><th>Abstract</th><th>Keywords</th></tr></tbody></table>");
			data = jQuery.parseJSON(data);

			console.debug(data);
			//data = data.response.docs;
			//data = data.response;
			//console.debug(data);

			$.each( data.response.docs, function( index, item){
				//table+='<tr><td>'+'88'+'</td><td>' +
				//'88' + '</td><td> ' +
				//'99' +  '</td></tr>';	table+= '<tr><td>Title4</td><td>Abstract</td><td>Keywords</td></tr>';
				table.append("<tr><td>" + item.title + '</td><td> ' +
					     item.abstract + '</td><td> ' +
					     item.keywords +  '</td></tr>');
			});

			$("body").replaceWith(table);
			$("td,th").css("border","1px solid black").css("border-collapse","collapse");
	//		$('#outTable').replaceWith(table);
		});
//	});
		//this overwrites the dom
	//document.body.appendChild(table);

	return false;

});
});





