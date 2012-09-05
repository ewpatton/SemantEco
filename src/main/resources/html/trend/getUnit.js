
function sendWaterSiteQuery(stateCode, countyCode){
	//var thisserviceagent="http://localhost/demoWater/trendData.php";
	var sparqlWaterSites="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
        "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
        "\r\n"+
        "SELECT DISTINCT ?siteId\r\n"+
        "WHERE {\r\n"+
        "?site rdf:type epa:MeasurementSite .\r\n"+
        "?site epa:hasStateCode \"" + stateCode + "\" .\r\n"+
        "?site epa:hasCountyCode \"" + countyCode + "\" .\r\n"+
        "?site epa:hasUSGSSiteId ?siteId .\r\n"+
        "}"

       $.ajax({type: "GET",
          url: thisserviceagent,
          data: "query="+encodeURIComponent(sparqlWaterSites),
          dataType: "xml", 
          success: processWaterSitesData,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}


