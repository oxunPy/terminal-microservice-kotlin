$(document).ready(function () {

    var datatable = $("#dataTable").DataTable({
        "ajax" : {
            "url" : "/companies",
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
                    }
                }));
            }
        },
        "columns" : [
            {
                "data" : "id",
                "className" : "text-center",
                orderable: false,
            },
            {
                "data" : "logoImgUrl",
                "className" : "text-center",
                orderable: false,
                "render": function(data, type, row){
                    return '<img src=' + data + ' style="width:50px;"/> '
                }
            },
            {
                "data" : "faviconUrl",
                "className": "text-center",
                orderable: false,
                "render": function(data, type, row){
                    return '<img src=' + data + ' style="width:50px" />'
                }
            },
            {
                "data" : "companyName",
                orderable: false
            },
            {
                "data" : "manager",
                orderable: false,
            },
            {
                "data" : "director",
                orderable: false,
            },
            {
                "data" : "isMain",
                "className": "text-center",
                orderable: false,
                "render" : (data, type, row) => {
                    return row.isMain ? '<input type="checkbox" disabled checked/>' : '<input type="checkbox" disabled unchecked/>';
                }
            },
            {
                "data" : "motto",
                orderable: false,
            },
            {
                "data" : "phoneNumber",
                orderable: false
            },
            {
                "data" : "telegramContact",
                orderable: false
            },
            {
                "data" : "email",
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

    initPage();
    addForm();
    editForm();
    deleteAction();
})

/* Filter methods */
function initPage() {

}

function openAddModal() {
    $('#addLogoInput').change((event) => {
        $('#addLogoImg').attr('src', URL.createObjectURL(event.target.files[0]));
    });

    $('#addFaviconInput').change((event) => {
        $('#addFaviconImg').attr('src', URL.createObjectURL(event.target.files[0]));
    });

    $('form#addForm')[0].reset();
    $('#addModal').modal('show');
}

function refreshTable() {
    $('#dataTable').DataTable().ajax.reload();
}

/* Form & Action */
function addForm() {
    $("form#addForm").submit(function (e) {
        $("#addAction").attr("disabled", "disabled");
        e.preventDefault();

        var formData = new FormData(this);
        $.ajax({
            url: '/company/save',
            type: 'POST',
            contentType: false,
            data: formData,
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

function openEditModal() {
    let selectedRow = document.querySelector('.table-info');
    if (selectedRow) {
        let rowId = selectedRow.querySelector('td').textContent;
        $('#editLogoInput').change((event) => {
            $('#editLogoImg').attr('src', URL.createObjectURL(event.target.files[0]));
        })

        $('#editFaviconInput').change((event) => {
            $('#editFaviconImg').attr('src', URL.createObjectURL(event.target.files[0]));
        })

        $('#editForm [name="id"]').val(rowId)

        $.ajax({
            url: "/company/get/" + rowId,
            type: "GET",
            contentType: "application/json; chartset=utf-8",
            dataType: 'json',
            cache: false,
            success: function (data) {
                if (data != null) {
                    $('#editModal [name="companyName"]').val(data.companyName);
                    $('#editModal [name="director"]').val(data.director);
                    $('#editModal [name="manager"]').val(data.manager);
                    $('#editFaviconImg').attr('src', data.faviconUrl);
                    $('#editLogoImg').attr('src', data.logoImgUrl);
                    $('#editModal [name="telegramContact"]').val(data.telegramContact)
                    $('#editModal [name="phoneNumber"]').val(data.phoneNumber)
                    $('#editModal [name="email"]').val(data.email)
                    $('#editModal [name="isMain"]').prop('checked', data.isMain)
                    $('#editModal [name="motto"]').val(data.motto)
                    $('#editModal').modal('show');
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

function editForm(){
    $('form#editForm').submit((e) => {
        $('#editAction').attr('disabled', 'disabled');
        e.preventDefault();

        console.log(this);
        // console.log(new FormData(this))

        var formData = new FormData(document.getElementById("editForm"));

        console.log(formData)
        $.ajax({
            url: '/company/edit',
            method: 'POST',
            contentType: false,
            data: formData,
            cache: false,
            processData: false,
            success: (data) => {
                console.log(data);
                $('#editModal').modal('hide')
                refreshTable();
                setTimeout(() => $('#editAction').removeAttr("disabled"), 1000);
            },
            error: (data) => {
                console.log("error");
            }
        })
    })
}

function deleteAction(){
    $('#deleteAction').click(() => {
        $('#deleteAction').attr('disabled', 'disabled')

        var rowId = $('#deleteModal [name="id"]').val();

        $.ajax({
            url: '/company/delete/' + rowId,
            type: 'DELETE',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            cache: false,
            processData: false,
            success: (data) => {
                console.log(data);
                $.notify({
                    message: "company deleted successfully!!!"
                }, {
                    type: 'success'
                });
                $('#deleteModal').modal('hide');
                $('#dataTable').DataTable().ajax.reload();
                setTimeout(() => {
                    $('#deleteAction').removeAttr('disabled');
                }, 1000);
            }
        })
    })
}

function openDeleteModal() {
    let selectedRow = document.querySelector('.table-info');
    if (selectedRow) {
        let rowId = selectedRow.querySelector('td').textContent;
        $('#deleteModal [name="id"]').val(rowId);
        $('#deleteModal').modal('show')
    }
}