#! /bin/bash
#usage: ./organizeByCounty.sh state, e.g. RI
# This script depends on the files in usgs-gov/national-water-information-system-nwis-sites/version/2011-Mar-20/manual/county-code-txt
#End of usage

#check the number of command line arguments
if [ $# -lt 1 ]; then
	echo need to specify the state to process e.g. RI ; exit 0;
fi

#state="CA"	#RI, CA
#stateCode="06" #44
state=$1 #RI
declare -A stateCodeTable=( ["CA"]="06" ["MA"]="25" ["NY"]="36" ["RI"]="44")
stateCode="${stateCodeTable[$state]}"
echo "State: $state; State code: $stateCode"

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
		#cat $facFile | awk BEING {RI=$} -F\| -v k=$ctyCode '$6 ~ k {print "automatic/RI/"$1".csv.e1.ttl"}' > $filesByCtyDir$ctyCode"Files.txt"
		#cat $facFile | awk -F\| -v k=$ctyCode -v st=$state '$6 ~ k {print "automatic/st/"$1".csv.e1.ttl"}' > $filesByCtyDir$ctyCode"Files.txt"
		cat $facFile | awk -F\| -v k=$ctyCode -v st=$state '$6 ~ k {printf "automatic/%s/%s.csv.e1.ttl\n", st, $1}' > $filesByCtyDir$ctyCode"Files.txt"
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
