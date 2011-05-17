#! /bin/bash
DestDir=./source
#download the source file that contains information about the EPA facilities
#pcurl.sh http://www.epa-echo.gov/ideadownloads/2010/ICIS_NPDES.zip -n $DestDir/ICIS_NPDES -e zip
#go to the source directory and unzip the file
cd $DestDir
#punzip.sh ICIS_NPDES.zip


cd ..
states="RI MA" #"CA RI MA NY"
for state in $states
do
	#select the subset for the states we are interested: CA, RI, MA, NY
	awk -F\| -v k=$state '$1 ~ /^PEREXNO/ || $1 ~ k {print $0}' source/ICP01.TXT > manual/$state-ICP01.TXT
	justify.sh source/ICP01.TXT manual/$state-ICP01.TXT select_subset
	#create the converters for the states
	#cr-create-convert-sh.sh manual/$state-ICP01.TXT > $state-convert-echo-facilities.sh
done


