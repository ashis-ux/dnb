const BASE_URL = (window.contextPath || "").replace(/\/$/, "");

document.addEventListener(
    "DOMContentLoaded",
    function() {

        console.log(
            "DNB Home Screen Loaded");

        initializeMenu();

        loadStipendMenu();

        loadResetMenu();
		
		loadSPostSAPtatus();
		
		loadLoggedInUser();
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

            const desktopMenu =
                document.getElementById("desktopStipendMenu");

            const mobileMenu =
                document.getElementById("mobileStipendMenu");

            if (data.authorized) {

                if (desktopMenu) {
                    desktopMenu.style.display = "block";
                }

                if (mobileMenu) {
                    mobileMenu.style.display = "block";
                }

            } else {

                if (desktopMenu) {
                    desktopMenu.style.display = "none";
                }

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

    fetch(BASE_URL + "/api/reset-pay/status")

        .then(response => {

            if (!response.ok) {

                throw new Error("Unable to load access.");
            }

            return response.json();
        })

        .then(data => {

            const desktopMenu =
                document.getElementById("desktopResetMenu");

            const mobileMenu =
                document.getElementById("mobileStipendMenu");

            if (data.authorized) {

                if (desktopMenu) {
                    desktopMenu.style.display = "block";
                }

                if (mobileMenu) {
                    mobileMenu.style.display = "block";
                }

            } else {

                if (desktopMenu) {
                    desktopMenu.style.display = "none";
                }

                if (mobileMenu) {
                    mobileMenu.style.display = "none";
                }
            }
        })

        .catch(error => {

            console.error(error);
        });
}


function loadSPostSAPtatus() {


    fetch(BASE_URL + "/api/post-sap/status")

	.then(response => {

	            if (!response.ok) {

	                throw new Error("Unable to load access.");
	            }

	            return response.json();
	        })

	        .then(data => {

	            const desktopMenu =
	                document.getElementById("desktopPostSAPMenu");

	            const mobileMenu =
	                document.getElementById("mobilePostSAPMenu");

	            if (data.authorized) {

	                if (desktopMenu) {
	                    desktopMenu.style.display = "block";
	                }

	                if (mobileMenu) {
	                    mobileMenu.style.display = "block";
	                }

	            } else {

	                if (desktopMenu) {
	                    desktopMenu.style.display = "none";
	                }

	                if (mobileMenu) {
	                    mobileMenu.style.display = "none";
	                }
	            }
	        })

	        .catch(error => {

	            console.error(error);
	        });

}



function initializeMenu() {

    const menuLinks =
        document.querySelectorAll(
            ".menu-link");

    menuLinks.forEach(link => {

        link.addEventListener(
            "click",
            function() {

                console.log(
                    this.innerText.trim()
                    + " clicked");
            });
    });
}

 

function loadLoggedInUser() {

    fetch(BASE_URL + "/sso/user")
        .then(response => {

            if (!response.ok) {
                throw new Error("Unable to fetch logged-in user.");
            }

            return response.json();
        })
        .then(data => {

            const userElement = document.getElementById("loggedInUser");

            if (userElement) {
                userElement.textContent = data;
            }
        })
        .catch(error => {

            console.error(error);

            const userElement = document.getElementById("loggedInUser");

            if (userElement) {
                userElement.textContent = "SYSTEM";
            }
        });
}

document.addEventListener("DOMContentLoaded", function () {
    loadLoggedInUser();
});