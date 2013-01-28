var data; // loaded asynchronously
var countData;// loaded asynchronously
var width = 960,
    height = 400;
var usStates=new Array(1);
usStates[0]="Washington";
var species=new Array(1);
species[0]="Canada Goose";
var comName ;
var usState ;
var year ;
var month;

var projection = d3.geo.albersUsa()
    .scale(width)
    .translate([width / 2, height / 2]);
 
//var path = d3.geo.path();
var path = d3.geo.path()
    .projection(projection);
    
var fill = d3.scale.log()
    .domain([20, 500]);

   
var svg = d3.select("#chart")
  .append("svg")
    .call(d3.behavior.zoom()
    .on("zoom", redraw))
  .append("g");

var counties = svg.append("g")
    .attr("id", "counties")
    .attr("class", "Blues");

var states = svg.append("g")
    .attr("id", "states");
  
  
function showSpcDistribution(){

d3.json("us-counties.json", function(json) {
  counties.selectAll("path")
      .data(json.features.filter(function(d, i) { return d.id.indexOf("53")==0; }))
    .enter().append("path")
      .attr("class", data ? quantize : null)
      .attr("d", path);
});

d3.json("us-states.json", function(json) {
  states.selectAll("path")
      .data(json.features.filter(function(d, i) { return d.id == "53"; }))
    .enter().append("path")
      .attr("d", path)
      .attr("fill", function(d) { return fill(path.area(d)); });
});

  comName = $("#spc_species_selection_canvas").val();
  usState = $("#spc_state_selection_canvas").val();
  year = $("#spc_year_selection_canvas").val();
  month = $("#spc_month_selection_canvas").val();
  //alert(comName+usState+year+month);

//var url="http://localhost/water/service/agent?comName=\"Canada Goose\"&state=\"Washington\"&year=2007&month=6&method=getBirdObservationByCounty";
  var url=thisserviceagent+"?comName=\""+ comName + "\"&state=\""+usState+"\"&year="+year+"&month="+month+"&method=getBirdObservationByCounty";
  d3.json(url, function(json) {
  data = json;
  counties.selectAll("path")
      .attr("class", quantize);
  //svg.attr("transform", "scale(4)");//
  //svg.attr("transform", "translate([-400, 400])");
});
}
  
function quantize(d) {
  return "q" + Math.min(8, ~~(data[d.id] * 9 / 12)) + "-9";
}

function redraw() {
  svg.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
}


function init_choropleth() {
	fill_species_selection();
  fill_state_selection();
  fill_year_selection();
  fill_month_selection();
  document.getElementById("spc_month_selection_canvas").selectedIndex=5;
}

function fill_species_selection(){
  var species_sel=document.getElementById("spc_species_selection_canvas");
  species_sel.innerHTML ="";
	for (var i = 0; i < usStates.length; i++) {
		append_selection_element(species_sel, species[i] , species[i]);
	}
}

function fill_state_selection(){
  var state_sel=document.getElementById("spc_state_selection_canvas");
  state_sel.innerHTML ="";
	for (var i = 0; i < usStates.length; i++) {
		append_selection_element(state_sel, usStates[i] , usStates[i]);
	}
}

function fill_year_selection(){
  var year_sel=document.getElementById("spc_year_selection_canvas");
  year_sel.innerHTML ="";
  for (var i=2007;i<2008;i++) {
      append_selection_element(year_sel, i, i);
  }
}

function fill_month_selection(){
  var month_sel=document.getElementById("spc_month_selection_canvas");
  month_sel.innerHTML ="";
  for (var i=1;i<=12;i++) {
      append_selection_element(month_sel, i, i);
  }
}

function append_selection_element(select, value, html){
  var element = document.createElement("option");
  element.setAttribute("value",value);
  element.innerHTML = html; 
  select.appendChild(element);
}

