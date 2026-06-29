const BASE_URL = (window.contextPath || "").replace(/\/$/, "");
document.addEventListener(
    "DOMContentLoaded",
    function() {

        loadPreviousMonth();

        document
            .getElementById("btnConfirmUpdate")
            .addEventListener(
                "click",
                function() {

                    bootstrap.Modal
                        .getInstance(
                            document.getElementById(
                                "confirmUpdateModal"))
                        .hide();

                    updateMaster();

                });

    });
function loadPreviousMonth() {

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

function updateMaster() {

    clearMessages();

    let yymm =
        document.getElementById(
            "yymm")
            .value;

    fetch(BASE_URL + "/api/dnb/runMonthlyUpdate/" + yymm, {
        method: "POST"
    })

        .then(async response => {

            const data =
                await response.json();

            if (!response.ok) {

                throw new Error(
                    data.exception ||
                    data.statusMsg);
            }

            return data;
        })
        .then(data => {

            showSuccess(
                "DNB Master updated successfully");
        })
        .catch(error => {

            showError(
                error.message);
        });
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