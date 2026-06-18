 
document.addEventListener(
    "DOMContentLoaded",
    function () {

        console.log(
            "DNB Home Screen Loaded");

        initializeMenu();

        initializeExit();
    });

function initializeMenu() {

    const menuLinks =
        document.querySelectorAll(
            ".menu-link");

    menuLinks.forEach(link => {

        link.addEventListener(
            "click",
            function () {

                console.log(
                    this.innerText.trim()
                    + " clicked");
            });
    });
}

function initializeExit() {

    const exitButton =
        document.querySelector(
            ".exit-btn");

    if (exitButton) {

        exitButton.addEventListener(
            "click",
            function (event) {

                event.preventDefault();

                if (confirm(
                    "Are you sure you want to exit?"
                )) {

                    /*
                     * Replace with actual logout URL later
                     */

                    window.location.href = "/";
                }
            });
    }
}
 
