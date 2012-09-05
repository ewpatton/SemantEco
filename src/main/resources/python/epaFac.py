from BeautifulSoup import BeautifulSoup
import sys
import re

#argv[1] is the input html file, argv[2] is the output file
#No CWA: facilityResult110041346184, With CWA: facilityResult110012303854
html = file(sys.argv[1])
soup = BeautifulSoup(html)
f = open(sys.argv[2], 'w')

tableName = soup.find(text=" CWA/NPDES Compliance Status")
if tableName == None:
	f.write("No CWA\n")
	f.close()
	sys.exit(0)
#[tag.name for tag in tableName.findParents()]
#table = tableName.findParent('table')
firstRow = tableName.findParent('tr').nextSibling.nextSibling

#thirdRow = firstRowName.findParent('tr').nextSibling.nextSibling
#qtr = thirdRow.find(text=re.compile("QTR*"))
#qtrDur =qtr.findNextSibling(text = lambda(text): len(text) == 10)
#print curRow.prettify()
#curRow = table.find(text=re.compile("Jul-Sep07")).findParent('tr')
#qtr = curRow.find(text=re.compile("QTR*"))
#qtrDur =qtr.next.next.next

qtr = firstRow.find(text=re.compile("QTR*"))
if qtr:
	curCell = qtr.findParent('td')
	qtrList = list()
	qtrDurList = list()
	a = 0
	while a < 12:
		a = a + 1
		qtr = curCell.find(text=re.compile("QTR*"))
		qtrList.append(qtr)
		qtrDur = qtr.next.next
		#qtrDur =qtr.findNextSibling(text = lambda(text): len(text) == 10)
		qtrDurList.append(qtrDur)
		curCell = curCell.findNext('td')
	print qtrList
	print qtrDurList

# Non-compliance in Quarter
booleanRowName = soup.find(text=re.compile("Non-compliance in Quarter"))#text="  Non-compliance in Quarter"
if booleanRowName: 
	booleanRow = booleanRowName.findParent('tr')
	#skip the row with "Non-compliance in Quarter" and the row with &nbsp
	curCell = booleanRow.find('td').findNext('td').findNext('td')
	booleanList = list()
	a = 0
	while a < 12:
		a = a + 1
		booleanValue = curCell.findNext(text=re.compile("Yes|No"))
		booleanList.append(booleanValue)
		curCell = curCell.findNext('td')
	print booleanList
#curCell = curCell.findNext('td')
#print curCell for testing 
#expecting '<td><font size="1" face="Arial, Helvetica" color="GRAY">&nbsp;</font></td>'

#pH
##[tag.name for tag in pHName.findParents()]
pHName = soup.find(text=re.compile("pH"))
if pHName:
	pHRow = pHName.findParent('tr')
	curCell = pHRow.find('td')
	#to skip the Row with content 'Neither'
	curCell = curCell.findNext('td')
	curCell = pHName.parent.parent.nextSibling.nextSibling
	pHList = list()
	a = 0
	while a < 12:
		a = a + 1
		pHValue = curCell.findNext(text=re.compile("nbsp|Lim Viol"))
		pHList.append(pHValue)
		curCell = curCell.findNext('td')
	print pHList
#Chlorine

#find the link of Only Charts with Violations
linkRowName = soup.find(text=re.compile("  Effluent Violations by NPDES Parameter:"))
curCell = linkRowName.findParent('td').findNext('a')
if curCell:
	f.write(curCell['href']+'\n')
else:
	f.write("No Only Charts with Violations")

f.write("qtr List\n")
f.write('\n'.join(qtrList))
f.write("\n")
f.write("qtr Duration List\n")
f.write('\n'.join(qtrDurList))
f.write("\n")
f.write("NC Boolean List\n")
f.write('\n'.join(booleanList))
f.write("\n")
#f.write("pH List\n")
#f.write('\n'.join(pHList))
#f.write("\n")

f.close()












