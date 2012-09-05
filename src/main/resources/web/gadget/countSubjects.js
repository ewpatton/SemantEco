sbjPrefix="";
sbjVersionId="";
sbjStates=new Array();
var tw2=false;

function countTW2ECHOMeasurements(){
  tw2=true;
	document.getElementById("result").innerHTML += "Num of TW2 ECHO Measurements<br>";
	sbjPrefix="http://tw2.tw.rpi.edu/water/EPA/";
	//sbjVersionId="2011-Mar-19";
	sbjStates=tw2echoStates;
	sbjType="epa:FacilityMeasurement";
	countSubjectsOneState(0);
}

function countTW2ECHOFacilities(){
  tw2=true;
	document.getElementById("result").innerHTML += "Num of TW2 ECHO Facilities<br>";
	sbjPrefix="http://tw2.tw.rpi.edu/water/EPA/";
	//sbjVersionId="2011-Mar-19";
	sbjStates=tw2echoStates;
	sbjType="epa:Facility";
	countSubjectsOneState(0);
}

function countECHOMeasurements(){
	document.getElementById("result").innerHTML += "Num of ECHO Measurements<br>";
	sbjPrefix="http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-measurements-";
	sbjVersionId="2011-Mar-19";
	sbjStates=echoMeasurementStates;
	sbjType="water:WaterMeasurement";
	countSubjectsOneState(0);
}

function countFOIAMeasurements(){
	document.getElementById("result").innerHTML += "Num of FOIA Measurements<br>";
	sbjPrefix="http://sparql.tw.rpi.edu/source/epa-gov/dataset/foia-measurements-";
	sbjVersionId="2011-Jul-23";
	sbjStates=FoiaStates;
	sbjType="water:WaterMeasurement";
	countSubjectsOneState(0);
}

function countUSGSMeasurements(){
	document.getElementById("result").innerHTML += "Num of USGS Measurements<br>";
	sbjPrefix="http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-measurements-";
	sbjVersionId="2011-Mar-20";
	sbjStates=USGSStates;
	sbjType="water:WaterMeasurement";
	countSubjectsOneState(0);
}

function countEPAFacilities(){
	document.getElementById("result").innerHTML += "Num of EPA Facilities<br>";
	sbjPrefix="http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-facilities-";
	sbjVersionId="2011-Mar-19";
	sbjStates=EPAFacStates;
	sbjType="water:WaterFacility";
	countSubjectsOneState(0);
}

function countUSGSSites(){
	document.getElementById("result").innerHTML += "Num of USGS Sites<br>";
	sbjPrefix="http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-sites-";
	sbjVersionId="2011-Mar-20";
	sbjStates=USGSStates;
	sbjType="water:WaterSite";
	countSubjectsOneState(0);
}

function countSubjectsOneState(i)
{
if(i==sbjStates.length)
	return;

var stateAbbr=sbjStates[i];
var spqCountSubjects =null;

if(tw2==true)
	spqCountSubjects="PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> \r\n"+
				"select distinct count (*)\r\n"+
        "from <"+sbjPrefix+stateAbbr+">\r\n"+
        "where {?s a "+sbjType+". }";
else
   spqCountSubjects ="PREFIX water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> \r\n"+
				"select distinct count (*)\r\n"+
        "from <"+sbjPrefix+stateAbbr+"/version/"+sbjVersionId+">\r\n"+
        "where {?s a "+sbjType+". }";     

   //alert(spqCountSubjects);

   var success = function(data) {
        var numsbj="";
        $(data).find('result').each(function(){
           $(this).find("binding").each(function(){
           if($(this).attr("name")=="callret-0")
           {
              numsbj=($(this).find("literal").text());
 							if(numsbj!="")
                 document.getElementById("result").innerHTML += stateAbbr+": "+ numsbj +"<br>";
           }
           });
        });
		i++;
		countSubjectsOneState(i);
   };

       $.ajax({type: "GET",
          url: tripleStoreAgent,
          data: "query="+encodeURIComponent(spqCountSubjects),
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
