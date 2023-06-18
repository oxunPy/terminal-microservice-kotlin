$(document).ready(function () {

    var datatable = $("#dataTable").DataTable({
        "ajax" : {
            "url" : "/dealers",
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
                        "name": $('#filterArea [name="filterName"]').val()
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
                "data" : "name",
                "className" : "text-center",
                orderable: true,
            },
            {
                "data" : "dealerCode",
                "className": "text-center",
                orderable: true,
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
    $('#filterArea [name="status"]').val(null);
    $('#filterArea [name="filterName"]').val('');

    doFilter();
}