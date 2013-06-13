/* -*- espresso-indent-level: 2; -*- */
var delim =';';

//function called when items have been selected and 'OK' pressed.
//This updates the parent window's textbox that is associated with
//the parameter(s) selected on this page.
function selectItems() {
  
  //get handle on associated textbox in parent window
  var selectionString = '';
  
  //update textbox in parent window
  var items = document.getElementById('checklist_div').getElementsByTagName('input');
  for (var i = 0; i < items.length; i++) {
    if (items[i].checked) {
      //this item has been chosen
      selectionString += items[i].value + delim;
    }
  }
  try {
    //update parent window textbox
    window.opener.document.getElementById(getURLParam('caller')).value = selectionString.substring(0, selectionString.length-1);
  } catch(err){
    alert("sorry!");
  }
  
}

function populateSelectList(items) {
  var checklist_div = document.getElementById('checklist_div');	//get a handle on element to put checkboxes in
  checklist_div.innerHTML = '';

  for (var i = 0; i < items.length; i++) {
    var value = items[i];
    var desc = items[i];

    var item_div = document.createElement('div');

    var check =  createOrDisplayCheckBox({
      name: 'list_item_' + i,
      value: value
    });

    //see if this item was previously selected
    var isChecked = isPreviouslyChecked(value);
    check.checked = isChecked;
    check.defaultChecked = isChecked;

    var label = document.createElement('label');
    label.innerHTML = desc;
    label.htmlFor = 'list_item_' + i;

    checklist_div.appendChild(item_div);
    item_div.appendChild(check);
    item_div.appendChild(label);

  }

  document.getElementById('toggle_all_div').style.display = 'block';
  // copy the submit buttom for the bottom
  var bottom_button_div = document.getElementById("bottom_submit_button_div");
  var top_button_div = document.getElementById("top_submit_button_div");
  var top_button = top_button_div.innerHTML;

  bottom_button_div.innerHTML = top_button.replace(/top_/,"bottom_");
}

function createOrDisplayCheckBox(config){
  var check = document.createElement('input');
  check.type = 'checkbox';
  check.value = config.value;
  check.name = config.name;
  check.id = config.name;
  return check;
}

function isPreviouslyChecked(value){
  var previousSelections = getURLParam('previousSelections').split(delim);
  if (previousSelections) {
    for (var j = 0; j < previousSelections.length; j++) {
      if (previousSelections[j] == value){
	return true;
      }
    }
  }
  return false;
}

function toggleChecks(node) {
  var items = document.getElementsByTagName('input');

  if (node.checked) {
    for (var i = 0; i < items.length; i++) {
      items[i].checked = true;
      items[i].defaultChecked = true;
    }
    document.getElementById('toggle_all').innerHTML = 'Uncheck The Box To Deselect All Items';
  } else {
    for (var i = 0; i < items.length; i++) {
      items[i].checked = false;
      items[i].defaultChecked = false;
    }
    document.getElementById('toggle_all').innerHTML = 'Check The Box To Select All Items';
  }
}
