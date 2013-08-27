// Brendan Edit: Organize all the code
var debugGlobal;

// Keeps track of the ID's of the bundles.
// These stay constant regardless of the bundle's resource or name, to make it
//	 easier to find the bundles.
var bundleIdManager = new BundleIdManager();

// Keeps track of ID's of annotations, just so we can iterate through them and make
//	 RDFa when the user commits enhancements.
var annotationID = 0;

// This array is for keeping track of the column indices of all selected columns.
//   It is empty at creation. The callbacks in the column selector function should
//   handle adding and removing indices from this array.
// Ideally, context menu functions that require knowledge of all selected columns
//   will also reference this array.
// * Note that lists of columns designated for links_via or cell-based conversion
//   are kept separately!
var currentlySelected = [];

// This array is for keeping track of column indices of those columns designated
//   for conversion using links_via. 
// It should be empty at creation, and the callback for "toggle-links_via" should
//   be the only function that can modify this array.
var links_via = [];

// Like the above, the indices of columns designated for cell-based conversion 
//    are kept in an array, as well, which is empty at creation. The callback for
//    "toggle-cell-based" adds to and removes from this array;
var cellBased = [];

// This arrayis for keeping track of bundles. Unlike the above, this array will 
//    contain OBJECTS, with each one describing one bundle. The bundle objects are
//    defined in the bundle function above; details about each part of the bundle
//    are elaborated upon there.
var bundles = [];

// Brendan Global Variable - Track selection in ontology dropdown for changes
var selectedOntologies = [];

// Brendan Global Variable - Track ontolgies added by user (custom ones via a URL)
var customUserOntologies = [];

// Generate the subtables for each column header in the CSV file.
//    The handleFileSelect function below that generates the table calls this repeatedly.
// Every subtable will be the same, (except for the ID's of the cells)
//    with 3 rows in 1 column. These are the DEFAULT subtables; ones for
//    IMPLICIT or EXPLICIT BUNDLES will be different (and in other functions) 
// Input parameters are:
// - text: presumably the header for the column from the original CSV file,
//	   to be placed in the first row of the column.
// - colIndex: the position of that cell in the table. Subrows are identified
//	   as "nameRow,"+colIndex, "propertyRow,"+colIndex, and "classRow,"+colIndex
//	   these last two rows are classed as droppable targets.
function createSubtable(text, colIndex) {
    var theader = '<table class="headerTable marginOverride">\n';
    var tbody = '';
    tbody += '<tr><td id=nameRow,' + colIndex + '><p class="ellipses marginOverride">' + text + '</p></td></tr>\n';
    tbody += '<tr><td style="color:red" class="droppable-prop" id=propertyRow,' + colIndex + '><p class="ellipses marginOverride property-label">[property]</p></td></tr>\n';
    tbody += '<tr><td style="color:red" class="droppable-class" id=classRow,' + colIndex + '><p class="ellipses marginOverride class-label">[class or datatype]</p></td></tr>\n';
    var tfooter = '</table>';
    var subtable = theader + tbody + tfooter;
    return subtable;
}

// This creates a subtable for describing a bundle
//  - One row is a dropdown menu that allows the user to select which column
//    is the resource representing the bundle, with the default option for "implicit"
//  - Ideally, selecting a column for the resource should copy the column header of the
//    original column. 
// Takes two arguments: 
//    - startIndex, the index of the first column in the bundle
//    - bundleSpan, the number of columns the bundle spans
// * NOTE that this method currently assumes bundled columns are consecutive and adjacent!
// TO DO: generate the dropdown menu! Probably in another function!
function createBundleSubtable(bundleID, implicitID) {
    var theader = '<table id=bundle,' + bundleID + '>\n';
    var tbody = '';
    
    // Brendan Edit: generate options based off of available headers
    var validHeadersToBundle = $("th.not-bundled,th.bundled-implicit").not(".hide-while-empty").not(".hidden").not(".ui-selected").filter(function () {
        return !($(this).find('td').hasClass("cellBased-on"));
    });

    // Build a list of items
    var generatedOptions = "<option value = \"-1\"" + implicitID + "\">Implicit Bundle " + implicitID + "</option>";
	var colID;
    validHeadersToBundle.each(function (index) {

        var itemLabel = "Unknown";
        var type = $.trim($(this).find("td:eq(0)").attr("id").split(",")[0]);
        if (type == "nameRow") {
            itemLabel = $(this).find("p:eq(0)").text();
        } else if ( type == "bundleResource" ) { 
            itemLabel = $(this).find("select").val();
        }
		colID = $(this).attr('id').split(",")[1];
        generatedOptions += "<option value = \"" + colID + "\">Column " + colID + " (" + itemLabel + ")</option>"
    });

    tbody += '<tr><td id=bundleResource,' + bundleID + '><form style="background:white" onchange="updateResource(\''+bundleID+'\');" action="return false;"><select id="bundle-resource-select,'+bundleID+'" class="bundle-select">' + generatedOptions + '</select></form></td></tr>\n';
    tbody += '<tr><td id=bundleName,' + bundleID + '><p class="ellipses marginOverride editable-input">[name template]</p></td></tr>\n';
    tbody += '<tr><td style="color:red" class="droppable-prop" id=bundlePropRow,' + bundleID + '><p class="ellipses marginOverride property-label">[property]</p></td></tr>\n';
    tbody += '<tr><td style="color:red" class="droppable-class" id=bundleClassRow,' + bundleID + '><p class="ellipses marginOverride class-label">[class or datatype]</p></td></tr>\n';
    var tfooter = '</table>';
    var subtable = theader + tbody + tfooter;
    return subtable;
}

// ******************************************************************************
//                   context menu and its callback functions!
// ******************************************************************************

// Enabling and dis
// Creates and enables the context menu on the column headers!
// Each function is placed under "items", and should have a NAME and a CALLBACK.
// If a callback is not specified, then it will utilize the default callback.
$(function () {
    $.contextMenu({
        selector: '.the-context-menu',
        build: function($trigger, e) {
            // this callback is executed every time the menu is to be shown
            // its results are destroyed every time the menu is hidden
            // e is the original contextmenu event, containing e.pageX and e.pageY (amongst other data)

            // Here we do some logic on which items are allowed for this menu
            console.log("Click Trigger:", $trigger);
            console.log("Selected at Trigger:", currentlySelected);

            // We use booleans to determine if a option is enabled or disabled,
            var toggle_cell_based_disabled_boolean = false;
            var create_bundle_disabled_boolean = false;

            // When nothing is selected, block the context menu
            if (currentlySelected.length == 0) {
                return false; // no idea if this works
            }


            // As we look over the items selected, keep track of which bundle we are in, so we can see if multiple bundles exist (this is really ugly. we need to rethink the bundles object!)
            //var selectedItemsBundleIndex = undefined;

            $.each(currentlySelected, function(index, value) {
                
                // Any cell based, means no bundling
                if ($("th#0\\," + value).find('td').hasClass("cellBased-on")) {
                    create_bundle_disabled_boolean = true;
                }

                // Of selected items in a bundle, if from multiple bundles then stop bundling (whew)
                $.each(bundles, function(idx, bundle) {
                    // Check if this selected item is in this bundle
                    if ($.inArray(value, bundle.columns) != -1) {
                        
                        // If in a bundle, then no cell-based
                        toggle_cell_based_disabled_boolean = true;

                        // keep track of bundles, to prevent multiple bundles being bundled
                        //if (selectedItemsBundleIndex == undefined) {
                        //    selectedItemsBundleIndex = idx
                        //} else if (selectedItemsBundleIndex != idx) {
                            // If this has been set but is not the same as our idx, then we are looking at multiple bundles so block bundling
                        //    create_bundle_disabled_boolean = true;
                        //}
                       // Break each loop
                       return false;
                    }
                });
            });

            return {
                // This is the default callback, which will be used for any functions
                //    that do not have their own callbacks specified. It echoes the 
                //    key of the selection and the column on which the menu was invoked
                //    to the console.
                callback: function (key, options) {
                    var index = $("th").index(this);
                    var m = "clicked: " + key + ", invoked on col: " + index;
                    console.log(m);
                },
                // Each of these is one item in the context menu list.
                // Documentation at http://medialize.github.io/jQuery-contextMenu/docs.html
                items: {
                    "toggle-cell-based": {
                        // Modifies the cellBased array to add or remove column indices.
                        // If a selected column is in the array, then it will be removed (toggled OFF)
                        // If the column is not in the array, then it will be added (toggled ON)
                        // * NOTE that if this is called when only one column is selected,
                        //   currently it will toggle the column on which the menu was invoked,
                        //   NOT the selected column.
                        name: "Toggle 'cell-based'",
                        disabled: toggle_cell_based_disabled_boolean,
                        callback: function () {
                            // if one or fewer columns are selected, add/remove the column on which the menu was invoked
                            if (currentlySelected.length <= 1) {
                                var index = $("th").index(this);
                                var toggle = document.getElementById("nameRow," + index);
                                // if the column is already there, remove it
                                if (existsA(cellBased, index)) {
                                    $(toggle).removeClass("cellBased-on");
                                    removeA(cellBased, index);
                                    console.log("removing col " + index);
                                }
                                // if the column is not there, add it
                                else {
                                    $(toggle).addClass("cellBased-on");
                                    //$(toggle).attr("class","cellBased-on");
                                    cellBased.push(index);
                                    console.log("adding col " + index);
                                }
                            } // /if
                            // if more than one column is selected, perform the toggle on ALL selected columns
                            else {
                                $.each(currentlySelected, function(index,colNum) {
                                    var toggle = document.getElementById("nameRow," + colNum);
                                    // if the column is already there, remove it
                                    if (existsA(cellBased, colNum)) {
                                        $(toggle).removeClass("cellBased-on");
                                        removeA(cellBased, colNum);
                                        console.log("removing col " + colNum);
                                    }
                                    // if the column is not there, add it
                                    else {
                                        $(toggle).addClass("cellBased-on");
                                        //$(toggle).attr("class","cellBased-on");
                                        cellBased.push(colNum);
                                        console.log("adding col " + colNum);
                                    }
                                }); // /$.each
                            }
                            console.log("currently specified for cell-based: " + cellBased);
                        } // /cell-based callback
                    }, // /cell-based

                    "toggle-links_via": {
                        // Modifies the links_via array to add or remove column indices.
                        // If a selected column is in the array, then it will be removed (toggled OFF)
                        // If the column is not in the array, then it will be added (toggled ON)
                        // * NOTE that if this is called when only one column is selected,
                        //   currently it will toggle the column on which the menu was invoked,
                        //   NOT the selected column.
                        name: "Toggle 'links_via'",
                        callback: function () {
                            // if one or fewer columns are selected, add/remove the column on which the menu was invoked
                            if (currentlySelected.length <= 1) {
                                var index = $("th").index(this);
                                var toggle = document.getElementById("nameRow," + index);
                                // if the column is already there, remove it
                                if (existsA(links_via, index)) {
                                    $(toggle).removeClass("links_via-on");
                                    removeA(links_via, index);
                                    console.log("removing col " + index);
                                }
                                // if the column is not there, add it
                                else {
                                    $(toggle).addClass("links_via-on");
                                    //$(toggle).attr("class","links_via-on");
                                    links_via.push(index);
                                    console.log("adding col " + index);
                                }
                            } // /if
                            // if more than one column is selected, perform the toggle on ALL selected columns
                            else {
                                $.each(currentlySelected, function(index,colNum) {
                                    var toggle = document.getElementById("nameRow," + colNum);
                                    // if the column is already there, remove it
                                    if (existsA(links_via, colNum)) {
                                        $(toggle).removeClass("links_via-on");
                                        removeA(links_via, colNum);
                                        console.log("removing col " + colNum);
                                    }
                                    // if the column is not there, add it
                                    else {
                                        $(toggle).addClass("links_via-on");
                                        //$(toggle).attr("class","links_via-on");
                                        links_via.push(colNum);
                                        console.log("adding col " + colNum);
                                    }
                                }); // /$.each
                            } // /else
                            console.log("currently specified for links_via: " + links_via);
                        } // /links_via callback
                    }, // /links_via

                    "bundle": {
                        name: "Create Bundle",
                        disabled: create_bundle_disabled_boolean,
                        callback: function () {
                        	console.log(currentlySelected);

                            // First, let's get a reference to all DOM items that were selected
                            var headerGroupings = [];
                            var aGroup = [];
                            $.each(currentlySelected, function(index, value) {
								if( !isNaN(value) ){ // If a currentlySelected column is a number, then that means it is not a bundle;
													 //  go ahead and push that to the grouping array.
									aGroup.push($("th#0\\," + value));
								}
								else { // If we have a bundle, add the headers for all the columns in that bundle.
									   // This will include all of the hidden ones! But this keeps them from dangling alone
									   //  without their superior bundle header.
									console.log("hark, a bundle!");
									var theBundle = getBundleById(value);
									$.each( theBundle.columns, function(colIndex, colNum){
										aGroup.push($("th#0\\," + colNum));
									});
								}
								// Detect selection gaps, so we can selectivly colspan
								if( index != currentlySelected.length - 1 ) {
									if( Math.abs(value - currentlySelected[index + 1]) != 1 ) {
										headerGroupings.push(aGroup);
										aGroup = [];
									}
								} else if (index == currentlySelected.length - 1) {
									headerGroupings.push(aGroup);
								}
                            });

                            console.log("Groups:", headerGroupings);

                            // Let's log these groupings for this bundle into our local bundles object\
                            // Use slice to pass by value and not reference
                            var newBundle = new Bundle(bundleIdManager.requestBundleID(), bundleIdManager.requestImplicitID(), currentlySelected.slice(0));
                            bundles.push(newBundle);

                            // Second, let's determine which columns have children below them that need to be pushed down before the bundle is created and push them down
                            // we will also build the new headers and insert them in this loop
                            $.each(headerGroupings, function(index, group) { 
                                $.each(group, function(index, item) { 
                                    var colspan = group.length;
                                    var selectedID = item.attr("id").split(",")[1];
                                    if ($("#bundledRow\\," + selectedID).children().length > 0) {
                                        // Expose the extended-bundles row (yuck)
                                        if (!$("#bundles-extended").is(":visible")) {
                                            $("#bundles-extended").removeClass("hide-while-empty");
                                        }
                                        // Move the item down
                                        $("#bundledRow\\," + selectedID).children(":first").removeClass("bundle-table").addClass("bundle-table-extended").appendTo("td#bundledRow-extended\\," + selectedID);
                                    }

                                    //Expose the bundles row
                                    if (!$("#bundles").is(":visible")) {
                                        $("#bundles").removeClass("hide-while-empty");
                                    }

                                    // Before we move the item down, handle colspan
                                    var itemColspan = item.attr('colspan');
                                    if (typeof itemColspan !== 'undefined' && itemColspan !== false) {
                                        // has a colspan so set colspan for dest
                                        $("td#bundledRow\\," + selectedID).attr("colspan", itemColspan);
										$("td#bundledRow\\," + selectedID).addClass("bundled");
										$("td#bundledRow\\," + selectedID).addClass("ui.selected")
                                    }
                                    // Before we move the item down, handle hidden cells
                                    if (item.hasClass("hidden")) {
                                        $("td#bundledRow\\," + selectedID).addClass("hidden");
										$("td#bundledRow\\," + selectedID).addClass("bundled");
                                    }

                                    // Before we move the cell down, handle bundle class (explicit, implicit)
                                    item.removeClass("not-bundled").addClass("bundled-implicit");

                                    // Now, move the item down
                                    if (item.attr("colspan") != undefined) {
                                        item.children(":first").removeClass("headerTable").addClass("bundle-table").addClass("table-width-override").appendTo("td#bundledRow\\," + selectedID);
                                    } else {
                                       item.children(":first").removeClass("headerTable").addClass("bundle-table").appendTo("td#bundledRow\\," + selectedID);
                                    }

                                    // Insert new header table, colspan if first in group, else hide
                                    if (index == 0) {
                                        item.append(createBundleSubtable(newBundle._id, newBundle.implicitId)).attr("colspan", colspan);                                
                                    } else {
                                        item.addClass("hidden");                               
                                    }
                                    // TODO: Disable the forms here .attr("disabled", "disabled");
                                });
                            });
							currentlySelected = [newBundle._id];
                        }
                    },

                    "comment": {
                        // Adds a comment to the Annotation Row
                        // * when finished, this should pop up a lightbox to solicit user input
                        //   including the type of comment (radio selector?) as well as the
                        //   comment text itself.
                        // If there is already a comment/the row is already showing, then just add it.
                        // Otherwise, show the row, then add the comment to the column on which the context
                        //    menu was called.
                        // * NOTE that if multiple columns are selected, it will only add the comment to the 
                        //   single column on which the menu is invoked!
                        name: "Add Comment",
                        disabled: true,
                        callback: function () {
							var index = $("th").index(this);
                        	$("#commentModal").dialog({
                                modal: true,
                                width: 800,
                                draggable: false,
                                resizable: false,
                                buttons: {
                                    Ok: function () {
										checkAnnotationRow();
										//var theTable = workingCol.getElementsByTagName('TABLE')[0];
										var cType = "rdfs:comment";
										var cText = document.getElementById("commentModalInput").value;
										//addAnnotationRDFa(index,annotationID,cType,cText);
										addAnnotation(index,cType,cText);
										GreenTurtle.attach(document,true);
                                        $(this).dialog("close");
									}// /OK function
                                }// /buttons
                            });// /dialog
                        } // /callback function
                    }, // /addComment

                    "edit Domain Template": {
                        // Adds a comment to the Annotation Row
                        // * when finished, this should pop up a lightbox to solicit user input
                        //   including the type of comment (radio selector?) as well as the
                        //   comment text itself.
                        // If there is already a comment/the row is already showing, then just add it.
                        // Otherwise, show the row, then add the comment to the column on which the context
                        //    menu was called.
                        // * NOTE that if multiple columns are selected, it will only add the comment to the 
                        //   single column on which the menu is invoked!
                        name: "Edit Domain Template",
                        disabled: true,
                        callback: function () {
                            // Show modal
                            $("#domainTemplateModal").dialog({
                                modal: true,
                                width: 800,
                                draggable: false,
                                resizable: false,
                                buttons: {
                                    Ok: function () {
                                        $(this).dialog("close");
                                    }
                                }
                            });
                        }
                    },

                    "add-canonical-value": {
                        // Like addComment, this allows a user to add a canonical value
                        //    using our conversion:eg. As the above, "egText" should eventually
                        //    be user-specified.
                        // Canonical Values hang out in the annotation row along with comments and
                        //    other annotations.
                        // * NOTE that, like the above, thsi will only add an eg to the column on which
                        //   the context menu was invoked, even if multiple columns are selected!
                        name: "Add Canonical Value",
                        disabled: true,
                        callback: function () {
							var index = $("th").index(this);
                        	 $("#canonicalModal").dialog({
                                modal: true,
                                width: 800,
                                draggable: false,
                                resizable: false,
                                buttons: {
                                    Ok: function () {
										checkAnnotationRow();
										var cType = "conversion:eg";
										var cText = document.getElementById("canonicalModalInput").value;
										//addAnnotationRDFa(index,annotationID,cType,cText);
										addAnnotation(index,cType,cText);
										GreenTurtle.attach(document,true);
                                        $(this).dialog("close");
                                    }
                                }
                            });
                            
                        } // /callback function

                    }, // /eg

                    "add-subject-annotation": {
                        // Subject Annotation addes new triples. Forced triples is what we like to call it.
                        name: "Add Subject Annotation",
                        callback: function () {
							var index = $("th").index(this);
                            checkAnnotationRow();
                            // Patrice wants it to default
                            var cType = "aPredicate";
                            var cText = "anObject";
                            //var cType = document.getElementById("subjectAnnotationPredicateModalInput").value;
                            //var cText = document.getElementById("subjectAnnotationObjectModalInput").value;
							//addAnnotationRDFa(index,annotationID,cType,cText);
                            addAnnotation(index,cType,cText);
                            GreenTurtle.attach(document,true);
                            /*$("#subjectAnnotationModal").dialog({
                                modal: true,
                                width: 800,
                                draggable: false,
                                resizable: false,
                                buttons: {
                                    Ok: function () {
                                        $(this).dialog("close");
                                    }
                                }
                            }); */
                        }
                    }
                } // /items
            };
        }
    }); // /context menu
}); // /context menu function

// Arguments for Drag and Drop for facets ( this applies to the jstree library. see: jstree.com)
var dnd = {
    "drop_target": ".column-header, .bundled-row, .bundled-row-extended, .annotation-row, div.global-properties-container",
    "drop_check": function (data) {
        if ( data.r.is("td.bundled-row") || data.r.is("td.bundled-row-extended") || data.r.is("td.annotation-row") ) {
            if ( data.r.children().length == 0 ) { 
                return false; 
            }
        }
        return true;
    },
    "drop_finish": function (data) {

        // Get which facet the drop came from
        //var sourceFacet = data.o.closest("div.facet").attr("id");
        // class, objectProperty, datatypeProperty, annotationProperty, datatype
        console.log("DRAG EVENT", data);
        debugGlobal = data;

        var sourceFacet = SemantEcoUI.HierarchicalFacet.entryForElement(data.o).rawData.type; // Thanks Evan :)

        // Determine the label we are looking for given the source facet
        var label = ( $.inArray(sourceFacet, ["annotationProperty", "datatypeProperty", "objectProperty"]) != -1 ? "property-label" : "class-label" );

        // We need to determine where we are now that a drop has happened. First, get the ID of the column we are in, next get the respective label for where we dropped
        var target, columnID, columnType;
        
        if (data.r.is("p." + label)) {
            target = data.r;
            columnType = data.r.closest("th.column-header, td.bundled-row, td.bundled-row-extended, td.annotation-row, div.global-properties-container").attr("id").split(",")[0];
			columnID = data.r.closest("th.column-header, td.bundled-row, td.bundled-row-extended, td.annotation-row, div.global-properties-container").attr("id").split(",")[1];
        } else if ( data.r.is("th.column-header") || data.r.is("td.bundled-row") || data.r.is("td.bundled-row-extended") || data.r.is("td.annotation-row") || data.r.is("div.global-properties-container") ) {
            target = data.r.find("p." + label + ":eq(0)");
            columnType = data.r.attr("id").split(",")[0];
			columnID = data.r.attr("id").split(",")[1];
        } else {
            var parent = data.r.closest("th.column-header, td.bundled-row, td.bundled-row-extended, td.annotation-row, div.global-properties-container");
            target = parent.find("p." + label + ":eq(0)");
            columnType = parent.attr("id").split(",")[0];
			columnID = parent.attr("id").split(",")[1];
        }
		
		
        // Handle drop source object having children in the tree
        if (data.o.hasClass("jstree-open")) {
            var payload = $.trim($(data.o.find('a.jstree-clicked')).text());
        } else {
            var payload = $.trim($(data.o).text());
        }
		
        // Set the value now that we have done some validation (some...)
        // [RDFa]: also sets the RDFa to the text in the node
        //  * still need URI/prefix for whatever ontology the node comes from.
        var uri = $(data.o).attr("hierarchy_id"); // not sure but this may need to be altered as well?
		target.empty().append(payload);
        target.parent().css("color", "black");
		//console.log("colType: " + columnType + ", colID: " + columnID);
		
		if (columnType == "annotationRow") { // we're dealing with an annotation
			var annotationID = target.attr("id").split(",")[2];
			console.log("dropped onto annotation #" + annotationID);
			if ( sourceFacet == "class" || sourceFacet == "datatype" ) {
				console.log("dnd updating annotation object...");
				updateAnnotationObj(columnID,annotationID,uri,payload);
				//updateClassType(columnID,columnType,uri,payload,sourceFacet);
			}
			else if (sourceFacet=="objectProperty" || sourceFacet=="datatypeProperty" || sourceFacet=="annotationProperty") {
				console.log("dnd updating annotation predicate...");
				updateAnnotationPred(columnID,annotationID,uri,payload);
			}
		}
		
		else { // not an annotation
			// check the source facet and make the appropriate RDFa update
			if ( sourceFacet == "class" || sourceFacet == "datatype" ) {
				//console.log("dnd is calling updateClassType here");
				updateClassType(columnID,columnType,uri,payload,sourceFacet);
			}
			else if (sourceFacet=="objectProperty" || sourceFacet=="datatypeProperty" || sourceFacet=="annotationProperty") {
				//console.log("dnd is calling updateProp here");
				updateProp(columnID,columnType,uri);
			}
			else 
				console.log("sourceFacet = " + sourceFacet);
		}
		
        // Apply suggestion logic
        modifyLabelAsRestriction(target, sourceFacet);

        // Only apply to siblings in bundle if this is a bundle cell, and not a column header cell (TODO: tag bundle cell with a class, dont have me search for a select)
        var siblingSelect = target.closest("tr").siblings().filter(function () {
            return $(this).find("select").length == 1;
        });

        console.log("SiblingSelect:", siblingSelect.length);
        if ( siblingSelect.length != 0 ) {
            // Delegate this drop event to all siblings in bundle (if this is a bundle)
            // First, is this item in a bundle
            $.each(bundles, function(idx, bundle) {
                if ( $.inArray(parseInt(columnID), bundle.columns ) != -1 ) {
                    
                    // This ID is in a bundle
                    $.each(bundle.columns, function(idx2, column) { 
                        if ( column != columnID ) {

                            // apply same DnD to sibling items
                            var siblingTarget = $("#" + columnType + "\\," + column).find("p." + label + ":eq(0)");
                            siblingTarget.empty().append(payload);
                            siblingTarget.parent().css("color", "black");
                            updateProp(column, columnType, uri); //I hope this is what you need katie
                            
                            // Apply suggestion logic
                            console.log("passing:", label, siblingTarget, sourceFacet);
                            modifyLabelAsRestriction(siblingTarget, sourceFacet);
                        }
                    });

                    // Break out of each on find
                    return false;
                }
            });
        }
    }
};

// A function that given a DnD dropTarget and the sourceFacet of the drop, restrict a label on this cell
function modifyLabelAsRestriction(dropTarget, sourceFacet) {
    // Manipulate the class-label to reflect what was just dropped

    // Determine label from what was passed
    var label = ( dropTarget.hasClass("property-label") ? "class-label" : "property-label" );
    
    // Find the sibling label
    var siblingLabel = dropTarget.closest("tr").siblings().filter(function () {
        return $(this).find("p." + label).length == 1;
    });

    console.log("More", siblingLabel, $(siblingLabel), siblingLabel.find("td").css("color"));

    // Only apply if has not been set yet (indicated by color)
    if ( siblingLabel.find("td").css("color") == "rgb(255, 0, 0)" ) { // Oh jquery, making me say red in rgb...
        siblingLabel = siblingLabel.find("p." + label);

        // Now apply logic
        if ( ( sourceFacet == "annotationPropertiesFacet" || sourceFacet == "dataPropertiesFacet" ) && label == "class-label" ) {
            siblingLabel.empty().append("[datatype]");
        } else if (sourceFacet == "objectPropertiesFacet" && label == "class-label" ) {
            siblingLabel.empty().append("[class]");
        } else if ( sourceFacet == "datatypesFacet" && label == "property-label" ) {
            siblingLabel.empty().append("[datatype or annotation property]");
        } else if ( sourceFacet == "classesFacet" && label == "property-label" ) {
            siblingLabel.empty().append("[object property]");
        } else {
            console.log("Source of DnD invalid, can't apply logic over label!");
        }
    }
}


// Extract a string from the jsTree (this is silly, re-write code so this is not needed)
function toString(obj, level) {
    if (typeof (level) === 'undefined') level = 0;
    var ret = "";
    if (typeof obj == "object") {
        ret += "\n";
        // for (var j = 0; j < level; ++j) ret += " ";
        // ret += "\u007B\n"; // left curly brace
        for (i in obj)
            if (!$.isEmptyObject(obj[i])) {
                for (var j = 0; j < level; ++j) ret += " ";
                ret += toString(i) + ": " + toString(obj[i], level + 2) + "\n" + "\n";
            }
    } else {
        ret += obj;
    }
    return ret;
}

//  Extracting comments, putting them in the comment box for a facet jsTree
$(window).bind("rendered_tree.semanteco", function (e, div) {
    $("div.loading").hide();
    $(div).addClass("jstree-default");
    $(div).delegate("a", "click", function (event, data) {
        event.preventDefault();

        // TODO: this needs to be looked at badly.
        var a = $.jstree._focused().get_selected();
        if (a.length > 0) {
            var lookup = $("#ClassTree div.jstree").data("hierarchy.lookup");
            var comments = lookup[a.attr("hierarchy_id")].rawData.comment;
            $("#ClassBox").html("<p class=\"ellipses marginOverride\">" + toString(comments, 0) + "</p>");
        }   
    });
});

// =====================================================================
// ====================== On Document Ready Calls ======================
// =====================================================================

$(document).ready(function () {

    // Check on mouseenter if ellipses are being used, qtip if they are (works on dynamicly created qtips)
    $('body').on('mouseenter' ,'.ellipses', function(e) {
        if (this.offsetWidth < this.scrollWidth) {
            $(this).qtip({
                content: {
                    text: function () {
                        if ( $(this).text().length != 0 ) {
                            return $(this).text();
                        } else if ( $(this).val().length != 0 ) {
                            return $(this).val();
                        } else {
                            return "Unknown";
                        }
                    }
                },
                overwrite: false, // Don't overwrite tooltips already bound
                show: {
                    event: e.type, // Use the same event type as above
                    ready: true // Show immediately - important!
                },
                position: {
                    my: 'bottom center',  // Position my top left...
                    at: 'top center', // at the bottom right of...
                    target: $(this) // my target
                }
            });
        } else {
            if (this.offsetWidth >= this.scrollWidth) {
                $(this).qtip('hide'); 
            }
        }
    });

    // Bind various click and form event listeners once the DOM is good to go
    $('#menu-commit-enhancement').click(function () {
		var turtle = turtleGen();
		AnnotatorModule.queryForEnhancingParams({"turtle":turtle}, function (d) {
			results = jQuery.parseJSON(d);
			var paramsURL = results.paramsFile;
			var rdfURL = results.rdfDataFile;
			console.log(paramsURL);
			console.log(rdfURL);
			var params = (document.getElementById("params-link-here")).getElementsByTagName('a')[0];
			var rdf = (document.getElementById("rdf-link-here")).getElementsByTagName('a')[0];
			$(params).attr('href',paramsURL);
			$(rdf).attr('href',rdfURL);
			//params.innerHTML = "parameters file";
			//rdf.innerHTML = "RDF file";
			$("#finalLinksModal").dialog({
				modal: true,
				width: 800,
				draggable: false,
				resizable: false,
				buttons: {
					Done: function () {
						$(this).dialog("close");
					}
				}// /buttons
			});// /dialog
		});// /queryForEnhancingParams
	});// /menu-commit-enhancement

    $('input#menu-show-globals').click(function () {
        if ( $('div.global-properties-container').is(":visible") ) {
            $('div.global-properties-container').slideUp();
        } else {
            $('div.global-properties-container').slideDown();
        }
    });

    // TODO: rewrite these in jquery syntax
    //document.getElementById('the_form').addEventListener('submit', handleFileSelect, false);
    document.getElementById('the_file').addEventListener('change', fileInfo, false);

    // Import File button shows modal for import
    $('#menu-import-file').click(function () {
        $("#fileDialogWrapper").dialog({
            modal: true,
            width: 800,
            draggable: false,
            resizable: false,
            buttons: {
                Import: function () {
                    if ( $("input#importSystemInput").is(":checked") ) {
                        handleFileSelect();    
                    } else {
                        handleUrlSelect();
                    }
                    // close dialog
                    $(this).dialog("close");
                }
            }
        });
    });

    /* $( ".selector" ).dialog( "close" ); */

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

    // getListofOntologies() call, builds the dropdown and then fills the facets
    AnnotatorModule.getListofOntologies({}, function (d) {
        d = $.parseJSON(d);
        if (d.length > 0) {
            var dropDown = $("<select></select>").attr("id", "checkboxDropDownOntologies").attr("name", "testCheckboxDropDown").attr("multiple", "multiple");
            d.sort();
            for (var i = 0; i < d.length; i++) {
                dropDown.append("<option>" + d[i] + "</option>");
            }

            // Push our dropdown to the DOM
            $("#ontology-dropdown").prepend(dropDown);
            // Turn it into what we want!
            $("#checkboxDropDownOntologies").dropdownchecklist({
                emptyText: "Select an Ontology ...",
                onComplete: function (selector) {
                	var values = []; // [RDFa]: can use this for prefixes?
					for (i = 0; i < selector.options.length; i++) {
					    if (selector.options[i].selected && (selector.options[i].value != "")) {
					        values.push(selector.options[i].value);
					    }
					}
					// Qeury given user selection
					queryOntologies(values);
                }
            });
        }
    });
});

// Query for ontologies. Broken into own function as can be called from multiple places
function queryOntologies(ontologies) {

	// No ontologies means no change to dropdown selection
	if ( ontologies == undefined ) {
		var current = $.bbq.getState("listOfOntologies");
		if ( current == undefined ) {
			// Nothing was sent to the bbq state yet
			current = [];
		}
		ontologies = current;
	}

	// Build current state of ontologies
	var ontologies = ontologies.concat(customUserOntologies);

	$.bbq.pushState({
	    "listOfOntologies": ontologies
	});

	// Activate facets based on some simple conditionals
	var differenceRemove = $.grep(selectedOntologies, function(item) { return $.inArray(item, ontologies) < 0 }); // Credit to: http://stackoverflow.com/questions/10927722/jquery-compare-2-arrays-return-difference
	var differenceAdd = $.grep(ontologies, function(item) { return $.inArray(item, selectedOntologies) < 0 }); // Credit to: http://stackoverflow.com/questions/10927722/jquery-compare-2-arrays-return-difference
	var activeFacets = $("div#facets").size() > 0 ? getActiveFacets($("div#facets")) : [];

	// Debug
	console.log("Arrays", ontologies, selectedOntologies, differenceRemove, differenceAdd);

	selectedOntologies = ontologies;
	if (differenceAdd.length > 0 || ( differenceRemove.length > 0 && ontologies.length > 0 )) {
		// Show user we are about to re-populate the facets with new jstrees
		$(".hierarchy").empty().append("<div class=\"loading\"><img src=\""+SemantEco.baseUrl+"images/spinner.gif\" /><br />Loading...</div>");
	 	
	 	// Look at all the facets TODO: don't reference by index but by name! Breaks if the items are shuffled around
	 	for (var i = 0; i < 5; i++) {
	 		if ($.inArray(i, activeFacets) < 0) {
	 			console.log("activate facet #", i);
	 			setActiveFacet($("div#facets"), i);	
	 		}
	 	}

	    // Call patrice's new silly init call thingy ( :D )
	    AnnotatorModule.initOWLModel({}, function (d) {
	        
	        // Clean up, then Re-query facets
	        $(".hierarchy").empty();

	        SemantEcoUI.HierarchicalFacet.create("#ClassTree", AnnotatorModule, "queryClassHM", "classes", {
	            "dnd": dnd,
	            "plugins": ["dnd"]
	        });
	        SemantEcoUI.HierarchicalFacet.create("#PropertyTree", AnnotatorModule, "queryObjPropertyHM", "objProperties", {
	            "dnd": dnd,
	            "plugins": ["dnd"]
	        });
	        SemantEcoUI.HierarchicalFacet.create("#dataPropertiesTree", AnnotatorModule, "queryDataPropertyHM", "dataProperties", {
	            "dnd": dnd,
	            "plugins": ["dnd"],
	        });
	        SemantEcoUI.HierarchicalFacet.create("#annotationPropertiesTree", AnnotatorModule, "queryAnnoPropertyHM", "annoProperties", {
	            "dnd": dnd,
	            "plugins": ["dnd"]
	        });
	        SemantEcoUI.HierarchicalFacet.create("#DataTypeTree", AnnotatorModule, "queryDataTypesHM", "dataTypes", {
	            "dnd": dnd,
	            "plugins": ["dnd"]
	        });
	        SemantEcoUI.HierarchicalFacet.create("#PaletteTree", AnnotatorModule, "nullnullnull", "nullnullnull", {
	            "dnd": dnd,
	            "plugins": ["dnd"],
	            "populate": false
	        });
	    });
	}
}

// =====================================================================
// ====================== ACCESSORY / MISC. FUNCTIONS ==================
// =====================================================================

// Bind to clicks on editables (text to input to text)
$(function () {
    $('body').on('click' ,'p.editable-input', function(e) {
		var thingID = this.id;
        console.log("Clicked editable, ID: " + thingID);
        var input = $('<input />', {'type': 'text', 'name': 'anEditable', 'value': $(this).html(), "class": $(this).attr('class')});
        $(this).parent().append(input);
        this.style.display="none";
        input.focus();
    });

    $('body').on('blur' ,'input.editable-input', function(e) {
		var original = $(this).parent().find("p.editable-input");
		original.html($(this).val());
		original[0].style.display="block";
        $(this).remove();
    });
});

// Bind to clicks on dropdowns (implicit and explicit bundles)
$(function () {
    var selectPreviousValue;

    $('body').on('focus' ,'select.bundle-select', function(e) {
        selectPreviousValue = $("option:selected", this).text().split(" ")[0];
    });

    $('body').on('change' ,'select.bundle-select', function(e) {
        console.log("Saw Change:", this);
        var newValue = $("option:selected", this).text().split(" ")[0]; // argh jqueryyyy
        var _this = this; // keep track of scope on triggering select item
        var bundleId = $(this).closest("td").attr("id").split(",")[1];
        var bundleDropdowns = $("th select.bundle-select").filter(function () { return $(this).closest("td").attr("id").split(",")[1] == bundleId });

        // Given the bundleId of this select, get the actual bundle from the bundles array
        var bundle = undefined;
        $.each(bundles, function (index, aBundle) {
            if ( aBundle._id == bundleId) {
                bundle = aBundle;
                return false; // break out
            }
        });

        if (newValue == "New") {
            // Changed to implicit (From explicit)            
            
            // First, change the vars of the bundle accordingly
            bundle.implicitId = bundleIdManager.requestImplicitID();

            // Now update all dropdowns of this bundle with the new value (and move them if necessary)
            $.each(bundleDropdowns, function (index, aDropdown) {
                console.log("trying to zero", aDropdown, aDropdown.selectedIndex);
                if ( aDropdown.selectedIndex != 0 ) {
                    console.log("zero", aDropdown)
                    aDropdown.selectedIndex = 0;
                }
                $("option:eq(0)", aDropdown).val("Implicit Bundle " + bundle.implicitId).text("Implicit Bundle " + bundle.implicitId);
            });

        } else if (newValue == "Column") {
            // Changed to Column
            // Set Bundle Vars (get id from id of table)

            // Now update all dropdowns of this bundle with the new value
            $.each(bundleDropdowns, function (index, aDropdown) {
                // Move to same item in dropdown, don't do it for the original dropdown of course
                if (aDropdown != _this ) {
                    // Alter selection to match
                    var matchOption = $("option", aDropdown).filter( function() { return $(this).text() == $("option:selected", _this).text() });
                    console.log("MatchOption:", matchOption, matchOption.index(), aDropdown, aDropdown.selectedIndex);
                    aDropdown.selectedIndex = matchOption.index();
                }
                
                // If this was not just column to column but Implicit to Column, switch first element to "New Implicit Bundle" (and return the id)
                console.log("prevValue", selectPreviousValue);
                if ( selectPreviousValue == "Implicit" ) {
                    $("option:eq(0)", aDropdown).val("New Implicit Bundle").text("New Implicit Bundle");
                    
                    // Return ID if we can
                    if ( bundle.implicitId != undefined ) {
                        bundleIdManager.returnImplicitID(bundle.implicitId);
                        bundle.implicitId = undefined;
                    }
                }
            });
        }
    });
});

// Load a CSV file from a URL
function handleUrlSelect() {
    $.bbq.pushState({ "csvUri": $("input#fileDialogFileURL").val() });

    // Show modal
    $("#data_info_form").dialog({
        modal: true,
        width: 800,
        buttons: {
            Ok: function () {
                //var uriPrefix = addPackageLevelData();
                //var prefixes = createPrefix(uriPrefix);
                //d3.select("#here-be-rdfa").attr("rdfa:prefix", prefixes);
                GreenTurtle.attach(document,true);
                $(this).dialog("close");
            }
        }
    });    

    AnnotatorModule.getCSVFile({}, function (d) {
        console.log(d);
        buildTable(d);
    });
}

// Load a CSV file from the user system
$(function () {
    $('body').on('click' ,'input#menu-show-data-info-form', function(e) {
        $("p.info-form-temp-notifier").hide();
        // Show modal
        $("#data_info_form").dialog({
            modal: true,
            width: 800,
            buttons: {
                Ok: function () {
                    //var uriPrefix = addPackageLevelData();
                    //var prefixes = createPrefix(uriPrefix);
                    //d3.select("#here-be-rdfa").attr("rdfa:prefix", prefixes);
                    GreenTurtle.attach(document,true);
                    $(this).dialog("close");
                }
            }
        });    
    });
});

// Load a CSV file from the user system
$(function () {
    $('body').on('click' ,'input#menu-add-new-ontology', function(e) {
        // Show modal
        $("#addOntologyModal").dialog({
            modal: true,
            width: 800,
            buttons: {
                Load: function () {
                	customUserOntologies.push($('input#addOntologyModalInput').val());
                	queryOntologies(undefined);
                    $(this).dialog("close");
                }
            }
        });    
    });
});

// if the row is hidden (ie, this is the first comment added), show the row
function checkAnnotationRow(){
	var cRow = document.getElementById("annotations");
	if (cRow.classList.contains("hide-while-empty")) {
		$(cRow).removeClass("hide-while-empty");
	}
}// /checkAnnotationRow

// Accessory function for adding to the annotation row
// Takes three arguments:
// - index: the index of the column where the triple will be added
// - predicate: predicate for the triple, which will be shown in the table as well
//	 as well as added to the RDFa. Note that this may be hard-coded depending on 
//	 what context menu function calls this.
// - object: the object of the triple. This will most likely be user-input!
function addAnnotation( index, predicate, object ){
	var workingCol = document.getElementById("annotationRow," + index);
	var theTable = workingCol.getElementsByTagName('TABLE')[0];
	console.log("Creating annotation #" + annotationID);
	$( theTable ).append( "<tr id=\"anno,"+ annotationID + "\">" +
	"<td class=\"hidden\">" + index + "</td>" + 
	"<td><p id=\"anno,"+index+","+annotationID+",pred\" class=\"ellipses marginOverride property-label editable-input\">" + predicate + "</p></td>" +
	"<td><p id=\"anno,"+index+","+annotationID+",obj\"class=\"ellipses marginOverride class-label editable-input\">" + object + "</p></td>" +
	"</tr>" );
	annotationID++;
}// /addAnnotation

// Accessory function for removing things from arrays
// Takes two arguments:
//  - the array
//  - the item to be removed
// And returns the array minus that object.
function removeA(arr) {
    var what, a = arguments,
        L = a.length,
        ax;
    while (L > 1 && arr.length) {
        what = a[--L];
        while ((ax = arr.indexOf(what)) !== -1) {
            arr.splice(ax, 1);
        }
    }
    return arr;
}// /removeA

// Accessory function for checking to see if a thing is
//    in an array.
// Takes two arguments:
//  - the array
//  - the item to look for
// And returns TRUE if the object is there or FALSE if not
function existsA(theArray, theThing) {
    if (theArray.indexOf(theThing) === -1) {
        return false;
    } else
        return true;
}// /existsA

// Given a bundle ID, returns the bundle that has that ID.
// Used for assigning properties and things.
function getBundleById(theID){
	var theBundle;
	console.log("Checking for bundle #" + theID);
	$.each(bundles, function(i,checkBundle){
		console.log("... bundle#" + checkBundle._id + "?");
		if ( checkBundle._id === theID ){	
			theBundle = checkBundle;
		}
	});
	if( !theBundle ){
		console.log("Bundle does not exist!");
		return;
	}
	return theBundle;
}// /getBundleById


// These are accessory functions for updating bundle objects based on user drag-and-drop.
// They need the bundle ID, and whatever is being updated for each.
function updateResource(bundleID){
	console.log("update resource is being called");
	var theForm = document.getElementById("bundle-resource-select," + bundleID);
	var newResource = theForm.options[theForm.selectedIndex].value;
	var theBundle = getBundleById(bundleID);
	console.log("Setting bundle #" + bundleID + "'s resource to " + newResource);
	theBundle.setResource(newResource);
}// /updateResource

function updateBundleProp(bundleID, theURI){
	var theBundle = getBundleById(bundleID);
	theBundle.setProp(theURI);
}// /updateBundleProp

function updateBundleClassType(bundleID, theURI){
	var theBundle = getBundleById(bundleID);
	theBundle.setType(theURI);
}// /updateBundleClassType

function updateBundleName(bundleID, theNameTemp){
	var theBundle = getBundleById(bundleID);
	theBundle.setName(theNameTemp);
}// /update

// Accessory function for creating a bundle 
// Takes three arguments:
//  - id: a unique static ID for the bundle, different from
//    its index in the bundles array.
//  - columns: the array of column indices of the columns to
//    be placed in the bundle.
//  - resource: the index of the column that describes the
//    bundle, IF it is EXPLICIT. A value of -1 indicates the
//    bundle is IMPLICIT; this is the default set here at 
//    creation.
// 	- **NOTE** That if resourceIsBundle == TRUE, then resource
// 		represents a bundle ID! Otherwise, it represents a column number!
// Prop and Type are null at creation, and must be set later
//	via drag and drop.
function Bundle(bundleId, implicitId, columns) {
    this._id = "b" + bundleId;
    this.implicitId = implicitId;
    this.columns = columns;
	this.resource = -1;
	this.nameTemp = "";
	this.type = "";
	this.prop = "";
}

Bundle.prototype.setResource = function(index){
	this.resource = index;
}

Bundle.prototype.getResource = function(){
	return this.resource;
}

Bundle.prototype.setType = function(typeURI){
	this.type = typeURI;
}

Bundle.prototype.getType = function(){
	return this.type;
}

Bundle.prototype.setProp = function(propURI){
	this.prop = propURI;
}

Bundle.prototype.getProp = function(){
	return this.prop;
}

Bundle.prototype.setName = function(theName){
	this.nameTemp = theName;
}

Bundle.prototype.getName = function(){
	return this.nameTemp;
}

Bundle.prototype.isExplicit = function() {
    if (this.implicitId != undefined && this.implicitId != -1) {
        return false;
    } else {
        return true;
    }
}

Bundle.prototype.isImplicit = function() {
    return !this.isExplicit();
}

// Manage the Ids for bundles. Can assign Ids, and Ids can be returned freeing them up for another bundle to use
function BundleIdManager() {
    this.bundleIds = new Queue();
    this.implicitIds = new Queue();
    this.curBundleId = 0;
    this.curImplicitId = 0;
}

BundleIdManager.prototype.requestImplicitID = function() {
    if (this.implicitIds.getLength() == 0) {
        return this.curImplicitId++;
    } else {
        return this.implicitIds.dequeue();
    }
}

BundleIdManager.prototype.returnImplicitID = function(id) {
    this.implicitIds.enqueue(id);
}

BundleIdManager.prototype.requestBundleID = function() {
    if (this.bundleIds.getLength() == 0) {
        return this.curBundleId++;
    } else {
        return this.bundleIds.dequeue();
    }
}

BundleIdManager.prototype.returnBundleID = function(id) {
    this.bundleIds.enqueue(id);
}

// We extend the accordion function of jquery to allow multiple items open at a time
$.fn.accordion = function(opts){
    var acc, toggle ;

    // Default options
    opts = opts || {
        "active":   0
    };
    
    toggle = function(target) {
        if(opts.ontoggle !== undefined) {
            if(typeof(opts.ontoggle) !== 'function') {
                console.log("opts.ontoggle is not a function");
            }
            else if(opts.ontoggle(target)===false)
                return;
        }
        $(target)
        .toggleClass("ui-accordion-header-active ui-state-active ui-state-default ui-corner-bottom")
        .find("> .ui-icon").toggleClass("ui-icon-triangle-1-e ui-icon-triangle-1-s").end()
        .next().slideToggle();
    };
    
    acc = this.each(function(){
        $(this).addClass("ui-accordion ui-accordion-icons ui-widget ui-helper-reset")
                  .find("h3")
                  .addClass("ui-accordion-header ui-helper-reset ui-accordion-icons ui-state-default ui-corner-top ui-corner-bottom")
                  .hover(function() { $(this).toggleClass("ui-state-hover"); })
                  .prepend('<span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-e"></span>')
                  .click(function() {
                      toggle(this);
                    return false;
                  })
                  .next()
                    .addClass("ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom")
                    .hide();
    });
    $.each($(this).find("h3"), function(i) {
            if(opts.active !== undefined) {
                if(typeof(opts.active) === 'object') {
                    if(opts.active.indexOf(i) !== -1)
                        toggle(this);
                }
                else if(typeof(opts.active) === 'number') {
                    if(opts.active === i)
                        toggle(this);
                }
            }
    });
    
    return acc;
};

// Accordion Helper Functions
function setActiveFacet(target, index) {
	$.each($(target).find("h3"), function(i) {
		//console.log("searching to activate", i, index, this);
		if(i == index) {
			$(this).toggleClass("ui-accordion-header-active ui-state-active ui-state-default ui-corner-bottom")
    			.find("> .ui-icon").toggleClass("ui-icon-triangle-1-e ui-icon-triangle-1-s").end()
    			.next().slideToggle();
    		return false; // break out of loop
		}
	});
};

function getActiveFacets(target) {
    var isActive = [];
    $.each($(target).find("h3"), function(i) {
        if($(this).hasClass("ui-state-active"))
            isActive.push(i);
    });
    return isActive;
};


// Attach the .compare method to Array's prototype to call it on any array | Credit: http://stackoverflow.com/questions/7837456/comparing-two-arrays-in-javascript
Array.prototype.compare = function (array) {
    // if the other array is a falsy value, return
    if (!array)
        return false;

    // compare lengths - can save a lot of time
    if (this.length != array.length)
        return false;

    for (var i = 0; i < this.length; i++) {
        // Check if we have nested arrays
        if (this[i] instanceof Array && array[i] instanceof Array) {
            // recurse into the nested arrays
            if (!this[i].compare(array[i]))
                return false;
        }
        else if (this[i] != array[i]) {
            // Warning - two different object instances will never be equal: {x:20} != {x:20}
            return false;
        }
    }
    return true;
}

// ==========================================
// = HERE LIES THE CODE ARCHIVE / GRAVEYARD =
// =           TRESSPASSERS BEWARE          =
// ==========================================


// Working a new way to generate the column headers....
/*
    function createSubtable(text, colIndex) { 
        var theTable = document.createElement('table');
        var nameRow = document.createElement('tr');
        var theName = document.createElement('td');
        
        var propRow = document.createElement('tr');
        var theProp = document.createElement('td');
        $(theProp).attr("class", "droppableProp");
        $(theProp).attr("id", "propertyRow,"+colIndex);
        
        var classRow = document.createElement('tr');
        var theClass = document.createElement('td');
        $(theClass).attr("class", "droppableClass");
        $(theClass).attr("id", "classRow,"+colIndex);
    }// /createSubtable */



// Tooltip function
// This just enables the mouseover tooltips.
//$(function () {
//    $(document).tooltip();
//});

// This is the jsTree for DATA TYPES
// To help clarify plugins:
//   "dnd" means "drag-and-drop"
//   "crrm" means "create, rename, remove and move" [nodes]
// They all have documentation on the jsTree main page

/*
    $(function() {
    $("#DataTypeTree").jstree({
        
        "themes" : {
                 "theme" : "apple",
                 "dots"  : true,
                 "icons" : true,
                 "url": "../../js/jstree/themes/apple/style.css"
            },// /themes
        
        "json_data": {
            "data": [
                {
                    "attr": { "id": "" },
                    "data": "[ Data Types ]",
                    "state": "open",
                    "children": [
                        {
                            "data": "xsd:string",
                            "attr": { "id": "1_datatype" }
                        },
                        {
                            "data": "xsd:boolean",
                            "attr": { "id": "2_datatype" }
                        },
                        {
                            "data": "xsd:decimal",
                            "attr": { "id": "3_datatype" }
                        },
                        {
                            "data": "xsd:float",
                            "attr": { "id": "4_datatype" }
                        },
                        {
                            "data": "xsd:double",
                            "attr": { "id": "5_datatype" }
                        },
                        {
                            "data": "xsd:integer",
                            "attr": { "id": "6_datatype" }
                        },
                        {
                            "data": "xsd:anyURI",
                            "attr": { "id": "7_datatype" }
                        }
                    ]
                }
            ]
        }, // /json_data
        
        // drag and drop for DATA TYPES 
        // From the documentation:
        //   data.o - the object being dragged
        //   data.r - the drop target
        // * The drop target will be the node in which the mouse button
        //   is released, NOT the node in which the object text will appear!
        "dnd" : {
            "drop_target" : "#list td",
            "drop_finish" : function (data) {
                $(data.r).css("color","black");
            //add the node name to the header, and get the parent node "id"
                // p = node id of the datathing from the JS tree
                var p = data.o.attr("id");
                // q = text in the datathing from the JS tree,
                //     essentially, the data type name itself
                var q = $(data.o).text();
                
                // i = id of the destination cell where the user drops the thing dragged from the JS tree
                var i = data.r.attr("id");
                // r = the text of that cell
                var r = $(data.r).text();
                
                // cn = column number
                var cn = i.split(',')[1];
                
                $("[id='classRow,"+cn+"']").text(q.replace(/^\s+|\s+$/g,''));
                
            // window.annotationMappings = {"RangeClass":[{"classname":"url"}], "Property":[{"propertyname": "url"}]};
                
            // var args = {};
            // args[r] = p;
           
                function keySearch(dict,key) {
                    for (var i in dict) if (key in dict[i]) 
                        return dict[i][key];
                    return null;                                                
                }
            // These are for the row-adding function, if there is no propertyRow or classRow
            // * This is currently not in use (???)
                var am = window.a
                var kg = keySearch(am,r);
                var args = {};
                args[r]  = {"DataType":p};
          
                if (kg!=null){                          
                    kg["DataType"] = p;                     
                }            
                else{
                    am.push(args);
                };
            }// /dropfinish function
        }, // /dnd
        
        "crrm" : {
            "move" : {
                "check_move" : function (data) {
                    if(data.r.attr("id") == "") {
                        
                    }
                }
            }
        },
        "plugins": ["themes", "json_data", "ui", "dnd", "crrm"]
        });// /jsTree
    });// /jsTree function
    */
// ** NOT CURRENTLY IN USE ** 
// ( drag-and-drop is currently handled via a jsTree plugin, NOT jQuery UI )
// Responsible for draggable and droppable functions for classes
//   and properties. They are separate from each other, to assist
//   with target highlighting.
/*
$(function () {
    // Draggable and Droppable for PROPERTIES
    $(".draggable-prop").draggable(
        {helper: function( event ) {
                    return $( "<div style=\"color:#aaa\">property!</div>");
                }// /helper
            }
    ); // /draggable-prop
    $(".droppable-prop").droppable({
        accept: ".draggable-prop",
        activeClass: "ui-state-hover",
        drop: function (event, ui) {
            $(this)
                .addClass("ui-state-highlight");
        } // /drop
    }); // /droppable-prop

    // Draggable and Droppable for CLASSES
    $(".draggable-class").draggable(
        {helper: function( event ) {
                    return $( "<div style=\"color:#aaa\">class!</div>");
                }// /helper
            }
    ); // /draggable-class
    $(".droppable-class").droppable({
        accept: ".draggable-class",
        activeClass: "ui-state-hover",
        drop: function (event, ui) {
            $(this)
                .addClass("ui-state-highlight");
        } // /drop function
    }); // /droppable-class
}); // /all draggable and droppable functions

// archive of robins old drag and drop code for js tree
var dnd = {

    "drop_target": "#list th",
    "drop_finish": function (data) {
        console.log("Drag: Finished | " + data);
        //add the node name to the header, and get the parent node "id"
        // p = node id of the datathing from the JS tree
        var p = data.o.attr("hierarchy_id");
        // q = text in the datathing from the JS tree,
        //     essentially, the data type name itself
        var q = $(data.o).text();
        // i = id of the destination cell where the user drops the thing dragged from the JS tree
        var i = data.r.attr("id");
        // r = the text of that cell
        var r = $(data.r).text();
        // cn = column number
        var cn = i.split(',')[1];

        $("[id='classRow," + cn + "']").empty().append("<p class=\"ellipses marginOverride\" style=\"color:black\">" + $.trim(q) + "</p>");
        //$("[id='classRow," + cn + "']").text(q.replace(/^\s+|\s+$/g, ''));

        //  window.annotationMappings = {"RangeClass":[{"classname":"url"}], "Property":[{"propertyname": "url"}]};

        //    var args = {};
        //    args[r] = p;

        function keySearch(dict, key) {
            for (var i in dict)
                if (key in dict[i])
                    return dict[i][key];
            return null;
        }

        var am = window.a
        var kg = keySearch(am, r);
        var args = {};
        args[r] = {
            "RangeClass": p
        };

        if (kg != null) {
            kg["RangeClass"] = p;
        } else {
            am.push(args);
        };
    } // /drop finish
};


// A context menu for selecting ontologies for the Class Hierarchy.
// Eventually, we'll want to populate this from some listing of all the ontologies we have?
// Right now, for sake of simplicity and testing, this is hard-coded and has no callbacks 
//    written....
$(function () {
    $.contextMenu({
        selector: '.ontology-selector',
        // This is the default callback, which will be used for any functions
        //    that do not have their own callbacks specified. It echoes the 
        //    key of the selection to the console.
        callback: function (key, options) {
            var m = "clicked: " + key;
            console.log(m);
        },
        items: {
            "semantEcoWater": {
                name: "SemantEco-Water",
                type: 'checkbox'
            },
            "prov": {
                name: "PROV",
                type: 'checkbox'
            },
            "wgs": {
                name: "WGS",
                type: 'checkbox'
            },
            "void": {
                name: "VOID",
                type: 'checkbox'
            },
        } // /items
    }); // /context menu

}); // /ontology-selector


*/

/*
// Arguments for Drag and Drop for class facets ( this applies to the jstree library. see: jstree.com)
var dnd_classes = {
    "drop_target": ".column-header, .bundled-row, .bundled-row-extended, .annotation-row",
    "drop_check": function (data) {
        if ( data.r.is("td.bundled-row") || data.r.is("td.bundled-row-extended") || data.r.is("td.annotation-row") ) {
            if ( data.r.children().length == 0 ) { 
                return false; 
            }
        }
        return true;
    },
    "drop_finish": function (data) {
        // We need to determine where we are now that a drop has happened. First, get the ID of the column we are in, next get the respective label for where we dropped
        var target, columnID, columnType;

        if (data.r.is("p.class-label")) {
            target = data.r;
			columnType = data.r.closest("th.column-header, td.bundled-row, td.bundled-row-extended, td.annotation-row").attr("id").split(",")[0];
            columnID = data.r.closest("th.column-header, td.bundled-row, td.bundled-row-extended, td.annotation-row").attr("id").split(",")[1];
        } else if ( data.r.is("th.column-header") || data.r.is("td.bundled-row") || data.r.is("td.bundled-row-extended") || data.r.is("td.annotation-row") ) {
            target = data.r.find("p.class-label:eq(0)");
			columnType = data.r.attr("id").split(",")[0];
            columnID = data.r.attr("id").split(",")[1];
        } else {
            var parent = data.r.closest("th.column-header, td.bundled-row, td.bundled-row-extended, td.annotation-row");
            target = parent.find("p.class-label:eq(0)");
			columnType = parent.attr("id").split(",")[0];
            columnID = parent.attr("id").split(",")[1];
        }

        console.log(columnType + " " + columnID);


        // handle source object having children
        if (data.o.hasClass("jstree-open")) {
            var payload = $.trim($(data.o.find('a.jstree-clicked')).text());
        } else {
            var payload = $.trim($(data.o).text());
        }

        var targetParent;
        if (data.r.hasClass("column-header") && data.r.is("th") && data.r.attr("id") != undefined) {
            targetID = data.r.attr("id").split(",")[1];
        } else if () {
            asdawd
        } else {
            // get the header of this element
            var parentHeader = data.r.parents("th:eq(0)");
            if (parentHeader != undefined && parentHeader.attr("id") != undefined) {
               targetID = parentHeader.attr("id").split(",")[1];
            }
        }


        // Set the value now that we have done some validation (some...)
		// [RDFa]: also sets the RDFa to the text in the node
		//  * still need URI/prefix for whatever ontology the node comes from.
        //var fullID = "[id='classRow," + targetID + "']";
		var uri = $(data.o).attr("hierarchy_id"); // not sure but this may need to be altered as well?
		target.empty().append(payload);
        target.parent().css("color", "black");
		updateClassType(columnID,columnType,uri,payload,sourceFacet);
        // Manipulate the property-label to reflect what was just dropped
        // First find the property-label
        var propertyLabel = target.closest("tr").siblings().filter(function () {
            return $(this).find("p.property-label").length == 1;
        });
		
		// Now get which fact the drop target was from
		var sourceFacet = data.o.closest("div.facet").attr("id");
        
		// Only apply if class has not been set yet
        if ( propertyLabel.find("td").css("color") == "rgb(255, 0, 0)" ) { // Oh jquery, making me say red in rgb...
            propertyLabel = propertyLabel.find("p.property-label");

            // Now apply logic
            if ( sourceFacet == "datatypesFacet" ) {
                propertyLabel.empty().append("[datatype or annotation property]");
            }
        }
		
    }// /drop_finish
};// /dnd_classes
*/