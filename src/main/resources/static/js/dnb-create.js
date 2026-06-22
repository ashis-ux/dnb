
document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadCategories();

		storepaninuppercase();

        showSessionMessage();
    });
	
	
	
	function storepaninuppercase() {
		document.getElementById("pan")
		            .addEventListener("input", function () {

		                this.value = this.value.toUpperCase();
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

 

function saveDnb() {

    clearMessages();

    if (!validateForm()) {

        return;
    }

    const dto = {

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
				
				mobileNo:
				       document.getElementById("mobileNo")
				           .value
				           .trim(),

				   // NEW
				   emailId:
				       document.getElementById("emailId")
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
                getValue(
                    "tuitionFeeInd"))
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

                "DNB Entry created successfully. "
                + "ID : "
                + data.id
            );

            window.location.reload();
        })

        .catch(error => {

            showError(

                error.message
                || "Unable to save DNB entry");
        });
}

function validateForm() {

    if (isBlank("name")) {

        showError(
            "Name is mandatory");

        return false;
    }

    if (isBlank("doj")) {

        showError(
            "DOJ is mandatory");

        return false;
    }
	
	if (isBlank("dos")) {

	       showError(
	           "DOS is mandatory");

	       return false;
	   }


    if (isBlank("sexCode")) {

        showError(
            "Gender is mandatory");

        return false;
    }

    if (isBlank("pan")) {

        showError(
            "PAN Number is mandatory");

        return false;
    }

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
    if (isBlank("bankCd")) {

        showError(
            "Bank Code is mandatory");

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
            "Mobile Number should contain 10 digits");

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
            "PAN should be in format ABCDE1234F");

        return false;
    }

    return true;
}

function validateAccount() {

    const account =
        document.getElementById("bankAcno")
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
 
