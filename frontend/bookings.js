const cancelButtons = document.querySelectorAll(".cancel-button");
const bookingsList = document.querySelector(".bookings-list");
const emptyBookings = document.querySelector(".empty-bookings");

cancelButtons.forEach(function(button) {
    button.addEventListener("click", function() {
        const bookingCard = button.closest(".booking-card");

        bookingCard.remove();

        const remainingBookings = document.querySelectorAll(".booking-card");

        if (remainingBookings.length === 0) {
            emptyBookings.style.display = "block";
        }
    });
});