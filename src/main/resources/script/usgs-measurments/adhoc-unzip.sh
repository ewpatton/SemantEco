#!/bin/bash
#usage: ./adhoc-unzip.sh

#call punzip.sh for files in BaseDir 
#and rename the csv files and the provenance files
 
#only for the water sites in NY
BaseDir=source/44
FILES="./*"
expName=US-44-001-result
strlen=${#expName}

cd $BaseDir
for f in $FILES
do
  name=$(basename "$f")
  # skip the provenance files
  if [[ $name == *.zip ]]; then
	 echo "Processing $f" 
     punzip.sh $f
     #get the file name, e.g. US-36-001-result.zip     
     prefix=${name:0:$strlen}
	 echo $prefix
	 #rename the csv file and the provenance file
     mv "data.csv" $prefix".csv"
     mv "data.csv.pml.ttl" $prefix".csv.pml.ttl"
  fi
done


