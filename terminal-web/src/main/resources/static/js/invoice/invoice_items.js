$(document).ready(function() {
    let invoiceId = $('#invoiceId').val();
    let datatable = $("#dataTable");
    loadInvoiceItemDataTable(datatable, invoiceId);
})


function loadInvoiceItemDataTable(table, invoiceId){
    if(table != null) table.DataTable().destroy();
    table.DataTable({
        "ajax" : {
            "url" : "/invoice/items/" + invoiceId,
            "type" : "POST",
            "content-type" : "application/json; charset=utf-8",
            "dataType" : 'json',
            "headers" : {
                'Content-type' : 'application/json',
                'Accept' : 'application/json'
            },
        },
        "columns" : [
            {
                "data" : "id",
                orderable: true
            },
            {
                "data" : "productName",
                orderable: false,
            },
            {
                "data" : "quantity",
                "className": "text-center",
                orderable: false
            },
            {
                "data" : "rate",
                "className": "text-right",
                orderable: false,
                render: function(data, type, row){
                    return (row.currency === 'UZS') ? formatMoneyRateUZS(data) : formatMoneyRateUSD(data);
                }
            },
            {
                "data" : "sellingRate",
                "className": "text-right",
                orderable: false,
                render: function(data, type, row){
                    return (row.currencyCode === 'UZS') ? formatMoneyRateUZS(data) : formatMoneyRateUSD(data);
                }
            },
            {
                "data" : "originalRate",
                "className": "text-right",
                orderable: false,
                render: function(data, type, row){
                    return (row.currencyCode === 'UZS') ? formatMoneyRateUZS(data) : formatMoneyRateUSD(data);
                }
            },
            {
                "data" : "currencyCode",
                "className" : "text-center",
                orderable: true,
            },
            {
                "data" : "total",
                "className": "text-right",
                orderable: false,
                render: function(data, type, row){
                    return (row.currencyCode === 'UZS') ? formatMoneyRateUZS(data) : formatMoneyRateUSD(data);
                }
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

    $('#invoiceItemDatatable tbody').on('click', 'tr', function () {
        $('.table-info').removeClass('table-info');
        $(this).toggleClass('table-info');
    });
}

function formatMoneyRateUSD(rate){
    const formatter = new Intl.NumberFormat('en-US',{
        style: 'currency',
        currency: 'USD',
        minimumFractionDigits: 1,
        maximumFractionDigits: 3
    })
    return formatter.format(rate);
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