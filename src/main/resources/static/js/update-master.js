const API_BASE = "/api";

document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadPreviousMonth();
    }
);

/* ---------------------------
   Load Previous Month
---------------------------- */

function loadPreviousMonth() {

    const today = new Date();

    today.setMonth(today.getMonth() - 1);

    const year = today.getFullYear();

    const month = String(today.getMonth() + 1).padStart(2, "0");

    document.getElementById("yymm").value = year + month;
}

/* ---------------------------
   Update Master
---------------------------- */

function updateMaster() {

    clearMessages();

    let yymm =
        document.getElementById("yymm").value;

    fetch(`${API_BASE}/dnb/runMonthlyUpdate/${yymm}`, {
        method: "POST"
    })

        .then(async response => {

            const data = await response.json();

            if (!response.ok) {

                throw new Error(
                    data.exception ||
                    data.statusMsg ||
                    "Update failed"
                );
            }

            return data;
        })

        .then(() => {

            showSuccess("DNB Master updated successfully");
        })

        .catch(error => {

            showError(error.message);
        });
}

/* ---------------------------
   Messages
---------------------------- */

function showSuccess(message) {

    document.getElementById("successPopup").innerHTML = message;
    document.getElementById("successPopup").style.display = "block";

    document.getElementById("errorPopup").style.display = "none";
}

function showError(message) {

    document.getElementById("errorPopup").innerHTML = message;
    document.getElementById("errorPopup").style.display = "block";

    document.getElementById("successPopup").style.display = "none";
}

function clearMessages() {

    document.getElementById("successPopup").style.display = "none";
    document.getElementById("errorPopup").style.display = "none";
}