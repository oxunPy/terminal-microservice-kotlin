$(document).ready(function () {

    var datatable = $("#dataTable").DataTable({
        "ajax" : {
            "url" : "/units",
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
                "data" : "name",
                "className" : "text-center",
                orderable: true,
            },
            {
                "data": "symbol",
                "className": "text-center",
                orderable: false,
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

    addForm();
    editForm();
    deleteAction();
})

function openAddModal(){
    $('form#addForm')[0].reset();
    $('#addModal').modal('show');
}

function openEditModal(){
    let selectedRow = document.querySelector('.table-info');
    if(selectedRow){

        $('form#editForm')[0].reset();
        $('#editModal').modal('show');

        let rowId = selectedRow.querySelector('td').textContent;

        $('#editForm [name="id"]').val(rowId)
        $('form#editModal [name="id"]').val(rowId);

        $.ajax({
            url: '/get/unit/' + rowId,
            type: "GET",
            contentType: "application/json; chartset=utf-8",
            dataType: 'json',
            cache: false,
            success: (data) => {
                if(data){
                    $('#editForm [name="name"]').val(data.name);
                    $('#editForm [name="symbol"]').val(data.symbol);
                }
                else{
                    $.notify(
                        {
                            "message": "Error retrieving data!!!"
                        },
                        {
                            type: "danger"
                        }
                    )
                }
            }
        })
    }
}

function openDeleteModal() {
    let selectedRow = document.querySelector('.table-info');
    if (selectedRow) {
        let rowId = selectedRow.querySelector('td').textContent;
        $('#deleteModal [name="id"]').val(rowId);
        $('#deleteModal').modal('show');
    }
}

function addForm(){
    $("form#addForm").submit(function (e) {
        $("#addAction").attr("disabled", "disabled");
        e.preventDefault();

        let formData = new FormData(this);
        $.ajax({
            url: '/save/unit',
            type: 'POST',
            contentType: false,
            data: formData,
            dataType: 'json',
            cache: false,
            processData: false,
            success: function (data) {
                $('#addModal').modal('hide');
                refreshTable();
                if (data) {
                    $.notify({
                        message: "item is edited successfully!!!"
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
    });
}

function editForm(){
    $("form#editForm").submit(function (e) {
        $("#editAction").attr("disabled", "disabled");
        e.preventDefault();

        let formData = new FormData(this);
        $.ajax({
            url: '/edit/unit',
            type: 'PUT',
            contentType: false,
            data: formData,
            dataType: 'json',
            cache: false,
            processData: false,
            success: function (data) {
                $('#editModal').modal('hide');
                refreshTable();
                if (data) {
                    $.notify({
                        message: "item is edited successfully!!!"
                    }, {
                        type: 'success'
                    });
                }
                setTimeout(() => $("#editAction").removeAttr("disabled"), 1000);
            },
            error: function (data) {
                console.log("error")
            }
        });
    });
}

function refreshTable() {
    $('#dataTable').DataTable().ajax.reload();
}

function deleteAction() {

    console.log("deleting unit!!!!")
    $('#deleteAction').click(() => {

        let rowId = $('#deleteModal [name="id"]').val();
        console.log(rowId)
        $.ajax({
            url: "/delete/unit/" + rowId,
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