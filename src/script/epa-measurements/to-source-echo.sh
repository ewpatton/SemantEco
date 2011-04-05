#! /bin/bash

# Usage:
# ./fetch.sh 
# The input file contains the list of the program IDs for the ICIS_NPDES DB
# Iterate through all the program IDs and call curl to download the csv files corresponding with the program IDs
#Input=./manual/ICP01.TXT.csv
Input=./source/ICP01.TXT
EffluentDataDir=./source
EffluentDataCgi=http://www.epa-echo.gov/cgi-bin/effluentdata.cgi
firstRow=1

while read line
do
   if [ $firstRow -eq 1 ]; then
      firstRow=0
   else
      echo $line;
      #stateAbbr=`expr substr $line 1 2`
	  stateAbbr=${line:0:2}

      echo $stateAbbr
      #only retrieve data for NY, CA
	  if [ $stateAbbr == "RI" ]; then	  
         #echo "In NY"
		 permitID=${line:0:9}
         echo $permitID
		 #make dir if necessary
         if [ ! -d $EffluentDataDir/$stateAbbr ]; then
            mkdir -p $EffluentDataDir/$stateAbbr
         fi
         if [ ! -d automatic/$stateAbbr ]; then
            mkdir -p automatic/$stateAbbr
         fi
         if [ ! -d manual/$stateAbbr ]; then
            mkdir -p manual/$stateAbbr
         fi
	     #download the file by calling pcurl.sh
         pcurl.sh $EffluentDataCgi -F "permit=$permitID" -F "hits=1" -n $EffluentDataDir/$stateAbbr/$permitID -e csv
      fi
   fi
done < $Input




