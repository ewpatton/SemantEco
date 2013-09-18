
console.log("executed test.js");


//  SemantEcoUI.HierarchicalFacet.create("#ClassTree", AnnotatorModule, "queryClassHM", "classes");


// Enable sortable and collapsable facets
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
