const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

const PayslipReport = {

    currentPage: 0,
    pageSize: 10,
    totalPages: 0,

    init: function () {
        this.bindEvents();
        this.loadYymmDropdown();
    },

    bindEvents: function () {

        $("#btnSearch").on("click", function () {
            PayslipReport.currentPage = 0;
            PayslipReport.searchPayslips();
        });

        $("#btnClear").on("click", function () {
            PayslipReport.clearScreen();
        });

        $("#pan").on("input", function () {
            this.value = this.value.toUpperCase();
        });
    },

    viewPayslip: function (yymm, id) {

        window.open(
            BASE_URL + "/api/payslip/view/" + yymm + "/" + id,
            "_blank"
        );

    },

    downloadPayslip: function (yymm, id) {

        window.location.href =
            BASE_URL + "/api/payslip/download/" + yymm + "/" + id;

    },

    loadYymmDropdown: function () {

        $.ajax({
            url: BASE_URL + "/api/reports/yymm",
            type: "GET",

            success: function (response) {

                let dropdown = $("#yymm");
                dropdown.empty();

                dropdown.append(`<option value="">Select YYMM</option>`);

                $.each(response, function (index, value) {
                    dropdown.append(
                        `<option value="${value}">${value}</option>`
                    );
                });
            },

            error: function () {
                PayslipReport.showError("Unable to load YYMM.");
            }
        });
    },

    searchPayslips: function () {

        this.hideMessages();

        let id = $("#id").val().trim();
        let pan = $("#pan").val().trim();
        let yymm = $("#yymm").val();

        if (id === "" && pan === "" && yymm === "") {
            this.showError("Please enter DNB ID/PAN or select YYMM.");
            return;
        }

        if (id !== "" && pan !== "") {
            this.showError("Please enter either DNB ID or PAN.");
            return;
        }

        let request = {
            page: this.currentPage,
            size: this.pageSize
        };

        if (id !== "") request.id = id;
        if (pan !== "") request.pan = pan;
        if (yymm !== "") request.yymm = yymm;

        $.ajax({
            url: BASE_URL + "/api/payslip/search",
            type: "GET",
            data: request,

            success: function (response) {

                PayslipReport.populateGrid(response.content);
                PayslipReport.buildPagination(response.totalPages);
            },

            error: function (xhr) {

                let msg = "Unable to fetch payslips.";

                if (xhr.responseJSON && xhr.responseJSON.message) {
                    msg = xhr.responseJSON.message;
                }

                PayslipReport.showError(msg);
            }
        });
    },

    populateGrid: function (data) {

        let tbody = $("#payslipBody");
        tbody.empty();

        if (!data || data.length === 0) {
            tbody.append(`<tr><td colspan="6">No records found</td></tr>`);
            return;
        }

        $.each(data, function (i, item) {

            tbody.append(`
            <tr>
                <td>${item.id}</td>
                <td>${item.name}</td>
                <td>${item.category}</td>
                <td>${item.yymm}</td>
                <td>${item.month}</td>

                <td class="text-center">

                    <button class="btn btn-primary btn-sm me-2"
                        onclick="PayslipReport.viewPayslip('${item.yymm}',${item.id})">

                        <i class="bi bi-eye"></i>

                    </button>

                    <button class="btn btn-success btn-sm"
                        onclick="PayslipReport.downloadPayslip('${item.yymm}',${item.id})">

                        <i class="bi bi-download"></i>

                    </button>

                </td>

            </tr>
            `);
        });
    },

    buildPagination: function (totalPages) {

        this.totalPages = totalPages;

        $("#paginationContainer").empty();

        if (totalPages === 0) return;

        let html = `<nav><ul class="pagination justify-content-center">`;

        html += `
            <li class="page-item ${this.currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="PayslipReport.previousPage(); return false;">
                    Previous
                </a>
            </li>
        `;

        for (let i = 0; i < totalPages; i++) {
            html += `
                <li class="page-item ${i === this.currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="PayslipReport.goToPage(${i}); return false;">
                        ${i + 1}
                    </a>
                </li>
            `;
        }

        html += `
            <li class="page-item ${this.currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="PayslipReport.nextPage(); return false;">
                    Next
                </a>
            </li>
        `;

        html += `</ul></nav>`;

        $("#paginationContainer").html(html);
    },

    goToPage: function (page) {
        this.currentPage = page;
        this.searchPayslips();
    },

    previousPage: function () {
        if (this.currentPage > 0) {
            this.currentPage--;
            this.searchPayslips();
        }
    },

    nextPage: function () {
        if (this.currentPage < this.totalPages - 1) {
            this.currentPage++;
            this.searchPayslips();
        }
    },

    clearScreen: function () {

        $("#id").val("");
        $("#pan").val("");
        $("#yymm").val("");

        $("#payslipBody").empty();
        $("#paginationContainer").empty();

        this.currentPage = 0;
        this.totalPages = 0;

        this.hideMessages();
    },

    showSuccess: function (message) {

        $("#successPopup")
            .text(message)
            .show();

        $("#errorPopup").hide();

        setTimeout(function () {
            $("#successPopup").fadeOut();
        }, 3000);
    },

    showError: function (message) {

        $("#errorPopup")
            .text(message)
            .show();

        $("#successPopup").hide();

        $("html, body").animate({ scrollTop: 0 }, 300);
    },

    hideMessages: function () {

        $("#successPopup").hide();
        $("#errorPopup").hide();
    }
};

$(document).ready(function () {
    PayslipReport.init();
});