<html>
	 
<head>
<title>Water Quaility Portal Questionnaire</title>
</head>
<body>
<form action="proc_portal_questionnaire.php" method=post>

<h2>Demographic Questions</h2>
<p>Gender&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="dem-gender" VALUE="male">male&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="dem-gender" VALUE="female">female&nbsp&nbsp&nbsp&nbsp
</p>

<p>To what age group do you belong?</p>
<input TYPE="radio" NAME="dem-age-group" VALUE="0"><10&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="dem-age-group" VALUE="1">10-19&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="dem-age-group" VALUE="2">20-29&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="dem-age-group" VALUE="3">30-39&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="dem-age-group" VALUE="4">40-49&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="dem-age-group" VALUE="5">50-59&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="dem-age-group" VALUE="6">60+&nbsp&nbsp&nbsp&nbsp
<br>

<p>Have you got any education, traing or experience on water quality investigation?</p>
<input TYPE="radio" NAME="dem-expe-type" VALUE="yes">yes&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="dem-expe-type" VALUE="no">no&nbsp&nbsp&nbsp&nbsp
<p>If yes, could you specify the education, traing or experience on water quality investigation you have got? e.g. the degree/certificate you have, the courses you took</p>
<textarea name="dem-expe-text" rows="3" cols="80"></textarea>
<br>

<h2>Survey Questions</h2>
<p>1. After using the application, on a scale from 1-5, letting 1 indicate no awareness and 5 indicate high awareness, how would you rate your awareness of pollution in your community?</p>
<input TYPE="radio" NAME="q-1-answer" VALUE="1">1&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-1-answer" VALUE="2">2&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-1-answer" VALUE="3">3&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-1-answer" VALUE="4">4&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-1-answer" VALUE="5">5&nbsp&nbsp&nbsp&nbsp

<p>2. After using the application, on a scale from 1-5, letting 1 indicate high concern and 5 indicate no concern, rate your concern about water pollution:
</p>
<!--<p>High concerned &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp	Not concerned</p> -->
<input TYPE="radio" NAME="q-2-answer" VALUE="1">1&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-2-answer" VALUE="2">2&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-2-answer" VALUE="3">3&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-2-answer" VALUE="4">4&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-2-answer" VALUE="5">5&nbsp&nbsp&nbsp&nbsp

<p>3. How many polluted bodies of water did you find in your area?&nbsp&nbsp&nbsp&nbsp
</p>
<!--<input type="text" name="numPollutedWater" id="numPollutedWater" />-->
<input TYPE="radio" NAME="q-3-answer" VALUE="1">0&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-3-answer" VALUE="2">1&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-3-answer" VALUE="3">2&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-3-answer" VALUE="4">3&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-3-answer" VALUE="5">4+&nbsp&nbsp&nbsp&nbsp

<p>4. On a scale of 1-5, letting 1 indicate not likely and 5 indicate very likely, how likely are you to use a tool such as the Water Quality Portal to follow pollution in your community?
</p>
<!--<p>High concerned &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp	Not concerned</p> -->
<input TYPE="radio" NAME="q-4-answer" VALUE="1">1&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-4-answer" VALUE="2">2&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-4-answer" VALUE="3">3&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-4-answer" VALUE="4">4&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-4-answer" VALUE="5">5&nbsp&nbsp&nbsp&nbsp

<p>5. On a scale of 1-5, letting 1 indicate no difficulty and 5 indicate high difficulty, how difficult was it to use the Water Quality Portal?
</p>
<!--<p>High concerned &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp	Not concerned</p> -->
<input TYPE="radio" NAME="q-5-answer" VALUE="1">1&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-5-answer" VALUE="2">2&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-5-answer" VALUE="3">3&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-5-answer" VALUE="4">4&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-5-answer" VALUE="5">5&nbsp&nbsp&nbsp&nbsp

<p>6. On a scale of 1-5, letting 1 indicate poor responsiveness and 5 indicate quick responsiveness, how responsive was the Water Quality Portal?
</p>
<!--<p>High concerned &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp	Not concerned</p> -->
<input TYPE="radio" NAME="q-6-answer" VALUE="1">1&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-6-answer" VALUE="2">2&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-6-answer" VALUE="3">3&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-6-answer" VALUE="4">4&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-6-answer" VALUE="5">5&nbsp&nbsp&nbsp&nbsp

<p>7. On a scale of 1-5, letting 1 indicate poor quality and 5 indicate excellent quality, please rate the quality of the data presented by the Water Quality Portal.
</p>
<!--<p>High concerned &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp	Not concerned</p> -->
<input TYPE="radio" NAME="q-7-answer" VALUE="1">1&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-7-answer" VALUE="2">2&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-7-answer" VALUE="3">3&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-7-answer" VALUE="4">4&nbsp&nbsp&nbsp&nbsp
<input TYPE="radio" NAME="q-7-answer" VALUE="5">5&nbsp&nbsp&nbsp&nbsp

<p>8. What benefits does the Water Quality Portal provide compared to existing solutions in your community?
</p>
<textarea name="q-8-answer" rows="3" cols="80"></textarea>

<p>9. What benefits does your existing solution provide that the Water Quality Portal does not?
</p>
<textarea name="q-9-answer" rows="3" cols="80"></textarea>

<br>
<br>
<input type="submit" name="formSubmit" value="Submit">
</form>
</body>
</html>


