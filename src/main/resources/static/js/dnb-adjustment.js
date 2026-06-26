const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

let categoryList = [];

document.addEventListener(
    "DOMContentLoaded",
    function () {

        setPreviousMonth();

        bindButtons();

        loadCategories();
    });

document.addEventListener("click",function(e){

    if(e.target.closest(".addRow")){

        addAdjustmentRow(
            e.target.closest(".addRow"));

    }

    if(e.target.closest(".removeRow")){

        removeAdjustmentRow(
            e.target.closest(".removeRow"));

    }

});

function bindButtons() {

    document.getElementById(
        "btnLoad")
        .addEventListener(
            "click",
            loadAdjustments);

    document.getElementById(
        "btnSave")
        .addEventListener(
            "click",
            saveAdjustments);

    document.getElementById(
        "btnClear")
        .addEventListener(
            "click",
            clearForm);

    document.getElementById(
        "btnExit")
        .addEventListener(
            "click",
            exitScreen);
}

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

/* ---------------------------
   Load Categories
---------------------------- */

function loadCategories() {

    fetch(
        BASE_URL + "/api/categories/logged-in-user-all-categories")

        .then(response =>
            response.json())

        .then(data => {

            categoryList = data;

            loadAdjustments();
        })

        .catch(error => {

            showError(
                "Unable to load categories");
        });
}

/* ---------------------------
   Load Adjustment Entry
---------------------------- */

function loadAdjustments() {

    clearMessages();

    const yymm = document.getElementById(
            "yymm")
            .value;

    fetch(
        BASE_URL + "/api/dnb-adj/entry/"
        + yymm)

        .then(response => {

            if (!response.ok) {

                throw new Error(
                    "Unable to load data");
            }

            return response.json();
        })

        .then(data => {

            populateGrid(data);
        })

        .catch(error => {

            showError(
                error.message);
        });
}

/* ---------------------------
   Populate Grid
---------------------------- */

function populateGrid(data) {

    const tbody =
        document.getElementById(
            "adjustmentBody");

    tbody.innerHTML = "";

    data.forEach(emp => {

        const disabled =
            emp.editable === 0
                ? "disabled"
                : "";

        const row =

        `<tr data-original-forym="${emp.originalForym || emp.forym || ''}">

            <td class="emp-id">

                ${emp.id}

            </td>

            <td>

                ${emp.name}

            </td>

            <td>

            <select class="forym"  ${disabled}>
                ${buildForymOptions(
                        document.getElementById(
                            "yymm").value,
                        emp.doj,
                        emp.forym)}

            </select>

            </td>

            <td>
                <select class="days"
                        ${disabled}>
                    <option value="${emp.days|| 0}" selected>
                        ${emp.days || 0}
                    </option>
                </select>
            </td>

            <td class="category"
                data-catg="${emp.catg}"
                data-year="${emp.yr}">
                ${emp.catg_desc}
            </td>

            <td>

                <input type="text"
                       class="amt"
                       value="${emp.amt ?? 0}"
                       readonly>

            </td>

            <td>

                <select class="stopAdj"
                        ${disabled}>

                    <option value="0"
                        ${emp.stopAdjInd === 0 ? "selected" : ""}>

                        No

                    </option>

                    <option value="1"
                        ${emp.stopAdjInd === 1 ? "selected" : ""}>

                        Yes

                    </option>

                </select>

            </td>

            <td class="text-center">

                <button
                    type="button"
                    class="btn btn-success btn-sm addRow"
                    ${disabled}>

                    <i class="bi bi-plus-lg"></i>

                </button>

                <button
                    type="button"
                    class="btn btn-danger btn-sm removeRow"
                    ${disabled}>

                    <i class="bi bi-dash-lg"></i>

                </button>

            </td>

        </tr>`;

        tbody.insertAdjacentHTML(
            "beforeend",
            row);
    });

    initializeRows(data);
}

/* ---------------------------
   Calculate Amount
---------------------------- */

function calculateAmount(row) {

    const id =
        parseInt(
            row.querySelector(".emp-id").innerText);

    const forym =
        row.querySelector(".forym").value;

    const days =
        parseInt(
            row.querySelector(".days").value || 0);

    if (!forym || days <= 0) {

        row.querySelector(".amt").value = 0;
        return;
    }

    fetch(
        BASE_URL + `/api/dnb-adj/calculate?id=${id}&forym=${forym}&days=${days}`)
        .then(response => response.json())
        .then(data => {

            row.querySelector(".amt").value = data;

        })
        .catch(() => {

            row.querySelector(".amt").value = 0;

        });
}

/* ---------------------------
   Save
---------------------------- */

function saveAdjustments() {

    clearMessages();

    const yymm =
        parseInt(
            document.getElementById("yymm").value);

    const dtoList = [];

    document.querySelectorAll("#adjustmentBody tr")
        .forEach(row => {

            const category =
                row.querySelector(".category");

            const forym =
                parseInt(
                    row.querySelector(".forym").value);

            const days =
                parseInt(
                    row.querySelector(".days").value || 0);

            const amount =
                parseInt(
                    row.querySelector(".amt").value || 0);

            if (!forym || days <= 0 || amount <= 0) {

                return;
            }

            dtoList.push({

                yymm: yymm,

                id: parseInt(
                    row.querySelector(".emp-id").innerText),

                originalForym:
                    row.dataset.originalForym
                        ? parseInt(row.dataset.originalForym)
                        : forym,

                forym: forym,

                days: days,

                catg: parseInt(
                    category.dataset.catg),

                yr: 0,

                amt: amount,

                stopAdjInd:
                    parseInt(
                        row.querySelector(".stopAdj").value)

            });

        });

    if (dtoList.length === 0) {

        showError(
            "No adjustment records to save.");

        return;
    }

    fetch(BASE_URL + "/api/dnb-adj/bulk", {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(dtoList)

    })
    .then(async response => {

        let result = "";

        try {

            result = await response.text();

        } catch (e) {

            result = "";
        }

        if (!response.ok) {

            throw new Error(
                result || "Unable to save adjustment.");

        }

        return result;

    })
    .then(message => {

        showSuccess(
             "Adjustment saved successfully.");

        setTimeout(function () {

            loadAdjustments();

        }, 2000);

    })
    .catch(error => {

        console.error(error);

        showError(
            error.message ||
            "Unable to save adjustment.");

        setTimeout(function () {

            loadAdjustments();

        }, 2000);

    });

}

/* ---------------------------
   Remaining code unchanged...
---------------------------- */

function clearForm() {

    document.getElementById(
        "adjustmentBody")
        .innerHTML = "";

    clearMessages();
}

function exitScreen() {

    window.location.href =
        "/home";
}

function showSuccess(message) {

    const popup =
        document.getElementById(
            "successPopup");

    popup.innerHTML = message;

    popup.style.display =
        "block";

    document.getElementById(
        "errorPopup")
        .style.display =
        "none";
}

function showError(message) {

    const popup =
        document.getElementById(
            "errorPopup");

    popup.innerHTML = message;

    popup.style.display =
        "block";

    document.getElementById(
        "successPopup")
        .style.display =
        "none";
}

function clearMessages() {

    document.getElementById(
        "successPopup")
        .style.display =
        "none";

    document.getElementById(
        "errorPopup")
        .style.display =
        "none";
}