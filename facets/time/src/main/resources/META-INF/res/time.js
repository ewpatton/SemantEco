$(window).bind("initialize", function() {
    var start = $("#TimeFacet input[name='from']");
    var end = $("#TimeFacet input[name='until']");
    var format = "yy-mm-dd";
    start.datepicker({
      changeMonth: true,
      dateFormat: format,
      maxDate: 0,
      onClose: function( selected ) {
        end.datepicker( "option", "minDate", selected );
      }
    });
    end.datepicker({
      changeMonth: true,
      dateFormat: format,
      maxDate: 0,
      onClose: function( selected ) {
        start.datepicker( "option", "maxDate", selected );
      }
    });
});