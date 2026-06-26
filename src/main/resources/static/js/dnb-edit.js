const BASE_URL = "";

document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadCategories();

        initializePanField();
    });

document.getElementById(
    "mobileNo")
    .addEventListener(
        "input",
        function () {

            this.value =
                this.value.replace(
                    /\D/g,
                    "");

            if (this.value.length > 10) {

                this.value =
                    this.value.substring(
                        0,
                        10);
            }
        });

function searchDnb() {

    clearMessages();

    const value =
        document.getElementById(
            "searchId")
            .value
            .trim();

    if (!value) {

        showError(
            "Please enter DNB ID or PAN");

        return;
    }

    fetch(
        BASE_URL + "/api/dnb/search?value="
        + encodeURIComponent(value))

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

function updateDnb() {

    clearMessages();

    const id =
        document.getElementById(
            "dnbId")
            .value;

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
                getValue("empStatus")),

        mobileNo:
            document.getElementById(
                "mobileNo")
                .value
                .trim(),

        emailId:
            document.getElementById(
                "emailId")
                .value
                .trim(),

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
                getValue("catg")),

        speciality:
            document.getElementById(
                "speciality")
                .value
                .trim(),

        tuitionFeeInd:
            parseInteger(
                getValue("tuitionFeeInd"))
    };

    fetch(BASE_URL + "/api/dnb/" + id, {

        method: "PUT",

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

    fetch(BASE_URL + "/api/categories/logged-in-user")

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