const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

const DnbMasterReport = {

    currentPage: 0,

    pageSize: 10,

    totalPages: 0,

    init: function () {

        this.bindEvents();

        this.loadYymmDropdown();
    },

    bindEvents: function () {

        $("#btnSearch").on("click", () => {

            this.currentPage = 0;

            this.loadReport();
        });

        $("#btnExport").on("click", () => {

            this.exportExcel();
        });

        $("#btnClear").on("click", () => {

            this.clearScreen();
        });

        // Pagination Events
        $(document).on("click", ".page-number", (e) => {

            e.preventDefault();

            this.goToPage($(e.currentTarget).data("page"));
        });

        $(document).on("click", "#prevPage", (e) => {

            e.preventDefault();

            this.previousPage();
        });

        $(document).on("click", "#nextPage", (e) => {

            e.preventDefault();

            this.nextPage();
        });
    },

    loadYymmDropdown: function () {

        $.ajax({

            url: BASE_URL + "/api/reports/yymm",

            type: "GET",

            success: (response) => {

                let dropdown = $("#yymm");

                dropdown.empty();

                dropdown.append('<option value="">Select YYMM</option>');

                $.each(response, function (index, yymm) {

                    dropdown.append(
                        `<option value="${yymm}">${yymm}</option>`
                    );

                });

            },

            error: () => {

                this.showError("Unable to load YYMM values");
            }
        });
    },

    loadReport: function () {

        this.hideMessages();

        let yymm = $("#yymm").val();

        if (!yymm) {

            this.showError("Please select YYMM");

            return;
        }

        $.ajax({

            url: `${BASE_URL}/api/reports/dnb-master/${yymm}?page=${this.currentPage}&size=${this.pageSize}`,

            type: "GET",

            success: (response) => {

                console.log(response);

                this.populateGrid(response.content);

                this.buildPagination(response.totalPages);

            },

            error: (xhr) => {

                let msg = "Unable to load report";

                if (xhr.responseJSON && xhr.responseJSON.message) {

                    msg = xhr.responseJSON.message;
                }

                this.showError(msg);
            }
        });
    },

    populateGrid: function (data) {

        $("#reportBody").empty();

        if (!data || data.length === 0) {

            $("#reportBody").append(`
                <tr>
                    <td colspan="19" class="text-center">
                        No Records Found
                    </td>
                </tr>
            `);

            return;
        }

        $.each(data, (index, row) => {

            $("#reportBody").append(`
                <tr>
                    <td>${row.id || ""}</td>
                    <td>${row.name || ""}</td>
                    <td>${this.formatDate(row.dob)}</td>
                    <td>${this.formatDate(row.doj)}</td>
                    <td>${this.formatDate(row.dos)}</td>
                    <td>${row.empStatus || ""}</td>
                    <td>${row.stipendRate || ""}</td>
                    <td>${row.dailyRate || ""}</td>
                    <td>${row.sexCode || ""}</td>
                    <td>${row.bankCd || ""}</td>
                    <td>${row.bankAcno || ""}</td>
                    <td>${row.pan || ""}</td>
                    <td>${row.catg || ""}</td>
                    <td>${row.catgDesc || ""}</td>
                    <td>${row.speciality || ""}</td>
                    <td>${row.trgDuration || ""}</td>
                    <td>${row.stopPayInd || ""}</td>
                    <td>${row.tuitionFeeInd || ""}</td>
                    <td>${row.dnbType || ""}</td>
                </tr>
            `);

        });
    },

    buildPagination: function (totalPages) {

        this.totalPages = totalPages;

        let html = "";

        if (totalPages <= 1) {

            $("#paginationContainer").html("");

            return;
        }

        html += `<nav><ul class="pagination justify-content-center">`;

        html += `
            <li class="page-item ${this.currentPage === 0 ? "disabled" : ""}">
                <a class="page-link" href="#" id="prevPage">
                    Previous
                </a>
            </li>
        `;

        for (let i = 0; i < totalPages; i++) {

            html += `
                <li class="page-item ${i === this.currentPage ? "active" : ""}">
                    <a class="page-link page-number"
                       href="#"
                       data-page="${i}">
                        ${i + 1}
                    </a>
                </li>
            `;
        }

        html += `
            <li class="page-item ${this.currentPage === totalPages - 1 ? "disabled" : ""}">
                <a class="page-link" href="#" id="nextPage">
                    Next
                </a>
            </li>
        `;

        html += `</ul></nav>`;

        $("#paginationContainer").html(html);
    },

    goToPage: function (page) {

        this.currentPage = page;

        this.loadReport();
    },

    previousPage: function () {

        if (this.currentPage > 0) {

            this.currentPage--;

            this.loadReport();
        }
    },

    nextPage: function () {

        if (this.currentPage < this.totalPages - 1) {

            this.currentPage++;

            this.loadReport();
        }
    },

    exportExcel: function () {

        let yymm = $("#yymm").val();

        if (!yymm) {

            this.showError("Please select YYMM before exporting.");

            return;
        }

        window.location.href =
            `${BASE_URL}/api/reports/dnb-master/export/${yymm}`;
    },

    clearScreen: function () {

        $("#reportBody").empty();

        $("#paginationContainer").empty();

        $("#yymm").val("");

        this.hideMessages();
    },

    showSuccess: function (msg) {

        $("#successPopup").text(msg).show();

        $("#errorPopup").hide();
    },

    showError: function (msg) {

        $("#errorPopup").text(msg).show();

        $("#successPopup").hide();
    },

    hideMessages: function () {

        $("#successPopup").hide();

        $("#errorPopup").hide();
    },

    formatDate: function (dateValue) {

        if (!dateValue) {

            return "";
        }

        return new Date(dateValue).toLocaleDateString("en-GB");
    }
};

$(function () {

    DnbMasterReport.init();
});