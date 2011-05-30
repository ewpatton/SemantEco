#!/bin/bash
#
# see https://github.com/timrdf/csv2rdf4lod-automation/wiki/Automated-creation-of-a-new-Versioned-Dataset

export CSV2RDF4LOD_CONVERT_OMIT_RAW_LAYER="true"
$CSV2RDF4LOD_HOME/bin/cr-create-versioned-dataset-dir.sh cr:auto                                                    \
                                                        'http://www.epa-echo.gov/ideadownloads/2010/ICIS_NPDES.zip' \
                                                       --header-line        1                                       \
                                                       --delimiter         '|'
