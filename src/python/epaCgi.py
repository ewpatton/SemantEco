from BeautifulSoup import BeautifulSoup
import re
import sys

#html = file("/home/ping/research/python/water/comm/epaCgiResult")
#argv[1] is the input html file, argv[2] is the directory for output
html = file(sys.argv[1])
soup = BeautifulSoup(html)
# open file for output
outPutDir = sys.argv[2]
#code = sys.argv[3]
epaCgiSoupFile = open(outPutDir+'/epaCgiSoupData', 'w')
rfsAddressFile = open(outPutDir+'/epaCgiSoupAddress', 'w')
rfsIDFile = open(outPutDir+'/epaCgiSoupRfsID', 'w')
rfsNameFile = open(outPutDir+'/epaCgiSoupRfsName', 'w')
rfs1stAddressLineFile = open(outPutDir+'/epaCgiSoup1stAddressLine', 'w')
rfs2ndAddressLineFile = open(outPutDir+'/epaCgiSoup2ndAddressLine', 'w')
numInspectionFile = open(outPutDir+'/epaCgiSoupNumInspection', 'w')
numQtrNCFile = open(outPutDir+'/epaCgiSoupNumQtrNC', 'w')
numEEFile = open(outPutDir+'/epaCgiSoupNumEE', 'w')

#rfsIDFile = open('/home/ping/research/python/water/CgiSoupOutput/epaCgiSoupRfsID', 'w')
#rfsNameFile = open('/home/ping/research/python/water/CgiSoupOutput/epaCgiSoupRfsName', 'w')
#rfs1stAddressLineFile = open('/home/ping/research/python/water/CgiSoupOutput/epaCgiSoup1stAddressLine', 'w')
#rfs2ndAddressLineFile = open('/home/ping/research/python/water/CgiSoupOutput/epaCgiSoup2ndAddressLine', 'w')
#numInspectionFile = open('/home/ping/research/python/water/CgiSoupOutput/epaCgiSoupNumInspection', 'w')
#numQtrNCFile = open('/home/ping/research/python/water/CgiSoupOutput/epaCgiSoupNumQtrNC', 'w')
#numEEFile = open('/home/ping/research/python/water/CgiSoupOutput/epaCgiSoupNumEE', 'w')


rfsIDName = soup.find(text=re.compile("FRS ID:"))#FRS ID: 110012303854
rfsIDRow = rfsIDName.findParent('tr')
curRow = rfsIDRow
#rfsIDCell = rfsIDRow.findNext('td')
#rfsIDCell = rfsIDCell.findNext('td')

rfsIDList = list()
rfsNameList = list()
rfs1stAddressLineList = list()
rfs2ndAddressLineList = list()
numInspectionList = list()
numQtrNCList = list()
numEEList = list()
a = 0
while curRow:
	rfsID = curRow.find(text=re.compile("FRS ID:"))	
	if rfsID:	
		rfsIDList.append(str(a)+' '+rfsID)
		epaCgiSoupFile.write(rfsID+'\n')
		numInspectionList.append('#')
		numQtrNCList.append('#')
		numEEList.append('#')
		rfsIDCell = curRow.findNext('td').findNext('td').findNext('td', valign="middle", width="80")
		#rfs Name
		rfsNameCell = curRow.findNext('a')
		rfsName = rfsNameCell.contents[0]
		rfsNameList.append(str(a)+' '+rfsName)
		epaCgiSoupFile.write('Name:'+rfsName+'\n')		
		#Address		
		# 2nd Address line, the position is the same if there is font or not	
		rfs2ndAddressLineCell = rfsNameCell.findNext('div')
		rfs2ndAddressLine = rfs2ndAddressLineCell.contents[0]
		rfs2ndAddressLineList.append(str(a)+' '+rfs2ndAddressLine)		
		# 1st Address line based on the position of the 2nd Address line
		rfs1stAddressLine = rfs2ndAddressLineCell.previous.previous
		rfs1stAddressLineList.append(str(a)+' '+rfs1stAddressLine)
		epaCgiSoupFile.write('AL1: '+rfs1stAddressLine+'\n')
		epaCgiSoupFile.write('AL2: '+rfs2ndAddressLine+'\n')
		rfsAddressFile.write(rfs1stAddressLine+', ')
		rfsAddressFile.write(rfs2ndAddressLine+'\n')
	else:
		rfsIDCell = curRow.findNext('td').findNext('td', valign="middle", width="80")
	#print rfsIDList
	

	#Num of Inspections	
	if rfsIDCell.find('a'):
		numInspection = u'&nbsp;'
	elif rfsIDCell.find('font'):
		numInspection = rfsIDCell.contents[0].contents[0].contents[0]
	else:
		numInspection = rfsIDCell.contents[0].contents[0]
	numInspectionList.append(str(a)+' '+numInspection)
	epaCgiSoupFile.write('I'+numInspection)
	#Qtrs in Non Compliance
	numQtrCell = rfsIDCell.findNext('td', valign="middle", width="80")
	if numQtrCell.find('font'):
		numQtrNC = numQtrCell.contents[0].contents[0].contents[0]
	else:
		numQtrNC = numQtrCell.contents[0].contents[0]
	numQtrNCList.append(str(a)+' '+numQtrNC)
	epaCgiSoupFile.write('Q'+numQtrNC)
	#Effluent Exceedances
	numEECell = numQtrCell.findNext('td', valign="middle", width="80")
	if numEECell.find('font'):
		numEE = numEECell.contents[0].contents[0].contents[0]
	else:
		numEE = numEECell.contents[0].contents[0]
	numEEList.append(str(a)+' '+numEE)
	epaCgiSoupFile.write('E'+numEE+'\n')
	#epaCgiSoupFile.write("\n")
	#proceed to the next row
	curRow = curRow.findNext('tr', {"class" : re.compile("tableBodyRow")})
	a = a+1
print numInspectionList 
print numQtrNCList
print numEEList

rfsIDFile.write("rfs ID List\n")
rfsIDFile.write('\n'.join(rfsIDList))
rfsIDFile.write("\n")
rfsNameFile.write("rfs Name List\n")
rfsNameFile.write('\n'.join(rfsNameList))
rfsNameFile.write("\n")
rfs1stAddressLineFile.write("rfs 1st Address Line\n")
rfs1stAddressLineFile.write('\n'.join(rfs1stAddressLineList))
rfs1stAddressLineFile.write("\n")
rfs2ndAddressLineFile.write("rfs 2nd Address Line\n")
rfs2ndAddressLineFile.write('\n'.join(rfs2ndAddressLineList))
rfs2ndAddressLineFile.write("\n")
numInspectionFile.write("number of Inspections List\n")
numInspectionFile.write('\n'.join(numInspectionList))
numInspectionFile.write("\n")
numQtrNCFile.write("number of Qtrs in Non Compliance List\n")
numQtrNCFile.write('\n'.join(numQtrNCList))
numQtrNCFile.write("\n")
numEEFile.write("number of Effluent Exceedances List\n")
numEEFile.write('\n'.join(numEEList))
numEEFile.write("\n")

rfsIDFile.close()
rfsNameFile.close()
rfs1stAddressLineFile.close()
rfs2ndAddressLineFile.close()
numInspectionFile.close()
numQtrNCFile.close()
numEEFile.close()
rfsAddressFile.close()
epaCgiSoupFile.close()
#f.close()












