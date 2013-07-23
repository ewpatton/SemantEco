// annotator-js-parser.js
// Handles creation of triples for the enhancement file on the client-side

// THE MAIN EVENT. Should call the other functions.
function queryForEnhancing(){
}// /queryForEnhancing



//	Prefixes!
//	There should be a better way later to read in prefixes from JSON maybe
//		or add/remove based on which ontologies the user has active?
//	But for time's sake right now, HARD CODE ALL THE THINGS.
//	This function is called when the user hits "OK" after entering package-level data.
//	Prefixes are stored in the #here-be-rdfa div.
// 	Takes as a parameter:
// 	 - the full URI of the dataset (requires user input!) to add as a prefix
function createPrefixList(datasetURI){
	var prefixes = "";
	prefixes += "rdf: http://www.w3.org/1999/02/22-rdf-syntax-ns# ";
	prefixes += "rdfs: http://www.w3.org/2000/01/rdf-schema# ";
	prefixes += "xsd: http://www.w3.org/2001/XMLSchema# ";
	prefixes += "skos: http://www.w3.org/2004/02/skos/core# ";
	prefixes += "geonames: http://www.geonames.org/ontology# ";
	prefixes += "prov: http://www.w3.org/ns/prov# ";
	prefixes += "qb: http://purl.org/linked-data/cube# ";
	prefixes += "dcterms: http://purl.org/dc/terms/ ";
	prefixes += "foaf: http://xmlns.com/foaf/0.1/ ";
	prefixes += "ov: http://open.vocab.org/terms/ ";
	prefixes += "sweet: http://sweet.jpl.nasa.gov/2.1/ ";
	prefixes += "void: http://rdfs.org/ns/void# ";
	prefixes += "conversion: http://purl.org/twc/vocab/conversion/ ";
	prefixes += ": " + datasetURI + " ";
	
	return prefixes;
}// /createPrefixList

// Creates appropriate div for a column, containing required basic triples
// This should be called repeatedly as the UI table is generated, 
// and takes two arguments:
//	- index: integer index of the column the div will be tied to
//	- label: the text of the label for that column
// * NOTE that other conversions on a column should be in elements attached
//   to the bNode created by this method!
function createEnhancementNode(label, index){
	// create blank node for the enhancement
	var bNode = document.createElement('div');
	$(bNode).attr("id", "enhance-col,"+index);
	// create nodes for the first enhancements
	var colNum = document.createElement('p');
	var colLabel = document.createElement('p');
	// node for property is created here, and given the necessary RDFa
	//   type for the eventual conversion, but it remains blank until the user
	//   specifies what it is.
	var colProp = document.createElement('p');
	$(colProp).attr("id","prop-enhance,"+index);
	// We can also create a node for the class, and give it an ID, however
	//	 depending on whether it is a class or a datatype, the structure will be
	//	 different. So this will be a dummy node that can be overridden/retyped
	//	 as necessary. We mostly just want to give it an ID right now for ease of
	//	 manipulation later =)
	var colType = document.createElement('p');
	$(colType).attr("id","type-enhance,"+index);
	
	// These are the only two things where we already know the necessary
	//   objects at table creation.
	$(colNum).text(index);
	$(colLabel).text(label);
	// But we know the predicates for everything except for class/type.
	d3.select(bNode).attr("rdfa:typeof","conversion:enhance");
	d3.select(colNum).attr("rdfa:typeof","ov:csvCol");
	d3.select(colLabel).attr("rdfa:typeof","ov:csvHeader conversion:label");
	d3.select(colProp).attr("rdfa:typeof","conversion:equivalent_property");
	
	// glue everything together....
	bNode.appendChild(colNum);
	bNode.appendChild(colLabel);
	bNode.appendChild(colProp);
	bNode.appendChild(colType);
	
	// stick it in the page
	var rdfaDiv = document.getElementById("e_process");
	rdfaDiv.appendChild(bNode);
}// createEnhancementNode

// When the user makes a property assignment, update that property in the RDFa; 
//     this should occur in the callback for dropping a property.
// This assumes that the node already exists - which it should, since it should
//     be generated at table creation.
// This takes two parameters:
// 	- index: the index of the column we are modifying
// 	- theProperty: full URI/CURIE of the property we are adding. Either one should
// 	     be okay, since we should have the prefixes stored in the main RDFa div.
function updateProp(index,theProperty){
	var propNode = document.getElementById("prop-enhance,"+index);
	$(propNode).text(theProperty);
}// /updateProp

// Checks to see whether there has been a Data Type assignment
//	 in the RDFa for the column at (index).
function hasDataType(index){
	var checking = document.getElementById("type-enhance,"+index);
	var rdfaType = d3.select(checking).attr("rdfa:typeof");
	console.log(rdfaType);
	if (rdfaType === "conversion:range"){
		return true; }
	else {
		return false; }
}// /hasDataType

// Checks to see whether there has been a Class assignment
//	 in the RDFa for the column at (index).
// There are TWO child nodes of the column's enhancement node
//	 if a class has been assigned! We are checking for the range_name
//	 one.
function hasClassType(index){
	var checking = document.getElementById("type-enhance,"+index);
	var rdfaType = d3.select(checking).attr("rdfa:typeof");
	console.log(rdfaType);
	if (rdfaType === "conversion:range_name"){
		return true; }
	else {
		return false; }
}// /hasClassType

// Class is more complex (https://github.com/ewpatton/SemantEco/wiki/subclassing-range-for-a-column-%28csv2rdf4lod-enhancement%29)
function addClassTypeEnhancement(index){
	//var bNode = document.getElementById("enhance-col,"+index);
	/*var colRangeName = document.createElement('p');
	var colClass = document.createElement('p'); // blank node
		// these will be attached as children to colClass
		var colClassName = document.createElement('p');
		var colSubclassOf = document.createElement('p');*/
	
	//d3.select(colRangeName).attr("rdfa:typeof","conversion:range_name");
	//d3.select(colClass).attr("rdfa:typeof","conversion:enhance");
	//d3.select(colClassName).attr("rdfa:typeof","conversion:class_name");
	//d3.select(colSubclassOf).attr("rdfa:typeof","conversion:subclass_of");
	
	//colClass.appendChild(colClassName);
	//colClass.appendChild(colSubclassOf);
	
	//bNode.appendChild(colRangeName);
	//bNode.appendChild(colClass);
}// /addClassTypeEnhancement

// pulls user input about the dataset and adds it to the RDFa
// RETURNS the full URI of the dataset, for addition to the prefix list.
function addPackageLevelData(){
	// get the data
	// * base_uri might not be hardcoded in the future
	var base = "http://purl.org/twc/semantgeo";
	var source = setSource();
	var dataset = setDataset();
	var version = setVersion();
	var full = base + "/source/" + source + "/dataset/" + dataset + "/version/" + version + "/conversion/enhancement/1";
	// create the divs
	var fullURI = document.createElement('p');
	var baseURI = document.createElement('p');
	var si = document.createElement('p');
	var di = document.createElement('p');
	var vi = document.createElement('p');
	var ei = document.createElement('p');
	// put the data in the divs
	$(fullURI).text(full);
	$(baseURI).text(base);
	$(si).text(source);
	$(di).text(dataset);
	$(vi).text(version);
	$(ei).text("1");
	// Type everything
	// Don't forget, the full URI here represents a resource!
	d3.select(fullURI).attr("rdfa:resource",full).attr("rdfa:typeof","conversion:LayerDataset void:Dataset");
	// and everything else here represents properties of that resource:
	d3.select(baseURI).attr("rdfa:typeof","conversion:base_uri");
	d3.select(si).attr("rdfa:typeof","conversion:source_identifier");
	d3.select(di).attr("rdfa:typeof","conversion:dataset_identifier");
	d3.select(vi).attr("rdfa:typeof","conversion:version_identifier");
	d3.select(ei).attr("rdfa:typeof","conversion:enhancement_identifier");
	// append ALL THE THINGS
	fullURI.appendChild(baseURI);
	fullURI.appendChild(si);
	fullURI.appendChild(di);
	fullURI.appendChild(vi);
	fullURI.appendChild(ei);
	
	document.getElementById("here-be-rdfa").appendChild(fullURI);
	return full;
}// /addPackageLevelData



function setSource(){
	var source = "";
	// if the user specified a source, use that...
	// * NOTE that in the future, we will probably want to 
	// have some kind of checking here to make sure the user
	// hasn't done anything silly (like only entered a space)
	if ( (document.getElementById("source_add_new").value) ){
		addSource = (document.getElementById("source_add_new").value);
		source += addSource;
	}
	// ... otherwise, check the dropdown... 
	else if( (document.getElementById("source_info").value) ) {
		addSource = (document.getElementById("source_info").value);
		source += addSource;
	}
	// ... and let the user know if everything's null
	else {
		console.log("Data Source field left blank!");
		return;
	}
	return source;
}// /setSource

function setDataset(){
	var dataset = ""
	// Do the same thing for the dataset name
	if ( (document.getElementById("dataset_add_new").value) ){
		addDataset = (document.getElementById("dataset_add_new").value);
		dataset += addDataset;
	}
	else if( (document.getElementById("dataset_info").value) ) {
		addDataset = (document.getElementById("dataset_info").value);
		dataset += addDataset;
	}
	else {
		console.log("Dataset Name field left blank!");
		return;
	}
	return dataset;
}// /setDataset

function setVersion(){
	// Version is only a text field
	// Just make sure that's not blank
	// * As with the above, we may want to check/clean user input a little
	var version = "";
	if ( document.getElementById("version_info").value ) {
		version += (document.getElementById("version_info").value);
		version.replace(/^\s+|\s+$/g,'');
		return version;
	}
	else 
		console.log("Version field left blank!");
		return;
}// /setVersion

//	Reads in Params File writes triples related to prefixes, source, dataset, and versionId.
//	Takes the headers and generates column specific enhancements.
function generateParmsFileFromHeaders(){
	
	// a loop goes here
	// call queryForPropertyToEnhance
	// call queryFor HeaderToEnhance
	
}// /generateParmsFileFromHeaders

function queryForPropertyToEnhance(){
	
}// /queryForPropertyToEnhance

function queryForHeaderToEnhance(){
	
}// /queryForHeaderToEnhance