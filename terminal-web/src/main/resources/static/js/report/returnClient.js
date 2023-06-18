$(document).ready(function () {
    let datatable = $("#dataTable").DataTable({
        "ajax" : {
            "url" : "report/returnClient",
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
                        "fromDate" : $('#filterArea [name="fromDate"]').val(),
                        "toDate" : $('#filterArea [name="toDate"]').val(),
                        "dealerClientId": $('#filterArea [name="dealerClient"]').val(),
                        "warehouseId": $('#filterArea [name="warehouseId"]').val()
                    }
                }));
            }
        },
        "columns" : [
            {
                "data" : "id",
                "className": "text-center",
                orderable: true
            },
            {
                "data" : "date",
                orderable: false,
                className: "text-center",
                render: function(data){
                    return formatDate(new Date(data));
                }
            },
            {
                "data" : "printableName",
                "className": "text-right",
                orderable: false
            },
            {
                "data": "warehouse",
                orderable: false,
                className: "text-center"
            },
            {
                "data" : "info",
                "className": "text-right",
                orderable: false
            },
            {
                "data" : "countOfItems",
                "className": "text-center",
                orderable: true,
            },
            {
                "data" : "total",
                "className": "text-right",
                orderable: false,
                render: function(data){
                    return formatMoneyRateUZS(data);
                }
            },
            {
                "data": "action",
                "className": "text-center",
                "render": function (data, type, row) {
                    <!--    Invoice Items Modal-->
                    return '<div class="btn-group">' +
                        '<button onclick="openViewModal(' + row.id + ')" type="button" class="btn btn-sm btn-outline-warning" data-toggle="tooltip" title="' + _('action.view') + '"><i class="fa fa-eye"></i></button>' +
                        '</div>';
                },
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


function doFilter() {
    $('#dataTable').DataTable().ajax.reload();
}

function clearFilter(){
    let fpFromDate = flatpickr('#filterArea [name="fromDate"]', {});
    let fpToDate = flatpickr('#filterArea [name="toDate"]', {});

    let date = new Date();
    fpFromDate.setDate(new Date(date.getFullYear(), date.getMonth(), 1))
    fpToDate.setDate(date)

    doFilter();
}

function initFilter(){
    $("#filterArea").css("display", "none");
    $("#filterBtn").click(function(){
        $("#filterArea").toggle();
    });

    flatpickrDate('#filterArea [name="fromDate"]');
    flatpickrDate('#filterArea [name="toDate"]');

    DataModule.select2WareHouse($('#filterArea [name="warehouseId"]'))
    DataModule.select2DealerClient($('#filterArea [name="dealerClient"]'), "/DEBITOR")

    let fpFromDate = flatpickr('#filterArea [name="fromDate"]', {});
    let fpToDate = flatpickr('#filterArea [name="toDate"]', {});

    let date = new Date();
    fpFromDate.setDate(new Date(date.getFullYear(), date.getMonth(), 1))
    fpToDate.setDate(date)
}

function openViewModal(rowId){
    loadInvoiceItemDataTable($('#invoiceItemDatatable'), rowId)
    $('#invoiceItemModalScrollable').modal('show')
}

function formatDate(date){
    const today = new Date();
    const yyyy = today.getFullYear();
    let mm = today.getMonth() + 1; // Months start at 0!
    let dd = today.getDate();

    if (dd < 10) dd = '0' + dd;
    if (mm < 10) mm = '0' + mm;

    return dd + '/' + mm + '/' + yyyy;
}

function flatpickrRange(space) {
    flatpickr(space, {
        mode: "range",
        altInput: true,
        dateFormat: 'Y-m-d',
        altFormat: "Y-m-d",
        allowInput: false
    });
}

function flatpickrDate(space, defaultDate) {
    flatpickr(space, {
        altInput: true,
        dateFormat: 'd.m.Y',
        altFormat: "d.m.Y",
        allowInput: false,
        defaultDate: defaultDate
    });
}

function formatMoneyRateUZS(rate){
    const formatter = new Intl.NumberFormat('uz-Uz',{
        style: 'currency',
        currency: 'UZS',
        minimumFractionDigits: 1,
        maximumFractionDigits: 3
    });
    return formatter.format(rate);
}
