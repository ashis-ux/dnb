const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

const AttendanceReport = {

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

            AttendanceReport.currentPage = 0;

            AttendanceReport.loadReport();
        });

    $("#btnExport").on(
        "click",
        AttendanceReport.exportExcel);

    $("#btnClear").on(
        "click",
        AttendanceReport.clearScreen);
},

loadYymmDropdown: function () {

    $.ajax({

        url:
            BASE_URL + "/api/attendance-report/yymm",

        type:
            "GET",

        success:
            function (response) {

                let dropdown =
                    $("#yymm");

                dropdown.empty();

                dropdown.append(

                    `<option value="">
                        Select YYMM
                     </option>`
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

                AttendanceReport.showError(
                    "Unable to load YYMM");
            }
    });
},

loadReport: function () {
    let yymm =
        $("#yymm").val();

    if (!yymm) {

        AttendanceReport.showError(
            "Please select YYMM");

        return;
    }

    AttendanceReport.hideMessages();

    $.ajax({

        url:
            BASE_URL + "/api/attendance-report/"
            + yymm
            + "?page="
            + AttendanceReport.currentPage
            + "&size="
            + AttendanceReport.pageSize,

        type:
            "GET",

        success:
            function (response) {

                AttendanceReport.populateGrid(
                    response.content);

                AttendanceReport.buildPagination(
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

                AttendanceReport.showError(
                    msg);
            }
    });
},

populateGrid: function (
    data) {

    $("#attendanceBody")
        .empty();

    if (!data ||
        data.length === 0) {

        $("#attendanceBody")
            .append(

                `<tr>

                    <td colspan="9"
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

            $("#attendanceBody")
                .append(

                    `<tr>

                        <td>${row.id || ''}</td>

                        <td>${row.name || ''}</td>

                        <td>${row.yymm || ''}</td>

                        <td>${row.duty || 0}</td>

                        <td>${row.al || 0}</td>

                        <td>${row.cl || 0}</td>

                        <td>${row.pl || 0}</td>

                        <td>${row.ml || 0}</td>

                        <td>${row.abs || 0}</td>

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

    if (totalPages === 0) {

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
               onclick="AttendanceReport.previousPage()">

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
                   onclick="AttendanceReport.goToPage(${i})">

                    ${i + 1}

                </a>

            </li>`;
    }

    html +=

        `<li class="page-item
            ${this.currentPage
                === totalPages - 1
                ? 'disabled'
                : ''}">

            <a class="page-link"
               href="#"
               onclick="AttendanceReport.nextPage()">

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

    window.location.href =
        BASE_URL + "/api/attendance-report/export/"
        + yymm;
},

clearScreen: function () {

    $("#attendanceBody")
        .empty();

    $("#paginationContainer")
        .empty();

    $("#yymm")
        .val("");
		
		$("#errorPopup")
		       .hide();

    this.hideMessages();
},

showSuccess: function (
    message) {

    $("#successPopup")
        .text(message)
        .show();

    $("#errorPopup")
        .hide();
},

showError: function (
    message) {

    $("#errorPopup")
        .text(message)
        .show();

    $("#successPopup")
        .hide();
},

hideMessages: function () {

    $("#successPopup")
        .hide();

    $("#errorPopup")
        .hide();
}
};

$(document).ready(
function () {
 
    AttendanceReport.init();
});