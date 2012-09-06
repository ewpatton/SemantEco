from BeautifulSoup import BeautifulSoup, BeautifulStoneSoup
import re
import sys

#countyCodeXml = file("/home/ping/research2009-2010/python/water2/36-countycode.xml")
stateCode=sys.argv[1]

countyCodeXml = file('source/county-code-xml/'+stateCode+'-county-code.xml')
xmlSoup = BeautifulStoneSoup(countyCodeXml)
# open file for output
countyCodesFile = open('manual/county-code-txt/'+stateCode+'-county-code.txt', 'w')
# create a list for saving the county codes
countyCodeList = list()

curRow=xmlSoup.codes.code
while curRow:
	countyCodeList.append(curRow.attrs[0][1])
	curRow=curRow.next

#print countyCodeList
#save to file
countyCodesFile.write('\n'.join(countyCodeList))
countyCodesFile.write("\n")
#close the file
countyCodesFile.close()
