<?php


if(isset($_REQUEST['state'])&&$_REQUEST['state']!=""&&isset($_REQUEST['county'])&&$_REQUEST['county']!=""&&isset($_REQUEST['source'])&&$_REQUEST['source']!=""){
 
  $county=$_REQUEST['county'];
  $source=$_REQUEST['source'];
  $state=strtoupper($_REQUEST['state']);
  $offset="0";
  $limit="2000";
  if(isset($_REQUEST['start'])&&$_REQUEST['start']!=""){
    $offset=$_REQUEST['start'];
  }
  if(isset($_REQUEST['limit'])&&$_REQUEST['limit']!=""){
    $limit=$_REQUEST['limit'];
  }
  
  $query="PREFIX epa:<http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> PREFIX time:<http://www.w3.org/2006/time#> prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#>  prefix rdfs: <http://www.w2.org/2000/02/rdf-schema#>".
         "CONSTRUCT {   ?s rdf:type epa:MeasurementSite . ?s epa:hasMeasurement ?measurement. ?s epa:hasCountyCode \"".$county."\". ?s epa:hasStateCode ?state. ?s wgs:lat ?lat. ?s wgs:long ?long. ?s rdfs:label ?label. ?measurement epa:hasElement ?element. ?measurement epa:hasValue ?value. ?measurement epa:hasUnit ?unit. ?measurement time:inXSDDateTime ?time. }".
         "WHERE { GRAPH <http://tw2.tw.rpi.edu/water/".$state."/".$source."> {?s rdf:type epa:MeasurementSite . ?s epa:hasUSGSSiteId ?id. ?s epa:hasCountyCode \"".$county."\". ?s epa:hasStateCode ?state. ?s wgs:lat ?lat. ?s wgs:long ?long. ?s rdfs:label ?label. ?measurement epa:hasUSGSSiteId ?id.    ?measurement epa:hasElement ?element.    ?measurement epa:hasValue ?value. ?measurement epa:hasUnit ?unit. ?measurement time:inXSDDateTime ?time.} } offset ".$offset." limit ".$limit;
  
  /*
  $query="PREFIX epa:<http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> PREFIX time:<http://www.w3.org/2006/time#> prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#>  prefix rdfs: <http://www.w2.org/2000/02/rdf-schema#>".
         "CONSTRUCT {   ?s rdf:type epa:MeasurementSite . ?s epa:hasCountyCode \"".$county."\". ?s epa:hasStateCode ?state. ?s wgs:lat ?lat. ?s wgs:long ?long.}".
         "WHERE { GRAPH <http://tw2.tw.rpi.edu/water/".$state."/USGS> {?s rdf:type epa:MeasurementSite . ?s epa:hasUSGSSiteId ?id. ?s epa:hasCountyCode \"".$county."\". ?s epa:hasStateCode ?state. ?s wgs:lat ?lat.    ?s wgs:long ?long.    ?measurement epa:hasUSGSSiteId ?id.    ?measurement epa:hasElement ?element.    ?measurement epa:hasValue ?value. ?measurement epa:hasUnit ?unit. ?measurement time:inXSDDateTime ?time.} } offset ".$offset." limit ".$limit;
  */
  $service="http://tw2.tw.rpi.edu/zhengj3/water_store/ARC2store/sparql.php";
  $url=$service."?query=".urlencode($query)."&output=&jsonp=&key=";
  if(isset($_REQUEST['debug'])){
    $query=str_replace("<","&lt;",$query);
    $query=str_replace(">","&gt;",$query);
     echo $query;
     return;
  //echo $url;
  }
  $data=@file_get_contents($url);
  

 $data=preg_replace("/<br \/>.*/","",$data);  
  $data=preg_replace("/<b>.*<\/b>/","",$data);



  $data=str_replace("<ns0:hasValue>","<ns0:hasValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">",$data);
  
  header("Content-type: text/xml");
  $data=str_replace("\n","",$data);
  echo $data;
  
 }

if(isset($_REQUEST['query'])&&$_REQUEST['query']!="")
  {


    $query=$_REQUEST['query'];

    $service="http://tw2.tw.rpi.edu/zhengj3/water_store/ARC2store/sparql.php";
    $url=$service."?query=".urlencode($query)."&output=json&jsonp=&key=";
    if(isset($_REQUEST['debug'])){
      $query=str_replace("<","&lt;",$query);
      $query=str_replace(">","&gt;",$query);
      echo $query;
      return;
      //echo $url;
    }
    $data=@file_get_contents($url);


    $data=preg_replace("/<br \/>.*/","",$data);
    $data=preg_replace("/<b>.*<\/b>/","",$data);
    $data=str_replace("\n","",$data);


    header("Content-type: application/json");

    echo $data;

  }

?>
