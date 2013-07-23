// annotator-js-parser.js
// Handles creation of triples for the enhancement file on the client-side

/* WILL REPLACE THE FOLLOWING FUNCTIONS:

after import of file: readCsvFileForInitialConversion

queryForEnhancing:
	calls: generateParmsFileFromHeaders
	calls in a loop: queryForPropertyToEnhance for property level enhancements)
					 queryForHeaderToEnhance (for class type enhancements)
	then calls: convertToRdfWithEnhancementsFile(csvFileLocation, paramsFile)
	then calls: mireotClass(rangeClass, null, outputRdfFileLocation);

generateParmsFileFromHeaders:


queryForPropertyToEnhance:
	gets triple with column string in the object, and for that anonymous node, gets the subject
	(the blank node id) and assert equivalent property
	(to do this: https://github.com/ewpatton/SemantEco/wiki/to-change-the-property-for-linking-to-a-column-(csv2rdf4lod-enhancement)

queryForHeaderToEnhance:
	adds new triples based on generated anonymous node, and replaces range_name triple.
	(to do this: https://github.com/ewpatton/SemantEco/wiki/subclassing-range-for-a-column-(csv2rdf4lod-enhancement)

convertToRdfWithEnhancementsFile: calls: csv2rdfObject.toRDF
*/

// THE MAIN EVENT. Should call the other functions.
function queryForEnhancing(){
}// /queryForEnhancing



//	Prefixes!
//	There should be a better way later to read in prefixes from a file maybe
//		or add/remove based on which ontologies the user has active.
//	But for time's sake right now, HARD CODE ALL THE THINGS.
//	annotator-js-file-io.js calls this at the very end of table construction;
//	prefixes are stored in the #list div.
function createPrefixList(){
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
	
	return prefixes
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
	$(colNum).text(index);
	$(colLabel).text(label);
	d3.select(colNum).attr("rdfa:typeof","ov:csvCol");
	d3.select(colNum).attr("rdfa:typeof","ov:csvHeader conversion:label");
	bNode.appendChild(colNum);
	bNode.appendChild(colLabel);
	var rdfaDiv = document.getElementById("e_process");
	rdfaDiv.appendChild(bNode);
}// createEnhancementNode

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
		//alert("Data Source field left blank!");
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
		//alert("Dataset Name field left blank!");
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
		addVersion = "\"" + (document.getElementById("version_info").value) + "\";\n";
		version += addVersion;
	}
	else 
		alert("Version field left blank!");
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