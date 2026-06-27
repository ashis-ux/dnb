const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

const PaybillReport = {

    currentPage: 0,

    pageSize: 10,

    totalPages: 0,

    init: function () {

        this.bindEvents();

        this.loadYears();
    },

    bindEvents: function () {

        $("#btnSearch").on(
            "click",
            function () {

                PaybillReport.currentPage = 0;

                PaybillReport.loadReport();
            });

        $("#btnExport").on(
            "click",
            PaybillReport.exportExcel);

        $("#btnClear").on(
            "click",
            PaybillReport.clearScreen);
    },

    loadYears: function () {

        let currentYear =
            new Date().getFullYear();

        for (let year = currentYear;
             year >= 2020;
             year--) {

            $("#year").append(

                `<option value="${year}">
                    ${year}
                 </option>`
            );
        }
    },

    getYymm: function () {

        let year =
            $("#year").val();

        let month =
            $("#month").val();

        if (!year ||
            !month) {

            return null;
        }

        return year + month;
    },

    loadReport: function () {

        let yymm =
            this.getYymm();

        if (!yymm) {

            this.showError(
                "Please select Year and Month.");

            return;
        }

        this.hideMessages();

        $.ajax({

            url:
                BASE_URL
                + "/api/paybill-report/"
                + yymm
                + "?page="
                + this.currentPage
                + "&size="
                + this.pageSize,

            type:
                "GET",

            success:
                function (response) {

                    PaybillReport.populateGrid(
                        response.content);

                    PaybillReport.buildPagination(
                        response.totalPages);
                },

            error:
                function (xhr) {

                    let msg =
                        "Unable to fetch report.";

                    if (xhr.responseJSON &&
                        xhr.responseJSON.message) {

                        msg =
                            xhr.responseJSON.message;
                    }

                    PaybillReport.showError(
                        msg);
                }
        });
    },

    populateGrid: function (data) {

        $("#reportBody").empty();

        if (!data ||
            data.length === 0) {

            $("#reportBody").append(

                `<tr>

                    <td colspan="13">

                        No Records Found

                    </td>

                 </tr>`
            );

            return;
        }

        $.each(
            data,
            function (
                index,
                row) {

                $("#reportBody").append(

                    `<tr>

                        <td>${row.id}</td>

                        <td>${row.name}</td>

                        <td>${row.catg}</td>

                        <td>${row.catgDesc}</td>

                        <td>${row.yymm}</td>

                        <td>${row.stipend}</td>

                        <td>${row.adj}</td>

                        <td>${row.itaxrec}</td>

                        <td>${row.cessrec}</td>

                        <td>${row.cessaddl}</td>

                        <td>${row.gpay}</td>

                        <td>${row.npay}</td>

                        <td>${row.higherTaxInd}</td>

                    </tr>`
                );
            });
    },

    buildPagination: function (
        totalPages) {

        this.totalPages =
            totalPages;

        $("#paginationContainer")
            .empty();

        if (totalPages <= 1) {

            return;
        }

        let html =
            `<nav>
                <ul class="pagination justify-content-center">`;

        html +=

            `<li class="page-item
                ${this.currentPage === 0
                    ? 'disabled'
                    : ''}">

                <a class="page-link"
                   href="#"
                   onclick="PaybillReport.previousPage()">

                    Previous

                </a>

            </li>`;

        for (let i = 0;
             i < totalPages;
             i++) {

            html +=

                `<li class="page-item
                    ${i === this.currentPage
                        ? 'active'
                        : ''}">

                    <a class="page-link"
                       href="#"
                       onclick="PaybillReport.goToPage(${i})">

                        ${i + 1}

                    </a>

                </li>`;
        }

        html +=

            `<li class="page-item
                ${this.currentPage === totalPages - 1
                    ? 'disabled'
                    : ''}">

                <a class="page-link"
                   href="#"
                   onclick="PaybillReport.nextPage()">

                    Next

                </a>

            </li>`;

        html +=
            `</ul>
            </nav>`;

        $("#paginationContainer")
            .html(html);
    },

    goToPage: function (
        page) {

        this.currentPage =
            page;

        this.loadReport();
    },

    previousPage: function () {

        if (this.currentPage > 0) {

            this.currentPage--;

            this.loadReport();
        }
    },

    nextPage: function () {

        if (this.currentPage
            < this.totalPages - 1) {

            this.currentPage++;

            this.loadReport();
        }
    },

    exportExcel: function () {

        let yymm =
            PaybillReport.getYymm();

        if (!yymm) {

            $("#errorPopup")
                .text(
                    "Please select Year And Month before exporting.")
                .show();

            return;
        }

        window.location.href =
            BASE_URL
            + "/api/paybill-report/export/"
            + yymm;
    },

    clearScreen: function () {

        $("#year").val("");

        $("#month").val("");

        $("#reportBody").empty();

        $("#paginationContainer")
            .empty();

        this.hideMessages();
    },

    showError: function (
        message) {

        $("#errorPopup")
            .text(message)
            .show();
    },

    hideMessages: function () {

        $("#errorPopup")
            .hide();

        $("#successPopup")
            .hide();
    }
};

$(document).ready(
    function () {

        PaybillReport.init();
    });