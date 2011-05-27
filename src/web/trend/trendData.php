<?php


if(isset($_REQUEST['query'])&&$_REQUEST['query']!="")
{


  $query=$_REQUEST['query'];

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
  $data=str_replace("\n","",$data);


  header("Content-type: text/xml");

  echo $data;

 }
?>

