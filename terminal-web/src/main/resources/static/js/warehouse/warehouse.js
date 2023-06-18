$(document).ready(function () {
    var datatable = $("#dataTable").DataTable({
        "ajax" : {
            "url" : "/warehouses",
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
                        "name" : $('#filterArea [name = "filterName"]').val(),
                    }
                }));
            }
        },
        "columns" : [
            {
                "data" : "id",
                orderable: true,
            },
            {
                "data" : "name",
                orderable: true,
            },
            {
                "data": "isDefault",
                orderable: false,
                "className" : "text-center",
                "render" : function(data, type, row){
                    if(row.isDefault){
                        return '<input type="checkbox" checked disabled/>'
                    }else {
                        return '<input type="checkbox" unchecked disabled/>'
                    }
                }

            },
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
    addForm();
    deleteAction();
    editForm();
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

function openEditModal() {
    let selectedRow = document.querySelector('.table-info');
    if (selectedRow) {
        let rowId = selectedRow.querySelector('td').textContent;
        $('form#editForm')[0].reset();
        $('#editModal').modal('show');

        $('#editForm [name="id"]').val(rowId)

        $('form#editModal [name="id"]').val(rowId);
        $.ajax({
            url: "/warehouse/get/" + rowId,
            type: "GET",
            contentType: "application/json; chartset=utf-8",
            dataType: 'json',
            cache: false,
            success: (data) => {
                if (data != null) {
                    console.log(data);
                    $('#editModal [name="name"]').val(data.name);
                    $('#editModal [name="isDefault"]').attr('checked', data.isDefault);
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

function openDeleteModal(){
    let selectedRow = document.querySelector('.table-info');
    if (selectedRow) {
        let rowId = selectedRow.querySelector('td').textContent;
        $('#deleteModal [name="id"]').val(rowId);
        $('#deleteModal').modal('show');
    }
}

function deleteAction() {
    $('#deleteAction').click(() => {
        let rowId = $('#deleteModal [name="id"]').val();
        $.ajax({
            url: "/warehouse/delete/" + rowId,
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

function editForm() {
    $("form#editForm").submit(function (e) {
        $("#editAction").attr("disabled", "disabled");
        e.preventDefault();

        let formData = new FormData(this);
        $.ajax({
            url: '/warehouse/edit',
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


function addForm() {
    $("form#addForm").submit(function (e) {
        $("#addAction").attr("disabled", "disabled");
        e.preventDefault();

        let formData = new FormData(this);
        $.ajax({
            url: '/warehouse/save',
            type: 'POST',
            contentType: false,
            data: formData,
            dataType: 'json',
            cache: false,
            processData: false,
            success: function (data) {
                console.log(data);
                $('#addModal').modal('hide');
                refreshTable();
                setTimeout(() => $("#addAction").removeAttr("disabled"), 1000);
            },
            error: function (data) {
                console.log("error")
            }
        });
    });
}

function openAddModal() {
    $('form#addForm')[0].reset();
    $('#addModal').modal('show');
}

function refreshTable() {
    $('#dataTable').DataTable().ajax.reload();
}