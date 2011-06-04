#!/bin/bash
#
# Script to retrieve and convert a new version of the dataset.
# See https://github.com/timrdf/csv2rdf4lod-automation/wiki/Automated-creation-of-a-new-Versioned-Dataset

if [ $# -lt 2 ]; then
   echo "usage: `basename $0` version-identifier state-fips-code" ; 
   echo "  version-identifier: http://purl.org/twc/vocab/conversion/version_identifier; use cr:today to use today's date." 
   echo "  state-fips-code: e.g. 44 for RI" 
   exit 1;
fi

version=$1
if [ $1 == "cr:today" ];
   version=`date +%Y-%b-%d`
fi

focus_state=$1

mkdir $version &> /dev/null
pushd $version &> /dev/null

   # Get the list of state codes (as XML): <Code value="US:01" desc="ALABAMA"/>
   mkdir source &> /dev/null

   pushd source &> /dev/null
      pcurl.sh http://qwwebservices.usgs.gov/Codes/statecode -n state-code -e xml
   popd &> /dev/null

   # Convert XML to a single column of state identifiers: "US:01"
   mkdir manual &> /dev/null
   if [ ! -f "manual/state-code.txt" ]; then
      python ../../bin/extract-state-code-from-xml.py source/state-code.xml > manual/state-code.txt
      justify.sh source/state-code.xml manual/state-code.txt parse_field
   fi

   # Use the state codes to ask for the county codes in that state: <Code value="US:01:001" desc="US, ALABAMA, AUTAUGA"/>
   pushd source &> /dev/null
      for state in `cat ../manual/state-code.txt`; do
        echo $state;
        if [ -f "$state-county-code.xml" ]; then
           echo "$state-county-code.xml already exists; no need to retrieve again."
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

      # Crawl the actual data files for the input state.
      mkdir $focus_state &> /dev/null
      pushd $focus_state &> /dev/null
         counter=0                                                            # DEBUG
         # Each county has a file, so iterate through the county codes in the file.
         for county_code in `cat ../../manual/US:$focus_state-county-code.txt`; do
            fname=${county_code//:/-}"-result"
            echo "$county_code --> $fname"

            if [ -f "$fname.zip" ]; then
               echo "$fname.zip exists; no need to retieve again."
            else
               pcurl.sh "http://qwwebservices.usgs.gov/Result/search?statecode=US:$focus_state&countycode=$county_code&mimeType=csv&zip=yes" -n $fname -e "zip"
               punzip.sh -n $fname -e csv $fname.zip
            fi

            let counter+=1                                                    # DEBUG
            if [[ $SWQP_SAMPLE_ONLY && $counter -ge $SWQP_SAMPLE_NUM ]]; then # 
                  break;                                                      #
            fi                                                                #
         done
      popd &> /dev/null

   popd &> /dev/null

   # Create the conversion trigger and pull it for each layer.
   cr-create-convert-sh.sh -w `find source/* -name "*.csv"`
   export CSV2RDF4LOD_CONVERT_OMIT_RAW_LAYER="true"
   for layer in e1; do 
      ./convert-*.sh
   done

popd &> /dev/null

exit
