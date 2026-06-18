let categoryList = [];

document.addEventListener(
    "DOMContentLoaded",
    function () {

        setPreviousMonth();

        bindButtons();

        loadCategories();
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
        "/api/categories/logged-in-user")

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

    const yymm =
        document.getElementById(
            "yymm")
            .value;

    fetch(
        "/api/dnb-adj/entry/"
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

				<select class="forym">

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
                    </select>

                </td>

                <td>

                    <select class="categorySelect"
                            ${disabled}>

                        ${buildCategoryOptions(
                            emp.catg,
                            emp.yr)}

                    </select>

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

                <td>

                    <select class="paid"
                            ${disabled}>

                        <option value="0"
                            ${emp.paidInd === 0 ? "selected" : ""}>

                            No

                        </option>

                        <option value="1"
                            ${emp.paidInd === 1 ? "selected" : ""}>

                            Yes

                        </option>

                    </select>

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
    selectedCatg,
    selectedYr) {

    let html =
        '<option value="">Select</option>';

    categoryList.forEach(cat => {

        const selected =

            cat.catg === selectedCatg
            &&
            cat.year === selectedYr

            ? "selected"
            : "";

        html +=

            `<option
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

            row.querySelector(
                ".categorySelect")

                .addEventListener(
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

    if (!forym ||
        forym.length !== 6) {

        return;
    }

    const year =
        parseInt(
            forym.substring(
                0,
                4));

    const month =
        parseInt(
            forym.substring(
                4,
                6));

    const maxDays =
        new Date(
            year,
            month,
            0)
            .getDate();

    for (let i = 1;
         i <= maxDays;
         i++) {

        select.innerHTML +=

            `<option value="${i}"
                     ${i === selectedValue ? "selected" : ""}>

                ${i}

            </option>`;
    }
}

/* ---------------------------
   Calculate Amount
---------------------------- */

function calculateAmount(row) {

    const forym =
        row.querySelector(
            ".forym")
            .value;

    const days =
        parseInt(
            row.querySelector(
                ".days")
                .value || 0);

    const category =
        row.querySelector(
            ".categorySelect");

    if (!forym ||
        !days ||
        !category.value) {

        return;
    }

    const option =
        category.options[
            category.selectedIndex];

    const stipend =
        parseInt(
            option.dataset.stipend);

    const year =
        parseInt(
            forym.substring(
                0,
                4));

    const month =
        parseInt(
            forym.substring(
                4,
                6));

    const daysInMonth =
        new Date(
            year,
            month,
            0)
            .getDate();

    const amount =
        Math.round(
            (stipend / daysInMonth)
            * days);

    row.querySelector(
        ".amt")
        .value =
        amount;
}

/* ---------------------------
   Save
---------------------------- */

function saveAdjustments() {

    const yymm =
        parseInt(
            document.getElementById(
                "yymm")
                .value);

    const dtoList = [];

    document.querySelectorAll(
        "#adjustmentBody tr")

        .forEach(row => {

            const category =
                row.querySelector(
                    ".categorySelect");

            if (!category.value) {

                return;
            }

            const option =
                category.options[
                    category.selectedIndex];

					const forym =
					    parseInt(
					        row.querySelector(
					            ".forym")
					            .value);

					const days =
					    parseInt(
					        row.querySelector(
					            ".days")
					            .value || 0);

					const amount =
					    parseInt(
					        row.querySelector(
					            ".amt")
					            .value || 0);

					/*
					 * Skip empty rows
					 */
					if (!forym ||
					    days <= 0 ||
					    amount <= 0) {

					    return;
					}

					dtoList.push({

					    yymm: yymm,

					    id: parseInt(
					        row.querySelector(
					            ".emp-id")
					            .innerText),

					    originalForym:
					        row.dataset.originalForym
					            ? parseInt(
					                row.dataset.originalForym)
					            : forym,

					    forym: forym,

					    days: days,

					    catg: parseInt(
					        option.dataset.catg),

					    yr: parseInt(
					        option.dataset.year),

					    amt: amount,

					    stopAdjInd: parseInt(
					        row.querySelector(
					            ".stopAdj")
					            .value),

					    paidInd: parseInt(
					        row.querySelector(
					            ".paid")
					            .value)
					});
        });

		fetch(
		    "/api/dnb-adj/bulk",
		    {
		        method: "POST",
		        headers: {
		            "Content-Type":
		                "application/json"
		        },
		        body: JSON.stringify(dtoList)
		    })

		.then(async response => {

		    const result =
		        await response.json();

		    if (!response.ok) {

		        throw new Error(
		            result.message ||
		            "Something went wrong");
		    }

		    return result;
		})

		.then(() => {

		    showSuccess(
		        "Adjustment saved successfully");

		    loadAdjustments();
		})

		.catch(error => {

		    showError(
		        error.message);
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