<%
String base = (String)request.getAttribute("moduleBase");
%>
<div id="DataTypeFacet" class="facet no-rest">
<input name="type" value="cleanwater" checked="checked" type="checkbox" />
<img height="12" src="<%=base %>clean-water.png" />Clean Water<br />
<input name="type" value="facility" checked="checked" type="checkbox" />
<img height="12" src="<%=base %>facility.png" />Facility<br />
<input name="type" value="pollutedwater" checked="checked" type="checkbox" />
<img height="12" src="<%=base %>polluted-water.png" />Polluted Water<br />
<input name="type" value="pollutedfacility" checked="checked" type="checkbox" />
<img height="12" src="<%=base %>polluted-facility.png" />Polluted Facility<br />
</div>