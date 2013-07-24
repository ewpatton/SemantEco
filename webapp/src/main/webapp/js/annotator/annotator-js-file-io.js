// Brendan Edit: Breakout the file I/O code to its own file
// The code in this file handles reading from a file, building a table to reflect the file structure, and any other calls or binds cocerning working with external files

// link testing
var link_reg = /(http:\/\/|https:\/\/)/i;

// Called when a file is imported, displays some file metadata to the user
function fileInfo(e) {
    var file = e.target.files[0];
    window.file_name = file.name;
    if (file.name.split(".")[1].toUpperCase() != "CSV") {
        alert('Invalid csv file !');
        e.target.parentNode.reset();
        return;
    } else {
        document.getElementById('file_info').innerHTML = "<p>File Name: " + file.name + " | " + file.size + " Bytes.</p>";
    }
}

//csv file parser and table loader
// This is what actually renders the table in the #list div!
function handleFileSelect(event) {
    if (window.hasTable == 1) return;
    var file = document.getElementById("the_file").files[0];
    var reader = new FileReader();
    
    // Show modal
    $("#data_info_form").dialog({
        modal: true,
        width: 800,
        buttons: {
            Ok: function () {
				var uriPrefix = addPackageLevelData();
				var prefixes = createPrefixList(uriPrefix);
				d3.select("#here-be-rdfa").attr("rdfa:prefix", prefixes);
				GreenTurtle.attach(document,true);
                $(this).dialog("close");
            }
        }
    });

    // Read the file in!
    reader.readAsText(file);

    // Called when the file is loaded in fully
    reader.onload = function (file) {
        buildTable(file.target.result);
    }; // /reader.onload

    $('#data_info_form').removeClass("hidden");
	d3.ns.qualify("rdfa", "http://www.w3.org/ns/rdfa.html");
}

// Remove duplicates by using a 'set' in javascript. Wait, no set in JS? No problem, JS objects act as sets, so use one of those. :)
function dedupe(items) {
   var set = {};
   for (var i = 0; i < items.length; i++)
      set[items[i]] = true;
   var clean = [];
   for (var item in set)
      clean.push(parseInt(item));
   return clean;
}

function buildTable(data) {
    var rows = data.split(/[\r\n|\n]+/);
    console.log(rows.length);
    var table = document.createElement('table');

    // We have seen some examples of CSV files where there are extraneous trailing columns at the end
    //    of the file. (eg: data,data,data,,,,,,,,,,) This variable will be used to count and then
    //    remove them in order to improve the table rendering and hopefully save some time.
    // * This assumes that the empty column headers to be removed are all at the end of the first row!
    // These are counted up as the column header row does its thing, and this value is output in console.log()
    //    once it knows how many extra rows there are, for error checking and so forth.
    var extraColumns = 0;

    // Generate column groups
    var group = document.createElement('colgroup');
    var oneCol;
    // Create subtabled column headers
    var thr = document.createElement('tr'); 
    $(thr).attr("class", 'col-selectable'); 
    $(thr).attr("id", 'col-headers');       
    var tharr = rows[0].split(',');
    var numcols = tharr.length;
    // Create row for bundled properties
    // * This is empty at table generation, and hidden by default
    var btr = document.createElement('tr');
    $(btr).attr("class", 'hide-while-empty');
    $(btr).attr("id", 'bundles');
    // Create row for extended bundled properties
    // * This is empty at table generation, and hidden by default
    var btrx = document.createElement('tr');
    $(btrx).attr("class", 'hide-while-empty');
    $(btrx).attr("id", 'bundles-extended');
    // Create row for annotations (and additional triples
    // * This row is also empty and hidden at table generation
    // * This row includes empty tables at creation for adding to later.
    var atr = document.createElement('tr');
    $(atr).attr("class", 'hide-while-empty');
    $(atr).attr("id", 'annotations');
    //$(atr).attr("style", 'display:none');
    var th, btd, atd, annTable;
    for (var j = 0; j < numcols; j++) { // i == 0 because this is only concerned with the column headers
        if (!tharr[j]) { // if we have extra columns trailing at the end,
            extraColumns++;
        } else {
            // This creates the portion in the DOM that will hold the RDFa!
            if (tharr[j] != ''){
                createEnhancementNode(tharr[j], j);
            }
            
            // colgroup
            oneCol = document.createElement('col');
            $(oneCol).attr("id", "colgroup," + j);
            group.appendChild(oneCol);
            // column header
            th = document.createElement('th');
            $(th).attr("class", "ui-widget-content the-context-menu column-header not-bundled");
            // $(th).attr("class", "the-context-menu"); // disabled for ease of debugging other things...
            $(th).attr("id", '0,' + j);
            d3.select(th).attr("rdfa:typeof","conversion:enhance");
            th.innerHTML= (createSubtable(tharr[j], j));
            thr.appendChild(th);
            // bundled column row
            btd = document.createElement('td');
            $(btd).attr("id", 'bundledRow,' + j);
            $(btd).attr("class", 'bundled-row');
            btr.appendChild(btd);
            // bundled column row
            btdx = document.createElement('td');
            $(btdx).attr("id", 'bundledRow-extended,' + j);
            $(btdx).attr("class", 'bundled-row-extended');
            btrx.appendChild(btdx);
            // annotation row
            atd = document.createElement('td');
            $(atd).attr("id", 'annotationRow,' + j);
            $(atd).attr("class", 'annotation-row');
            annTable = document.createElement('table');
            $(annTable).attr("class", 'annotation-table');
            atd.appendChild(annTable);
            atr.appendChild(atd);
        }// /else
    } // /for
    table.appendChild(group);
    table.appendChild(thr);
    table.appendChild(btr);
    table.appendChild(btrx);
    table.appendChild(atr);
    numcols = numcols - extraColumns;
    console.log("removed " + extraColumns + " extra blank columns");
    // Once the header row with subtables has been created,
    //   continue and create the rest of the table. 
    // This is actually data from the CSV file!
    var maxRows = 20;
    if (maxRows > rows.length) {
        maxRows = rows.length;
    }
    for (var i = 1; i < maxRows; i++) {
        var tr = document.createElement('tr');
        var arr = rows[i].split(',');
        var td;
        for (var j = 0; j < numcols; j++) {
            td = document.createElement('td');
            $(td).attr("id", i + ',' + j);
            if (link_reg.test(arr[j])) { // if the thing from the CSV file is a link
                var a = document.createElement('a');
                a.href = arr[j];
                a.target = "_blank";
                a.innerHTML = arr[j];
                td.appendChild(a);
            } else { // the thing is not a link
                td.innerHTML = arr[j];
            }
            tr.appendChild(td);
        } // /for each column in a row
        table.appendChild(tr);
    } // /for rows

    // Push to DOM and reveal to user
    $('#list').append(table).removeClass("hidden");
    GreenTurtle.attach(document,true);

    // Make the column headers selectable and updates the currentlySelected
    //    array accordingly when things are selected or unselected.
    $(function () {
        $("tr.col-selectable").selectable({
            filter: "th.column-header",
            autoRefresh: true,
            selected: function (event, ui) {
                var index = parseInt($(ui.selected).attr("id").split(",")[1]);
                currentlySelected.push(index);
                currentlySelected = dedupe(currentlySelected);
             
                $.each(bundles, function(idx, bundle) {
                    if ($.inArray(index, bundle.columns) != -1) {
                        // Okay, found one of the bundles tied to this item (can be more), see if any of the other items in the bundle are selected or not, select the ones that are not
                        $.each(bundle.columns, function(idx, item) {
                            if ($.inArray(item, currentlySelected) == -1) {
                                $("th#0\\," + item + ".column-header").addClass("ui-selected");
                                currentlySelected.push(parseInt(item));
                            }
                        });
                    }
                });

                //$("colgroup,"+index).addClass("selected-col");
                // We have added new items to the array, so sort it ascending by index
                currentlySelected.sort();
                console.log("selecting col " + index + "....");
                console.log("currently Selected: " + currentlySelected);
            }, // /selected
            unselected: function (event, ui) {
                //console.log(this, $(this), event, ui);
                var index = parseInt($(ui.unselected).attr("id").split(",")[1]);
                var indexInSelected = currentlySelected.indexOf(index);
                console.log(currentlySelected, index, indexInSelected);
                currentlySelected.splice(indexInSelected, 1);

                console.log("unselecting col " + index + "....");
                console.log("currently Selected: " + currentlySelected);
            }
        });
    }); 

    window.file_contents = data;
    window.column_numbers = rows[0].split(',').length;
    window.hasTable = 1;
    // annotationMappings initialization  
    window.a = [];
    // window.a.push({"annotationMappings":[{"initialization":"0"}]});
    // send the csv file to the server
    AnnotatorModule.readCsvFileForInitialConversion({
            "csvFile": window.file_contents
        },
        function (d) {
            console.log(d);
        });
}
