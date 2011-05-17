There are several scripts in the directory.
Here are how to use them.

0. install BeautifulSoup, which is available at http://www.crummy.com/software/BeautifulSoup/
1. The script collect-county-code.sh depends on the input file: manual/state-code.txt
2. ./collect-county-code.sh, which will call other 2 python script
3. ./to-source-usgs-sites.sh, note that you need to make a change in the script to specify the state you are interested
4. The above script download data as zip files, you need to ./adhoc-unzip.sh, note that you need to make a change in the script to specify the state you are interested
5. And also ./fix-csv-pml.sh, note that you need to make a change in the script to specify the state you are interested


