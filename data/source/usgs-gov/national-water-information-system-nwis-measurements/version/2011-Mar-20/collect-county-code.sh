#!/bin/bash

# Usage:
# This script depends on the input file: manual/state-code.txt
# Enf of Usage--------------------------------
stateCode=manual/state-code.txt
countyCodeXml=source/county-code-xml/
countyCodeTxt=manual/county-code-txt/

#extract the state codes from the xml file
python extract-state-code-from-xml.py
if [ ! -f $stateCode ]; then
   echo "extract-state-code-from-xml.py failed\n"
   exit 2
fi

justify.sh source/statecode.xml $stateCode parse_field

#mkdir if necessary
if [ ! -d $countyCodeXml ]; then
   mkdir -p $countyCodeXml
fi
if [ ! -d $countyCodeTxt ]; then
   mkdir -p $countyCodeTxt
fi

#download the county code xml files and parse the xml files
while read line
do
   echo $line;
  if [ -f $countyCodeXml$line"-county-code.xml" ]
  then
    echo "$countyCodeXml$line-county-code.xml already exists"
  else
    pcurl.sh "http://qwwebservices.usgs.gov/Codes/countycode?statecode="$line -n $countyCodeXml$line-county-code -e xml
    #call the python script to parse the xml file
    python extract-county-code.py $line
    justify.sh $countyCodeXml$line"-county-code.xml" $countyCodeTxt$line"-county-code.txt" parse_field
  fi

done < $stateCode

