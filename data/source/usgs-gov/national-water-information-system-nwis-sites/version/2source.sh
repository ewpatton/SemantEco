#!/bin/bash
#
# Script to retrieve and convert a new version of the dataset.
# See https://github.com/timrdf/csv2rdf4lod-automation/wiki/Automated-creation-of-a-new-Versioned-Dataset

export CSV2RDF4LOD_CONVERT_OMIT_RAW_LAYER="true"

version=`date +%Y-%b-%d`

if [ ! -e $version -o "debug" == "debug" ]; then

   mkdir $version
   pushd $version &> /dev/null

      # Get the list of state codes (as XML): <Code value="US:01" desc="ALABAMA"/>
      mkdir source &> /dev/null
      pushd source &> /dev/null
         pcurl.sh http://qwwebservices.usgs.gov/Codes/statecode -n statecode -e xml
      popd &> /dev/null

      # Convert XML to a single column of state identifiers: "US:01"
      mkdir manual &> /dev/null
      python ../../bin/extract-state-code-from-xml.py # source/statecode.xml > manual/state-code.txt
      justify.sh source/statecode.xml manual/state-code.txt parse_field

      # Use the state codes to ask for the county codes in that state: <Code value="US:01:001" desc="US, ALABAMA, AUTAUGA"/>
      pushd source &> /dev/null
         for state in `cat ../manual/state-code.txt`; do
           echo $state;
           if [ -f "$state-county-code.xml" -a "debug" == "pass" ]; then
              echo "$state-county-code.xml already exists"
           else
              echo $state;
              #pcurl.sh "http://qwwebservices.usgs.gov/Codes/countycode?statecode=$state" -n $state-county-code -e xml
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
      popd &> /dev/null

   popd &> /dev/null
else
   echo "$version already exists. Skipping."
fi

exit
