function vocab ()
{$("div#vocab")[0].innerHTML="<h3>Searching for: " + $("[name=term]").val() + "...</hr3><br>"; return true;}