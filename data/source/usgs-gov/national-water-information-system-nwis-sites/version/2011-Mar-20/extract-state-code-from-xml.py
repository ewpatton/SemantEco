from BeautifulSoup import BeautifulSoup, BeautifulStoneSoup
import re
import sys

stateCodeXml = file("./source/statecode.xml")
xmlSoup = BeautifulStoneSoup(stateCodeXml)
# open file for output
stateCodesFile = open('./manual/state-code.txt', 'w')
# create a list for saving the state codes
stateCodeList = list()


curRow=xmlSoup.codes.code
while curRow:
	stateCodeList.append(curRow.attrs[0][1])
	curRow=curRow.next

print stateCodeList
#save to file
stateCodesFile.write('\n'.join(stateCodeList))
stateCodesFile.write("\n")
#close the file
stateCodesFile.close()
