document.addEventListener(
    "DOMContentLoaded",
    function () {

        setPreviousMonth();

        loadStatus();

        document.getElementById(
            "btnProceed")
            .addEventListener(
                "click",
                createStipend);

    });

function loadStatus() {

    fetch("/api/stipend/access")

        .then(response => response.json())

        .then(data => {

            document.getElementById(
                "yymm")
                .value =
                data.previousMonth;

            const btn =
                document.getElementById(
                    "btnProceed");

            if (!data.authorized) {

                btn.style.display =
                    "none";

                showError(
                    "You are not authorized.");

                return;
            }

            if (data.paybillGenerated) {

                btn.disabled =
                    true;

                showError(
                    "Stipend already generated for this month.");

            } else {

                btn.disabled =
                    false;
            }

        })

        .catch(error => {

            showError(
                "Unable to load status.");

        });
}

function createStipend() {

    const btn =
        document.getElementById(
            "btnProceed");

    btn.disabled = true;

    fetch("/api/stipend/create", {

        method: "POST"

    })

    .then(response => {

        if (!response.ok) {

            throw new Error(
                "Unable to create stipend.");
        }

        return response.text();

    })

    .then(message => {

        showSuccess(message);

        loadStatus();

    })

    .catch(error => {

        btn.disabled = false;

        showError(error.message);

    });

}

function showSuccess(message) {

    document.getElementById("errorPopup")
            .style.display = "none";

    document.getElementById("successPopup")
            .innerHTML = message;

    document.getElementById("successPopup")
            .style.display = "block";
}

function showError(message) {

    document.getElementById("successPopup")
            .style.display = "none";

    document.getElementById("errorPopup")
            .innerHTML = message;

    document.getElementById("errorPopup")
            .style.display = "block";
}

function setPreviousMonth() {

    const date = new Date();

    date.setMonth(date.getMonth() - 1);

    const yymm =
        date.getFullYear().toString() +
        String(date.getMonth() + 1).padStart(2, "0");

    document.getElementById("yymm").value = yymm;
}