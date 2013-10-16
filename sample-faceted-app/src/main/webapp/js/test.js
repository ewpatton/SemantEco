
console.log("executed test.js");


SemantEcoUI.HierarchicalFacet.create("#ChemicalTree", FacetedModuleModule, "queryChemicalHM", "chemicals");
SemantEcoUI.HierarchicalFacet.create("#GeospatialFeatureTree", FacetedModuleModule, "queryGeospatialFeaturesHM", "features");
SemantEcoUI.HierarchicalFacet.create("#OrganismTree", FacetedModuleModule, "queryOrganismsHM", "organisms");
SemantEcoUI.HierarchicalFacet.create("#TopicTree", FacetedModuleModule, "queryTopicsHM", "topics");
SemantEcoUI.HierarchicalFacet.create("#PhenoscapeTree", FacetedModuleModule, "queryPhenoscapeHM", "phenoscape");


//Enable sortable and collapsable facets
$("div#facets")
.accordion({
	header: "h3",
	collapsible: true,
	heightStyle: "fill"
}).sortable({
	axis: "y",
	items: "div.module-facet-container",
	handle: "h3",
	placeholder: "ui-state-highlight",
	forcePlaceholderSize: true
});


$(window).bind("rendered_tree.semanteco", function(e, d) {
	//if ($(d).parents('div#ChemicalTree').length) {
	/*fires when any node is selected */
		$(d).bind("select_node.jstree", function() { 		
			//		$("#IndividualTree").empty();			
			//		SemantEcoUI.HierarchicalFacet.create("#IndividualTree", AnnotatorModule, "queryIndividualHM", "individuals", {
			//			"dnd": dnd,
			//			"plugins": ["dnd"]
			//		})
			console.log("something was clicked");
			
			//how about in each query method check for all the params array
			//as a uniform method for updating the query object for that facet
			
			//$("#ChemicalTree").empty();			
			//SemantEcoUI.HierarchicalFacet.create("#ChemicalTree", FacetedModuleModule, "queryChemicalHM", "chemicals");
			
			$("#GeospatialFeatureTree").empty();			
			SemantEcoUI.HierarchicalFacet.create("#GeospatialFeatureTree", FacetedModuleModule, "queryGeospatialFeaturesHM", "features");
			
			FacetedModuleModule.performResultPanelSearch({}, function (data) {
			
				console.debug("performResultPanelSearch"); 
				data = jQuery.parseJSON(data);

				console.debug(data);
				$("#resultsView").empty();
				$("#resultsView").append(table);		

				var table=$("<table><tr><th>Title</th><th>Abstract</th><th>Id</th><th>Author</th></tr></table>");
				
				console.debug(data.data);

				//put results in the table
				
			$.each( data.data, function( index, item){
				console.debug("title : " + item.document);
					//table+='<tr><td>'+'88'+'</td><td>' +
					//'88' + '</td><td> ' +
					//'99' +  '</td></tr>';	table+= '<tr><td>Title4</td><td>Abstract</td><td>Keywords</td></tr>';
					table.append("<tr><td>" + 
							item.title + '</td><td> ' +
							item.abstract + '</td><td> ' +
							item.id + '</td><td> ' +

							item.author +  '</td></tr>');
				});
				
				
				$("#resultsView").append(table);		


				
			});
			
			//call query methods for updating trees
			
			//call update to results at the end.
			
			
			
			
			//here we will do a query that involves the chemical
		//	FacetedModuleModule.updateResults({}, function (data) {
		//		console.log("client updateResults");
		//		console.log("data : " . data = jQuery.parseJSON(data));
		//	});
		})
		//}
});



//var table=$("<table><tr><th>Title</th><th>Abstract</th><th>Keywords</th></tr></table>");
//$("#resultsView").append(table);		
//console.debug("initialize event");



//$(window).bind("initialize", function() {



//fire on a div's node click event.


//});


