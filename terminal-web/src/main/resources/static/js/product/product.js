$(document).ready(function () {

    var datatable = $("#dataTable").DataTable({
        initComplete: function () {
            $('.dataTables_filter input[type="search"]').css({'width': '400px', 'display': 'inline-block'});
        },
        "ajax": {
            "url": "/products",
            "type": "POST",
            "content-type": "application/json; charset=utf-8",
            "dataType": 'json',
            "headers": {
                'Content-type': 'application/json',
                'Accept': 'application/json'
            },
            "data": function (json) {
                return JSON.stringify($.extend({}, json, {
                    "filter": {}
                }));
            }
        },
        "columns": [
            {
                "data": "id",
                orderable: true,
            },
            {
                "data": "productName",
                orderable: true,
            },
            {
                "data": "barcode",
                orderable: false,
            },
            {
                "data": "groupName",
                orderable: false
            },
            {
                "data": "rate",
                "className": "text-right",
                orderable: false,
                render: function (data, type, row) {
                    return row.currency === 'UZS' ? formatMoneyRateUZS(data) : formatMoneyRateUSD(data);
                }
            },
            {
                "data": "originalRate",
                "className": "text-right",
                orderable: false,
                render: function (data, type, row) {
                    return row.currency === 'UZS' ? formatMoneyRateUZS(data) : formatMoneyRateUSD(data);
                }
            },
            {
                "data": "currency",
                "className": "text-center",
                orderable: false
            }
        ],
        "serverSide": true,
        "processing": true,
        "pageLength": 10,
        "ordering": "true",
        "order": [],
        "info": true,
        "searching": true,
        "language": {
            url: "/datatables/localisation"
        }
    });

    $('#dataTable tbody').on('click', 'tr', function () {
        $('.table-info').removeClass('table-info');
        $(this).toggleClass('table-info');
    });

    flatpickrDate($('#price [name="ppDate"]'))
    flatpickrDate($('#price [name="spDate"]'))


    uploadFileForm();
    uploadExcel();

//    #crud products
    saveAndEditProductAction();
    deleteProductAction();

//    upload product Images
    uploadProductImages();

//    open equality case
    openUnitEqualityInCase();

//    init pp AND sp
    initPPAndSPTable(null);

//     modal close
    modalCloseClearAll();
})


function initPPAndSPTable(selectedRowId) {           // PP-purchase-price and SP-selling-price
    let datatableObj = {
        "serverSide": false,
        "processing": false,
        "ordering": false,
        "paging": false,
        "order": [],
        "info": false,
        "searching": false,
        "language": {
            url: "/datatables/localisation"
        },
    }

    let ppDataObj = datatableObj;
    let spDataObj = datatableObj;

    if (selectedRowId) {
        datatableObj["serverSide"] = true;
        datatableObj["processing"] = true;
        datatableObj["ordering"] = true;
        datatableObj["order"] = [];

        datatableObj["ajax"] = {
            "url": "/product/rate/" + selectedRowId,
            "type": "POST",
            "content-type": "application/json; charset=utf-8",
            "dataType": 'json',
            "headers": {
                'Content-type': 'application/json',
                'Accept': 'application/json'
            },
        }
        datatableObj["columns"] = [{
            "data": "id",
            className: "text-center",
            orderable: true,
        },
            {
                "data": "date",
                orderable: true,
                className: "text-center",
                render: function (data, type, row) {
                    console.log(data)
                    return formatDate(new Date(data));
                }
            },
            {
                "data": "standardCost",
                orderable: false,
                className: "text-right",
                render: function (data, type, row) {
                    console.log(data)
                    return row.currency === 'USD' ? formatMoneyRateUSD(data) : formatMoneyRateUZS(data);
                }
            }]

        ppDataObj = JSON.parse(JSON.stringify(datatableObj))
        spDataObj = JSON.parse(JSON.stringify(datatableObj))

        ppDataObj['ajax']['data'] = function (json) {
            return JSON.stringify($.extend({}, json, {
                "filter": {"buy": true}
            }));
        }
        spDataObj['ajax']['data'] = function (json) {
            return JSON.stringify($.extend({}, json, {
                "filter": {"sell": true}
            }));
        }
    }

    $('#ppBuy').DataTable().destroy();
    $('#ppSell').DataTable().destroy();

    $('#ppBuy').DataTable(ppDataObj);
    $('#ppSell').DataTable(spDataObj);
}

function openUploadModal() {
    let excelModal = $('#exampleModalCenter');
    excelModal.modal('show');

    excelModal.on('hidden.bs.modal', function (e) {
        let filedata = ` <p>Files Supported: CSV</p>
                         <input type="file" hidden accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" id="fileID" style="display:none;">
                         <button class="btn btn-primary" id="btnForFile">Choose File</button>
                       `;
        document.querySelector("#dropBox").innerHTML = filedata;
        uploadFileForm();
    })
}

function uploadExcel() {

    document.querySelector("#fileID").addEventListener('change', function (e) {
        if (e.target.files[0]) $("#btnUploadExcel").removeAttr("disabled");
    })

    $('#btnUploadExcel').click(function (e) {
        $("#btnUploadExcel").attr("disabled", "disabled");
        let exForm = document.querySelector('#formExcel');
        if (exForm != null && exForm.querySelector("#fileID").files.length > 0) {
            let formData = new FormData(exForm);
            $.ajax({
                url: '/product/upload/excel',
                method: 'POST',
                dataType: 'json',
                contentType: false,
                data: formData,
                cache: false,
                processData: false,
                success: function (data) {
                    console.log(data);
                    refreshTable();
                    setTimeout(() => $("#btnUploadExcel").removeAttr("disabled"), 500);
                    $('#exampleModalCenter').modal('hide');
                },
                error: function () {
                    $.notify({
                            message: "Error uploading excel!!!"
                        },
                        {
                            type: "danger"
                        })

                }
            });
        }
    })
}

function refreshTable() {
    $('#dataTable').DataTable().ajax.reload();
}

function exportExcel() {
    startDownload();
}

function modalCloseClearAll() {
    $('#addAndEditModal').on('hidden.bs.modal', function () {
        //put your default event here
        $('#productName [name = "name"]').val(null)
        $('#productBarcode [name = "barcode"]').val(null)

        //clear unit and group
        $('#productUnit0 [name="unitId"]').val(null).trigger('change');
        $('#productUnit1 [name="unitId"]').val(null).trigger('change');
        $('#productGroup [name="groupId"]').val(null).trigger('change');

        //hide unitEquality
        $('#unitEquality').hide();

        // destroy product-price datatable
        $('#ppBuy').DataTable().clear();
        $('#ppSell').DataTable().clear();
        initPPAndSPTable(null);

        //clear product-images
        $('#productImages').empty();

        //clear selected-product-id
        $('#fProductInfo [name="id"]').val(null);

        //clear product-price properties
        $('#price [name="ppPriceVal"]').val(null);
        $('#price [name="spPriceVal"]').val(null);
        flatpickr($('#price [name="ppDate"]'), {
            altInput: true,
            dateFormat: 'd.m.Y',
            altFormat: "d.m.Y",
            allowInput: false
        }).clear();
        flatpickr($('#price [name="spDate"]'), {
            altInput: true,
            dateFormat: 'd.m.Y',
            altFormat: "d.m.Y",
            allowInput: false
        }).clear();
    })
}

function startDownload() {
    let url = window.location.href;
    if (url.search("lang") > -1) url = url.slice(0, url.search("lang") - 1)

    url = url.concat('/download').concat('?search=').concat($('#dataTable').DataTable().search());
    window.open(url, 'Download');
}

function uploadFileForm() {
    const dropArea = document.querySelector("#dropBox"),
        button = dropArea.querySelector("#btnForFile"),
        input = dropArea.querySelector("#fileID");

    button.onclick = () => {
        input.click();
    };

    let files;

    input.addEventListener("change", function (e) {
        let fileName = e.target.files[0].name;
        files = e.target.files;
        let filedata = `<form action="" method="post" enctype="multipart/form-data" accept-charset="UTF-8" id="formExcel">
                            <h4>${fileName}</h4>
                            <input name="fileName" id="fileName" type="text" hidden/>
                            <input name = "file" type="file" hidden accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" id="fileID" style="display:none;">
                        </form>`;
        dropArea.innerHTML = filedata;
        $('#formExcel[name="fileName"]').value = fileName;
        dropArea.querySelector("#fileName").value = fileName;
        dropArea.querySelector('#fileID').files = files;
    });
}

function uploadProductImages() {
    $('#file-5').on('change', function () {
        let fileList = this.files;

        for (let i = 0; i < fileList.length; i++) {
            // get a blob
            $('#productImages').append('<img height="60" width="100" style="padding-right: 10px" src="' + URL.createObjectURL(fileList[i]) + '" />');
            j = i + 1
            if (j % 4 == 0) {
                $('#productImages').append('<br>')
            }
        }
    })
}

function modalAddOrEditShow() {
    $('#addAndEditModal').modal('show');
    DataModule.select2Unit($('#productUnit0 [name="unitId"]'))
    DataModule.select2Unit($('#productUnit1 [name="unitId"]'))
    DataModule.select2ProductGroup($('#productGroup [name="groupId"]'))

    DataModule.select2Currency($('#price [name="ppCurrency"]'))
    DataModule.select2Currency($('#price [name="spCurrency"]'))

}


function openAddModal() {
    modalAddOrEditShow();
}

function openEditModal() {
    let selectedRow = document.querySelector('.table-info');
    if (selectedRow) {
        let rowId = selectedRow.querySelector('td').textContent;

        $.ajax({
            url: "/product/get/" + rowId,
            type: "GET",
            contentType: "application/json; charset=utf-8",
            dataType: 'json',
            cache: false,
            success: (data) => {
                if (data) {
                    // console.log(data);
                    modalAddOrEditShow();
                    initPPAndSPTable(rowId);
                    $('#fProductInfo [name="id"]').val(rowId)
                    $('#fProductInfo [name="name"]').val(data.productName);
                    $('#fProductInfo [name="barcode"]').val(data.barcode);
                    $('#productUnit0 [name="unitId"]').val(data.baseUnitId0);
                    $('#productUnit1 [name="unitId"]').val(data.baseUnitId1);
                    $('#productGroup [name="groupId"]').val(data.productGroupId);
                } else {
                    $.notify({
                        message: "Error retrieving data!!!"
                    }, {
                        type: 'danger'
                    });
                }
            }
        });
    }
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

function openUnitEqualityInCase() {
    let baseUnit0 = $('#productUnit0 [name="unitId"]');
    let baseUnit1 = $('#productUnit1 [name="unitId"]')
    baseUnit0.on('change', function (e) {
        if (baseUnit0.val() && baseUnit1.val()) {
            showEquality();
        } else $('#unitEquality').hide();
    })
    baseUnit1.on('change', function (e) {
        if (baseUnit0.val() && baseUnit1.val()) {
            showEquality();
        } else $('#unitEquality').hide();
    })
}

function showEquality() {
    // show unit-equality
    $('#unitEquality [name = "baseUnit0Text"]').text($('#productUnit0 [name="unitId"]').select2('data')[0].text)
    $('#unitEquality [name = "baseUnit1Text"]').text($('#productUnit1 [name="unitId"]').select2('data')[0].text)
    $('#unitEquality').show()
}

function openDeleteModal() {
    console.log(434343436566755656565656565)
    let selectedRow = document.querySelector('.table-info');
    if (selectedRow) {
        let rowId = selectedRow.querySelector('td').textContent;
        $('#deleteModal [name="id"]').val(rowId);
        $('#deleteModal').modal('show');
    }
}

function saveAndEditProductAction() {
    $('#saveOrEditAction').on('click', function () {

        let product = {
            'productName': $('#fProductInfo [name="name"]').val(),
            'barcode': $('#fProductInfo [name="barcode"]').val(),
            'productGroupId': $('#fProductInfo [name="groupId"]').val(),
            'baseUnitId0': $('#productUnit0 [name="unitId"]').val(),
            'baseUnitId1': $('#productUnit1 [name="unitId"]').val(),
            'baseUnitVal0': $('#unitEquality [name="baseUnit0Val"]').val(),
            'baseUnitVal1': $('#unitEquality [name="baseUnit1Val"]').val(),
            'buyProductPrice': {
                date: $('#price [name="ppDate"]').val(),
                standardCost: $('#price [name="ppPriceVal"]').val(),
                currencyId: $('#price [name="ppCurrency"]').val()
            },
            'sellProductPrice': {
                date: $('#price [name="spDate"]').val(),
                standardCost: $('#price [name="spPriceVal"]').val(),
                currencyId: $('#price [name="spCurrency"]').val()
            },
        }

        // if productId is not null edit it
        let productId = $('#fProductInfo [name="id"]').val();
        if (productId) {
            product['id'] = productId;
        }
        //send product-save-form to the backend
        let fd = new FormData();
        for (let i = 0; i < $("#file-5").prop("files").length; i++) {
            fd.append("images", $("#file-5").prop("files")[i]);
        }
        fd.append("body", new Blob([JSON.stringify(product)], {type: "application/json"}));

        $.ajax({
            url: '/saveOrEdit/product',
            type: 'POST',
            contentType: false,
            data: fd,
            dataType: 'json',
            cache: false,
            processData: false,
            success: function (data) {
                $('#addModal').modal('hide');
                refreshTable();
                if (data) {
                    $.notify({
                        message: "item is saved successfully!!!"
                    }, {
                        type: 'success'
                    });
                }
                setTimeout(() => $("#addAction").removeAttr("disabled"), 1000);
            },
            error: function (data) {
                console.log("error")
            }
        });

    })
}

function deleteProductAction() {
    console.log("deleting action!!!")
    $('#deleteAction').click(() => {
        let rowId = $('#deleteModal [name="id"]').val();
        $.ajax({
            url: "/product/delete/" + rowId,
            type: "DELETE",
            contentType: "application/json; chartset=utf-8",
            dataType: 'json',
            cache: false,
            success: function (data) {
                console.log(data);
                $('#deleteModal').modal('hide');
                refreshTable();
                if (data) {
                    $.notify({
                        message: "item is removed successfully!!!"
                    }, {
                        type: 'success'
                    });
                }
            }
        })
    })
}