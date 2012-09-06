<html>
<head>
<title>Water Quaility Portal Questionnaire</title>
</head>

<?php

function processForm()
{
	//print "In processForm <br>";
  date_default_timezone_set('UTC');
	$fileNameID = date("Ymd");
	$filename = $fileNameID."questionnaire_data.txt";


	if($_POST['formSubmit'] == "Submit")
	{
		//print "In formSubmit <br>";
		//Demographic Info
		$varGender = $_POST['dem-gender'];
		$varAgeGroup = $_POST['dem-age-group'];
		$varExpeType = $_POST['dem-expe-type'];
		$varExpeText = $_POST['dem-expe-text'];
		$varExpeText = stripslashes($varExpeText);
		//Survey Info
		$varQ1Answer = $_POST['q-1-answer'];
		$varQ2Answer = $_POST['q-2-answer'];
		$varQ3Answer = $_POST['q-3-answer'];
		$varQ4Answer = $_POST['q-4-answer'];
		$varQ5Answer = $_POST['q-5-answer'];
		$varQ6Answer = $_POST['q-6-answer'];
		$varQ7Answer = $_POST['q-7-answer'];
		$varQ8Answer = $_POST['q-8-answer'];
		$varQ8Answer = stripslashes($varQ8Answer);
		$varQ9Answer = $_POST['q-9-answer'];
		$varQ9Answer = stripslashes($varQ9Answer);
		$errorMessage = "";
 
		// - - - sanity check - - -
   if(empty($varQ1Answer)) {
      $errorMessage .= "<li>Please answer question 1</li>";
   }
   if(empty($varQ2Answer)) {
      $errorMessage .= "<li>Please answer question 2</li>";
   }
   if(empty($varQ3Answer)) {
      $errorMessage .= "<li>Please answer question 3</li>";
   }
   if(empty($varQ4Answer)) {
      $errorMessage .= "<li>Please answer question 4</li>";
   }
   if(empty($varQ5Answer)) {
      $errorMessage .= "<li>Please answer question 5</li>";
   }
   if(empty($varQ6Answer)) {
      $errorMessage .= "<li>Please answer question 6</li>";
   }
   if(empty($varQ7Answer)) {
      $errorMessage .= "<li>Please answer question 7</li>";
   }


	if($errorMessage != ""){
		print "$errorMessage";
		return;
	}

	$prefix="";
	if (! file_exists($filename)) {
		echo "The file $filename does not exist <br>";
		$prefix="@prefix twcwater: <http://purl.org/twc/ontology/swqp/core#> .\n";
	} 
  $triples=$prefix;

  $CurID = time();
	$triples .= 	"twcwater:$CurID twcwater:hasQ1Answer \"$varQ1Answer\" ; \n" .
								"\t\t twcwater:hasQ2Answer \"$varQ2Answer\" ; \n" .
								"\t\t twcwater:hasQ3Answer \"$varQ3Answer\" ; \n" .
								"\t\t twcwater:hasQ4Answer \"$varQ4Answer\" ; \n" .
								"\t\t twcwater:hasQ5Answer \"$varQ5Answer\" ; \n" .
								"\t\t twcwater:hasQ6Answer \"$varQ6Answer\" ; \n" .
								"\t\t twcwater:hasQ7Answer \"$varQ7Answer\" . \n" ;
  //Optional Questions
	if(!empty($varQ8Answer))		
		$triples .= "twcwater:$CurID twcwater:hasQ8Answer \"$varQ8Answer\" . \n";
	if(!empty($varQ9Answer))
		$triples .= "twcwater:$CurID twcwater:hasQ9Answer \"$varQ9Answer\" . \n";

	//Optional Demographic Data
	if(!empty($varGender))
		$triples .= "twcwater:$CurID twcwater:hasGender \"$varGender\" . \n";
	if(!empty($varAgeGroup))
		$triples .= "twcwater:$CurID twcwater:hasAgeGroup \"$varAgeGroup\" . \n";
	if(!empty($varExpeType))
		$triples .= "twcwater:$CurID twcwater:hasExperienceType \"$varExpeType\" . \n";
	if(!empty($varExpeText))
		$triples .= "twcwater:$CurID twcwater:hasExperience \"$varExpeText\" . \n";


	print "$triples <br>";


	$fp = fopen("$filename",  "a");
	if (flock($fp, LOCK_EX | LOCK_NB)) {
		fwrite($fp, $triples);
    flock($fp, LOCK_UN);
		print "Your results have been submitted! <br>";
	} else {
    print "Another user is submitting, so could not save your answers.<br>Please try again. <br>";
	}

		fclose($fp);
	}
}

/*
function produceTriples($ID){
//e1:constructiondatetext a rdf:Property ;
//	ov:csvCol "31"^^xsd:integer ;
//	ov:csvHeader "ConstructionDateText".
//http://purl.org/twc/ontology/swqp/core

$triples= "twcwater:". $ID . " twcwater:hasGender " . $varGender . "; \n";
print "$triples";
}*/

processForm();
?>


<body>
<!--<p>Current ID: <?php print $CurID; ?></p>-->
</body>
</html>
