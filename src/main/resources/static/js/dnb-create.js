const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadCategories();

        initializeBankLookup();

        storepaninuppercase();

        showSessionMessage();

        initializeMobileField();

        document.getElementById("bankNameRow")
                .style.display = "none";
    });

function storepaninuppercase() {

    document.getElementById("pan")
            .addEventListener(
                "input",
                function () {

                    this.value =
                        this.value.toUpperCase();

                });
}

function showSessionMessage() {

    let successMessage =
        sessionStorage.getItem(
            "successMessage");

    if (successMessage) {

        document.getElementById(
            "successPopup")
            .innerHTML =
            successMessage;

        document.getElementById(
            "successPopup")
            .style.display =
            "block";

        sessionStorage.removeItem(
            "successMessage");

        setTimeout(function () {

            document.getElementById(
                "successPopup")
                .style.display =
                "none";

        }, 3000);
    }
}

/*---------------------------------------
 * Mobile Number
 *--------------------------------------*/

function initializeMobileField() {

    document.getElementById("mobileNo")
            .addEventListener(
                "input",
                function () {

                    this.value =
                        this.value.replace(/\D/g, "");

                    if (this.value.length > 10) {

                        this.value =
                            this.value.substring(0, 10);
                    }
                });
}

/*---------------------------------------
 * Category Dropdown
 *--------------------------------------*/

function loadCategories() {

    fetch(BASE_URL + "/api/categories/logged-in-user")

        .then(response => {

            if (!response.ok) {

                throw new Error(
                    "Unable to load categories");
            }

            return response.json();
        })

        .then(data => {

            let category =
                document.getElementById(
                    "catg");

            category.innerHTML =
                `<option value="">
                    Select
                 </option>`;

            data.forEach(cat => {

                category.innerHTML +=

                    `<option value="${cat.catg}">
                        ${cat.description}
                     </option>`;
            });

        })

        .catch(error => {

            showError(
                error.message);
        });
}

/*---------------------------------------
 * Bank Lookup
 *--------------------------------------*/

function initializeBankLookup() {

    document.getElementById("ifscCode")
            .addEventListener(
                "blur",
                loadBankNames);

    document.getElementById("bankName")
            .addEventListener(
                "change",
                loadBankCode);
}

/*---------------------------------------
 * Load Bank Names
 *--------------------------------------*/

function loadBankNames() {

    const ifsc =
        document.getElementById("ifscCode")
                .value
                .trim()
                .toUpperCase();

    const dropdown =
        document.getElementById(
            "bankName");

    dropdown.innerHTML =
        `<option value="">
            Select
         </option>`;

    document.getElementById("bankCd").value = "";

    if (ifsc === "") {

        document.getElementById(
            "bankNameRow")
            .style.display =
            "none";

        return;
    }

    fetch(
        BASE_URL + "/api/bank/bank-names?ifscCode="
        + encodeURIComponent(ifsc))

        .then(response => {

            if (!response.ok) {

                throw new Error(
                    "Invalid IFSC Code");
            }

            return response.json();

        })

        .then(data => {

            if (data.length === 0) {

                throw new Error(
                    "Invalid IFSC Code");
            }

            data.forEach(bank => {

                dropdown.innerHTML +=

                    `<option value="${bank}">
                        ${bank}
                     </option>`;
            });

            document.getElementById(
                "bankNameRow")
                .style.display =
                "flex";

        })

        .catch(error => {

            document.getElementById(
                "bankNameRow")
                .style.display =
                "none";

            document.getElementById(
                "bankCd")
                .value =
                "";

            showError(
                error.message);

        });
}

/*---------------------------------------
 * Load Bank Code
 *--------------------------------------*/

function loadBankCode() {

    const bankName =
        document.getElementById(
            "bankName")
            .value;

    const ifsc =
        document.getElementById(
            "ifscCode")
            .value
            .trim()
            .toUpperCase();

    if (!bankName || !ifsc) {

        document.getElementById(
            "bankCd")
            .value =
            "";

        return;
    }

    fetch(
        BASE_URL + "/api/bank/bank-code"
        + "?bankName="
        + encodeURIComponent(bankName)
        + "&ifscCode="
        + encodeURIComponent(ifsc))

        .then(response => {

            if (!response.ok) {

                throw new Error(
                    "Invalid Bank / IFSC");
            }

            return response.json();

        })

        .then(data => {

            document.getElementById(
                "bankCd")
                .value =
                data.bankCode;

        })

        .catch(error => {

            document.getElementById(
                "bankCd")
                .value =
                "";

            showError(
                error.message);

        });
}

/*---------------------------------------
 * Save DNB
 *--------------------------------------*/

function saveDnb() {

    clearMessages();

    if (!validateForm()) {

        return;
    }

    const dto = {

        name:
            document.getElementById("name")
                    .value
                    .trim(),

        dob: getValue("dob"),
        doj: getValue("doj"),
        dos: getValue("dos"),
        sexCode: getValue("sexCode"),

        empStatus:
            parseInteger(getValue("empStatus")),

        bankCd:
            parseInteger(getValue("bankCd")),

        bankAcno:
            document.getElementById("bankAcno")
                    .value
                    .trim(),

        pan:
            document.getElementById("pan")
                    .value
                    .trim()
                    .toUpperCase(),

        mobileNo:
            document.getElementById("mobileNo")
                    .value
                    .trim(),

        emailId:
            document.getElementById("emailId")
                    .value
                    .trim(),

        catg:
            parseInteger(getValue("catg")),

        speciality:
            document.getElementById("speciality")
                    .value
                    .trim(),

        tuitionFeeInd:
            parseInteger(getValue("tuitionFeeInd"))
    };

    fetch(BASE_URL + "/api/dnb", {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(dto)
    })

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

        sessionStorage.setItem(
            "successMessage",
            "DNB Entry created successfully. ID : " + data.id
        );

        window.location.reload();

    })

    .catch(error => {

        showError(
            error.message || "Unable to save DNB Entry");

    });
}

/*---------------------------------------
 * exit
--------------------------------------*/

function exitScreen() {

    window.location.href = BASE_URL + "/home";
}