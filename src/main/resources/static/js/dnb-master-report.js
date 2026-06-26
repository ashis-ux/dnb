const BASE_URL = "";

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
                BASE_URL + "/api/reports/yymm",

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
                BASE_URL + "/api/reports/dnb-master/"
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

    exportExcel: function () {

        let yymm =
            $("#yymm").val();

        if (!yymm) {

            $("#errorPopup")
                .text(
                    "Please select YYMM before exporting.")
                .show();

            return;
        }

        window.location.href =
            BASE_URL + "/api/reports/dnb-master/export/"
            + yymm;
    },

    // बाकी code unchanged (same as yours)
};

$(document).ready(function () {

    DnbMasterReport.init();
});