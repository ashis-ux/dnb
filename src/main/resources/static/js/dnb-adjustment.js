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

   fetch(BASE_URL + "/api/categories/logged-in-user-all-categories")

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

    fetch(BASE_URL + "/api/dnb-adj/entry/" + yymm)

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
   Category Dropdown
---------------------------- */

function buildCategoryOptions(
        empCatg,
        selectedCatg,
        selectedYr) {

    let html =
        '<option value="">Select</option>';

    categoryList
        .filter(cat => cat.catg == empCatg)
        .forEach(cat => {

            const selected =
                cat.catg == selectedCatg &&
                cat.year == selectedYr
                    ? "selected"
                    : "";

            html += `
                <option
                    value="${cat.catg}_${cat.year}"
                    data-catg="${cat.catg}"
                    data-year="${cat.year}"
                    data-stipend="${cat.stipend}"
                    ${selected}>

                    ${cat.description}
                    - Year ${cat.year}

                </option>`;
        });

    return html;
}


function buildForymOptions(
        yymm,
        doj,
        selectedForym) {

    let options =
        '<option value="">Select</option>';

    const currentYymm =
        parseInt(yymm);

    let currentDate =
        new Date(
            parseInt(
                String(currentYymm)
                    .substring(0, 4)),
            parseInt(
                String(currentYymm)
                    .substring(4, 6)) - 1,
            1);

    currentDate.setMonth(
        currentDate.getMonth() - 1);

    let oldestDate =
        new Date(currentDate);

    oldestDate.setMonth(
        oldestDate.getMonth() - 11);

    let dojDate =
        new Date(doj);

    let dojMonth =
        new Date(
            dojDate.getFullYear(),
            dojDate.getMonth(),
            1);

    for (let dt =
         new Date(currentDate);

         dt >= oldestDate;

         dt.setMonth(
            dt.getMonth() - 1)) {

        let yymmValue =

            dt.getFullYear()
            +
            String(
                dt.getMonth() + 1)
                .padStart(
                    2,
                    "0");

        let candidateMonth =
            new Date(
                dt.getFullYear(),
                dt.getMonth(),
                1);

        if (candidateMonth < dojMonth) {

            continue;
        }

        options +=

            `<option value="${yymmValue}"
                ${parseInt(yymmValue)
                    === selectedForym
                    ? "selected"
                    : ""}>

                ${yymmValue}

            </option>`;
    }

    return options;
}

/* ---------------------------
   Initialize Rows
---------------------------- */

function initializeRows(data) {

    document.querySelectorAll(
        "#adjustmentBody tr")

        .forEach((row, index) => {

            const dto =
                data[index];

            const forymInput =
                row.querySelector(
                    ".forym");

            const daysSelect =
                row.querySelector(
                    ".days");

            if (dto.forym) {

                populateDaysDropdown(
                    daysSelect,
                    dto.forym,
                    dto.days);
            }

            forymInput.addEventListener(
                "change",
                function () {

                    populateDaysDropdown(
                        daysSelect,
                        this.value,
                        dto.days);

                    calculateAmount(
                        row);
                });

            daysSelect.addEventListener(
                "change",
                function () {

                    calculateAmount(
                        row);
                });

             
        });
}

/* ---------------------------
   Days Dropdown
---------------------------- */

function populateDaysDropdown(
    select,
    forym,
    selectedValue) {

    select.innerHTML = "";

    forym = String(forym);

    if (!forym ||
        forym.length !== 6) {

        select.innerHTML =
            '<option value="0" selected>0</option>';

        return;
    }

    const year =
        parseInt(forym.substring(0, 4));

    const month =
        parseInt(forym.substring(4, 6));

    const maxDays =
        new Date(
            year,
            month,
            0)
            .getDate();

    select.innerHTML +=
        `<option value="0"
            ${selectedValue == 0 ? "selected" : ""}>
            0
        </option>`;

    for (let i = 1; i <= maxDays; i++) {

        select.innerHTML +=
            `<option value="${i}"
                ${i == selectedValue ? "selected" : ""}>
                ${i}
            </option>`;
    }
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
        `/api/dnb-adj/calculate?id=${id}&forym=${forym}&days=${days}`)
        .then(response => response.json())
        .then(data => {

            row.querySelector(".amt").value =data;

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

            /*
             * Skip blank rows
             */
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

                /*
                 * Backend will calculate year.
                 */
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
	                error.message
	                || "Unable to save adjustment."
	            );

	});

}

/* ---------------------------
   Utility
---------------------------- */

function clearForm() {

    document.getElementById(
        "adjustmentBody")
        .innerHTML = "";

    clearMessages();
}

function exitScreen() {

   window.location.href = BASE_URL + "/";
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

    document.getElementById("errorPopup").innerHTML = message;
    document.getElementById("errorPopup").style.display = "block";

    window.scrollTo({ top: 0, behavior: "smooth" });
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

function addAdjustmentRow(button) {

    const currentRow = button.closest("tr");

    const clone = currentRow.cloneNode(true);

    clone.dataset.originalForym = "";

    clone.querySelector(".forym").value = "";

    clone.querySelector(".days").innerHTML =
        '<option value="0">0</option>';

    clone.querySelector(".amt").value = 0;

    clone.querySelector(".stopAdj").value = 0;

    currentRow.after(clone);

    initializeSingleRow(clone);

    refreshForymDropdowns();
}

function removeAdjustmentRow(button) {

    const tbody =
        document.getElementById("adjustmentBody");

    if (tbody.rows.length == 1) {

        showError("At least one row is required.");

        return;
    }

    button.closest("tr").remove();

    refreshForymDropdowns();
}

function refreshForymDropdowns() {

    const selectedMonths = [];

    document.querySelectorAll(".forym").forEach(function(select){

        if(select.value){

            selectedMonths.push(select.value);

        }

    });

    document.querySelectorAll(".forym").forEach(function(select){

        const current = select.value;

        Array.from(select.options).forEach(function(option){

            if(option.value=="")

                return;

            option.hidden =
                option.value != current &&
                selectedMonths.includes(option.value);

        });

    });
	}
	function initializeSingleRow(row){

	    const forym = row.querySelector(".forym");

	    const days = row.querySelector(".days");

	    forym.addEventListener("change",function(){

	        populateDaysDropdown(
	            days,
	            this.value,
	            0);

	        calculateAmount(row);

	        refreshForymDropdowns();

	    });

	    days.addEventListener("change",function(){

	        calculateAmount(row);

	    });

	}

