#!/bin/bash
# national-water-information-system-nwis-sites 2011-Mar-20 ()
#--------------------------------------------------------------

CSV2RDF4LOD_HOME=${CSV2RDF4LOD_HOME:?"not set; source csv2rdf4lod/source-me.sh"}

surrogate="http://logd.tw.rpi.edu" # Came from $CSV2RDF4LOD_BASE_URI when cr-create-convert-sh.sh created this script.
sourceID="usgs-gov"
datasetID="national-water-information-system-nwis-sites"
datasetVersion="2011-Mar-20"        # NO SPACES; Use curl -I -L http://www.data.gov/download/national-water-information-system-nwis-sites/csv | grep Last-Modified: | awk '{printf(%s-%s-%s,,,)}'
versionID="2011-Mar-20"        # renaming datasetVersion (deprecating datasetVersion)
eID="1"                             # enhancement identifier
if [ $# -ge 2 ]; then
   if [ $1 == "-e" ]; then
     eID="$2" 
   fi
fi


# source/44/US-44-001-site.csv
sourceDir="source/44"                  # if directly from source, 'source'; if manual manipulations of source were required, 'manual'.
destDir="automatic/44"                 # always 'automatic'
#--------------------------------------------------------------


#-----------------------------------
datafile="US-44-001-site.csv"
data="$sourceDir/$datafile"
subjectDiscriminator="us-44-001-site" # Additional part of URI for subjects created; must be URI-ready (e.g., no spaces).
header=                             # Line that header is on; only needed if not '1'. '0' means no header.
dataStart=                          # Line that data starts; only needed if not immediately after header.
repeatAboveIfEmptyCol=              # 'Fill in' value from row above for this column.
onlyIfCol=                          # Do not process if value in this column is empty
interpretAsNull=                    # NO SPACES
dataEnd=                            # Line on which data stops; only needed if non-data bottom matter (legends, footnotes, etc).
source $CSV2RDF4LOD_HOME/bin/convert.sh


#-----------------------------------
datafile="US-44-003-site.csv"
data="$sourceDir/$datafile"
subjectDiscriminator="us-44-003-site" # Additional part of URI for subjects created; must be URI-ready (e.g., no spaces).
header=                             # Line that header is on; only needed if not '1'. '0' means no header.
dataStart=                          # Line that data starts; only needed if not immediately after header.
repeatAboveIfEmptyCol=              # 'Fill in' value from row above for this column.
onlyIfCol=                          # Do not process if value in this column is empty
interpretAsNull=                    # NO SPACES
dataEnd=                            # Line on which data stops; only needed if non-data bottom matter (legends, footnotes, etc).
source $CSV2RDF4LOD_HOME/bin/convert.sh


#-----------------------------------
source $CSV2RDF4LOD_HOME/bin/convert-aggregate.sh
