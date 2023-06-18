$( document ).ajaxError(function( event, jqxhr, settings, thrownError ) {

    if (jqxhr.status == "401" || jqxhr.status == 401) {
        window.location.href = '/logout.do';
    }
    // error 0 status bilan keladi en
    if (jqxhr.status == 0) {
        $.notify({
            message: jqxhr.statusText
        }, {
            type: 'danger'
        });
    }
});