const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

document.addEventListener(
    "DOMContentLoaded",
    function () {

        showSessionMessage();

        loadStatus();

        document.getElementById(
            "btnReset")
            .addEventListener(
                "click",
                openResetConfirmation);

        document.getElementById(
            "confirmResetBtn")
            .addEventListener(
                "click",
                executeReset);

    });

function showSessionMessage() {

    const successMessage =
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

            sessionStorage.removeItem(
                "resetCompleted");

        }, 3000);
    }
}

function loadStatus() {

    fetch(BASE_URL + "/api/reset-pay/status")

        .then(response => {

            if (!response.ok) {

                throw new Error(
                    "Unable to load Reset Pay Data status.");
            }

            return response.json();
        })

        .then(data => {

            document.getElementById(
                "yymm")
                .value =
                data.previousMonth;

            const btn =
                document.getElementById(
                    "btnReset");

            if (!data.authorized) {

                btn.disabled = true;

                btn.classList.add(
                    "disabled");

                showError(
                    data.message);

                return;
            }

            if (!data.paybillExists) {

                btn.disabled = true;

                btn.classList.add(
                    "disabled");

                if (!sessionStorage.getItem(
                    "successMessage")) {

                    const resetCompleted =
                        sessionStorage.getItem(
                            "resetCompleted");

                    if (!resetCompleted) {

                        showError(data.message);
                    }
                }

                return;
            }

            btn.disabled = false;

            btn.classList.remove(
                "disabled");

            document.getElementById(
                "errorPopup")
                .style.display =
                "none";
        })

        .catch(error => {

            showError(
                error.message);

        });
}

function openResetConfirmation() {

    const btn =
        document.getElementById(
            "btnReset");

    if (btn.disabled) {

        return;
    }

    const modal =
        new bootstrap.Modal(
            document.getElementById(
                "resetConfirmModal"));

    modal.show();
}

function executeReset() {

    const modal =
        bootstrap.Modal.getInstance(
            document.getElementById(
                "resetConfirmModal"));

    modal.hide();

    const btn =
        document.getElementById(
            "btnReset");

    btn.disabled = true;

    fetch(BASE_URL + "/api/reset-pay/reset", {

        method: "POST"

    })

        .then(response => {

            if (!response.ok) {

                return response.json()

                    .then(error => {

                        throw new Error(
                            error.message);

                    });
            }

            return response.text();

        })

        .then(message => {

            sessionStorage.setItem(
                "resetCompleted",
                "true");

            sessionStorage.setItem(
                "successMessage",
                message);

            window.location.reload();

        })

        .catch(error => {

            btn.disabled = false;

            showError(
                error.message);

        });
}

function showSuccess(message) {

    document.getElementById(
        "errorPopup")
        .style.display =
        "none";

    document.getElementById(
        "successPopup")
        .innerHTML =
        message;

    document.getElementById(
        "successPopup")
        .style.display =
        "block";

    setTimeout(function () {

        document.getElementById(
            "successPopup")
            .style.display =
            "none";

    }, 3000);
}

function showError(message) {

    document.getElementById(
        "successPopup")
        .style.display =
        "none";

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