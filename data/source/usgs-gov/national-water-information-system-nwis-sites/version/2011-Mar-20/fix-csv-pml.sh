#!/bin/bash
# Usage:
# The script can process multiple states
# You need to specify the states you would like to process in line 6
# Enf of Usage--------------------------------

stateDirs="36" #44 25  06   
baseDir=source
expName=US-25-001-site
strlen=${#expName}

for stateDir in $stateDirs
do
	cd $baseDir/$stateDir
	FILES="./*"
	for f in $FILES
	do
		name=$(basename "$f")
		# echo "Processing $f" 
		if [[ $name == *.csv.pml.ttl ]]; then
			echo $name
     		prefix=${name:0:$strlen}
			echo $prefix
			sed "s/data.csv/$prefix.csv/g" $f > fixed-$name
		fi
	done
	cd ../..
	pwd
done
