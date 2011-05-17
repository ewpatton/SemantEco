#!/bin/bash
# Usage:
# process one state a time
# You need to specify the state you would like to process in line 12
# ./adhoc-unzip.sh
# Enf of Usage--------------------------------


#call punzip.sh for files in BaseDir 
#and rename the csv files and the provenance files
 
#only for the water sites in NY, CA, RI
stateCode=25
BaseDir=source/$stateCode
FILES="./*"
expName=US-$stateCode-001-site
strlen=${#expName}

cd $BaseDir
for f in $FILES
do
  name=$(basename "$f")
  # skip the provenance files
  if [[ $name == *.zip ]]; then
	 echo "Processing $f" 
     punzip.sh $f
     #get the file name, e.g. US-36-001-site.zip
     prefix=${name:0:$strlen}
	 echo $prefix
	 #rename the csv file and the provenance file
     mv "data.csv" $prefix".csv"
     mv "data.csv.pml.ttl" $prefix".csv.pml.ttl"
  fi
done


