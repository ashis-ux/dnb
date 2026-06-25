document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadCategories();

        initializeBankLookup();

        storepaninuppercase();

        showSessionMessage();

        initializeMobileField();

        // Hide bank name until IFSC is entered
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

    fetch("/api/categories/logged-in-user")

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
        "/api/bank/bank-names?ifscCode="
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
        "/api/bank/bank-code"
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

        dob:
            getValue("dob"),

        doj:
            getValue("doj"),

        dos:
            getValue("dos"),

        sexCode:
            getValue("sexCode"),

        empStatus:
            parseInteger(
                getValue("empStatus")),

        bankCd:
            parseInteger(
                getValue("bankCd")),

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
            parseInteger(
                getValue("catg")),

        speciality:
            document.getElementById("speciality")
                    .value
                    .trim(),

        tuitionFeeInd:
            parseInteger(
                getValue("tuitionFeeInd"))
    };

    fetch("/api/dnb", {

        method: "POST",

        headers: {

            "Content-Type":
                "application/json"
        },

        body:
            JSON.stringify(dto)
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

            "DNB Entry created successfully. ID : "
            + data.id);

        window.location.reload();

    })

    .catch(error => {

        showError(

            error.message
            || "Unable to save DNB Entry");

    });

}

/*---------------------------------------
 * Validation
 *--------------------------------------*/

function validateForm() {

    clearMessages();

    if (isBlank("name")) {

        showError("Name is mandatory");

        return false;
    }

    if (isBlank("doj")) {

        showError("DOJ is mandatory");

        return false;
    }

    if (isBlank("dos")) {

        showError("DOS is mandatory");

        return false;
    }

    if (isBlank("sexCode")) {

        showError("Gender is mandatory");

        return false;
    }

    if (isBlank("pan")) {

        showError("PAN Number is mandatory");

        return false;
    }

    if (isBlank("mobileNo")) {

        showError("Mobile Number is mandatory");

        return false;
    }

    if (isBlank("emailId")) {

        showError("Email ID is mandatory");

        return false;
    }

    if (isBlank("ifscCode")) {

        showError("IFSC Code is mandatory");

        return false;
    }

    /*
     * Bank Name dropdown appears only
     * after IFSC validation.
     */

    if (document.getElementById("bankNameRow")
            .style.display !== "none"
        && isBlank("bankName")) {

        showError(
            "Please select Bank Name");

        return false;
    }

    if (isBlank("bankCd")) {

        showError(
            "Please enter a valid IFSC Code.");

        return false;
    }

    if (isBlank("bankAcno")) {

        showError(
            "Bank Account Number is mandatory");

        return false;
    }

    if (isBlank("catg")) {

        showError(
            "Category is mandatory");

        return false;
    }

    if (isBlank("speciality")) {

        showError(
            "Speciality is mandatory");

        return false;
    }

    if (!validatePan()) {

        return false;
    }

    if (!validateAccount()) {

        return false;
    }

    if (!validateDOS()) {

        return false;
    }

    if (!validateMobile()) {

        return false;
    }

    if (!validateEmail()) {

        return false;
    }

    return true;
}

/*---------------------------------------
 * Mobile Validation
 *--------------------------------------*/

function validateMobile() {

    const mobile =
        document.getElementById("mobileNo")
                .value
                .trim();

    const regex =
        /^[6-9]\d{9}$/;

    if (!regex.test(mobile)) {

        showError(
            "Mobile Number should contain 10 digits.");

        return false;
    }

    return true;
}

/*---------------------------------------
 * Email Validation
 *--------------------------------------*/

function validateEmail() {

    const email =
        document.getElementById("emailId")
                .value
                .trim();

    const regex =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!regex.test(email)) {

        showError(
            "Invalid Email ID.");

        return false;
    }

    return true;
}

/*---------------------------------------
 * DOJ / DOS Validation
 *--------------------------------------*/

function validateDOS() {

    const doj =
        document.getElementById("doj").value;

    const dos =
        document.getElementById("dos").value;

    if (doj && dos) {

        const dojDate =
            new Date(doj);

        const dosDate =
            new Date(dos);

        if (dosDate <= dojDate) {

            showError(
                "DOS must be greater than DOJ.");

            return false;
        }
    }

    return true;
}

/*---------------------------------------
 * PAN Validation
 *--------------------------------------*/

function validatePan() {

    const pan =
        document.getElementById("pan")
                .value
                .trim()
                .toUpperCase();

    const regex =
        /^[A-Z]{5}[0-9]{4}[A-Z]$/;

    if (!regex.test(pan)) {

        showError(
            "PAN should be in format ABCDE1234F.");

        return false;
    }

    return true;
}

/*---------------------------------------
 * Account Number Validation
 *--------------------------------------*/

function validateAccount() {

    const account =
        document.getElementById("bankAcno")
                .value
                .trim();

    const regex =
        /^\d{9,17}$/;

    if (!regex.test(account)) {

        showError(
            "Bank Account Number should contain 9 to 17 digits.");

        return false;
    }

    return true;
}

/*---------------------------------------
 * Helper Methods
 *--------------------------------------*/

function getValue(id) {

    return document.getElementById(id).value;
}

function parseInteger(value) {

    if (value === null
            || value === undefined
            || value === "") {

        return null;
    }

    return parseInt(value);
}

function isBlank(id) {

    return getValue(id).trim() === "";
}

/*---------------------------------------
 * Clear Messages
 *--------------------------------------*/

function clearMessages() {

    document.getElementById("successPopup")
            .style.display =
            "none";

    document.getElementById("errorPopup")
            .style.display =
            "none";

    document.getElementById("successPopup")
            .innerHTML = "";

    document.getElementById("errorPopup")
            .innerHTML = "";
}

/*---------------------------------------
 * Error Popup
 *--------------------------------------*/

function showError(message) {

    clearMessages();

    document.getElementById("errorPopup")
            .innerHTML =
            message;

    document.getElementById("errorPopup")
            .style.display =
            "block";

    window.scrollTo({

        top: 0,

        behavior: "smooth"

    });

    setTimeout(function () {

        document.getElementById("errorPopup")
                .style.display =
                "none";

    }, 5000);
}

/*---------------------------------------
 * Success Popup
 *--------------------------------------*/

function showSuccess(message) {

    clearMessages();

    document.getElementById("successPopup")
            .innerHTML =
            message;

    document.getElementById("successPopup")
            .style.display =
            "block";

    window.scrollTo({

        top: 0,

        behavior: "smooth"

    });

    setTimeout(function () {

        document.getElementById("successPopup")
                .style.display =
                "none";

    }, 3000);
}

/*---------------------------------------
 * Reset Bank Details
 *--------------------------------------*/

function resetBankDetails() {

    const bankDropdown =
        document.getElementById("bankName");

    bankDropdown.innerHTML =
        `<option value="">
            Select
         </option>`;

    document.getElementById("bankCd").value = "";

    document.getElementById("bankNameRow")
            .style.display = "none";
}

/*---------------------------------------
 * IFSC Events
 *--------------------------------------*/

document.getElementById("ifscCode")
        .addEventListener(
            "input",
            function () {

                resetBankDetails();

            });

document.getElementById("ifscCode")
        .addEventListener(
            "keyup",
            function () {

                this.value =
                    this.value.toUpperCase();

            });

/*---------------------------------------
 * Bank Name Change
 *--------------------------------------*/

document.getElementById("bankName")
        .addEventListener(
            "change",
            function () {

                if (this.value === "") {

                    document.getElementById("bankCd")
                            .value = "";

                }

            });

/*---------------------------------------
 * Clear Form
 *--------------------------------------*/

function clearForm() {

    document.querySelector("form").reset();

    clearMessages();

    resetBankDetails();

    document.getElementById("bankCd")
            .value = "";
}

/*---------------------------------------
 * Exit Button
 *--------------------------------------*/

function exitScreen() {

    window.location.href = "/home";

}