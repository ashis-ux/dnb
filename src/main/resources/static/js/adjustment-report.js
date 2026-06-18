const AdjustmentReport = {

    currentPage: 0,

    pageSize: 10,

    totalPages: 0,

    init: function () {

        this.bindEvents();

        this.loadYymm();
    },

    bindEvents: function () {

        $("#btnSearch").on(
            "click",
            function () {

                AdjustmentReport.currentPage = 0;

                AdjustmentReport.loadReport();
            });

        $("#btnExport").on(
            "click",
            AdjustmentReport.exportExcel);

        $("#btnClear").on(
            "click",
            AdjustmentReport.clearScreen);
    },

    loadYymm: function () {

        $.ajax({

            url:
                "/api/adjustment-report/yymm",

            type:
                "GET",

            success:
                function (response) {

                    let dropdown =
                        $("#yymm");

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
                }
        });
    },

	loadReport: function () {

	    let yymm =
	        $("#yymm").val();

	    if (!yymm) {

	        $("#errorPopup")
	            .text(
	                "Please select YYMM.")
	            .show();

	        return;
	    }

	    $("#errorPopup")
	        .hide();

	    $.ajax({

	        url:
	            "/api/adjustment-report/"
	            + yymm
	            + "?page="
	            + this.currentPage
	            + "&size="
	            + this.pageSize,

	        type:
	            "GET",

	        success:
	            function (response) {

	                AdjustmentReport.populateGrid(
	                    response.content);

	                AdjustmentReport.buildPagination(
	                    response.totalPages);
	            },

	        error:
	            function (xhr) {

	                let msg =
	                    "Unable to load report.";

	                if (xhr.responseJSON &&
	                    xhr.responseJSON.message) {

	                    msg =
	                        xhr.responseJSON.message;
	                }

	                $("#errorPopup")
	                    .text(msg)
	                    .show();
	            }
	    });
	},
    populateGrid: function (
        data) {

        $("#reportBody")
            .empty();

        if (data.length === 0) {

            $("#reportBody")
                .append(

                    `<tr>

                        <td colspan="10">

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

                $("#reportBody")
                    .append(

                        `<tr>

                            <td>${row.id}</td>

                            <td>${row.name}</td>

                            <td>${row.yymm}</td>

                            <td>${row.forym}</td>

                            <td>${row.days}</td>

                            <td>${row.catg}</td>

                            <td>${row.yr}</td>

                            <td>${row.amt}</td>

                            <td>${row.stopAdjInd}</td>

                            <td>${row.paidInd}</td>

                        </tr>`
                    );
            });
    },

    buildPagination: function (
        totalPages) {

        this.totalPages =
            totalPages;

        let html =
            '<nav><ul class="pagination justify-content-center">';

        html +=
            `<li class="page-item ${this.currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link"
                   href="#"
                   onclick="AdjustmentReport.previousPage()">
                    Previous
                </a>
            </li>`;

        for (let i = 0; i < totalPages; i++) {

            html +=
                `<li class="page-item ${i === this.currentPage ? 'active' : ''}">
                    <a class="page-link"
                       href="#"
                       onclick="AdjustmentReport.goToPage(${i})">
                       ${i + 1}
                    </a>
                </li>`;
        }

        html +=
            `<li class="page-item ${this.currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link"
                   href="#"
                   onclick="AdjustmentReport.nextPage()">
                    Next
                </a>
            </li>`;

        html +=
            "</ul></nav>";

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

	        $("#errorPopup")
	            .text(
	                "Please select YYMM before exporting.")
	            .show();

	        return;
	    }

	    $("#errorPopup")
	        .hide();

	    window.location.href =
	        "/api/adjustment-report/export/"
	        + yymm;
	},

    clearScreen: function () {

        $("#reportBody")
            .empty();

        $("#paginationContainer")
            .empty();

        $("#yymm")
            .val("");
			
			$("#errorPopup")
			       .hide();
    }
};

$(document).ready(
    function () {

        AdjustmentReport.init();
    });