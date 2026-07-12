const supportForm = document.getElementById("supportForm");
const supportSuccess = document.getElementById("supportSuccess");

supportForm.addEventListener("submit", function(event) {
    event.preventDefault();

    supportSuccess.style.display = "block";

    supportForm.reset();
});