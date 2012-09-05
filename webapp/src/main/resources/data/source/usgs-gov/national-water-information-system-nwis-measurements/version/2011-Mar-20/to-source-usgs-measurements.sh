#!/bin/bash

#Usage:
#You need to execute collect-county-code.sh
# We usually crawl data from USGS once a state. 
# To do so, you need to change the constant at line 58!!!
# Enf of Usage--------------------------------

#for real use
sourceDir=manual/county-code-txt
FILES=manual/county-code-txt/*
OutputDir=source
#echo $FILES
#echo $OutputDir

#WebTarget="http://qwwebservices.usgs.gov/Station/search?statecode="
#PostFix=-sites
WebTarget="http://qwwebservices.usgs.gov/Result/search?statecode="
PostFix=-result

crawl_one_state()
{
  statecode="US:"$1
  echo $statecode

  #iterate through the county codes in the file
  counter=0
  while read line
  do
  echo $line
  fname=${line//:/-}$PostFix
  echo $fname

  if [ -f $OutputDir"/"$sCode"/"$fname".zip" ]
  then
    echo "$OutputDir/$sCode/$fname.zip exists"
  else
    pcurl.sh $WebTarget$statecode"&countycode="$line"&mimeType=csv&zip=yes" -n $OutputDir/$sCode/$fname -e "zip"
    #sleep 1
  fi

 let counter+=1 
 if [[ $SWQP_SAMPLE_ONLY && $counter -ge $SWQP_SAMPLE_NUM  ]] ; then
   break;
 fi

  done < $2
}

crawl_states()
{
#echo $1
 #iterate through the files for the states
for f in $FILES
do
  # take action on each file. $f store current file name
  #echo "Processing $f"  
  name=$(basename "$f")
  # skip the provenance files
  if [[ $name != *.pml.ttl ]]; then
	 echo "Processing $name" 
     sCode=${name:3:2}
     #only retrieve data for 36(NY), 06(CA), 44(RI), 25(MA)
     #if [ $sCode == "25" ]; then	
	 if [ $sCode ==  $1 ]; then	
        if [ ! -d $OutputDir/$sCode ]; then
           mkdir $OutputDir/$sCode
        fi
        #download the csv files for the water sites in one state
        echo "to crawl the state $sCode"
        crawl_one_state $sCode $f  
     fi
  fi #end of skip the provenance files
done
}

#collect the files that this script depends on
prepare() {
	if [ ! -d $sourceDir ]; then
		mkdir $sourceDir
		./collect-county-code.sh
	fi
}

#check the number of command line arguments
#echo $#;
if [ $# -lt 1 ]; then
	echo need to specify the state to crawl e.g. RI ; exit 0;
fi

prepare
declare -A stateCodeTable=( ["CA"]="06" ["MA"]="25" ["NY"]="36" ["RI"]="44")
inputState="${stateCodeTable[$1]}"
echo "The state code is $inputState"
crawl_states $inputState
./adhoc-unzip.sh $inputState
./fix-csv-pml.sh $inputState



