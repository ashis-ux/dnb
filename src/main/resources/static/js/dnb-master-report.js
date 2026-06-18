const DnbMasterReport = {

    currentPage: 0,

    pageSize: 10,

    totalPages: 0,

    init: function () {

        this.bindEvents();

        this.loadYymmDropdown();
    },

    bindEvents: function () {

        $("#btnSearch").on(
            "click",
            function () {

                DnbMasterReport.currentPage = 0;

                DnbMasterReport.loadReport();
            });

        $("#btnExport").on(
            "click",
            DnbMasterReport.exportExcel);

        $("#btnClear").on(
            "click",
            DnbMasterReport.clearScreen);
    },

    loadYymmDropdown: function () {

        $.ajax({

            url:
                "/api/reports/yymm",

            type:
                "GET",

            success:
                function (response) {

                    let dropdown =
                        $("#yymm");

                    dropdown.empty();

                    dropdown.append(
                        '<option value="">Select YYMM</option>'
                    );

                    $.each(
                        response,
                        function (
                            index,
                            yymm) {

                            dropdown.append(

                                `<option value="${yymm}">
                                    ${yymm}
                                </option>`
                            );
                        });
                },

            error:
                function () {

                    DnbMasterReport.showError(
                        "Unable to load YYMM values");
                }
        });
    },

    loadReport: function () {

        let yymm =
            $("#yymm").val();

        if (!yymm) {

            DnbMasterReport.showError(
                "Please select YYMM");

            return;
        }

        $.ajax({

            url:
                "/api/reports/dnb-master/"
                + yymm
                + "?page="
                + DnbMasterReport.currentPage
                + "&size="
                + DnbMasterReport.pageSize,

            type:
                "GET",

            success:
                function (response) {

                    DnbMasterReport.populateGrid(
                        response.content);

                    DnbMasterReport.buildPagination(
                        response.totalPages);
                },

            error:
                function (xhr) {

                    let msg =
                        "Unable to load report";

                    if (xhr.responseJSON &&
                        xhr.responseJSON.message) {

                        msg =
                            xhr.responseJSON.message;
                    }

                    DnbMasterReport.showError(
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
                    <td colspan="19"
                        class="text-center">

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

                        <td>${row.id || ''}</td>
                        <td>${row.name || ''}</td>
                        <td>${DnbMasterReport.formatDate(row.dob)}</td>
                        <td>${DnbMasterReport.formatDate(row.doj)}</td>
                        <td>${DnbMasterReport.formatDate(row.dos)}</td>
                        <td>${row.empStatus || ''}</td>
                        <td>${row.stipendRate || ''}</td>
                        <td>${row.dailyRate || ''}</td>
                        <td>${row.sexCode || ''}</td>
                        <td>${row.bankCd || ''}</td>
                        <td>${row.bankAcno || ''}</td>
                        <td>${row.pan || ''}</td>
                        <td>${row.catg || ''}</td>
                        <td>${row.catgDesc || ''}</td>
                        <td>${row.speciality || ''}</td>
                        <td>${row.trgDuration || ''}</td>
                        <td>${row.stopPayInd || ''}</td>
                        <td>${row.tuitionFeeInd || ''}</td>
                        <td>${row.dnbType || ''}</td>

                    </tr>`
                );
            });
    },

    buildPagination: function (totalPages) {

        this.totalPages =
            totalPages;

        $("#paginationContainer")
            .empty();

        if (totalPages === 0) {

            return;
        }

        let html =
            `<nav>
                <ul class="pagination justify-content-center">`;

        html +=
            `<li class="page-item ${this.currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link"
                   href="#"
                   onclick="DnbMasterReport.previousPage()">
                   Previous
                </a>
            </li>`;

        for (let i = 0; i < totalPages; i++) {

            html +=
                `<li class="page-item ${i === this.currentPage ? 'active' : ''}">
                    <a class="page-link"
                       href="#"
                       onclick="DnbMasterReport.goToPage(${i})">
                       ${i + 1}
                    </a>
                </li>`;
        }

        html +=
            `<li class="page-item ${this.currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link"
                   href="#"
                   onclick="DnbMasterReport.nextPage()">
                   Next
                </a>
            </li>`;

        html +=
            `</ul>
             </nav>`;

        $("#paginationContainer")
            .html(html);
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

        if (this.currentPage <
            this.totalPages - 1) {

            this.currentPage++;

            this.loadReport();
        }
    },

    exportExcel: function () {

        let yymm =
            $("#yymm").val();

        if (!yymm) {

            DnbMasterReport.showError(
                "Please select YYMM");

            return;
        }

        window.location.href =
            "/api/reports/dnb-master/export/"
            + yymm;
    },

    clearScreen: function () {

        $("#reportBody").empty();

        $("#paginationContainer").empty();

        $("#yymm").val("");

        this.hideMessages();
    },

    showSuccess: function (msg) {

        $("#successPopup")
            .text(msg)
            .show();

        $("#errorPopup")
            .hide();
    },

    showError: function (msg) {

        $("#errorPopup")
            .text(msg)
            .show();

        $("#successPopup")
            .hide();
    },

    hideMessages: function () {

        $("#successPopup").hide();

        $("#errorPopup").hide();
    },

    formatDate: function (dateValue) {

        if (!dateValue) {

            return "";
        }

        return new Date(dateValue)
            .toLocaleDateString(
                "en-GB");
    }
};

$(document).ready(function () {

    DnbMasterReport.init();
});