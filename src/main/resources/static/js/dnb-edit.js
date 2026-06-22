
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
			        "/api/dnb/search?value="
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

function validateDOS(){
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
	                "DOS must be greater than DOJ");

	            return false;
	        }
	    }
		return true;
}

function populateForm(data) {
	
	document.getElementById(
	    "dnbId")
	    .value =
	    data.id;

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
		    "mobileNo")
		    .value =
		    data.mobileNo || "";

		document.getElementById(
		    "emailId")
		    .value =
		    data.emailId || "";

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
		
		setEditableFields();
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
                getValue(
                    "empStatus")),
					
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

function setEditableFields() {

    // Read Only Fields

    document.getElementById("name").disabled = true;

    document.getElementById("sexCode").disabled = true;

    document.getElementById("empStatus").disabled = true;

    document.getElementById("bankCd").readOnly = true;

    document.getElementById("bankAcno").readOnly = true;

    document.getElementById("catg").disabled = true;

    document.getElementById("tuitionFeeInd").disabled = true;

    // Editable Fields

    document.getElementById("dob").readOnly = false;

    document.getElementById("pan").readOnly = false;

    document.getElementById("doj").readOnly = false;

    document.getElementById("dos").readOnly = false;

    document.getElementById("speciality").readOnly = false;
	
	document.getElementById("mobileNo").readOnly = false;

	document.getElementById("emailId").readOnly = false;
}

function validateForm() {
	
	if (isBlank("mobileNo")) {

	    showError(
	        "Mobile Number is mandatory");

	    return false;
	}

	if (isBlank("emailId")) {

	    showError(
	        "Email ID is mandatory");

	    return false;
	}

    if (isBlank("name")) {

        showError(
            "Name is mandatory");

        return false;
    }
	
	if (!validateDOS()) {

		    return false;
		}

    if (!validatePan()) {

        return false;
    }

    if (!validateAccount()) {

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

function validateMobile() {

    const mobile =
        document.getElementById(
            "mobileNo")
            .value
            .trim();

    const regex =
        /^[6-9]\d{9}$/;

    if (!regex.test(mobile)) {

        showError(
            "Mobile Number should contain exactly 10 digits");

        return false;
    }

    return true;
}

function validateEmail() {

    const email =
        document.getElementById(
            "emailId")
            .value
            .trim();

    const regex =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!regex.test(email)) {

        showError(
            "Invalid Email ID");

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

