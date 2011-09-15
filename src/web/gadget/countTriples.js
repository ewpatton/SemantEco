USGSStates = new Array("ak", "as", "ca", "ct", "dc", "fl", "id", "in", "ma", "md", "mi", "nc", "nh", "ny", "oh", "pa", "ri", "sc", "vi", "wi");
EPAFacStates= new Array("ak", "ar", "as", "ca", "co", "ct", "dc", "ga", "hi", "id", "la", "ma", "md", "ne", "nh", "nm", "nv", "ny", "ok", "pa", "pr", "ri", "sd", "ut", "vi", "wi");
FoiaStates= new Array("ak", "ar", "as", "co", "ct", "dc", "ga", "hi", "id", "la", "md", "ne", "nh", "nm", "nv", "ok", "pa", "pr", "sd", "ut", "vi", "wi");
testStates = new Array("as", "ri");
prefix="";
versionId="";
states=new Array();

/*
function countUSGSSiteTriples(){
	document.getElementById("result").innerHTML += "Num of triples of the USGS Sites<br>";
	countUSGSSiteTriplesOneState(0);
}*/

function countUSGSSiteTriples(){
	document.getElementById("result").innerHTML += "Num of triples of the USGS Sites<br>";
	prefix="http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-sites-";
	versionId="2011-Mar-20";
	states=USGSStates;
	countTriplesOneState(0);
}

function countUSGSSiteTriplesOneState(i)
{
if(i==USGSStates.length)
	return;

var stateAbbr=USGSStates[i];
var sparqlUSGSSiteTriples ="select count (*)\r\n"+
        "from <http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-sites-"+stateAbbr+"/version/2011-Mar-20>\r\n"+
        "where {?s ?p ?o}";

   //alert(sparqlUSGSSiteTriples);

   var success = function(data) {
        var numTriples="";
        $(data).find('result').each(function(){
           $(this).find("binding").each(function(){
           if($(this).attr("name")=="callret-0")
           {
              numTriples=($(this).find("literal").text());
 							if(numTriples!="")
                 document.getElementById("result").innerHTML += stateAbbr+": "+ numTriples +"<br>";
           }
           });
        });
		i++;
		countUSGSSiteTriplesOneState(i);
   };

       $.ajax({type: "GET",
          url: tripleStoreAgent,
          data: "query="+encodeURIComponent(sparqlUSGSSiteTriples),
          dataType: "xml",
          error: function(xhr, text, err) {
              if(xhr.status == 200) {
                success(xhr.responseXML);
              }
            },
            success: success
     });

}


function countUSGSMeasurementTriples(){
	document.getElementById("result").innerHTML += "Num of triples of the USGS Measurements<br>";
//http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-measurements-ak/version/2011-Mar-20
	prefix="http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-measurements-";
	versionId="2011-Mar-20";
	states=USGSStates;
	countTriplesOneState(0);
}

function countFOIAMeasurementTriples(){
	document.getElementById("result").innerHTML += "Num of triples of the FOIA Measurements<br>";
//http://sparql.tw.rpi.edu/source/epa-gov/dataset/foia-measurements-ar/version/2011-Jul-23
	prefix="http://sparql.tw.rpi.edu/source/epa-gov/dataset/foia-measurements-";
	versionId="2011-Jul-23";
	states=FoiaStates;
	countTriplesOneState(0);
}

function countEPAFacilitiesTriples(){
	document.getElementById("result").innerHTML += "Num of triples of the EPA facilities<br>";
//http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-facilities-md/version/2011-Mar-19
	prefix="http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-facilities-";
	versionId="2011-Mar-19";
	states=EPAFacStates;
	countTriplesOneState(0);
}

function countTriplesOneState(i)
{
if(i==states.length)
	return;

var stateAbbr=states[i];
var sparqlUSGSSiteTriples ="select count (*)\r\n"+
        "from <"+prefix+stateAbbr+"/version/"+versionId+">\r\n"+
        "where {?s ?p ?o}";

   //alert(sparqlUSGSSiteTriples);

   var success = function(data) {
        var numTriples="";
        $(data).find('result').each(function(){
           $(this).find("binding").each(function(){
           if($(this).attr("name")=="callret-0")
           {
              numTriples=($(this).find("literal").text());
 							if(numTriples!="")
                 document.getElementById("result").innerHTML += stateAbbr+": "+ numTriples +"<br>";
           }
           });
        });
		i++;
		countTriplesOneState(i);
   };

       $.ajax({type: "GET",
          url: tripleStoreAgent,
          data: "query="+encodeURIComponent(sparqlUSGSSiteTriples),
          dataType: "xml",
          error: function(xhr, text, err) {
              if(xhr.status == 200) {
                success(xhr.responseXML);
              }
            },
					timeout: 12000000,
            success: success
     });

}
