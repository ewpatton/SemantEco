// annotator-rdfa-handler.js
// Handles creation of triples for the enhancement file on the client-side
// These methods are called in the annotator-js-core and annotator-js-file-io javascript files


//	Prefixes!
//	There should be a better way later to read in prefixes from JSON maybe
//		or add/remove based on which ontologies the user has active?
//	But for time's sake right now, HARD CODE ALL THE THINGS.
//	This function is called when the user hits "OK" after entering package-level data.
//	Prefixes are stored in the #here-be-rdfa div.
// 	Takes as a parameter:
// 	 - the full URI of the dataset (requires user input!) to add as a prefix
function createPrefix(datasetURI){
	var prefixes = "conversion: http://purl.org/twc/vocab/conversion/ "
	prefixes += ": " + datasetURI + " ";
	
	return prefixes;
}// /createPrefix

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
	var colNum = document.createElement('div');
	var colLabel = document.createElement('div');
	// node for property is created here, and given the necessary RDFa
	//   type for the eventual conversion, but it remains blank until the user
	//   specifies what it is.
	var colProp = document.createElement('a');
	$(colProp).attr("id","prop-enhance,"+index);
	// We can also create a node for the class, and give it an ID, however
	//	 depending on whether it is a class or a datatype, the structure will be
	//	 different. So this will be a dummy node that can be overridden/retyped
	//	 as necessary. We mostly just want to give it an ID right now for ease of
	//	 manipulation later =)
	var colType = document.createElement('div');
	$(colType).attr("id","type-enhance,"+index);
	
	// These are the only two things where we already know the necessary
	//   objects at table creation.
	$(colNum).text(index);
	$(colLabel).text(label);
	// But we know the predicates for everything except for class/type.
	d3.select(bNode).attr("rdfa:rel","conversion:enhance");
	d3.select(colNum).attr("rdfa:property","ov:csvCol");
	d3.select(colLabel).attr("rdfa:property","ov:csvHeader conversion:label");
	d3.select(colProp).attr("rdfa:property","conversion:equivalent_property");
	
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
// 	- colType: the type of the column (regular, bundle, etc)
// 	- theProperty: full URI/CURIE of the property we are adding. Either one should
// 	     be okay, since we should have the prefixes stored in the main RDFa div.
function updateProp(index,colType,theProperty){
	console.log("updateProp is being called");
	// Only update the thing if it's not in a bundle.
	// Bundles are handled elsewhere!
	var header = document.getElementById("0,"+index);
	if ( $(header).hasClass("not-bundled")){
		var propNode = document.getElementById("prop-enhance,"+index);
		$(propNode).attr("href",theProperty);
	}
	else // if it is in a bundle, we'll handle it later
		console.log("RDFa for Property-ing bundles is handled elsewhere");
}// /updateProp

function updateClassType(index,colType,classURI,classLabel,sourceFacet){
	console.log("updateClassType: " + sourceFacet);
	// Only update the thing if it's not in a bundle.
	// Bundles are handled elsewhere!
	var header = document.getElementById("0,"+index);
	if ( $(header).hasClass("not-bundled")){
		var bNode = document.getElementById("enhance-col,"+index);
		// get the node we want to update, and check its type if any
		var typeNode = document.getElementById("type-enhance,"+index);
		var subclassNode,classNameNode,eNode;	// These are the nodes for the subclassing enhancements. They
												//	  might exist, OR we might need to create them. Either way,
												//    use these variable.
		var isClass = hasClassType(index);
		var isDataType = hasDataType(index);
		// Is the thing we are adding a Class or a Data Type?
		if ( sourceFacet == "classesFacet" ){ // if it's a class
			if ( isClass ){ 	// and so is the thing we're adding...
				// The enhancement property is already set for typeNode
				// Just add the stuff
				// * NOTE that eNode is meant to remain blank!
				console.log("updating class");
				classNameNode = document.getElementById("classname-enhance,"+index);
				subclassNode = document.getElementById("subclass-enhance,"+index);
				$(typeNode).text(classLabel);
				$(classNameNode).text(classLabel);
				$(subclassNode).attr("href",classURI);
			}// /if isClass
			else { 				// if it isn't, then we need to create nodes....
				console.log("adding class");
				eNode = document.createElement('div');
				classNameNode = document.createElement('div');
				subclassNode = document.createElement('a');
				// ... give them ID's.... 
				$(eNode).attr("id","class-root-enhance,"+index);
				$(classNameNode).attr("id","classname-enhance,"+index);
				$(subclassNode).attr("id","subclass-enhance,"+index);
				// ... and type them.
				d3.select(eNode).attr("rdfa:rel","conversion:enhance");
				d3.select(typeNode).attr("rdfa:property","conversion:range_name");
				d3.select(classNameNode).attr("rdfa:property","conversion:class_name");
				d3.select(subclassNode).attr("rdfa:property","conversion:subclass_of");
				$(subclassNode).attr("href",classURI);
				$(typeNode).text(classLabel);
				$(classNameNode).text(classLabel);
				$(subclassNode).text(classURI);
				
				eNode.appendChild(classNameNode);
				eNode.appendChild(subclassNode);
				bNode.appendChild(eNode);
			}
		}// /adding a class
		else if (sourceFacet == "datatypesFacet"){ // the thing is a datatype
			if (isDataType){					  // and so is the node we're adding it to....
				// override the things; property is already set
				console.log("updating datatype");
				$(typeNode).text(classURI);
			}// /isDataType
			else {
				// we have to delete some nodes =(
				console.log("removing class, adding datatype");
				eNode = document.getElementById("class-root-enhance,"+index);
				classNameNode = document.getElementById("classname-enhance,"+index);
				subclassNode = document.getElementById("subclass-enhance,"+index);
				if ( eNode ) { // but only if the nodes exist....
					eNode.removeChild(classNameNode);
					eNode.removeChild(subclassNode);
					bNode.removeChild(eNode);
				}
				// then re-type the typeNode and add the thing
				d3.select(typeNode).attr("rdfa:property","conversion:range");
				$(typeNode).attr("href",classURI);
			}// /!isDataType
		}// /adding a datatype
		else // for whatever reason the new thing is neither a class nor a datatype!?!?1//
			console.log("why are you calling this method???");
	}// /the thing's not in a bundle
	else // if it is in a bundle, we need to do some shenanigans....
		console.log("RDFa for Typing/Classing bundles is handled elsewhere");
}// /updateClassType

// Checks to see whether there has been a Data Type assignment
//	 in the RDFa for the column at (index).
function hasDataType(index){
	var checking = document.getElementById("type-enhance,"+index);
	var rdfaType = d3.select(checking).attr("rdfa:property");
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
	var rdfaType = d3.select(checking).attr("rdfa:property");
	console.log(rdfaType);
	if (rdfaType === "conversion:range_name"){
		return true; }
	else {
		return false; }
}// /hasClassType


// pulls user input about the dataset and adds it to the RDFa
// RETURNS the full URI of the dataset, for addition to the prefix list.
//	* NOTE that this should only create nodes if they don't already exist!
function addPackageLevelData(){
	var rootNode = $("#here-be-rdfa");
	var otherParams = $("#e_process");
	// get the data
	// * base_uri might not be hardcoded in the future
	var base = "http://purl.org/twc/semantgeo";
	var source = setSource();
	var dataset = setDataset();
	var version = setVersion();
	var full = base + "/source/" + source + "/dataset/" + dataset + "/version/" + version + "/conversion/enhancement/1";
	
	rootNode.attr("about",full);
	var elem = $("<div>");
	elem.attr("property","conversion:base_uri").append(base);
	elem.insertBefore(otherParams);
	elem = $("<div>");
	elem.attr("property","conversion:source_identifier").append(source);
	elem.insertBefore(otherParams);
	elem = $("<div>");
	elem.attr("property","conversion:dataset_identifier").append(dataset);
	elem.insertBefore(otherParams);
	elem = $("<div>");
	elem.attr("property","conversion:version_identifier").append(version);
	elem.insertBefore(otherParams);
	elem = $("<div>");
	elem.attr("property","conversion:enhancement_identifier").append("1");
	elem.insertBefore(otherParams);
	
	return full;
}// /addPackageLevelData

// in progress
function createNode(attr,pred){
	elem = $("<div>");
	elem.attr(attr,pred);
	return elem;
}

// Gets the source from the user input lightbox, and returns it.
// If we want to do any input checking/handling, put it in here?
function setSource(){
	var source = "";
	// if the user specified a source, use that...
	// * NOTE that in the future, we will probably want to 
	// have some kind of checking here to make sure the user
	// hasn't done anything silly (like only entered a space)
	if ( (document.getElementById("source_add_new").value) ){
		addSource = (document.getElementById("source_add_new").value).replace(/\W+/g,'');
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

// Gets the dataset name from the user input lightbox, and returns it.
// If we want to do any input checking/handling, put it in here?
function setDataset(){
	var dataset = ""
	// Do the same thing for the dataset name
	if ( (document.getElementById("dataset_add_new").value) ){
		addDataset = (document.getElementById("dataset_add_new").value).replace(/\W+/g,'');
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

// Gets the version from the user input lightbox, and returns it.
// If we want to do any input checking/handling, put it in here?
function setVersion(){
	// Version is only a text field
	// Just make sure that's not blank
	// * As with the above, we may want to check/clean user input a little
	var version = "";
	if ( document.getElementById("version_info").value ) {
		version += (document.getElementById("version_info").value);
		version = version.replace(/\W+/g,'');
		return version;
	}
	else 
		console.log("Version field left blank!");
		return;
}// /setVersion


function createCellBasedNode(cbCols){
	var mainNode = document.getElementById("here-be-rdfa");
	var cbNode = document.createElement('div');
	$(cbNode).attr("id","cell-based-enhance");
	d3.select(cbNode).attr("rdfa:rel","conversion:enhance");
	var cols = document.createElement('div');
	var type = document.createElement('div');
	d3.select(cols).attr("rdfa:property","ov:csvColumn");
	d3.select(type).attr("rdfa:typeof","qb:Observation");
	var colList = "";
	for ( i in cbCols ){
		colList += cbCols[i] + ",";
	}
	colList = colList.substring(0, colList.length - 1); // strip off the last comma
	$(cols).text(colList);
	cbNode.appendChild(cols);
	cbNode.appendChild(type);
	mainNode.appendChild(cbNode);
}

// needs:
//	- resource name for the bundle, typed conversion:ImplicitBundle
//	- a property
//	- name template
//	- type name
function createImplicitBundleNode(bResource, bProp, bNameTemp, bType){
	var mainNode = document.getElementById("here-be-rdfa");
	// create nodes...
	var bundleNode = document.createElement('div');
	var bundleProp = document.createElement('a');
	var bundleNameTemp = document.createElement('div');
	var bundleType = document.createElement('a');
	// ... type them...
	d3.select(bundleNode).attr("rdfa:about","#"+bResource).attr("rdfa:typeof","conversion:ImplicitBundle");
	d3.select(bundleProp).attr("rdfa:property","conversion:property_name");
	d3.select(bundleNameTemp).attr("rdfa:property","name_template");
	d3.select(bundleType).attr("rdfa:property","conversion:type_name");
	// ... add text...
	$(bundleNode).text(bResource);
	$(bundleProp).attr("href",bProp);
	$(bundleNameTemp).text(bNameTemp);
	$(bundleType).attr("href",bType);
	// ... put subtree together
	bundleNode.appendChild(bundleProp);
	bundleNode.appendChild(bundleNameTemp);
	bundleNode.appendChild(bundleType);
	mainNode.appendChild(bundleNode);
}// /createImplicitBundleNode

// adds a "conversion:bundled_by", to indicate a column is bundled by another
//	- bundleCol: the BUNDLING column
// 	- subCol: the column that is IN the bundle
function createExplicitBundledBy(bundleCol, subCol){
	var subEnhancement = document.getElementById("enhance-col,"+subCol);
	var bbNode = document.createElement('div');
	d3.select(bbNode).attr("rdfa:property","conversion:bundled_by").attr("rdfa:typeof","ov:csvCol");
	$(bbNode).text( bundleCol );
	subEnhancement.appendChild(bbNode);
}// /createExplicitBundledBy

function addAnnotationRDFa( index, predicate, object ){
	// create nodes...
	var anNodetation = document.createElement('div');
	var colNumber = document.createElement('div');
	var pred = document.createElement('div');
	var obj = document.createElement('div');
	// ... type them...
	d3.select(anNodetation).attr("rdfa:typeof","conversion:enhance");
	d3.select(colNumber).attr("rdfa:property","ov:csvCol");
	d3.select(pred).attr("rdfa:property","conversion:predicate");
	d3.select(obj).attr("rdfa:property","converion:object");
	// ... add things...
	$(colNumber).text(index);
	$(pred).text(predicate);
	$(obj).text(object);
	// ... and stick it all together!
	var mainNode = document.getElementById("e_process");
	anNodetation.appendChild(colNumber);
	anNodetation.appendChild(pred);
	anNodetation.appendChild(obj);
	mainNode.appendChild(anNodetation);
}// /addAnnotation


// Adds triples for: 
//	- package-level data [DONE]
//	- cell-based conversions
//	- implicit bundles
//	- explicit bundles
//	- subject annotations
//	- name template
//	- domain template
// This should be called inside the turtleGen function, or AFTER the user has 
//	completed enhancements, so that we are not unnecessarily updating RDFa 
//	while the user is still making changes
function finalizeTriples(){
	// handle package level data
	var uriPrefix = addPackageLevelData();
    var prefixes = createPrefix(uriPrefix);
	d3.select("#here-be-rdfa").attr("rdfa:prefix", prefixes);
	
	// handle cell-based
	createCellBasedNode(cellBased);
	
	GreenTurtle.attach(document,true);
}


// Working from the GreenTurtle API here: https://code.google.com/p/green-turtle/wiki/API
// This should parse the RDFa and return the graph in .ttl format.
function turtleGen(){
	finalizeTriples()
	document.data.implementation.attach(document);
	var turtle = document.data.graph.toString({shorten: true});
	console.log(turtle);
    return turtle;
}// /turtleGen