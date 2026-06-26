const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

document.addEventListener(
    "DOMContentLoaded",
    function () {

        setPreviousMonth();

        loadAttendance();
    });

function setPreviousMonth() {

    const today =
        new Date();

    today.setMonth(
        today.getMonth() - 1);

    const year =
        today.getFullYear();

    const month =
        String(
            today.getMonth() + 1)
            .padStart(2, "0");

    document.getElementById(
        "yymm")
        .value =
        year + month;
}

function loadAttendance() {

    const yymm = document.getElementById(
        "yymm"
    ).value.trim();

    if (!/^\d{6}$/.test(yymm)) {

        showError(
            "YYMM should be in YYYYMM format."
        );

        return;
    }

    fetch(
        BASE_URL + "/api/dnb-att/entry/"
        + yymm
    )

        .then(response => {

            if (!response.ok) {

                return response.json()
                    .then(error => {

                        throw error;
                    });
            }

            return response.json();
        })

        .then(data => {

            populateGrid(data);
        })

        .catch(error => {

            showError(
                error.message
                || "Unable to load attendance."
            );
        });
}

function populateGrid(data) {

    const tbody =
        document.getElementById(
            "attendanceBody"
        );

    tbody.innerHTML = "";

    if (!data ||
        data.length === 0) {

        tbody.innerHTML =

            `<tr>
                <td colspan="10"
                    class="text-center empty-row">

                    No attendance data found.

                </td>
            </tr>`;

        return;
    }

    data.forEach(emp => {

        tbody.innerHTML +=

            `<tr>

                <td class="employee-id">

                    ${emp.id}

                </td>

                <td class="employee-name">

                    ${emp.name}

                </td>

                <td>

                    ${formatDate(emp.doj)}

                </td>

                <td>

                    <span class="badge bg-primary eligible-days">

                        ${emp.eligibleDays}

                    </span>

                    <input type="hidden"
                           class="eligibleDays"
                           value="${emp.eligibleDays}">

                </td>

                <td>

                    <input type="number"
                           min="0"
                           class="form-control attendance-input duty"
                           value="${emp.duty ?? 0}">

                </td>

                <td>

                    <input type="number"
                           min="0"
                           class="form-control attendance-input al"
                           value="${emp.al ?? 0}">

                </td>

                <td>

                    <input type="number"
                           min="0"
                           class="form-control attendance-input cl"
                           value="${emp.cl ?? 0}">

                </td>

                <td>

                    <input type="number"
                           min="0"
                           class="form-control attendance-input pl"
                           value="${emp.pl ?? 0}">

                </td>

                <td>

                    <input type="number"
                           min="0"
                           class="form-control attendance-input ml"
                           value="${emp.ml ?? 0}">

                </td>

                <td>

                    <input type="number"
                           min="0"
                           class="form-control attendance-input abs"
                           value="${emp.abs ?? 0}">

                </td>

            </tr>`;
    });

    initializeNumericValidation();
}

function saveAttendance() {

    clearMessages();

    if (!validateAttendanceGrid()) {

        return;
    }

    const yymm =
        parseInt(
            document.getElementById(
                "yymm").value
        );

    const rows =
        document.querySelectorAll(
            "#attendanceBody tr"
        );

    let attendanceList = [];

    rows.forEach(row => {

        if (!row.querySelector(".employee-id")) {
            return;
        }

        attendanceList.push({

            yymm: yymm,

            id: parseInt(
                row.querySelector(".employee-id")
                    .innerText
                    .trim()
            ),

            duty: parseInteger(
                row.querySelector(".duty").value
            ),

            al: parseInteger(
                row.querySelector(".al").value
            ),

            cl: parseInteger(
                row.querySelector(".cl").value
            ),

            pl: parseInteger(
                row.querySelector(".pl").value
            ),

            ml: parseInteger(
                row.querySelector(".ml").value
            ),

            abs: parseInteger(
                row.querySelector(".abs").value
            )
        });
    });

    fetch(BASE_URL + "/api/dnb-att/bulk", {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(attendanceList)
    })

        .then(async response => {

            if (!response.ok) {

                const error =
                    await response.json();

                throw error;
            }

            const text =
                await response.text();

            return text
                ? JSON.parse(text)
                : [];
        })

        .then(data => {

            console.log(
                "Attendance saved successfully"
            );

            showSuccess(
                "Attendance saved successfully."
            );

            setTimeout(function () {

                loadAttendance();

            }, 3000);
        })

        .catch(error => {

            console.error(error);

            showError(
                error.message
                || "Unable to save attendance."
            );
        });
}

function validateAttendanceGrid() {

    const rows =
        document.querySelectorAll(
            "#attendanceBody tr"
        );

    for (const row of rows) {

        if (!row.querySelector(".employee-id")) {
            continue;
        }

        const duty =
            parseInteger(row.querySelector(".duty").value);

        const al =
            parseInteger(row.querySelector(".al").value);

        const cl =
            parseInteger(row.querySelector(".cl").value);

        const pl =
            parseInteger(row.querySelector(".pl").value);

        const ml =
            parseInteger(row.querySelector(".ml").value);

        const abs =
            parseInteger(row.querySelector(".abs").value);

        const eligible =
            parseInteger(row.querySelector(".eligibleDays").value);

        const total =
            duty + al + cl + pl + ml + abs;

        if (total === 0) {
            continue;
        }

        if (total !== eligible) {

            const empId =
                row.querySelector(".employee-id")
                    .innerText
                    .trim();

            showError(
                "Employee "
                + empId
                + " total attendance must be "
                + eligible
                + " days. Entered : "
                + total
            );

            return false;
        }
    }

    return true;
}

function initializeNumericValidation() {

    const inputs =
        document.querySelectorAll(".attendance-input");

    inputs.forEach(input => {

        input.addEventListener("input", function () {

            if (this.value < 0) {
                this.value = 0;
            }

            this.value =
                this.value.replace(/\D/g, "");
        });
    });
}

function clearForm() {

    clearMessages();

    setPreviousMonth();

    document.getElementById("attendanceBody").innerHTML =

        `<tr>
            <td colspan="10"
                class="text-center empty-row">

                Click Load to fetch attendance.

            </td>
        </tr>`;
}

function exitScreen() {

    const modal =
        new bootstrap.Modal(
            document.getElementById("exitModal")
        );

    modal.show();
}

function confirmExit() {

    window.location.href =
        BASE_URL + "/";
}

function parseInteger(value) {

    if (!value || value === "") {
        return 0;
    }

    return parseInt(value);
}

function formatDate(date) {

    if (!date) {
        return "";
    }

    const d =
        new Date(date);

    const day =
        String(d.getDate()).padStart(2, "0");

    const month =
        String(d.getMonth() + 1).padStart(2, "0");

    const year =
        d.getFullYear();

    return day + "-" + month + "-" + year;
}

function clearMessages() {

    document.getElementById("successPopup").style.display = "none";
    document.getElementById("errorPopup").style.display = "none";
}

function showSuccess(message) {

    document.getElementById("successPopup").innerHTML = message;
    document.getElementById("successPopup").style.display = "block";

    window.scrollTo({ top: 0, behavior: "smooth" });
}

function showError(message) {

    document.getElementById("errorPopup").innerHTML = message;
    document.getElementById("errorPopup").style.display = "block";

    window.scrollTo({ top: 0, behavior: "smooth" });
}