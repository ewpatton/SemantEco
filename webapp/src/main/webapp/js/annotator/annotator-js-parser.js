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

//	Reads in Params File writes triples related to prefixes, source, dataset, and versionId.
//	Takes the headers and generates column specific enhancements.

function generateParmsFileFromHeaders(){
	//	Prefixes
	//	* Definitely need ov, qb
	
	//	Package-level data from the html:
	//		"source_info"/"source_add_new"
	//		"dataset_info"/"name_add_new"
	//		"version_info"
	var baseURI = "conversion:base_uri\t\"http://purl.org/twc/semantgeo\"^^xsd:anyURI;\n";
	var source = "conversion:source_identifier\t";
	var dataset = "conversion:dataset_identifier\t";
	var version = "conversion:version_identifier\t";
	var addSource, addDataset, addVersion;
	// if the user specified a source, use that...
	// * NOTE that in the future, we will probably want to 
	// have some kind of checking here to make sure the user
	// hasn't done anything silly (like only entered a space)
	if ( (document.getElementById("source_add_new").value) ){
		addSource = "\"" + (document.getElementById("source_add_new").value) + "\";\n";
		source += addSource;
	}
	// ... otherwise, check the dropdown... 
	else if( (document.getElementById("source_info").value) ) {
		addSource = "\"" + (document.getElementById("source_info").value) + "\";\n";
		source += addSource;
	}
	// ... and let the user know if everything's null
	else 
		alert("Data Source field left blank!");
	
	// Do the same thing for the dataset name
	if ( (document.getElementById("dataset_add_new").value) ){
		addDataset = "\"" + (document.getElementById("dataset_add_new").value) + "\";\n";
		dataset += addDataset;
	}
	else if( (document.getElementById("dataset_info").value) ) {
		addDataset = "\"" + (document.getElementById("dataset_info").value) + "\";\n";
		dataset += addDataset;
	}
	else 
		alert("Dataset Name field left blank!");
		
	// Version is only a text field
	// Just make sure that's not blank
	// * As with the above, we may want to check/clean user input a little
	if ( (document.getElementById("version_info").value) ){
		addVersion = "\"" + (document.getElementById("version_info").value) + "\";\n";
		version += addVersion;
	}
	else 
		alert("Version field left blank!");
	
	console.log(baseURI + source + dataset + version);
	
	
	
	// a loop goes here
	// call queryForPropertyToEnhance
	// call queryFor HeaderToEnhance
}// /generateParmsFileFromHeaders

function queryForPropertyToEnhance(){
	
}// /queryForPropertyToEnhance

function queryForHeaderToEnhance(){
	
}// /queryForHeaderToEnhance