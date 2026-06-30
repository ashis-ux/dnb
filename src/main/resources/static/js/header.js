 
document.addEventListener(
    "DOMContentLoaded",
    function() {

        console.log(
            "DNB Home Screen Loaded");

        loadStipendMenu();
		
		loadResetMenu();
		
		loadPostIntoSAPMenu();
		
		loadLoggedInUser();
    });
	
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

    fetch(BASE_URL + "/api/reset-pay/status")

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

    fetch(BASE_URL + "/api/post-sap/status")

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