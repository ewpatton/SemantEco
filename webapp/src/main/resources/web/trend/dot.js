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


function prepareValueData(result){
	var len=result.dateArr.length;
	var prdData=new Array(len);

  for (i=0;i<len;i++) {
    prdData[i]=new Array(3); //[date, value, color, color]
		prdData[i][0]=result.dateArr[i];
		prdData[i][1]=result.valueArr[i];
		prdData[i][2]='green';
  }

	return prdData;
}

function prepareValueDataWithHighlight(result){
	var len=result.dateArr.length;
	var prdData=new Array(len);
	//var testArea=document.getElementById('test1')
	//testArea.innerHTML= '';

  for (i=0;i<len;i++) {
    prdData[i]=new Array(4); //[date, value, color, color]
		prdData[i][0]=result.dateArr[i];
		prdData[i][1]=result.valueArr[i];
		if(result.limitOperator=="<="){
			if(result.valueArr[i]<=result.limitValueArr[i])
				prdData[i][2]='green';
			else
				prdData[i][2]='red';
		}
		else if(result.limitOperator=="<"){
			if(result.valueArr[i]<result.limitValueArr[i])
				prdData[i][2]='green';
			else
				prdData[i][2]='red';
		}
		else if(result.limitOperator==">="){
			if(result.valueArr[i]>=result.limitValueArr[i])
				prdData[i][2]='green';
			else
				prdData[i][2]='red';
		}
		else if(result.limitOperator==">"){
			if(result.valueArr[i]>result.limitValueArr[i])
				prdData[i][2]='green';
			else
				prdData[i][2]='red';
		}
		//testArea.innerHTML+= prdData[i][1]+', ';
  }

  for (i=0;i<len-1;i++) {
		if(prdData[i][2]=='red'&&prdData[i+1][2]=='red')
				prdData[i][3]='red';
		else
				prdData[i][3]='green';
	}

	return prdData;
}

function prepareLimitValueData(result){
	var prdData=new Array();
	var len=result.dateArr.length;
	//var testArea=document.getElementById('test2')
	//testArea.innerHTML= '';

  for (i=0;i<len;i++) {
    prdData[i]=new Array();
		prdData[i][0]=result.dateArr[i];
		prdData[i][1]=result.limitValueArr[i];
		prdData[i][2]=1;
		//testArea.innerHTML+= prdData[i][1]+', ';
  }
	return prdData;
}

function switchTo(n) { 
	var len = visArray.length;
	for (i = 0; i < len; i++) {
		visArray[i].visible(false);
	}
	visArray[n].visible(true);
	vis.render();
} 


function drawDotChart(dataSource, result){
/* Sizing and scales. */
	var len=result.dateArr.length;
	if(len==0)
		return;

	//alert(result.dateArr[0]+', '+result.dateArr[len-1]);
	var minDate=result.dateArr[0],
			maxDate=result.dateArr[len-1];

	var maxValue = Math.round(pv.max(result.valueArr))+1;


	if(dataSource==='EPA'){
		var valueData=prepareValueDataWithHighlight(result);
		var limitValueData=prepareLimitValueData(result);
		var maxLimitValue = Math.round(pv.max(result.limitValueArr))+1;
	}
	else{
		var valueData=prepareValueData(result);
		var maxLimitValue=maxValue;
	}

	var x = pv.Scale.linear(minDate, maxDate).range(0, w);
	var y = pv.Scale.linear(0, maxValue>=maxLimitValue?maxValue:maxLimitValue).range(0, h);
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

if(dataSource==='EPA'){
//for the line of the limit values
curVis.add(pv.Line)
   .data(limitValueData)
	.left(function(d) { return x(d[0]); })
	.bottom(function(d) { return y(d[1]); })
    .strokeStyle("blue")
    .tension(0.5)
    .lineWidth(2);

//for the line of the measured values
curVis.add(pv.Line)
   .data(valueData)
	.left(function(d) { return x(d[0]); })
	.bottom(function(d) { return y(d[1]); })
	.strokeStyle(function(d) { return d[3]; })
		.segmented(true)
    .tension(0.5)
    .lineWidth(2);
}
else if(dataSource==='USGS'){
//for the line of the measured values
    curVis.add(pv.Line)
	.data(valueData)
	.left(function(d) { return x(d[0]); })
	.bottom(function(d) { return y(d[1]); })
	.strokeStyle(function(d) { return d[2]; })
	.segmented(true)
	.tension(0.5)
	.lineWidth(2);

}

//for the dot of the measured values
curVis.add(pv.Dot)
 		.def("active", -1)
    .data(valueData)
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

switchTo(layoutIndex);
}
