/* -*- espresso-indent-level: 2; tab-width: 8; -*- */
var map = null;
var geocoder = null;
var pollutedwatersource = new Array();
var violatedfacility = new Array();
var pollutedMarkers = new Array();
var cleanMarkers = new Array();
var violatedMarker = new Array();
var facilityMarker = new Array();
var state;
var countyCode;
var start;
var limit = 5;
var stateCode = "";
var healthEffect = "";
var curPage = 0;
var wqpMarkers = {
	"pollutedWater" : [],
	"cleanWater" : [],
	"pollutedFacility" : [],
	"facility" : [],
	"flood" : []
}
var stateAbbr2Code = [];
stateAbbr2Code["RI"] = "US:44";
stateAbbr2Code["CA"] = "US:06";
stateAbbr2Code["MA"] = "US:25";
stateAbbr2Code["NY"] = "US:36";
stateAbbr2Code["WA"] = "US:53";
var pagedData = [];
var lat, lng;
var zipcode;
var countyFips;
var industry;
//
var reg = new Array(3);
// Array that contains the name of the regulations for human
reg['human'] = new Array(5);
reg['human']['EPA'] = "EPA Regulation";
reg['human']['ca'] = "CA Regulations";
reg['human']['ma'] = "MA Regulations";
reg['human']['ny'] = "NY Regulations";
reg['human']['ri'] = "RI Regulations";
// Array that contains the name of the regulations for aquatic life
reg['aquatic'] = new Array(2);
reg['aquatic']['EPA'] = "EPA regulation for aquatic life";
reg['aquatic']['ne'] = "NE regulation for aquatic life";
// Array that contains the name of the regulations for canadian goose
reg['CanadaGoose'] = new Array(2);
reg['CanadaGoose']['EPA'] = "EPA regulation for aquatic life";
reg['CanadaGoose']['ne'] = "NE regulation for aquatic life";

//
esciencePrefix = "http://escience.rpi.edu/ontology/semanteco/2/0/";
sparqlPrefix = "http://sparql.tw.rpi.edu/ontology/semanteco/2/0/";
var regOwl = new Array(2);
// Array for the owl files of the regulations for human
regOwl['human'] = new Array(5);
regOwl['human']['EPA'] = "EPA-regulation.owl";
regOwl['human']['ca'] = "ca-regulation.owl";
regOwl['human']['ma'] = "ma-regulation.owl";
regOwl['human']['ny'] = "ny-regulation.owl";
regOwl['human']['ri'] = "ri-regulation.owl";
regOwl['aquatic'] = new Array(2);
regOwl['aquatic']['EPA'] = "epa-aqua-acute-regulation.owl";
regOwl['aquatic']['ne'] = "species-regulation.owl";

// naics code
var naicsCode = new Array();
naicsCode['all'] = 'All Data';
naicsCode['11'] = 'Agriculture, Forestry, Fishing and Hunting';
naicsCode['21'] = 'Mining, Quarrying, and Oil and Gas Extraction';
naicsCode['22'] = 'Utilities';
naicsCode['31-32-33'] = 'Manufacturing';
naicsCode['42'] = 'Wholesale Trade';
naicsCode['44-45'] = 'Retail Trade';
naicsCode['48-49'] = 'Transportation and Warehousing';
naicsCode['51'] = 'Information';
naicsCode['52'] = 'Finance and Insurance';
naicsCode['53'] = 'Real Estate and Rental and Leasing';
naicsCode['54'] = 'Professional, Scientific, and Technical Services';
naicsCode['55'] = 'Management of Companies and Enterprises';
naicsCode['56'] = 'Administrative and Support and Waste Management and Remediation Services';
naicsCode['61'] = 'Educational Services';
naicsCode['62'] = 'Health Care and Social Assistance';
naicsCode['71'] = 'Arts, Entertainment, and Recreation';
naicsCode['72'] = 'Accommodation and Food Services';
naicsCode['81'] = 'Other Services (except Public Administration)';
naicsCode['92'] = 'Public Administration';

// to store the highlighted water bodies, so that they can be de-highlighted later
var highlight = [];

// Terminology explanations from http://nhd.usgs.gov/NHDDataDictionary_model2.0.pdf
var nhd_term_ex = {
		"ComID": "Integer value that uniquely identifies the occurrence of " +
				"each feature in the National Hydrology Dataset (NHD).",
		"Permanent_": "Identifier, 40-char GUID value that uniquely identifies " +
				"the occurrence of each feature in The National Map.",
		"FDate": "Date of last feature modification.",
		"Resolution": "Source resolution.	Domain of values: 1 = Local >1:12,000;" +
				" 2 = High 1:24,000/12,000; 3 = Medium 1:100,000",
		"GNIS_ID": "Unique identifier assigned by GNIS, 10-char value. " +
				"leading zeros may be missing on some features, null if no name " +
				"associated with the feature",
		"GNIS_Name": "Proper name, specific term, or expression by which a particular " +
				"geographic entity is known, null if no name is associated with the feature",
		"LengthKM": "Length of linear feature based on Albers Equal Area",
		"AreaSqKm": "Area of areal feature based on Albers Equal Area",
		"Elevation": "The vertical distance from a given datum, in meters. Stage " +
				"of the water elevation is encoded in the FCode.",
		"ReachCode": "Unique identifier for a 'reach'. The first eight numbers are " +
				"the WBD_HUC8. The next six numbers are randomly assigned, " +
				"sequential numbers that are unique within a HUC8, 14-char value.",
		"FlowDir": "Direction of flow relative to coordinate order. Domain of values: " +
				"1 = With digitized; 0 = Uninitialized",
		"WBAreaComID": "The ComID of the waterbody through which the flowline flows." +
				"Applies only to Artificial Path FType; null or '0' for all other FTypes.",
		"WBArea_Permanent_ID": "Permanent_Identifier of the waterbody	through which " +
				"the flowline flows. Applies only to Artificial Path FType; null or 0 " +
				"for all other FTypes.",
		"FType": "Three-digit integer value; unique identifier of a feature type.",
		"FCode": "Five-digit integer value; comprised of the feature type and " +
				"combinations of characteristics and values. Only some features " +
				"have attributes; last two digits are '00' if no additional values " +
				"are encoded."
};

// what plot_bird_count() generates, copied from firebug window, will figure out 
// how to get this string in a better way
var birdcount_plot_html = "<div id='plot'><svg width='480' height='250'><g transform='translate(40,40)'><clipPath id='clip'><rect width='400' height='170'/></clipPath><path class='area' clip-path='url(#clip)' d='M0,0.6800000000000068C0.30146045300657004,2.0120832389881596,25.35202456255394,164.55094191708915,37.12574850299401,164.73S58.89055223009507,164.55988020399602,70.65868263473054,164.56S95.7931491410262,167.90260697422417,107.73453093812375,166.26S131.5954425430549,97.53380427328958,143.6626746506986,96.39S171.3552943220488,159.9836906045715,180.7884231536926,165.07S204.54372226760867,158.79475367777582,216.71656686626747,158.61S241.4693041232776,163.37358499328047,253.84231536926146,163.54S278.7925102566441,163.7440255296109,290.96806387225547,163.71S314.7342242281937,162.92767353373216,326.89620758483034,162.52S351.8449895042267,161.71806784308194,364.0219560878244,162.01S394.0170173188822,163.4272978446875,400,163.71L400,170C394.0036593479707,170,376.20592149035264,170,364.0219560878244,170S339.07185628742513,170,326.89620758483034,170S303.14371257485027,170,290.96806387225547,170S266.2175648702595,170,253.84231536926146,170S228.89221556886227,170,216.71656686626747,170S192.96407185628743,170,180.7884231536926,170S155.8383233532934,170,143.6626746506986,170S119.9018629407851,170,107.73453093812375,170S82.4268130405855,170,70.65868263473054,170S48.90219560878243,170,37.12574850299401,170S6.187624750499001,170,0,170Z'/><g class='x axis' transform='translate(0,170)'><line class='tick minor' style='opacity: 1;' y2='-170' x2='0' transform='translate(18.562874251497004,0)'/><line class='tick minor' style='opacity: 1;' y2='-170' x2='0' transform='translate(52.09580838323353,0)'/><line class='tick minor' style='opacity: 1;' y2='-170' x2='0' transform='translate(89.17165668662675,0)'/><line class='tick minor' style='opacity: 1;' y2='-170' x2='0' transform='translate(125.09980039920158,0)'/><line class='tick minor' style='opacity: 1;' y2='-170' x2='0' transform='translate(162.2255489021956,0)'/><line class='tick minor' style='opacity: 1;' y2='-170' x2='0' transform='translate(198.15369261477045,0)'/><line class='tick minor' style='opacity: 1;' y2='-170' x2='0' transform='translate(235.27944111776446,0)'/><line class='tick minor' style='opacity: 1;' y2='-170' x2='0' transform='translate(272.4051896207585,0)'/><line class='tick minor' style='opacity: 1;' y2='-170' x2='0' transform='translate(308.33333333333337,0)'/><line class='tick minor' style='opacity: 1;' y2='-170' x2='0' transform='translate(345.4590818363273,0)'/><line class='tick minor' style='opacity: 1;' y2='-170' x2='0' transform='translate(381.437125748503,0)'/><g style='opacity: 1;' transform='translate(0,0)'><line class='tick' y2='-170' x2='0'/><text y='3' x='0' dy='.71em' text-anchor='middle'>2007</text></g><g style='opacity: 1;' transform='translate(37.12574850299401,0)'><line class='tick' y2='-170' x2='0'/><text y='3' x='0' dy='.71em' text-anchor='middle'>Feb</text></g><g style='opacity: 1;' transform='translate(70.65868263473054,0)'><line class='tick' y2='-170' x2='0'/><text y='3' x='0' dy='.71em' text-anchor='middle'>Mar</text></g><g style='opacity: 1;' transform='translate(107.73453093812375,0)'><line class='tick' y2='-170' x2='0'/><text y='3' x='0' dy='.71em' text-anchor='middle'>Apr</text></g><g style='opacity: 1;' transform='translate(143.6626746506986,0)'><line class='tick' y2='-170' x2='0'/><text y='3' x='0' dy='.71em' text-anchor='middle'>May</text></g><g style='opacity: 1;' transform='translate(180.7884231536926,0)'><line class='tick' y2='-170' x2='0'/><text y='3' x='0' dy='.71em' text-anchor='middle'>Jun</text></g><g style='opacity: 1;' transform='translate(216.71656686626747,0)'><line class='tick' y2='-170' x2='0'/><text y='3' x='0' dy='.71em' text-anchor='middle'>Jul</text></g><g style='opacity: 1;' transform='translate(253.84231536926146,0)'><line class='tick' y2='-170' x2='0'/><text y='3' x='0' dy='.71em' text-anchor='middle'>Aug</text></g><g style='opacity: 1;' transform='translate(290.96806387225547,0)'><line class='tick' y2='-170' x2='0'/><text y='3' x='0' dy='.71em' text-anchor='middle'>Sep</text></g><g style='opacity: 1;' transform='translate(326.89620758483034,0)'><line class='tick' y2='-170' x2='0'/><text y='3' x='0' dy='.71em' text-anchor='middle'>Oct</text></g><g style='opacity: 1;' transform='translate(364.0219560878244,0)'><line class='tick' y2='-170' x2='0'/><text y='3' x='0' dy='.71em' text-anchor='middle'>Nov</text></g><g style='opacity: 1;' transform='translate(400,0)'><line class='tick' y2='-170' x2='0'/><text y='3' x='0' dy='.71em' text-anchor='middle'>Dec</text></g><path class='domain' d='M0,-170V0H400V-170'/></g><g class='y axis' transform='translate(400,0)'><g style='opacity: 1;' transform='translate(0,170)'><line class='tick' x2='6' y2='0'/><text x='9' y='0' dy='.32em' text-anchor='start'>0</text></g><g style='opacity: 1;' transform='translate(0,136)'><line class='tick' x2='6' y2='0'/><text x='9' y='0' dy='.32em' text-anchor='start'>200</text></g><g style='opacity: 1;' transform='translate(0,102)'><line class='tick' x2='6' y2='0'/><text x='9' y='0' dy='.32em' text-anchor='start'>400</text></g><g style='opacity: 1;' transform='translate(0,68)'><line class='tick' x2='6' y2='0'/><text x='9' y='0' dy='.32em' text-anchor='start'>600</text></g><g style='opacity: 1;' transform='translate(0,34)'><line class='tick' x2='6' y2='0'/><text x='9' y='0' dy='.32em' text-anchor='start'>800</text></g><g style='opacity: 1;' transform='translate(0,0)'><line class='tick' x2='6' y2='0'/><text x='9' y='0' dy='.32em' text-anchor='start'>1,000</text></g><path class='domain' d='M6,0H0V170H6'/></g><path class='line' clip-path='url(#clip)' d='M0,0.6800000000000068C0.30146045300657004,2.0120832389881596,25.35202456255394,164.55094191708915,37.12574850299401,164.73S58.89055223009507,164.55988020399602,70.65868263473054,164.56S95.7931491410262,167.90260697422417,107.73453093812375,166.26S131.5954425430549,97.53380427328958,143.6626746506986,96.39S171.3552943220488,159.9836906045715,180.7884231536926,165.07S204.54372226760867,158.79475367777582,216.71656686626747,158.61S241.4693041232776,163.37358499328047,253.84231536926146,163.54S278.7925102566441,163.7440255296109,290.96806387225547,163.71S314.7342242281937,162.92767353373216,326.89620758483034,162.52S351.8449895042267,161.71806784308194,364.0219560878244,162.01S394.0170173188822,163.4272978446875,400,163.71'/><text x='200' y='0' text-anchor='middle' font-size='12px'>Bird count for canadian geese in Washington state in 2007</text><text x='200' y='0' text-anchor='middle' dy='2.5ex' font-size='12px'>Provenance: http://www.avianknowledge.net/</text></g></svg></div>";
	
function isChecked(str) {
	var el = document.getElementById(str);
	return el.checked;
}

function closeHelpers() {
	if (codesWindow)
		codesWindow.close();
}

function showhide(str) {
	var check = document.getElementById(str);
	if (check.checked) {
		for ( var i = 0; i < window.wqpMarkers[str].length; i++) {
			window.wqpMarkers[str][i].show();
		}
	} else {
		for ( var i = 0; i < window.wqpMarkers[str].length; i++) {
			window.wqpMarkers[str][i].hide();
		}
	}
}

function initialize() {
	if (GBrowserIsCompatible()) {
		map = new GMap2(document.getElementById("map_canvas"));
		map.setCenter(new GLatLng(37.4419, -122.1419), 10);
		var c = new GLargeMapControl();
		map.addControl(c, c.getDefaultPosition());
		geocoder = new GClientGeocoder();
	}

	document.getElementById("species").selectedIndex = 1;
	onchange_species_selection();
	// for industry facet
	fill_industry_selection();
	
//	$("body").append("<div id='plot'></div>");
//	plot_bird_count();
//	$("#birdcount").toggle(function($e) {
//		$e.preventDefault();
//		$("table#p").hide();
//		$("#plot").show();
//		$("#birdcount").text("Water Body Properties");
//	}, function($e) {
//		$e.preventDefault();
//		$("#plot").hide();
//		$("table#p").show();
//		$("#birdcount").text("Bird Count");
//	});

}

function plot_bird_count() {
	// for bird count display
	var m = [40, 40, 40, 40],
    w = 480 - m[1] - m[3],
    h = 250 - m[0] - m[2],
    parse = d3.time.format("%b %Y").parse;

	// Scales and axes. Note the inverted domain for the y-scale: bigger is up!
	var x = d3.time.scale().range([0, w]),
	y = d3.scale.linear().range([h, 0]),
	xAxis = d3.svg.axis().scale(x).tickSize(-h).tickSubdivide(true),
	yAxis = d3.svg.axis().scale(y).ticks(4).orient("right");

	// An area generator, for the light fill.
	var area = d3.svg.area()
	    .interpolate("monotone")
	    .x(function(d) { return x(d.date); })
	    .y0(h)
	    .y1(function(d) { return y(d.number); });

	// A line generator, for the dark stroke.
	var line = d3.svg.line()
	    .interpolate("monotone")
	    .x(function(d) { return x(d.date); })
	    .y(function(d) { return y(d.number); });

	d3.csv("readme2.csv", function(data) {
		// Filter to one symbol; the bird count.
		var values = data.filter(function(d) {
			return d.symbol == "Bird Count";
		});
		// Parse dates and numbers. We assume bird counts are sorted by date.
		data.forEach(function(d) {
			d.date = parse(d.date);
		});
		// Compute the minimum and maximum date, and the maximum number.
		x.domain([ values[0].date, values[values.length - 1].date ]);
		y.domain([ 0, d3.max(values, function(d) {
			return d.number;
		}) ]).nice();
		// Add an SVG element with the desired dimensions and margin.
		var svg = d3.select("#plot")
					.append("svg:svg")
					.attr("width", w + m[1] + m[3])
					.attr("height", h + m[0] + m[2])
					.append("svg:g")
					.attr("transform", "translate(" + m[3] + "," + m[0] + ")");
		// Add the clip path.
		svg.append("svg:clipPath")
			.attr("id", "clip")
			.append("svg:rect")
			.attr("width", w)
			.attr("height", h);
		// Add the area path.
		svg.append("svg:path")
			.attr("class", "area")
			.attr("clip-path", "url(#clip)")
			.attr("d", area(values));

		// Add the x-axis.
		svg.append("svg:g")
			.attr("class", "x axis")
			.attr("transform", "translate(0," + h + ")")
			.call(xAxis);

		// Add the y-axis.
		svg.append("svg:g")
			.attr("class", "y axis")
			.attr("transform", "translate(" + w + ",0)")
			.call(yAxis);

		// Add the line path.
		svg.append("svg:path")
			.attr("class", "line")
			.attr("clip-path", "url(#clip)")
			.attr("d", line(values));

		// Add caption for the plot.
		svg.append("svg:text")
			.attr("x", w / 2)
			.attr("y", 0)
			.attr("text-anchor", "middle")
			.attr("font-size", "12px")
			.text("Bird count for canadian geese in Washington state in 2007");

		svg.append("svg:text")
			.attr("x", w / 2)
			.attr("y", 0)
			.attr("text-anchor", "middle")
			.attr("dy", "2.5ex")
			.attr("font-size", "12px")
			.text("Provenance: http://www.avianknowledge.net/");

	});
//	console.log(d3.select("#plot").html());

}

function fill_industry_selection() {
	var industry_sel = document.getElementById("industry_selection_canvas");
	industry_sel.innerHTML = "";
	for ( var i in naicsCode) {
		append_selection_element(industry_sel, i, naicsCode[i]);
	}
}

function onchange_species_selection() {
	var regTable = document.getElementById("regDiv");
	regTable.innerHTML = "";
	var species = $("#species").val();
	// alert(species);
	if (species == "" || species == "Human") {
		$("#spinner").css("display", "block");
		for ( var i in reg['human']) {
			// alert(reg['human'][i]);
			append_radio_element(regTable, "regulation", esciencePrefix
					+ regOwl['human'][i], "unchecked", reg['human'][i]);
		}
		console.log(highlight);
		$(highlight).each(function() {
			map.removeOverlay(this);
		});
		$("#spinner").css("display", "none");
	}
	if (species == "" || species == "Aquatic-life") {
		$("#spinner").css("display", "block");
		for ( var i in reg['aquatic']) {
			// alert(reg['aquatic'][i]);
			append_radio_element(regTable, "regulation", sparqlPrefix
					+ regOwl['aquatic'][i], "unchecked", reg['aquatic'][i]);
		}
		$(highlight).each(function() {
			map.removeOverlay(this);
		});
		$("#spinner").css("display", "none");
	}

	if (species == "CanadaGoose") {
		$("#spinner").css("display", "block");
		for ( var i in reg['CanadaGoose']) {
			append_radio_element(regTable, "regulation", sparqlPrefix
					+ regOwl['aquatic'][i], "unchecked", reg['aquatic'][i]);
		}
		var zip = $('#zip').val();
		//alert(zip);
		if (zip != '98103' && zip != '02809')
			$("#spinner").css("display", "none");
		else {
			highlight = [];
			if (zip == "98103") huc8waterbodyHighlight("canada_goose_wa_huc8.json", 
					"water-bodies.json");
			else if (zip == "02809") huc8waterbodyHighlight("canada-goose-ri-huc8.json",
					"ri-waterbody.json");
			$("#spinner").css("display", "none");
		}
	}

	var spcIndex = 'human';
	if (species == "Aquatic-life")
		spcIndex = 'aquatic';
	if (species == "CanadaGoose")
		spcIndex = 'CanadaGoose';
	console.log(reg[spcIndex]['EPA']);
	document.getElementById(reg[spcIndex]['EPA'].replace(/ /g, "_"))
			.setAttribute("checked", "checked");
}

/**
 * filter water bodies from waterbodyfile by huc8file, only highlight those water bodies 
 * covered by one of the huc8 areas in the huc8file and are in between southWest and 
 * northEast bounds and are more than 0.1 square kilometer in size
 * @param huc8file the name of a file containing huc8 codes used to filter water bodies 
 * to be highlighted
 * @param waterbodyfile the name of the water body file, containing a GeoJSON object
 */
function huc8waterbodyHighlight(huc8file, waterbodyfile) {
	var bounds = map.getBounds();
	var southWest = bounds.getSouthWest();
	var northEast = bounds.getNorthEast();
	$.getJSON(huc8file, function(ret) {
		var huc = ret.HUC_8;
		$.getJSON(waterbodyfile, function(json) {
				$(json.features).each(function() {
					if (this.properties.AreaSqKm > 0.1) {
						var feature = this;
						$(huc).each(function() {
							if (feature.properties.ReachCode.indexOf(this) === 0) {
								var coords = feature.geometry.coordinates;
								var lng = coords[0][0][0];
								var lat = coords[0][0][1];
								// highlight the water body only if the first point
								// of the water body falls in the viewport of the map,
								if (lng < northEast.lng() && lat < northEast.lat()
									&& lng > southWest.lng() && lat > southWest.lat())
									highlightPolygon(coords, feature.properties);
							}
						});
					}
				});
			});
		});
}

/**
 * highlight a polygon extracted from the geometry attribute of a GeoJSON object
 * 
 * @param polygon
 *            the polygon to highlight polygon ::= [outline, hole-1, hole-2,
 *            ..., hole-n] outline, hole-k ::= [coord-1, coord-2, ..., coord-m]
 *            coord-k ::= [longitude, latitude] longitude, latitude ::= floating
 *            number such as -119.08
 * @param properties
 *            the properties of the water body recorded in the GeoJSON object
 */
function highlightPolygon(polygon, properties) {
	var first = true;
	$(polygon).each(
			function() {
				var poly_coords = [];
				$(this).each(function() {
					poly_coords.push(new GLatLng(this[1], this[0]));
				});
				var polygon1 = null;
				if (first) {
					polygon1 = new GPolygon(poly_coords, "#fff", 0, 0, "#00f",
							0.5);
					GEvent.addListener(polygon1, "mouseover", function() {
						$(this).css('cursor', 'pointer');
					});
					GEvent.addListener(polygon1, "mouseout", function() {
						$(this).css('cursor', 'auto');
					});
					GEvent.addListener(polygon1, "click", function() {
						//TODO: find out a better way to get birdcount_plot_html, which is 
						//generated by plot_bird_count()
						map.openInfoWindowTabsHtml(polygon1.getBounds().getCenter(),
								[new GInfoWindowTab("Water Body Properties", 
										makeTable(properties)), 
								new GInfoWindowTab("Bird Count", 
										birdcount_plot_html)]);						
					});
					first = false;
				} else {
					polygon1 = new GPolygon(poly_coords, "#fff", 0, 0, "#fff",
							0.5);
				}
				highlight.push(polygon1);
				map.addOverlay(polygon1);
			});
}

/**
 * make an html table from a properties field from a GeoJSON object
 * 
 * @param properties
 *            the properties where the data in the table come from
 * @return a table containing all the items in the properties
 */
function makeTable(properties) {
	var ret = "<table id='p'>";
	
	for ( var p in properties) if (p != "Shape_Leng" && p != "Shape_Area")
			ret += "<tr><td title=\"" + nhd_term_ex[p] + "\">" + 
			p + "</td><td>" + properties[p] + "</td></tr>";
	
	ret += "<tr><td><b>Provenance:</b></td><td></td></tr>";
	ret += "<tr><td>water body shapes</td><td>ftp://www.ecy.wa.gov/gis_a/hydro/nhd/NHDmajor.zip</td></tr>";
	ret += "<tr><td>bird distribution</td><td>http://www.avianknowledge.net/</td></tr>";
	ret += "<tr><td>terminology explanation</td>" +
		"<td>National Hydrography Dataset Data Dictionary " +
		"http://nhd.usgs.gov/NHDDataDictionary_model2.0.pdf</td></tr>";
	ret += "</table>";
//	ret += "<div id='plotarea'><a id='birdcount' href='#'>Bird Count</a>" +
//		"<div id='plot'></div></div>";

	return ret;
}

function showReportSite(state) {
	var reportSiteUrl = "http://was.tw.rpi.edu/swqp/reportSite.php";
	$.ajax({
		type : "GET",
		url : reportSiteUrl,
		dataType : "xml",
		success : function(data) {
			$(data).find("Organization").each(
					function() {
						if ($(this).find("hasState").text() == state) {
							var site = $(this).find("hasReportSite").text();
							$("#reportSite").html(
									"Report Environmental Problems: <a href=\""
											+ site + "\">" + site + "</a>");
							return;
						}
					});
		}
	});
}

/**
 * show markers on the map given a zip code
 * 
 * @param address
 *            zip code
 */
function showAddress(address) {
	if (address.length != 5) {
		alert("The input zip code is not valid! Please check and input again.");
		return;
	}
	window.pagedData = [];
	window.curPage = 0;
	var waterquery = "";
	var facilityquery = "";
	if (geocoder) {
		geocoder.getLatLng(address, function(point) {
			if (!point) {
				alert(address + " not found");
			} else {
				map.setCenter(point, 10);
			}
		});

		$("#spinner").css("display", "block");
		$
				.ajax({
					type : "GET",
					url : thiszipagent, // SPARQL service URI
					data : "code=" + address, // query parameter
					dataType : "json",
					success : function(data) {
						state = data.result.stateAbbr;
						showReportSite(state);
						thisStateCode = data.result.stateCode;						
						if (thisStateCode == undefined)
							thisStateCode = stateAbbr2Code[state];
						stateCode = thisStateCode.split(":")[1];
						countyCode = data.result.countyCode;
						countyCode = countyCode.replace("US:", "");// strip the
						// "US:"
						countyFips = countyCode.replace(":", "");// strip the
						// ":"
						// alert(countyFips);
						countyCode = countyCode.split(":")[1];
						countyCode = countyCode.replace(/^0+/, "");
						lat = data.result.lat;
						lng = data.result.lng;

						var contaminants = $("#characteristicName").val();
						var effects = $("#health").val();
						var time = $("#time").val();
						var sources = JSON.stringify($.map(
								$('[name="source"]:checked'), function(x) {
									return x.value;
								}));
						var regulation = $('[name="regulation"]:checked').val();
						industry = $("#industry_selection_canvas").val();
						// alert(industry);
						// getLimitData(sources, regulation, contaminants,
						// effects, time, industry);
						var bounds = map.getBounds();
						var southWest = bounds.getSouthWest();
						var northEast = bounds.getNorthEast();
						var lngLow = southWest.lng();
						var lngHigh = northEast.lng();
						var latLow = southWest.lat();
						var latHigh = northEast.lat();
						getAllData(sources, regulation, contaminants, effects,
								time, industry, lngLow, lngHigh, latLow,
								latHigh);
					},
					error : function(data) {
						window
								.alert("Unable to determine enough information about your location.");
						$("#spinner").css("display", "none");
					}
				});
	}
}

function deCodeZip() {
	var address = document.getElementById("zip").value;
	if (address.length != 5) {
		alert("The input zip code is not valid! Please check and input again.");
		return;
	}
	window.curPage = 0;
	var waterquery = "";
	var facilityquery = "";
	if (geocoder) {
		geocoder.getLatLng(address, function(point) {
			if (!point) {
				alert(address + " not found");
			} else {
				map.setCenter(point, 10);
			}
		});

		$("#spinner").css("display", "block");
		$
				.ajax({
					type : "GET",
					url : thiszipagent, // SPARQL service URI
					data : "code=" + address, // query parameter
					dataType : "json",
					success : function(data) {
						state = data.result.stateAbbr;
						showReportSite(state);
						thisStateCode = data.result.stateCode;
						if (thisStateCode == undefined)
							thisStateCode = stateAbbr2Code[state];
						stateCode = thisStateCode.split(":")[1];
						countyCode = data.result.countyCode;
						countyCode = countyCode.replace("US:", "");// strip the
						// "US:"
						countyFips = countyCode.replace(":", "");// strip the
						// ":"
						// alert(countyFips);
						countyCode = countyCode.split(":")[1];
						countyCode = countyCode.replace(/^0+/, "");
						lat = data.result.lat;
						lng = data.result.lng;

						showHUC();
					},
					error : function(data) {
						window
								.alert("Unable to determine enough information about your location.");
						$("#spinner").css("display", "none");
					}
				});
	}
}

function showHUC() {
	// alert(countyFips);
	$
			.ajax({
				type : "GET",
				url : thisserviceagent,
				// data: "fips="+countyFips+"&method=getHUC8Codes",
				data : {
					"fips" : "\"" + countyFips + "\"",
					"method" : "getHUC8Codes"
				},
				dataType : "json",
				success : function(data) {
					if (data.error) {
						window.alert("An error occurred: " + data.errorString);
						return;
					}
					var hucArr = data.HUC_8;
					if (hucArr.length == 0) {
						window
								.alert("No data are available for this county at this time.");
					}
					// for(var i=0;i<hucArr.length;i++) {
					var hucStr = hucArr.join();
					window.alert(hucStr);
				},
				error : function(err) {
					window.alert("Unable to retrieve data from the server.");
					$("#spinner").css("display", "none");
				}
			});
}

/**
 * get data within a viewport
 * 
 * @param sources
 * @param regulation
 * @param contaminants
 * @param effects
 * @param time
 * @param industry
 * @param lngLow
 *            left bound of the viewport
 * @param lngHigh
 *            right bound of the viewport
 * @param latLow
 *            bottom bound of the viewport
 * @param latHigh
 *            top bound of the viewport
 */
function getAllData(sources, regulation, contaminants, effects, time, industry,
		lngLow, lngHigh, latLow, latHigh) {
	if (sources == null) {
		sources = JSON.stringify($.map($('[name="source"]:checked'),
				function(x) {
					return x.value;
				}));
		regulation = $('[name="regulation"]:checked').val();
		contaminants = $("#characteristicName").val();
		effects = $("#health").val();
		time = $("#time").val();
		industry = $("#industry_selection_canvas").val();
	}
	map.clearOverlays();
	// if (window.pagedData[curPage]) {
	// for ( var i = 0; i < window.pagedData[curPage].length; i++) {
	// var marker = window.pagedData[curPage][i];
	// map.addOverlay(marker);
	// if ((marker.siteData.isPolluted && !isChecked("pollutedWater"))
	// || (!marker.siteData.isPolluted && !isChecked("cleanWater")))
	// marker.hide();
	// else
	// marker.show();
	// }
	// return;
	// }
	$("#spinner").css("display", "block");
	$
			.ajax({
				type : "GET",
				url : thisserviceagent,
				data : {
					"sources" : sources,
					"regulation" : regulation,
					"contaminants" : contaminants,
					"effects" : effects,
					"time" : time,
					"industry" : industry,
					"countyCode" : countyCode,
					"state" : state,
					"lat" : lat,
					"lng" : lng,
					"lngLow" : lngLow,
					"lngHigh" : lngHigh,
					"latLow" : latLow,
					"latHigh" : latHigh,
					"method" : "getAllData",
					"limit" : JSON.stringify(window.limits)
				},
				dataType : "json",
				success : function(data) {
					// if(window.pagedData[curPage] == undefined)
					// window.pagedData[curPage] = [];
					var bindings = data.results.bindings;
					var found = {};
					if (bindings.length == 0) {
						window
								.alert("No data are available for this county at this time.");
					}
					for ( var i = 0; i < bindings.length; i++) {
						var result = bindings[i];
						var uri = result["s"].value;
						if (found[uri] == true)
							continue;
						found[uri] = true;
						var lat = result["lat"].value;
						var lng = result["long"].value;
						if (lng > 0)
							lng = -lng;
						var label = result["label"] ? result["label"].value
								: "";
						var facility = eval(result["facility"].value);
						var polluted = eval(result["polluted"].value);
						var site = {
							'uri' : uri,
							'label' : label,
							'isFacility' : facility,
							'isPolluted' : polluted
						};
						var iconfile;
						if (facility && polluted)
							iconfile = "image/facilitypollute.png";
						else if (facility && !polluted)
							iconfile = "image/facility.png";
						else if (!facility && polluted)
							iconfile = "image/pollutedwater.png";
						else if (!facility && !polluted)
							iconfile = "image/cleanwater2.png";
						var icon = new GIcon(G_DEFAULT_ICON, iconfile);
						icon.iconSize = new GSize(polluted ? 29 : 30, 34);
						var latlng = new GLatLng(lat, lng);
						markerOptions = {
							"icon" : icon,
							"title" : label
						};
						var marker = new GMarker(latlng, markerOptions);
						marker.siteData = site;
						GEvent.addListener(marker, "click",
								getDataCallback(marker));
						map.addOverlay(marker);
						if ((!facility && polluted && !isChecked("pollutedWater"))
								|| (!facility && !polluted && !isChecked("cleanWater"))
								|| (facility && polluted && !isChecked("pollutedFacility"))
								|| (facility && !polluted && !isChecked("facility")))
							marker.hide();
						// window.pagedData[curPage].push(marker);
						if (facility && polluted)
							wqpMarkers["pollutedFacility"].push(marker);
						else if (facility && !polluted)
							wqpMarkers["facility"].push(marker);
						else if (!facility && polluted)
							wqpMarkers["pollutedWater"].push(marker);
						else if (!facility && !polluted)
							wqpMarkers["cleanWater"].push(marker);
					}
					$("#spinner").css("display", "none");
				},
				error : function(err) {
					window.alert("Unable to retrieve data from the server.");
					$("#spinner").css("display", "none");
				}
			});
}

/**
 * show markers in a paged manner
 * 
 * @param sources
 * @param regulation
 * @param contaminants
 * @param effects
 * @param time
 * @param industry
 */
function getLimitData(sources, regulation, contaminants, effects, time,
		industry) {
	$
			.ajax({
				type : "GET",
				url : thisserviceagent,
				data : {
					"sources" : sources,
					"regulation" : regulation,
					"contaminants" : contaminants,
					"effects" : effects,
					"time" : time,
					"industry" : industry,
					"countyCode" : countyCode,
					"state" : state,
					"method" : "getLimitData"
				},
				dataType : "json",
				success : function(data) {
					if (data.error) {
						window.alert("An error occurred: " + data.errorString);
						return;
					}
					if (data.facilityCount + data.siteCount == 0) {
						window
								.alert("Our datasets do not contain your county at this time.");
						return;
					}
					window.facilityCount = (data.facilityCount ? data.facilityCount
							: 0);
					window.siteCount = (data.siteCount ? data.siteCount : 0);
					window.limits = {
						"facility" : {},
						"site" : {}
					};
					limits.facility["offset"] = 0;
					limits.site["offset"] = 0;
					if (facilityCount > siteCount) {
						limits.site["limit"] = Math.min(limit, siteCount);
						limits.facility["limit"] = Math.min(facilityCount, 2
								* limit - limits.site["limit"]);
					} else {
						limits.facility["limit"] = Math.min(limit,
								facilityCount);
						limits.site["limit"] = Math.min(siteCount, 2 * limit
								- limits.facility["limit"]);
					}
					generatePaging();
					getData(sources, regulation, contaminants, effects, time,
							industry);
				},
				error : function(err) {
					window.alert("Unable to retrieve data from the server.");
					$("#spinner").css("display", "none");
				}
			});
}

function changePage(p) {
	p = p - 1;
	var start = 2 * limit * p;
	var fac_start = limit * p;
	var site_start = limit * p;

	// update ui
	$("div#page a.selected").toggleClass("selected");
	$($("div#page a")[p]).toggleClass("selected");

	if (fac_start > facilityCount) {
		site_start = start - facilityCount;
		fac_start = facilityCount;
	} else if (site_start > siteCount) {
		fac_start = start - siteCount;
		site_start = siteCount;
	}

	limits.facility.offset = fac_start;
	limits.site.offset = site_start;

	if (fac_start + limit > facilityCount && site_start + limit > siteCount) {
		limits.facility.limit = Math.min(facilityCount - fac_start, limit);
		limits.site.limit = Math.min(siteCount - site_start, limit);
	} else if (fac_start + limit <= facilityCount
			&& site_start + limit <= siteCount) {
		limits.facility.limit = limit;
		limits.site.limit = limit;
	} else if (fac_start + limit > facilityCount) {
		limits.facility.limit = facilityCount - fac_start;
		limits.site.limit = 2 * limit - limits.facility.limit;
	} else if (site_start + limit > siteCount) {
		limits.site.limit = siteCount - site_start;
		limits.facility.limit = 2 * limit - limits.site.limit;
	}

	if (fac_start > facilityCount) {
		limits.facility.limit = 0;
	}
	if (site_start > siteCount) {
		limits.site.limit = 0;
	}

	curPage = p;
	getData();
}

function generatePagingCallback(i) {
	return function() {
		changePage(i);
		return false;
	};
}

function generatePaging() {
	var div = $("#page");
	div.empty();
	div.append("Page: ");
	var offset = limits.facility.offset + limits.site.offset;
	var limit = facilityCount + siteCount;
	var page = Math.floor(offset / (2 * window.limit)) + 1;
	var pages = Math.floor(limit / (2 * window.limit)) + 1;
	for ( var i = 1; i < page; i++) {
		var el = document.createElement("a");
		div.append(el);
		$(el).click(generatePagingCallback(i));
		$(el).attr("href", "#");
		$(el).text(i.toString());
		div.append(" ");
	}
	var el = document.createElement("a");
	div.append(el);
	$(el).click(generatePagingCallback(page));
	$(el).attr("href", "#");
	$(el).toggleClass("selected");
	$(el).text(page.toString());
	div.append(" ");
	for ( var i = page + 1; i <= pages; i++) {
		var el = document.createElement("a");
		div.append(el);
		$(el).click(generatePagingCallback(i));
		$(el).attr("href", "#");
		$(el).text(i.toString());
		div.append(" ");
	}
}

function getDataCallback(marker) {
	return function() {
		queryForWaterPollution(marker);
	};
}

function getData(sources, regulation, contaminants, effects, time, industry) {
	if (sources == null) {
		sources = JSON.stringify($.map($('[name="source"]:checked'),
				function(x) {
					return x.value;
				}));
		regulation = $('[name="regulation"]:checked').val();
		contaminants = $("#characteristicName").val();
		effects = $("#health").val();
		time = $("#time").val();
		industry = $("#industry_selection_canvas").val();
	}
	map.clearOverlays();
	if (window.pagedData[curPage]) {
		for ( var i = 0; i < window.pagedData[curPage].length; i++) {
			var marker = window.pagedData[curPage][i];
			map.addOverlay(marker);
			if ((marker.siteData.isPolluted && !isChecked("pollutedWater"))
					|| (!marker.siteData.isPolluted && !isChecked("cleanWater")))
				marker.hide();
			else
				marker.show();
		}
		return;
	}
	$("#spinner").css("display", "block");
	$
			.ajax({
				type : "GET",
				url : thisserviceagent,
				data : {
					"sources" : sources,
					"regulation" : regulation,
					"contaminants" : contaminants,
					"effects" : effects,
					"time" : time,
					"industry" : industry,
					"countyCode" : countyCode,
					"state" : state,
					"lat" : lat,
					"lng" : lng,
					"method" : "getData",
					"limit" : JSON.stringify(window.limits)
				},
				dataType : "json",
				success : function(data) {
					if (window.pagedData[curPage] == undefined)
						window.pagedData[curPage] = [];
					var bindings = data.results.bindings;
					var found = {};
					if (bindings.length == 0) {
						window
								.alert("No data are available for this county at this time.");
					}
					for ( var i = 0; i < bindings.length; i++) {
						var result = bindings[i];
						var uri = result["s"].value;
						if (found[uri] == true)
							continue;
						found[uri] = true;
						var lat = result["lat"].value;
						var lng = result["long"].value;
						if (lng > 0)
							lng = -lng;
						var label = result["label"] ? result["label"].value
								: "";
						var facility = eval(result["facility"].value);
						var polluted = eval(result["polluted"].value);
						var site = {
							'uri' : uri,
							'label' : label,
							'isFacility' : facility,
							'isPolluted' : polluted
						};
						var iconfile;
						if (facility && polluted)
							iconfile = "image/facilitypollute.png";
						else if (facility && !polluted)
							iconfile = "image/facility.png";
						else if (!facility && polluted)
							iconfile = "image/pollutedwater.png";
						else if (!facility && !polluted)
							iconfile = "image/cleanwater2.png";
						var icon = new GIcon(G_DEFAULT_ICON, iconfile);
						icon.iconSize = new GSize(polluted ? 29 : 30, 34);
						var latlng = new GLatLng(lat, lng);
						markerOptions = {
							"icon" : icon,
							"title" : label
						};
						var marker = new GMarker(latlng, markerOptions);
						marker.siteData = site;
						GEvent.addListener(marker, "click",
								getDataCallback(marker));
						map.addOverlay(marker);
						if ((!facility && polluted && !isChecked("pollutedWater"))
								|| (!facility && !polluted && !isChecked("cleanWater"))
								|| (facility && polluted && !isChecked("pollutedFacility"))
								|| (facility && !polluted && !isChecked("facility")))
							marker.hide();
						window.pagedData[curPage].push(marker);
						if (facility && polluted)
							wqpMarkers["pollutedFacility"].push(marker);
						else if (facility && !polluted)
							wqpMarkers["facility"].push(marker);
						else if (!facility && polluted)
							wqpMarkers["pollutedWater"].push(marker);
						else if (!facility && !polluted)
							wqpMarkers["cleanWater"].push(marker);
					}
					$("#spinner").css("display", "none");
				},
				error : function(err) {
					window.alert("Unable to retrieve data from the server.");
					$("#spinner").css("display", "none");
				}
			});
}

function showFlood() {
	var success = function(data) {
		pollutedwatersource = new Array();
		$(data).find('result').each(function() {
			var lat = "", lng = "", sub = "", label = "";
			$(this).find("binding").each(function() {

				if ($(this).attr("name") == "lat") {
					lat = ($(this).find("literal").text());
				}
				if ($(this).attr("name") == "long") {
					lng = ($(this).find("literal").text());
				}
				if ($(this).attr("name") == "label") {
					label = ($(this).find("literal").text());
				}
				if ($(this).attr("name") == "s") {
					sub = ($(this).find("uri").text());
					pollutedwatersource.push(sub);
				}
			});
			if (lat != "" && lng != "") {
				// document.getElementById("test").innerHTML="ready to display";
				var site = {
					'uri' : sub,
					'label' : label,
					'isPolluted' : true
				};
				var blueIcon = new GIcon(G_DEFAULT_ICON, "image/flood.png");
				blueIcon.iconSize = new GSize(29, 34);
				var latlng = new GLatLng(lat, lng);
				markerOptions = {
					icon : blueIcon
				};
				var marker = new GMarker(latlng, markerOptions);
				GEvent.addListener(marker, "click", function() {
					var info = queryForFlood(site, false, marker);
					marker.openInfoWindow(info);
				});
				map.addOverlay(marker);
				wqpMarkers["flood"].push(marker);
			}
		});
	};
	var query = "prefix	rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type this:Flood. ?s geo:lat ?lat. ?s geo:long ?long. }"
	var source = null;
	if (data_source["USGS"] == 1)
		source = "USGS";
	var parameter = "data=water&state=" + state + "&countyCode=" + countyCode
			+ "&query=" + encodeURIComponent(query) + "&start=" + start
			+ "&limit=" + limit + "&source=" + source;
	if (regulation != "") {
		parameter += "&regulation=" + regulation;
	}

	$.ajax({
		type : "GET",
		url : thisserviceagent, // SPARQL service URI
		data : parameter,// "state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query),
		// // query parameter
		dataType : "xml",
		success : success
	});

}

function showPollutedWater(query) {
	var success = function(data) {
		pollutedwatersource = new Array();
		$(data).find('result').each(
				function() {
					var lat = "", lng = "", sub = "", label = "";
					$(this).find("binding").each(function() {

						if ($(this).attr("name") == "lat") {
							lat = ($(this).find("literal").text());
						}
						if ($(this).attr("name") == "long") {
							lng = ($(this).find("literal").text());
						}
						if ($(this).attr("name") == "label") {
							label = ($(this).find("literal").text());
						}
						if ($(this).attr("name") == "s") {
							sub = ($(this).find("uri").text());
							pollutedwatersource.push(sub);
						}
					});
					if (lat != "" && lng != "") {
						// document.getElementById("test").innerHTML="ready to
						// display";
						var site = {
							'uri' : sub,
							'label' : label,
							'isPolluted' : true
						};
						var blueIcon = new GIcon(G_DEFAULT_ICON,
								"image/pollutedwater.png");
						blueIcon.iconSize = new GSize(29, 34);
						var latlng = new GLatLng(lat, lng);
						markerOptions = {
							icon : blueIcon
						};
						var marker = new GMarker(latlng, markerOptions);
						GEvent.addListener(marker, "click", function() {
							var info = queryForWaterPollution(site, false,
									marker);
							marker.openInfoWindow(info);
						});
						map.addOverlay(marker);
						wqpMarkers["pollutedWater"].push(marker);
					}
				});
		showCleanWater();
	};
	var source = null;
	if (data_source["USGS"] == 1)
		source = "USGS";
	var parameter = "data=water&state=" + state + "&countyCode=" + countyCode
			+ "&query=" + encodeURIComponent(query) + "&start=" + start
			+ "&limit=" + limit + "&source=" + source;
	if (regulation != "") {
		parameter += "&regulation=" + regulation;
	}

	$.ajax({
		type : "GET",
		url : thisserviceagent, // SPARQL service URI
		data : parameter,// "state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query),
		// // query parameter
		dataType : "xml",
		success : success
	});

}

function showCleanWater() {
	var success = function(data) {

		$(data)
				.find('result')
				.each(
						function() {
							var lat = "", lng = "", sub = "", label = "";
							var show = true;
							$(this)
									.find("binding")
									.each(
											function() {

												if ($(this).attr("name") == "s") {
													// document.getElementById("test").innerHTML+=pollutedwatersource.length;
													for ( var i = 0; i < pollutedwatersource.length; i++) {
														// document.getElementById("test").innerHTML+=pollutedwatersource[i]+"
														// ";
														if ($(this).find("uri")
																.text() == pollutedwatersource[i]) {
															show = false;
															break;
														}
													}
													sub = $(this).find("uri")
															.text();
												}
												if ($(this).attr("name") == "lat") {
													lat = ($(this).find(
															"literal").text());
													// document.getElementById("test").innerHTML+=lat;
												}
												if ($(this).attr("name") == "long") {
													lng = ($(this).find(
															"literal").text());
													// document.getElementById("test").innerHTML+=lng;
												}
												if ($(this).attr("name") == "label") {
													label = ($(this).find(
															"literal").text());
													// document.getElementById("test").innerHTML+=label;
												}
											});
							if (lat != "" && lng != "" && show) {

								var thisIcon = new GIcon(G_DEFAULT_ICON,
										"image/cleanwater2.png");
								thisIcon.iconSize = new GSize(30, 34);
								var latlng = new GLatLng(lat, lng);
								markerOptions = {
									icon : thisIcon
								};

								var site = {
									'uri' : sub,
									'label' : label,
									'isPolluted' : false
								};
								var marker = new GMarker(latlng, markerOptions);
								GEvent.addListener(marker, "click", function() {
									var info = queryForWaterPollution(site,
											false, marker);
									marker.openInfoWindowHtml(info);
								});
								map.addOverlay(marker);
								wqpMarkers["cleanWater"].push(marker);
							}
							;
						});
		/*
		 * var facilityquery="prefix rdf:
		 * <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs:
		 * <http://www.w3.org/2000/01/rdf-schema#> prefix epa:
		 * <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo:
		 * <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type
		 * epa:ViolatingFacility. ?s geo:lat ?lat. ?s geo:long ?long.}";
		 */
		var facilityquery = "";
		if (noFilterForCharacteristicFlag)
			facilityquery = "prefix	rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type epa:ViolatingFacility. ?s rdfs:label ?label. ?s geo:lat ?lat. ?s geo:long ?long.}";
		else
			facilityquery = buildPollutingFacilityQuery();

		showViolatedFacility(facilityquery);
	};
	var query = "prefix	rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> select * where{?s rdf:type <http://sweet.jpl.nasa.gov/2.1/realmHydroBody.owl#BodyOfWater>. ?s geo:lat ?lat. ?s geo:long ?long. }"
	var source = null;
	if (data_source["USGS"] == 1)
		source = "USGS";
	var parameter = "data=water&state=" + state + "&countyCode=" + countyCode
			+ "&query=" + encodeURIComponent(query) + "&start=" + start
			+ "&limit=" + limit + "&source=" + source;
	if (regulation != "") {
		parameter += "&regulation=" + regulation;
	}
	$.ajax({
		type : "GET",
		url : thisserviceagent, // SPARQL service URI
		data : parameter,// "state="+state+"&countyCode="+countyCode+"&query="
							// +
		// encodeURIComponent(), // query parameter
		dataType : "xml",
		success : success
	});
}

function showViolatedFacility(query) {
	var success = function(data) {
		violatedfacility = new Array();
		$(data).find('result').each(
				function() {
					var lat = "", lng = "", sub = "", label = "";
					$(this).find("binding").each(function() {
						if ($(this).attr("name") == "lat") {
							lat = ($(this).find("literal").text());
						}
						if ($(this).attr("name") == "long") {
							lng = ($(this).find("literal").text());
							if (lng.charAt(0) != "-") {
								lng = "-" + lng;
							}
						}
						if ($(this).attr("name") == "label") {
							label = ($(this).find("literal").text());
						}
						if ($(this).attr("name") == "s") {
							sub = ($(this).find("uri").text());
							violatedfacility.push(sub);
						}
					});
					if (lat != "" && lng != "") {
						var site = {
							'uri' : sub,
							'label' : label,
							'isPolluted' : true
						};
						var blueIcon = new GIcon(G_DEFAULT_ICON,
								"image/facilitypollute.png");
						blueIcon.iconSize = new GSize(29, 34);
						var latlng = new GLatLng(lat, lng);
						markerOptions = {
							icon : blueIcon
						};
						var marker = new GMarker(latlng, markerOptions);
						GEvent.addListener(marker, "click",
								function() {
									var info = queryForFacilityInfo(site,
											false, marker);
									marker.openInfoWindow(info);
								});
						map.addOverlay(marker);
						wqpMarkers["pollutedFacility"].push(marker);
					}
				});
		showFacility();
	};
	var source = null;
	if (data_source["EPA"] == 1)
		source = "EPA";
	var parameter = "data=facility&state=" + state + "&countyCode="
			+ countyCode + "&query=" + encodeURIComponent(query) + "&start="
			+ start + "&limit=" + limit + "&type=ViolatingFacility&source="
			+ source;
	;

	$.ajax({
		type : "GET",
		url : thisserviceagent, // SPARQL service URI
		data : parameter,// "state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query),
		// // query parameter
		dataType : "xml",
		success : success
	});

	/*
	 * $.ajax({type: "GET", url: thisserviceagent, // SPARQL service URI data:
	 * "session="+window.sessionID+ "&query=" + encodeURIComponent("prefix rdf:
	 * <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this:
	 * <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo:
	 * <http://www.w3.org/2003/01/geo/wgs84_pos#> prefix rdfs:
	 * <http://www.w3.org/2000/01/rdf-schema#> select * where{?s a
	 * this:ViolatingFacility . ?s rdfs:label ?label . ?s geo:lat ?lat . ?s
	 * geo:long ?log . }"), beforeSend: function(xhr) {
	 * xhr.setRequestHeader("Accept", "application/sparql-results+xml"); },
	 * dataType: "xml", success: success, error: function(xhr, text, err) {
	 * if(xhr.status==200) { success(xhr.responseXML); } } });
	 */
}

function showFacility() {
	var success = function(data) {
		$(data).find('result').each(function() {
			var lat = "", lng = "", sub = "", label = "";
			var show = true;
			$(this).find("binding").each(function() {
				if ($(this).attr("name") == "s") {
					for ( var i = 0; i < violatedfacility.length; i++) {
						if ($(this).find("uri").text() == violatedfacility[i]) {
							show = false;
						}
					}
					sub = $(this).find("uri").text();
				}
				if ($(this).attr("name") == "lat") {
					lat = ($(this).find("literal").text());
				}
				if ($(this).attr("name") == "log") {
					lng = ($(this).find("literal").text());
					if (lng.charAt(0) != "-") {
						lng = "-" + lng;
					}
				}
				if ($(this).attr("name") == "label") {
					label = ($(this).find("literal").text());
				}
			});
			if (lat != "" && lng != "" && show) {

				var thisIcon = new GIcon(G_DEFAULT_ICON, "image/facility.png");
				thisIcon.iconSize = new GSize(30, 34);
				var latlng = new GLatLng(lat, lng);
				markerOptions = {
					icon : thisIcon
				};

				var site = {
					'uri' : sub,
					'label' : label,
					'isPolluted' : false
				};

				var marker = new GMarker(latlng, markerOptions);
				GEvent.addListener(marker, "click", function() {
					var info = queryForFacilityInfo(site, false, marker);
					marker.openInfoWindow(info);
				});
				map.addOverlay(marker);
				window.wqpMarkers["facility"].push(marker);

			}
			;
		});
		// showFlood();
	};
	var query = "prefix	rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> prefix this: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> select * where{?s rdf:type this:Facility .	?s geo:lat ?lat. ?s geo:long ?log.}";
	var source = null;
	if (data_source["EPA"] == 1)
		source = "EPA";
	var parameter = "data=facility&state=" + state + "&countyCode="
			+ countyCode + "&query=" + encodeURIComponent(query) + "&start="
			+ start + "&limit=" + limit + "&type=facility&source=" + source;
	$.ajax({
		type : "GET",
		url : thisserviceagent, // SPARQL service URI
		data : parameter,
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Accept", "application/sparql-results+xml");
		},
		dataType : "xml",
		success : success,
		error : function(xhr, text, err) {
			if (xhr.status == 200) {
				success(xhr.responseXML);
			}
		}
	});
}
