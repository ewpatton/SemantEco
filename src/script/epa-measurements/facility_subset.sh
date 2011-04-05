#! /bin/bash

awk -F\| '$1 ~ /^PEREXNO/ || $1 ~ /^RI/ {print $0}' source/ICP01.TXT > manual/RI-ICP01.TXT
justify.sh source/ICP01.TXT manual/RI-ICP01.TXT select_subset

cat manual/RI-ICP01.TXT | sed -e 's/^/"/' -e 's/|/","/g' -e 's/$/"/' > manual/RI-ICP01.TXT.csv
justify.sh manual/RI-ICP01.TXT manual/RI-ICP01.TXT.csv redelimit
