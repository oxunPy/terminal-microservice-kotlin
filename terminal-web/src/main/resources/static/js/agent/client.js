$(document).ready(function () {

    var datatable = $("#dataTable").DataTable({
        "ajax" : {
            "url" : "/data/account",
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
                        "accountType": "DEBITOR"
                    }
                }));
            }
        },
        "columns" : [
            {
                "data" : "id",
                "className": "text-center",
                orderable: true,
            },
            {
                "data" : "firstName",
                "className" : "text-center",
                orderable: true,
            },
            {
                "data": "lastName",
                "className": "text-center",
                orderable: false,
            },
            {
                "data": "printableName",
                "className": "text-center",
                orderable: false
            },
            {
                "data": "phoneNumber",
                "className": "text-center",
                orderable: false
            },
            {
                "data": "openingBalance",
                "className": "text-center",
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
            url : "datatables/localisation"
        }
    });
    $('#dataTable tbody').on('click', 'tr', function () {
        $('.table-info').removeClass('table-info');
        $(this).toggleClass('table-info');
    });
})