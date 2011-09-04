<?php


if(isset($_REQUEST['query'])&&$_REQUEST['query']!="")
{


  $query=$_REQUEST['query'];

  //$service="http://tw2.tw.rpi.edu/zhengj3/water_store/ARC2store/sparql.php";
  $service="http://sparql.tw.rpi.edu/virtuoso/sparql";
  //$service="http://logd.tw.rpi.edu:8890/sparql";
  //$url=$service."?query=".urlencode($query)."&output=&jsonp=&key=";
	$query=stripslashes($query);
  $url=$service."?default-graph-uri=&should-sponge=&query=".urlencode($query)."&format=application/sparql-results+xml&timeout=";
  if(isset($_REQUEST['debug'])){
    $query=str_replace("<","&lt;",$query);
    $query=str_replace(">","&gt;",$query);
     echo $query."<br>";
     echo $url;
     return;
  
  }
  $data=@file_get_contents($url);


  $data=preg_replace("/<br \/>.*/","",$data);
  $data=preg_replace("/<b>.*<\/b>/","",$data);
  $data=str_replace("\n","",$data);


  header("Content-type: text/xml");

  echo $data;

 }
?>

