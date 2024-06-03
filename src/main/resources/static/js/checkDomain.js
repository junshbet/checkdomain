let processedData = [];
function showLoader() {
    $('#preloder').css('display', 'block').find('.loader').css('display', 'block');
}

function closeLoader() {
    $('#preloder').css('display', 'none').find('.loader').css('display', 'none');
}

function getData(id, domain) {
    $("#exampleModal").modal("show");
    $("#domain").val(domain);
    $("#id_domain").val(id);
}

function showLoadding() {
    Swal.fire({
        title: 'Please wait...',
        text: 'Checking domains in progress...',
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });
}
document.getElementById('currentYear').textContent = new Date().getFullYear();
function closeLoading() {
    Swal.close();
}
function showTableLoading() {
    $('#domainTable_processing').show();
}

function hideTableLoading() {
    $('#domainTable_processing').hide();
}

$(document).ready(function () {
    let datatable = $('#domainTable').DataTable({
        ajax: {
            url: '/api/v1/getAll',
            dataSrc: '',
            beforeSend: function () {
                showTableLoading();
            },
            complete: function () {
                hideTableLoading();
                closeLoader();
            }

        },
        language: {
            searchPlaceholder: "Search...",
            search: "",
            lengthMenu: "_MENU_",
            zeroRecords: `<div class="text-center p-4"><img class="mb-3" src="https://anhtuanlc.click/assets/cms/svg/illustrations/sorry.svg" alt="Image Description" style="width: 7rem;"><p class="mb-0">No matching records found</p></div>`,
            paginate: {
                first: "First",
                last: "Last",
                next: "Next",
                previous: "Previous"
            }
        },
        columns: [
            {
                data: null, render: function (data, type, row) {
                    return `<input class="form-check-input row-checkbox" type="checkbox" value="${row.id}">`;
                }, orderable: false
            },
            {data: 'domain'},
            {
                data: 'status.status',
                defaultContent: 'N/A',
                render: function (data, type, row) {
                    if (!data) {
                        return 'N/A';
                    } else {
                        let statusClass = '';
                        if (Number(data) < 200) {
                            statusClass = 'bg-info text-dark';
                        } else if (Number(data) >= 200 && Number(data) < 300) {
                            statusClass = 'bg-success';
                        } else if (Number(data) >= 300 && Number(data) < 400) {
                            statusClass = 'bg-secondary';
                        } else if (Number(data) >= 400 && Number(data) < 500) {
                            statusClass = 'bg-warning text-dark';
                        } else {
                            statusClass = 'bg-danger';
                        }
                        return `<span class="badge rounded-pill ${statusClass}">${data}</span>`;
                    }
                }
            }
            ,
            {data: 'status.message', defaultContent: 'N/A'},
            {
                data: 'checkTime', render: function (data, type, row) {
                    return data ? new Date(data).toLocaleString() : 'N/A';
                }
            },
            {
                data: null, render: function (data, type, row) {
                    return `
                    <button type="button" class="btn btn-primary" onclick="getData('${row.id}','${row.domain}')" >Edit</button>
                    <button type="button" class="btn btn-danger btn_del" data-id="${row.id}">Del</button>
                `;
                }, orderable: false
            }
        ],
        columnDefs: [
            {orderable: false, targets: [0, 5]},
            {className: "text-center", targets: [2, 5]}
        ]
    });

    $('#domainTable tbody').on('click', 'td:nth-child(2)', function () {
        let data = datatable.row($(this).closest('tr')).data();
        console.log(data);
        let url = data.domain;
        if (url.startsWith("https")) {
            $('#offcanvas-type').val("HTTPS")
        } else {
            $('#offcanvas-type').val("HTTP")
        }
        $("#offcanvas-check-status").val(data.status === null ? "N/A" : data.status.status)
        $("#offcanvas-check-mesage").val(data.status === null ? "N/A" : data.status.message)
        $('#offcanvas-check-time').val(data.checkTime === null ? "N/A" : new Date(data.checkTime).toLocaleString());
        url = url.replace(/^https?:\/\//, '');
        $('#offcanvas-domain-name').text(url);
        $('#offcanvas-domain').val(data.domain);
        let offcanvasElement = $('#offcanvasRight');
        let offcanvasInstance = new bootstrap.Offcanvas(offcanvasElement[0]);
        let trs = '';
        if (data.checkList.length === 0) {
            trs += `<tr>
                        <td colspan="3"><div class="alert alert-info text-center">No matching records found</div></td>
                    </tr>`;
        } else {
            data.checkList.forEach(check => {
                let st = '';
                if (!check) {
                    st = 'N/A';
                } else {
                    let statusClass = '';
                    let status = Number(check.status.status);
                    if (Number(status) < 200) {
                        statusClass = 'bg-info text-dark';
                    } else if (Number(status) >= 200 && Number(status) < 300) {
                        statusClass = 'bg-success';
                    } else if (Number(status) >= 300 && Number(status) < 400) {
                        statusClass = 'bg-secondary';
                    } else if (Number(status) >= 400 && Number(status) < 500) {
                        statusClass = 'bg-warning text-dark';
                    } else {
                        statusClass = 'bg-danger';
                    }
                    st = `<span class="badge rounded-pill ${statusClass}">${status}</span>`;
                }

                trs += ` <tr>
                    <td>${check.name}</td>
                    <td>${st}</td>
                    <td>${check.status.message}</td>
                </tr>`;
            })
        }
        $("#showStaus").html(trs);
        offcanvasInstance.show();
        getIpByDomain(url).then((response) => {
            if (response !== null) {
                $('#offcanvas-ip').val(response.join(' | '))
            } else {
                $('#offcanvas-ip').val('No IP address found for this domain.');
            }
        })

    });

    $("#btn_save_domain").on("click", function () {
        let domain = $("#domain").val();
        let id = $("#id_domain").val();
        if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
            domain = "https://" + domain;
        }
        let domainRegex = /^(http(s)?:\/\/)([\w-]+\.)*[\w-]+(\.[a-zA-Z]{2,})+$/;
        let ipRegex = /^(http(s)?:\/\/)(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}|([\w-]+\.)*[\w-]+(\.[a-zA-Z]{2,})+)$/;
        if (domainRegex.test(domain) || ipRegex.test(domain)) {
            $.ajax({
                url: "api/v1/saveDomain",
                type: "POST",
                data: {
                    id: id,
                    domain: domain
                }, success: function (response) {
                    $("#exampleModal").modal("hide")
                    let isExistingRow = false;
                    datatable.rows().every(function () {
                        let rowData = this.data();
                        if (rowData.id === response.id) {
                            this.data(response).draw();
                            isExistingRow = true;
                            return false;
                        }
                    });
                    if (!isExistingRow) {
                        datatable.row.add(response).draw();
                    }
                    Toast("success", "success");
                }, error: function (e) {
                    if (e.getResponseHeader("status") == "unique") {
                        Toast("Domain already exists.", "error");
                    }
                    console.log(e);
                }
            });
        } else {
            Toast("Invalid domain name !", "error");
        }
    });


    $("#btn-check").on("click", function () {
        let checkedValues = [];
        $('#domainTable tbody input[type="checkbox"]:checked').each(function () {
            checkedValues.push($(this).val());
        });

        showLoadding();

        $.ajax({
            url: "/api/v1/checkAll",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({arrDomain: checkedValues}),
            success: function () {
                datatable.ajax.reload();
                closeLoading();
                Toast("Domains checked successfully!", "success");
            },
            error: function (e) {
                closeLoading();
                datatable.ajax.reload();
                console.log(e);
            }
        });
    });
    $("#btn-delete-all").on("click", function () {
        let checkedValues = [];
        $('#domainTable tbody input[type="checkbox"]:checked').each(function () {
            checkedValues.push($(this).val());
        });
        ConfirmSweet("Are you sure?","Once deleted, it cannot be restored !","Cancel","Yes").then((response) => {
            if (response) {
                showLoadding();
                $.ajax({
                    url: "/api/v1/deleteAll",
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify({arrDomain: checkedValues}),
                    success: function () {
                        datatable.ajax.reload();
                        closeLoading();
                        Toast("Domains delete successfully!", "success");
                    },
                    error: function (e) {
                        closeLoading();
                        Toast("An error occurred while checking domains.", "error");
                        console.log(e);
                    }
                });
            }
        })
    });

    $("#inputGroupFile01").on("change", function (event) {
        ConfirmSweet("Are you sure?", "You won't be able to revert this!", "Cancel", "Yes").then((res) => {
            if (res) {
                const file = event.target.files[0];
                const reader = new FileReader();
                reader.onload = function (e) {
                    const data = new Uint8Array(e.target.result);
                    const workbook = XLSX.read(data, {type: 'array'});
                    const firstSheetName = workbook.SheetNames[0];
                    const worksheet = workbook.Sheets[firstSheetName];
                    let processedData = processData(worksheet);
                    let successCount = 0;
                    let errorCount = 0;


                    let ajaxPromises = processedData.map(domain => {
                        return new Promise((resolve, reject) => {
                            let domainRegex = /^(http(s)?:\/\/)([\w-]+\.)*[\w-]+(\.[a-zA-Z]{2,})+$/;
                            let ipRegex = /^(http(s)?:\/\/)(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}|([\w-]+\.)*[\w-]+(\.[a-zA-Z]{2,})+)$/;
                            if (domainRegex.test(domain) || ipRegex.test(domain)) {
                                $.ajax({
                                    url: "api/v1/saveDomain",
                                    type: "POST",
                                    data: {
                                        id: "",
                                        domain: domain,
                                    },
                                    success: function (response) {
                                        datatable.row.add(response).draw();
                                        successCount++;
                                        resolve();
                                    },
                                    error: function () {
                                        errorCount++;
                                        resolve();
                                    }
                                });
                            } else {
                                errorCount++;
                                resolve();
                            }
                        });
                    });

                    Promise.all(ajaxPromises).then(() => {
                        if (successCount < 1) {
                            Toast(errorCount + " domain(s) failed to add!", "error");
                        } else if (errorCount < 1) {
                            Toast(successCount + " domain(s) added successfully!", "success");
                        } else {
                            Toast(successCount + " added successfully and " + errorCount + " failed to add", "warning");
                        }
                    });

                };
                reader.readAsArrayBuffer(file);
            }
            $("#inputGroupFile01").val('');
        });
    });

    function processData(worksheet) {
        const range = XLSX.utils.decode_range(worksheet['!ref']);
        let startRow = 3;
        let dataImport = [];
        for (let row = startRow; row <= range.e.r; row++) {
            const cellC = worksheet[XLSX.utils.encode_cell({r: row, c: 2})];
            const cellD = worksheet[XLSX.utils.encode_cell({r: row, c: 3})];

            if (!cellC || !cellD) {
                break;
            }

            const valueC = cellC.v;
            const valueD = cellD.v;

            dataImport.push(valueC + valueD);
        }
        return dataImport;
    }


    $("#btn-clear").on("click", function () {
        ConfirmSweet("Are you sure?", "You won't be able to revert this!", "Cancel", "Yes").then((res) => {
            if (res) {
                showLoadding();
                $.ajax({
                    url: "api/v1/clearData",
                    type: "GET",
                    success: function () {
                        closeLoading()
                        datatable.ajax.reload();
                        Toast("Clear successfully !", "success");
                    }, error: function (e) {
                        console.log(e);
                        closeLoading()
                        Toast("Clear error !", "error")
                    }
                })
            }
        })
    })

    $(document).on("click", ".btn_del", function () {
        let id = $(this).data("id");
        let tr = $(this).closest('tr');
        ConfirmSweet("Are you sure?", "You won't be able to revert this!", "Cancel", "Yes").then((res) => {
            if (res) {
                $.ajax({
                    url: "/api/v1/deleteDomain",
                    type: "DELETE",
                    data: {
                        id: id,
                    }, success: function () {
                        Toast("success", "success");
                        // Xóa hàng khỏi DataTable
                        datatable.row(tr).remove().draw();
                    }, error: function (e) {
                        console.log(e);
                        Toast("error", "error");
                    }
                });
            }
        });
    });
    $('#checkAll').on('click', function () {
        let rows = datatable.rows({'search': 'applied'}).nodes();
        $('input[type="checkbox"]', rows).prop('checked', this.checked);
    });
    $('#domainTable').on('xhr.dt', function () {
        $("#checkAll").prop("checked", false);
        $('#btn-check').hide();
        $('#btn-delete-all').hide();
    });
    $('#domainTable tbody').on('change', 'input[type="checkbox"]', function () {
        if (!this.checked) {
            let el = $('#checkAll').get(0);
            if (el && el.checked && ('indeterminate' in el)) {
                el.indeterminate = true;
            }
        }
    });

    function toggleCheckButton() {
        let checkedCheckboxes = $('#domainTable tbody input[type="checkbox"]:checked');
        if (checkedCheckboxes.length > 0) {
            $('#btn-check').show(); // Hiển thị nút khi có ít nhất một checkbox được chọn
            $('#btn-delete-all').show().text("Xóa (" + checkedCheckboxes.length + ")");
        } else {
            $('#btn-check').hide(); // Ẩn nút khi không có checkbox nào được chọn
            $('#btn-delete-all').hide();
        }
    }

    $('#domainTable tbody').on('change', 'input[type="checkbox"]', function () {
        toggleCheckButton();
        // Cập nhật trạng thái của checkbox checkAll
        let allCheckboxes = $('#domainTable tbody input[type="checkbox"]');
        let checkedCheckboxes = $('#domainTable tbody input[type="checkbox"]:checked');
        $('#checkAll').prop('checked', checkedCheckboxes.length === allCheckboxes.length);
        $('#checkAll').prop('indeterminate', checkedCheckboxes.length > 0 && checkedCheckboxes.length < allCheckboxes.length);
    });
    $('#btn-check').hide();
    $('#btn-delete-all').hide();
    $('#checkAll').on('click', function () {
        let rows = datatable.rows({'search': 'applied'}).nodes();
        let allCheckboxes = $('input[type="checkbox"]', rows);
        allCheckboxes.prop('checked', this.checked);
        toggleCheckButton(); // Cập nhật trạng thái của nút khi có thay đổi
    });

    $("#btn-add-new").on("click", function () {
        $("#id_domain").val("");
        $("#domain").val("");
        $("#exampleModal").modal('show');
    });

    function ConfirmSweet(title, message, btnCancel, btnConfirm) {
        return new Promise((resolve, reject) => {
            Swal.fire({
                title: title,
                text: message,
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                confirmButtonText: btnConfirm,
                cancelButtonText: btnCancel
            }).then((result) => {
                if (result.isConfirmed) {
                    resolve(true);
                } else {
                    resolve(false);
                }
            });
        });
    }

    function getIpByDomain(domain) {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: `https://dns.google.com/resolve?name=${domain}`,
                method: 'GET',
                dataType: 'json',
                success: function (data) {
                    if (data.Answer) {
                        const ipAddresses = data.Answer
                            .filter(answer => answer.type === 1)
                            .map(answer => answer.data);
                        resolve(ipAddresses)
                    } else {
                        resolve(null);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.error('Error:', textStatus, errorThrown);
                    resolve(null);
                }
            });
        })
    }

    function Toast(message, status) {
        const Toast = Swal.mixin({
            toast: true,
            position: "top-end",
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.onmouseenter = Swal.stopTimer;
                toast.onmouseleave = Swal.resumeTimer;
            }
        });
        Toast.fire({
            icon: status,
            title: message
        });
    }
});