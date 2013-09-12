var expansionTermsList = [];


$(window).bind("initialize", function() {
	//$("input[type=button]").click(function(e) {
	
	
	$("input[id='Expansion']").click(function(e){

		//vocab();
		e.preventDefault();
		console.debug("Expand button selected.");

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

//		var jsonString  = DataoneModule.accessService({}, function(d){console.debug(d)});
		//$(document).ready(function(){

	/*
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
//		});
		//this overwrites the dom
		//document.body.appendChild(table);

		return false;

	});
*/
		

		//should conditionally check if topic expansion is selected
		if ($('#topic').is(':checked')) {
			$("#topic").show();
			console.debug("topic is checked");




			var topicDiv = $("<div id=topicExpansion></div>");
			DataoneSolrModule.expandTopicJSON({}, function(d){
				console.debug(d); 
				arr= JSON.parse(d); 
				console.debug(arr); 
				$.map(arr, function(item) { 
					expansionTermsList.push(item);
					topicDiv.append($("<input>").attr("type","checkbox").attr("name","expansionTerm").val(item)); 
					topicDiv.append($("<label>").attr("for",item).text(item)); 
					topicDiv.append($("<br>"));
				});
			});

			//<table id="data-source-module" border="1">
			//<tr><th>Domain</th></tr><tr>
			//var newTable = $(<"table">).attr("id","topicExpansion").attr("border",1);

			$("#vocab").append(topicDiv);


		}
		
		//should conditionally check if topic expansion is selected
		if ($('#vocabulary').is(':checked')) {
			$("#vocabulary").show();
			console.debug("vocabulary is checked");
			
			var vocabDiv = $("<div id=expansion2></div>");
			DataoneSolrModule.expandConceptJSON({}, function(d){
				console.debug(d); 
				arr= JSON.parse(d); 
				console.debug(arr); 
				$.map(arr, function(item) { 
					expansionTermsList.push(item);
					vocabDiv.append($("<input>").attr("type","checkbox").attr("name","expansionTerm").val(item)); 
					vocabDiv.append($("<label>").attr("for",item).text(item)); 
					vocabDiv.append($("<br>"));
				});
			});

			//<table id="data-source-module" border="1">
			//<tr><th>Domain</th></tr><tr>
			//var newTable = $(<"table">).attr("id","topicExpansion").attr("border",1);

			$("#vocab").append(vocabDiv);		
		}
		
		}); //expansion button
	//$("#vocab").append(newdiv.html());


	$("input[id='Search']").click(function(e){
		
		
		DataoneSolrModule.outputExpansionSelections({}, function(d){
			console.debug(d); 
		});
		
		console.debug("got to perform search.1"); 

		DataoneSolrModule.performSearch({}, function (data) {
			console.debug("got to perform search"); 

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

		//return false;

	});
	
});


/*
 * 
 * do the following:
 * 
 * DataoneSolrModule.expandTopicJSONPassteriformes({}, function(d){console.debug(d); arr= JSON.parse(d); console.debug(arr); 
 * $.map(arr, function(item) { 
 * newdiv.append($("<input>").attr("type","checkbox").attr("name",item).val(item)); 
 * newdiv.append($("<label>").attr("for",item).text(item)); }); });


$("#vocab").append(newdiv.html());

 * 
 * 
 * DataoneSolrModule.expandTopicJSONPassteriformes({}, function(d){console.debug(d); arr= JSON.parse(d); console.debug(arr); 
 * 
 * $.map(arr, function(item) { html += '<input type="checkbox" name="' + item + '" value=\"' + item + '" >'; }); });
 * 
 * 
 * 
 * DataoneSolrModule.expandTopicJSONPassteriformes({}, function(d){console.debug(d); arr= JSON.parse(d); console.debug(arr); 
 * 
 * $.map(arr, function(item) { html += $("<input>").attr("type","checkbox").attr("name",item).val(item);  }); });


undefined
XHR finished loading: "http://localhost:8081/SolrTesting/rest/DataoneSolrModule/expandTopicJSONPassteriformes?term=&domain=%5B%5D&searchType=%5B%5D". jquery-1.7.1.min.js:4
["species","avian","breeding","warblers","warbler","passerine","migratory","neotropical","population","habitat","populations","avifauna","songbirds","passerines","great","songbird","dendroica","insectivorous","aves","acrocephalus","parrots","reed"]
["species", "avian", "breeding", "warblers", "warbler", "passerine", "migratory", "neotropical", "population", "habitat", "populations", "avifauna", "songbirds", "passerines", "great", "songbird", "dendroica", "insectivorous", "aves", "acrocephalus", "parrots", "reed"]
html

"null[object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object][object Object]"
 * 
 * 
 * 
 */


