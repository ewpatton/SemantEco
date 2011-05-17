#! /bin/bash
#1. move fixed-$state-ICP01.TXT to manual
#2. change the state and state code in the 2 lines below
# This script depends on the files in usgs-gov/national-water-information-system-nwis-sites/version/2011-Mar-20/manual/county-code-txt
#End of usage

state="CA" #RI
stateCode="06" #44
ctyCodeFile="../../../../usgs-gov/national-water-information-system-nwis-sites/version/2011-Mar-20/manual/county-code-txt/US:"$stateCode"-county-code.txt"
facFile="manual/fixed-"$state"-ICP01.TXT"
baseDir="organized/"$state"/"	#this directory will be made automatically

gen_fac_by_county()
{
if [ ! -d $baseDir ]; then 
	echo "$baseDir does NOT exist, mkdir"
	mkdir -p $baseDir
fi
	while read line
	do
		ctyCode=$state${line:6}
		#echo $line
		echo $ctyCode
		curFile=$baseDir$ctyCode"-ICP01.TXT"
		if [ -f $curFile ]; then
			echo "$curFile exists, rm it"
			rm $curFile
		fi
		echo "PEREXNO|FCLTUIN|FCLNAME|FCLTYAD|FCLTCIT|FCLCNTY|FCLSTCD|FCLZIPC|FCLGLAT|FCLGLON|S303D|" > $curFile
		cat $facFile | awk -F\| -v k=$ctyCode '$6 ~ k {print $0}' >> $curFile
	done < $ctyCodeFile
}

gen_fac_by_county
