ZipCodeModule.showAddress = function(zip) {
    zip = zip || $("#zip").val();
    if(zip == null || zip == "") {
        return;
    }
    if(zip.length != 5) {
        alert("The input zip code is not valid! Please check and input again.");
        return;
    }
    $.bbq.removeState("uri");
    SemantEco.action = "decodeZipCode";
    if($.bbq.getState("zip") == zip) {
        // handle if the "Go" button is clicked a second time without
        // the zip code changing
        SemantEco.getLimitData();
    }
    $.bbq.pushState({"zip": zip});
    $("#zip").val(zip);
    return false;
};

ZipCodeModule.processZipCode = function(response) {
    var data = JSON.parse(response);
    console.log(response);
    SemantEco.action = "getLimitData";
    $.bbq.pushState({"state":data.result.stateAbbr,
        "stateCode":data.result.stateCode,
        "county":data.result.countyCode,
        "lat":data.result.lat, "lng":data.result.lng});
};

// this will need to be assigned to SemantEco until a better solution is implemented
// in the action protocol
SemantEco.decodeZipCode = function() {
    var zip = $.bbq.getState("zip");
    if(zip == null) {
        SemantEcoUI.hideSpinner();
        return;
    }
    SemantEco.pagedData = [];
    SemantEco.curPage = 0;
    SemantEcoUI.doGeocode(zip);
    SemantEcoUI.showSpinner();
    ZipCodeModule.decodeZipCode({}, ZipCodeModule.processZipCode);
};

$(window).bind('initialize',function() {
  $("#zip")[0].onkeypress = function(e) {
    if(e.charCode == 13) {
      ZipCodeModule.showAddress();
      return false;
    }
    return true;
  };
  ZipCodeModule.deferLookup = ($.bbq.getState('zip') != null);
});

$(window).bind('initialized.semanteco', function() {
  if(ZipCodeModule.deferLookup) {
    SemantEco.decodeZipCode();
  }
  $("div.search").unbind("click");
  $("div.search").bind("click", function() {
    ZipCodeModule.showAddress();
  });
});
