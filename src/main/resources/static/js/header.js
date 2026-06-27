

document.addEventListener(
    "DOMContentLoaded",
    function() {

        console.log(
            "DNB Home Screen Loaded");

        loadStipendMenu();
    });

function loadStipendMenu() {

    fetch(BASE_URL + "/api/stipend/access")

        .then(response => {

            if (!response.ok) {

                throw new Error("Unable to load access.");
            }

            return response.json();
        })

        .then(data => {

            const mobileMenu =
                document.getElementById("mobileStipendMenu");

            if (data.authorized) {

                if (mobileMenu) {
                    mobileMenu.style.display = "block";
                }

            } else {

                if (mobileMenu) {
                    mobileMenu.style.display = "none";
                }
            }
        })

        .catch(error => {

            console.error(error);
        });
}