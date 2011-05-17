#! /bin/bash
#usage: 1. please change the state name and code in the 2 lines below
# please change the state name in gen_county_files()
# This script depends on the files in usgs-gov/national-water-information-system-nwis-sites/version/2011-Mar-20/manual/county-code-txt
# For now, it is copying the files into the organized directory, you can change cp to mv at line 57 if you want 
#End of usage
state="CA"	#RI, CA
stateCode="06" #44
ctyCodeFile="../../../../usgs-gov/national-water-information-system-nwis-sites/version/2011-Mar-20/manual/county-code-txt/US:"$stateCode"-county-code.txt"
facFile="organized/fac/fixed-"$state"-ICP01.TXT"
baseDir="organized/"$state"/"	#this directory will be made automatically
filesByCtyDir=$baseDir"filesByCounty/" #this directory will be made automatically
filesByCty=$baseDir"filesByCounty/*.txt"

mkdir_directories()
{
	#make dir if necessary
	if [ ! -d $baseDir ]; then
		mkdir -p $baseDir
	fi

	while read line
	do
		ctyCode=${line:6}
		#echo $line
		echo $ctyCode
		if [ -d $baseDir$ctyCode ]; then
			echo "$baseDir$ctyCode exists"
		else
			mkdir $baseDir$ctyCode
		fi
	done < $ctyCodeFile
}

gen_county_files()
{
	if [ ! -d $filesByCtyDir ]; then 
		echo "$filesByCtyDir does NOT exist, mkdir"
		mkdir $filesByCtyDir
	fi
	while read line
	do
		ctyCode=$state${line:6}
		#echo $line
		echo $ctyCode
		#please change the state name !!!!!!!!!!!!!!
		cat $facFile | awk -F\| -v k=$ctyCode '$6 ~ k {print "automatic/CA/"$1".csv.e1.ttl"}' > $filesByCtyDir$ctyCode"Files.txt"
	done < $ctyCodeFile
}

organize_files_for_one_county() 
{
	while read line
	do
		echo $line
		echo $baseDir$2
		cp $line $baseDir$2
	done < $1
}

organize_files()
{
	for f in $filesByCty
	do
		name=$(basename "$f")
		ctyCode=${name:2:3}
		echo $name
		echo $ctyCode
		organize_files_for_one_county $f $ctyCode
	done
}

mkdir_directories
gen_county_files
organize_files
