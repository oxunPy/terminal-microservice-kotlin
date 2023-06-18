$(document).ready(function () {

    var datatable = $("#dataTable").DataTable({
        "ajax" : {
            "url" : "/reports",
            "type" : "POST",
            "content-type" : "application/json; charset=utf-8",
            "dataType" : 'json',
            "headers" : {
                'Content-type' : 'application/json',
                'Accept' : 'application/json'
            },
            "data": function (json) {
                return JSON.stringify($.extend({}, json, {
                    "filter": {
                        "from" : $('#filterArea [name = "fromDate"]').val(),
                        "to" : $('#filterArea [name = "toDate"]').val()
                    }
                }));
            }
        },
        "columns" : [
            {
                "data" : "invoiceItemId",
                orderable: true,
            },
            {
                "data" : "dateOfSell",
                orderable: true,
            },
            {
                "data": "productQuantity",
                orderable: false
            },
            {
                "data" : "productPrice",
                orderable: false,
            },
            {
                "data" : "typeOfOperation",
                orderable: false
            },
            {
                "data": "productName",
                orderable: false
            },
            {
                "data": "priceCurrency",
                orderable: false
            }
        ],
        "serverSide": true,
        "processing": true,
        "pageLength" : 10,
        "ordering" : "true",
        "order" : [],
        "info" : true,
        "searching" : false,
        "language" : {
            url : "/datatables/localisation"
        }
    });
    $('#dataTable tbody').on('click', 'tr', function () {
        $('.table-info').removeClass('table-info');
        $(this).toggleClass('table-info');
    });
    initFilter();
})

function initFilter(){
    $("#filterArea").css("display", "none");
    $("#filterBtn").click(function(){
        $("#filterArea").toggle();
    });
}

function doFilter() {
    $('#dataTable').DataTable().ajax.reload();
}

function clearFilter(){
    $('#filterArea [name="status"]').val(null).trigger('change');
    $('#filterArea [name="filterName"]').val('');

    doFilter();
}