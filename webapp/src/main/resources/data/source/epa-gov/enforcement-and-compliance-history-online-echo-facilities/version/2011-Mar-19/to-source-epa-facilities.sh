#! /bin/bash
DestDir=./source
sourceFile=source/ICP01.TXT

process_one_state() {
	#select the subset for the states we are interested: CA, RI, MA, NY
	awk -F\| -v k=$1 '$1 ~ /^PEREXNO/ || $1 ~ k {print $0}' source/ICP01.TXT > manual/$1-ICP01.TXT
	justify.sh source/ICP01.TXT manual/$1-ICP01.TXT select_subset
	#create the converter for the state
	#cr-create-convert-sh.sh manual/$1-ICP01.TXT > $1-convert-echo-facilities.sh
}

prepare() {
	if [ ! -f $sourceFile ]; then
      if [ ! -d source ]; then
         mkdir source
      fi
      pushd source &> /dev/null
         # Download the source file that contains information about the EPA facilities
         pcurl.sh "http://www.epa-echo.gov/ideadownloads/2010/ICIS_NPDES.zip"
         punzip.sh $DestDir/ICIS_NPDES.zip
         #cd ..
      popd &> /dev/null
	fi
}

#check the number of command line arguments
#echo $#;
if [ $# -lt 1 ]; then
	echo need to specify the state to crawl e.g. RI ; exit 0;
fi

prepare
process_one_state $1
