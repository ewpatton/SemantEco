
//ref: http://stackoverflow.com/questions/163563/javascript-date-constructor-doesnt-work
//the input str is like: 1970-03-03T11:45:00
function myDate(str){
	//var str="1970-03-03";
	var strLen=str.length;
	if(strLen<10){
		alert("Invalid date string: "+str);
		return undefined;
	}
  //var str='1970-03-03T11:45:01';
	var dateStr=str.substring(0, 10);
  //var curDate=new Date(dateStr);
	//date
	var dateArray = dateStr.split("-");
	var theDate = new Date(dateArray[0],dateArray[1],dateArray[2]); 

	//hour, min, second
	if(str.length>=19){
		var  hourStr=str.substring(11, 13);
		theDate.setHours(hourStr);
		var  minStr=str.substring(14, 16);
		theDate.setMinutes(minStr);
		var  secondStr=str.substring(17);
		theDate.setSeconds(secondStr);
	}
	//alert(str.length);
	//alert(dateStr+", "+hourStr+", "+minStr+", "+secondStr);
	//alert(theDate);
	return theDate;
}
