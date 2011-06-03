#!/bin/bash
#
# Script to retrieve and convert a new version of the dataset.
# See https://github.com/timrdf/csv2rdf4lod-automation/wiki/Automated-creation-of-a-new-Versioned-Dataset

export CSV2RDF4LOD_CONVERT_OMIT_RAW_LAYER="true"

version=`date +%Y-%b-%d`

if [ $# -lt 1 ]; then
	echo "need to specify the state to crawl e.g. 44 for RI" ; 
	exit 0;
fi

if [ ! -e $version -o "debug" == "debug" ]; then

   mkdir $version
   pushd $version &> /dev/null

      # Get the list of state codes (as XML): <Code value="US:01" desc="ALABAMA"/>
	if [ ! -d source ]; then
      mkdir source &> /dev/null
	fi

      pushd source &> /dev/null
		 if [ ! -f "statecode.xml" ]; then
         	pcurl.sh http://qwwebservices.usgs.gov/Codes/statecode -n statecode -e xml
		 fi
      popd &> /dev/null

      # Convert XML to a single column of state identifiers: "US:01"
	if [ ! -d manual ]; then
      mkdir manual &> /dev/null
	fi

	if [ ! -f "manual/state-code.txt" ]; then
	  python ../../bin/extract-state-code-from-xml.py source/statecode.xml > manual/state-code.txt
      justify.sh source/statecode.xml manual/state-code.txt parse_field
 	fi

      # Use the state codes to ask for the county codes in that state: <Code value="US:01:001" desc="US, ALABAMA, AUTAUGA"/>
      pushd source &> /dev/null
         for state in `cat ../manual/state-code.txt`; do
           echo $state;
           if [ -f "$state-county-code.xml" -a "debug" == "pass" ]; then
              echo "$state-county-code.xml already exists"
           else
              echo $state;
              pcurl.sh "http://qwwebservices.usgs.gov/Codes/countycode?statecode=$state" -n $state-county-code -e xml
              pushd ../ &> /dev/null
                 # Convert XML to a single column of county identifiers: "US:01:001"
                 python ../../bin/extract-county-code.py "source/$state-county-code.xml" > manual/$state-county-code.txt
                 justify.sh "source/$state-county-code.xml" "manual/$state-county-code.txt" parse_field
              popd &> /dev/null
           fi


            # DO NOT RUN THIS FOR ALL STATES! IT TAKES DAYS!
            # call 2011-Mar-20/to-source-usgs-sites.sh to pcurl a zip file of csvs.
            # punzip.sh the zip, ADD -n -e to punzip.sh
         done

		#crawl the actual data files for the input state
  		#each county has a file, so iterate through the county codes in the file
		inputState="US:"$1
        if [ ! -d $1 ]; then
           mkdir $1;
        fi
  		counter=0
  		while read countyCode
  		do
  			echo $countyCode
  			fname=${countyCode//:/-}"-result"
  			echo $fname

  			if [ -f "$1/$fname.zip" ]
  			then
    			echo "$1/$fname.zip exists"
  			else
    			pcurl.sh "http://qwwebservices.usgs.gov/Result/search?statecode=$inputState&countycode=$countyCode&mimeType=csv&zip=yes" -n $1/$fname -e "zip"
			fi

 			let counter+=1 
 			if [[ $SWQP_SAMPLE_ONLY && $counter -ge $SWQP_SAMPLE_NUM  ]] ; then
   				break;
 			fi
  		done < "../manual/$inputState-county-code.txt"


      popd &> /dev/null

   popd &> /dev/null
else
   echo "$version already exists. Skipping."
fi




exit
