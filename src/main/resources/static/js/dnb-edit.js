
document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadCategories();

        initializePanField();
    });

function searchDnb() {

    clearMessages();

    const id =
        document.getElementById(
            "searchId")
            .value
            .trim();

    if (!id) {

        showError(
            "Please enter DNB ID");

        return;
    }

    fetch("/api/dnb/" + id)

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

            populateForm(data);

            document.getElementById(
                "editSection")
                .style.display =
                "block";
        })

        .catch(error => {

            document.getElementById(
                "editSection")
                .style.display =
                "none";

            showError(
                error.message
                || "DNB Record not found");
        });
}

function populateForm(data) {

    document.getElementById(
        "name")
        .value =
        data.name || "";

    document.getElementById(
        "dob")
        .value =
        formatDate(
            data.dob);

    document.getElementById(
        "pan")
        .value =
        data.pan || "";

    document.getElementById(
        "doj")
        .value =
        formatDate(
            data.doj);

    document.getElementById(
        "dos")
        .value =
        formatDate(
            data.dos);

    document.getElementById(
        "sexCode")
        .value =
        data.sexCode || "";

    document.getElementById(
        "empStatus")
        .value =
        data.empStatus;

    document.getElementById(
        "bankCd")
        .value =
        data.bankCd || "";

    document.getElementById(
        "bankAcno")
        .value =
        data.bankAcno || "";

    document.getElementById(
        "catg")
        .value =
        data.catg;

    document.getElementById(
        "speciality")
        .value =
        data.speciality || "";

    document.getElementById(
        "tuitionFeeInd")
        .value =
        data.tuitionFeeInd;
}

function updateDnb() {

    clearMessages();

    const id =
        document.getElementById(
            "searchId")
            .value
            .trim();

    if (!validateForm()) {

        return;
    }

    const dto = {

        id: parseInt(id),

        name:
            document.getElementById(
                "name")
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
                getValue(
                    "empStatus")),

        bankCd:
            getValue("bankCd"),

        bankAcno:
            document.getElementById(
                "bankAcno")
                .value
                .trim(),

        pan:
            document.getElementById(
                "pan")
                .value
                .trim(),

        catg:
            parseInteger(
                getValue(
                    "catg")),

        speciality:
            document.getElementById(
                "speciality")
                .value
                .trim(),

        tuitionFeeInd:
            parseInteger(
                getValue(
                    "tuitionFeeInd"))
    };

    fetch("/api/dnb/" + id, {

        method: "PUT",

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

            showSuccess(
                "DNB Record updated successfully");
        })

        .catch(error => {

            showError(
                error.message
                || "Unable to update DNB Record");
        });
}

function loadCategories() {

    fetch("/api/categories/logged-in-user")

        .then(response => response.json())

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
        });
}

function validateForm() {

    if (isBlank("name")) {

        showError(
            "Name is mandatory");

        return false;
    }

    if (!validatePan()) {

        return false;
    }

    if (!validateAccount()) {

        return false;
    }

    return true;
}

function validatePan() {

    const pan =
        document.getElementById(
            "pan")
            .value
            .trim()
            .toUpperCase();

    const regex =
        /^[A-Z]{5}[0-9]{4}[A-Z]$/;

    if (!regex.test(pan)) {

        showError(
            "PAN should be in format ABCDE1234F");

        return false;
    }

    return true;
}

function validateAccount() {

    const account =
        document.getElementById(
            "bankAcno")
            .value
            .trim();

    const regex =
        /^\d{9,17}$/;

    if (!regex.test(account)) {

        showError(
            "Bank Account Number should contain 9 to 17 digits");

        return false;
    }

    return true;
}

function initializePanField() {

    document.getElementById(
        "pan")
        .addEventListener(
            "input",
            function () {

                this.value =
                    this.value.toUpperCase();
            });

    document.getElementById(
        "bankAcno")
        .addEventListener(
            "input",
            function () {

                this.value =
                    this.value.replace(
                        /\D/g,
                        "");
            });
}

function formatDate(date) {

    if (!date) {

        return "";
    }

    return date.substring(0, 10);
}

function getValue(id) {

    return document.getElementById(
        id).value;
}

function parseInteger(value) {

    if (!value) {

        return null;
    }

    return parseInt(value);
}

function isBlank(id) {

    return getValue(id)
        .trim() === "";
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

function showSuccess(message) {

    document.getElementById(
        "successPopup")
        .innerHTML =
        message;

    document.getElementById(
        "successPopup")
        .style.display =
        "block";

    window.scrollTo({

        top: 0,

        behavior: "smooth"
    });
}

function showError(message) {

    document.getElementById(
        "errorPopup")
        .innerHTML =
        message;

    document.getElementById(
        "errorPopup")
        .style.display =
        "block";

    window.scrollTo({

        top: 0,

        behavior: "smooth"
    });
}

