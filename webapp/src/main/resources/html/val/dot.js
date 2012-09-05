visArray = Array();	
var layoutIndex=-1;
//var	valueData=[];
//var	limitValueData=[];
//var x, y;

var w = 600,
    h = 400;

/* The root panel. */
var	vis = new pv.Panel()//.canvas('EPA_visualization')
    .width(w)
    .height(h)
    .bottom(20)
    .left(20)
    .right(10)
    .top(5)
		.events("all")
    .event("mousemove", pv.Behavior.point());

/* Interaction state. Focus scales will have domain set on-render. */
var fx = pv.Scale.linear().range(0, w),
    fy = pv.Scale.linear().range(0, h);

function switchTo(n) { 
	var len = visArray.length;
	for (i = 0; i < len; i++) {
		visArray[i].visible(false);
	}
	visArray[n].visible(true);
	vis.render();
} 


function prepareValueData(curRes, color){
	len=curRes.dateArr.length;
	prdData=new Array(len);

  for (i=0;i<len;i++) {
    prdData[i]=new Array(3); //[date, value, color, color]
		prdData[i][0]=curRes.dateArr[i];
		prdData[i][1]=curRes.valueArr[i];
		prdData[i][2]=color; //'green'
  }

	return prdData;
}

function drawDotChart(dotRes){
/* Sizing and scales. */
	var epaLen=dotRes.epa.dateArr.length;
  var usgsLen=dotRes.usgs.dateArr.length;
	if(epaLen==0 && usgsLen==0)
		return;

	//alert(dotRes.dateArr[0]+', '+dotRes.dateArr[len-1]);
	var epaMinDate, epaMaxDate, usgsMinDate, usgsMaxDate;
	var epaData, usgsData;
	var epaMaxValue, usgsMaxValue;
	var minDate=null, maxDate=null, maxValue=null;
	if(epaLen>0){
	    epaMinDate=dotRes.epa.dateArr[0],
			epaMaxDate=dotRes.epa.dateArr[epaLen-1];	
			epaData=prepareValueData(dotRes.epa, 'blue');		
			epaMaxValue = Math.round(pv.max(dotRes.epa.valueArr))+1;
			minDate=epaMinDate;
			maxDate=epaMaxDate;
			maxValue=epaMaxValue;
	}
  if(usgsLen>0){ 
    usgsMinDate=dotRes.usgs.dateArr[0],
	  usgsMaxDate=dotRes.usgs.dateArr[usgsLen-1];
	  usgsData=prepareValueData(dotRes.usgs, 'green');
	  usgsMaxValue = Math.round(pv.max(dotRes.usgs.valueArr))+1;	
	  if(minDate==null)
	    minDate=usgsMinDate;
	  else
	    minDate = (epaMinDate<=usgsMinDate?epaMinDate:usgsMinDate);
	  if(maxDate==null)
	    maxDate=usgsMaxDate;
	  else
	    maxDate = (epaMaxDate>=usgsMaxDate?epaMaxDate:usgsMaxDate);
	  if(maxValue==null)
	    maxValue=usgsMaxValue;
	  else
	    maxValue = (epaMaxValue>=usgsMaxValue?epaMaxValue:usgsMaxValue);	    
  }
	
	var x = pv.Scale.linear(minDate, maxDate).range(0, w);
	var y = pv.Scale.linear(0, maxValue).range(0, h);
	//c = pv.Scale.log(1, 100).range("orange", "brown");
	layoutIndex++;
	//alert('Index: '+layoutIndex+', and flag: '+visibleFlags[layoutIndex]);
	var curVis = vis.add(pv.Panel).events("all").event("mousemove", pv.Behavior.point());
	visArray.push(curVis);

/* Y-axis and ticks. */
curVis.add(pv.Rule)
    .data(y.ticks())
    .bottom(y)
	.strokeStyle(function(d) { return d ? "#eee" : "#000"; })
  .anchor("left").add(pv.Label)
    .text(y.tickFormat);

/* X-axis and ticks. */
curVis.add(pv.Rule)
    .data(x.ticks())
    .left(x)
	.strokeStyle(function(d) { return d ? "#eee" : "#000"; })
  .anchor("bottom").add(pv.Label)
    .text(x.tickFormat);

//for the line of the measured values
	if(epaLen>0){
    curVis.add(pv.Line)
	.data(epaData)
	.left(function(d) { return x(d[0]); })
	.bottom(function(d) { return y(d[1]); })
	.strokeStyle(function(d) { return d[2]; })
	.segmented(true)
	.tension(0.5)
	.lineWidth(2);

	
	//for the dot of the measured values
curVis.add(pv.Dot)
 		.def("active", -1)
    .data(epaData)
	.left(function(d) { return x(d[0]); })
	.bottom(function(d) { return y(d[1]); })
	.strokeStyle(function(d) { return d[2]; })
	.fillStyle(function() { return this.strokeStyle().alpha(.2); })
	.size(function(d) { return 30; })
	.event("point", function() { return this.active(this.index).parent; })
	.event("unpoint", function() { return this.active(-1).parent; })
	.anchor("top").add(pv.Label)
	.visible(function() { return this.anchorTarget().active() == this.index; })
	.text(function(d) { return 'Date: '+d[0]+', Value: '+d[1]; });//d[0].format("yyyy-mm-dd")
	
}
	
		if(usgsLen>0){
			//for the line of the measured values
    curVis.add(pv.Line)
	.data(usgsData)
	.left(function(d) { return x(d[0]); })
	.bottom(function(d) { return y(d[1]); })
	.strokeStyle(function(d) { return d[2]; })
	.segmented(true)
	.tension(0.5)
	.lineWidth(2);
	
//for the dot of the measured values

curVis.add(pv.Dot)
 		.def("active", -1)
    .data(usgsData)
	.left(function(d) { return x(d[0]); })
	.bottom(function(d) { return y(d[1]); })
	.strokeStyle(function(d) { return d[2]; })
	.fillStyle(function() { return this.strokeStyle().alpha(.2); })
	.size(function(d) { return 30; })
	.event("point", function() { return this.active(this.index).parent; })
	.event("unpoint", function() { return this.active(-1).parent; })
	.anchor("top").add(pv.Label)
	.visible(function() { return this.anchorTarget().active() == this.index; })
	.text(function(d) { return 'Date: '+d[0]+', Value: '+d[1]; });//d[0].format("yyyy-mm-dd")
}
switchTo(layoutIndex);
}
