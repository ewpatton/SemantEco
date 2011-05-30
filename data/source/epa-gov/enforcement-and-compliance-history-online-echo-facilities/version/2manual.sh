#!/bin/bash
#
# This is run from the conversion cockpit by 2source.sh
# see https://github.com/timrdf/csv2rdf4lod-automation/wiki/Conversion-cockpit


# OPTION 1:
#
# Convert just one of the files from the zip, and do it in one pass.
# This is the traditional csv2rdf4lod approach.

#$CSV2RDF4LOD_HOME/bin/cr-create-convert-sh.sh -w source/ICP01.TXT



# OPTION 2:
#
# Convert the same one file from the zip, but slice by state.
# This is the water portal's content-based (instead of source-based) management
#
# (for some reason, calling from 2source.sh fails to create the manual/*.TXT; running it manually after 2source.sh works)

for state in AK AL AR AS CA CO CT DC GA GE GM GU HI ID IL IN JA LA MA MD MP MT MW NE NH NM NN NV NY OK PA PE PR RI SD SR TN TX UT VI WI; do
   cat source/ICP01.TXT | awk -F\| -v state=${state} '$1 ~ /^PEREXNO/ || $1 ~ state {print $0}' > manual/${state}-ICP01.TXT
   justify.sh source/ICP01.TXT manual/${state}-ICP01.TXT select_subset
done

$CSV2RDF4LOD_HOME/bin/cr-create-convert-sh.sh -w manual/*.TXT
