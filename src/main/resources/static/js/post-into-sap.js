const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

let confirmModal;

document.addEventListener("DOMContentLoaded", function () {

    confirmModal = new bootstrap.Modal(
        document.getElementById("confirmModal")
    );

    loadStatus();

    document
        .getElementById("btnProceed")
        .addEventListener("click", function () {

            if (!this.disabled) {
                confirmModal.show();
            }

        });

    document
        .getElementById("confirmProceed")
        .addEventListener("click", processPayment);

});

/*
 * Load Status
 */
function loadStatus() {

    hideMessages();

    fetch(BASE_URL + "/api/post-sap/status")

        .then(response => {

            if (!response.ok) {
                throw new Error("Unable to fetch status.");
            }

            return response.json();

        })

        .then(data => {

            const yymmField =
                document.getElementById("yymm");

            if (yymmField) {
                yymmField.value =
                    data.previousMonth ?? "";
            }

            const btn =
                document.getElementById("btnProceed");

            /*
             * Default disabled
             */
            btn.disabled = true;

            /*
             * User not authorised
             */
            if (!data.authorized) {

                showError(
                    data.message ||
                    "You are not authorised to access this option."
                );

                return;
            }

            /*
             * Stipend not generated
             */
            if (!data.paybillGenerated) {

                showError(
                    data.message ||
                    "Create stipend first, then continue."
                );

                return;
            }

            /*
             * SAP payment data not available
             */
            if (data.sapPaymentAvailable) {

                showError(
                    data.message ||
                    "SAP payment data not available."
                );

                return;
            }

            /*
             * Everything OK
             */
            btn.disabled = false;

            hideMessages();

        })

        .catch(error => {

            showError(error.message);

        });

}

/*
 * Process Payment
 */
function processPayment() {

    confirmModal.hide();

    hideMessages();

    const btn =
        document.getElementById("btnProceed");

    btn.disabled = true;

    fetch(BASE_URL + "/api/post-sap/process", {

        method: "POST"

    })

        .then(response => {

            if (!response.ok) {

                return response.json()
                    .then(error => {

                        throw new Error(
                            error.message ||
                            "Unable to process payment."
                        );

                    });

            }

            return response.text();

        })

        .then(message => {

            showSuccess(message);

            /*
             * Refresh status after success
             */
            setTimeout(function () {

                loadStatus();

            }, 1500);

        })

        .catch(error => {

            showError(error.message);

            loadStatus();

        });

}

/*
 * Success Message
 */
function showSuccess(message) {

    document.getElementById("errorPopup").style.display = "none";

    document.getElementById("successPopup").innerHTML = message;

    document.getElementById("successPopup").style.display = "block";

}

/*
 * Error Message
 */
function showError(message) {

    document.getElementById("successPopup").style.display = "none";

    document.getElementById("errorPopup").innerHTML = message;

    document.getElementById("errorPopup").style.display = "block";

}

/*
 * Hide Messages
 */
function hideMessages() {

    document.getElementById("successPopup").style.display = "none";

    document.getElementById("errorPopup").style.display = "none";

}