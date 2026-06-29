

document.addEventListener(
    "DOMContentLoaded",
    function() {

        console.log(
            "DNB Home Screen Loaded");

        loadStipendMenu();
		
		loadResetMenu();
		
		loadPostIntoSAPMenu();
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


function loadResetMenu() {

    fetch(BASE_URL + "/api/reset-pay/access")

        .then(response => {

            if (!response.ok) {

                throw new Error("Unable to load access.");
            }

            return response.json();
        })

        .then(data => {

            const mobileMenu =
                document.getElementById("mobileResetMenu");

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


function loadPostIntoSAPMenu() {

    fetch(BASE_URL + "/api/post-sap/access")

        .then(response => {

            if (!response.ok) {

                throw new Error("Unable to load access.");
            }

            return response.json();
        })

        .then(data => {

            const mobileMenu =
                document.getElementById("mobilePostIntoSAPMenu");

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