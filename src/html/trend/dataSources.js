
function onchange_data_source_selection(){
	var data_source_select=document.getElementById('data_source_selection_canvas');
	var curDataSourceIndex=data_source_select.selectedIndex;
	curDataSource=data_source_select.options[curDataSourceIndex].value;
	//alert(curDataSource);
}
