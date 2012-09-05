#!/bin/bash
# 
#
# Usage:
#
#   html2csv-prov.sh epa-regulation.html
#   (produces epa-regulation.csv.pml.ttl)
#

usage_message="usage: `basename $0` the name of the html file"

if [ $# -lt 1 ]; then
   echo $usage_message 
   exit 1
fi


CSV2RDF4LOD_HOME=${CSV2RDF4LOD_HOME:?"not set; source csv2rdf4lod/source-me.sh or see https://github.com/timrdf/csv2rdf4lod-automation/wiki/CSV2RDF4LOD-not-set"}

srcFile=$1
#file=$1".csv"
fname=$(echo $srcFile|cut -d'.' -f1)
file=${fname}.csv
echo $file

myMD5=`${CSV2RDF4LOD_HOME}/bin/util/md5.sh $0`

if [ ! -e $file.pml.ttl ]; then
   requestID=`java edu.rpi.tw.string.NameFactory`
   #usageDateTime=`date +%Y-%m-%dT%H:%M:%S%z | sed 's/^\(.*\)\(..\)$/\1:\2/'`
   usageDateTime=`dateInXSDDateTime.sh`

   if [ `man stat | grep 't timefmt' | wc -l` -gt 0 ]; then
      # mac version
      modDateTime=`stat -t "%Y-%m-%dT%H:%M:%S%z" $srcFile | awk '{gsub(/"/,"");print $9}' | sed 's/^\(.*\)\(..\)$/\1:\2/'`
   elif [ `man stat | grep '%y     Time of last modification' | wc -l` -gt 0 ]; then
      # some other unix version
      modDateTime=`stat -c "%y" $srcFile | sed -e 's/ /T/' -e 's/\..* / /' -e 's/ //' -e 's/\(..\)$/:\1/'`
   fi

     # Relative paths.
     inferenceStep="inferenceStep$requestID"
     wasControlled="wasControlledBy$requestID"

      # Relative paths.
      fileURI="<$file>"
      sourceUsage="<sourceUsage$requestID>"
      nodeSet="<nodeSet$requestID>"
      zipNodeSet="<nodeSet${requestID}_zip_antecedent>"

      echo "@prefix rdfs:       <http://www.w3.org/2000/01/rdf-schema#> ."                       > $file.pml.ttl
      echo "@prefix xsd:        <http://www.w3.org/2001/XMLSchema#> ."                          >> $file.pml.ttl
      echo "@prefix dcterms:    <http://purl.org/dc/terms/> ."                                  >> $file.pml.ttl
      echo "@prefix nfo:        <http://www.semanticdesktop.org/ontologies/nfo/#> ."            >> $file.pml.ttl
      echo "@prefix pmlp:       <http://inference-web.org/2.0/pml-provenance.owl#> ."           >> $file.pml.ttl
      echo "@prefix pmlj:       <http://inference-web.org/2.0/pml-justification.owl#> ."        >> $file.pml.ttl
      echo "@prefix conv:       <http://purl.org/twc/vocab/conversion/> ."                      >> $file.pml.ttl
      echo "@prefix foaf:       <http://xmlns.com/foaf/0.1/> ."                                 >> $file.pml.ttl
      echo "@prefix sioc:       <http://rdfs.org/sioc/ns#> ."                                   >> $file.pml.ttl
      echo "@prefix oboro:      <http://obofoundry.org/ro/ro.owl#> ."                           >> $file.pml.ttl
      echo "@prefix oprov:      <http://openprovenance.org/ontology#> ."                        >> $file.pml.ttl
      echo "@prefix hartigprov: <http://purl.org/net/provenance/ns#> ."                         >> $file.pml.ttl
      echo                                                                                      >> $file.pml.ttl
      $CSV2RDF4LOD_HOME/bin/util/user-account.sh                                                >> $file.pml.ttl
      echo                                                                                      >> $file.pml.ttl
      echo $fileURI                                                                             >> $file.pml.ttl
      echo "   a pmlp:Information;"                                                             >> $file.pml.ttl
      echo "   pmlp:hasReferenceSourceUsage $sourceUsage;"                                      >> $file.pml.ttl
      echo "."                                                                                  >> $file.pml.ttl
      $CSV2RDF4LOD_HOME/bin/util/nfo-filehash.sh "$file"                                        >> $file.pml.ttl
      echo                                                                                      >> $file.pml.ttl
      echo "$sourceUsage"                                                                       >> $file.pml.ttl
      echo "   a pmlp:SourceUsage;"                                                             >> $file.pml.ttl
      echo "   pmlp:hasSource        <$srcFile>;"                                                   >> $file.pml.ttl
      echo "   pmlp:hasUsageDateTime \"$usageDateTime\"^^xsd:dateTime;"                         >> $file.pml.ttl
      echo "."                                                                                  >> $file.pml.ttl
      echo                                                                                      >> $file.pml.ttl
      echo "<$srcFile>"                                                                             >> $file.pml.ttl
      echo "   a pmlp:Source;"                                                                  >> $file.pml.ttl
      if [ ${#modDateTime} -gt 0 ]; then
      echo "   pmlp:hasModificationDateTime \"$modDateTime\"^^xsd:dateTime;"                 >> $file.pml.ttl
      fi
      echo "."                                                                                  >> $file.pml.ttl
      echo                                                                                      >> $file.pml.ttl
      echo $nodeSet                                                                             >> $file.pml.ttl
      echo "   a pmlj:NodeSet;"                                                                 >> $file.pml.ttl
      echo "   pmlj:hasConclusion $fileURI;"                                                    >> $file.pml.ttl
      echo "   pmlj:isConsequentOf ["                                                           >> $file.pml.ttl
      echo "      a pmlj:InferenceStep;"                                                        >> $file.pml.ttl
      echo "      pmlj:hasIndex 0;"                                                             >> $file.pml.ttl
      echo "      pmlj:hasAntecedentList ();"                                      >> $file.pml.ttl
      echo "      pmlj:hasSourceUsage     $sourceUsage;"                                        >> $file.pml.ttl
      echo "   pmlj:hasInferenceEngine conv:html2csv_$myMD5;"                                        >> $file.pml.ttl
      echo "   pmlj:hasInferenceRule   conv:Extract_and_Reformat;" 									>> $file.pml.ttl
      echo "      oboro:has_agent          `$CSV2RDF4LOD_HOME/bin/util/user-account.sh --cite`;">> $file.pml.ttl
      echo "      hartigprov:involvedActor `$CSV2RDF4LOD_HOME/bin/util/user-account.sh --cite`;">> $file.pml.ttl
      echo "   ];"                                                                              >> $file.pml.ttl
      echo "."                                                                                  >> $file.pml.ttl
      echo                                                                                      >> $file.pml.ttl
      echo "conv:html2csv_$myMD5"                                                           >> $file.pml.ttl
      echo "   a pmlp:InferenceEngine, conv:html2csv;"                                          >> $file.pml.ttl
      echo "   dcterms:identifier \"md5_$myMD5\";"                                              >> $file.pml.ttl
      echo "."                                                                                  >> $file.pml.ttl
      echo                                                                                      >> $file.pml.ttl
      echo "conv:html2csv rdfs:subClassOf pmlp:InferenceEngine ."                               >> $file.pml.ttl
      echo                                                                                      >> $file.pml.ttl
else 
	echo "$file already exists."
fi


