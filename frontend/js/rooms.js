const API_BASE = (() => {
    if (window.location.protocol === "file:") {
        return "http://localhost:8080/api";
    }

    const port = window.location.port;
    if (port === "8080") {
        return `${window.location.protocol}//${window.location.host}/api`;
    }

    return `${window.location.protocol}//${window.location.hostname}:8080/api`;
})();

const roomsGrid = document.getElementById("roomsGrid");
const roomsFilter = document.getElementById("roomsFilter");
const checkInInput = document.getElementById("checkIn");
const checkOutInput = document.getElementById("checkOut");
const guestsInput = document.getElementById("guestsInput");
const emptyState = document.getElementById("emptyState");
const resultsText = document.getElementById("resultsText");

const roomImages = {
    STANDARD: "https://images.unsplash.com/photo-1631049307264-da0ec9d70304",
    DELUXE: "https://images.unsplash.com/photo-1566665797739-1674de7a421a",
    SUITE: "https://images.unsplash.com/photo-1578683010236-d716f9a3f461",
    PRESIDENTIAL: "https://images.unsplash.com/photo-1590490360182-c33d57733427"
};

const state = {
    page: 0,
    size: 12
};

function getToken() {
    return localStorage.getItem("token")
        || localStorage.getItem("jwt")
        || localStorage.getItem("authToken");
}

function getHeaders(includeJson = false) {
    const headers = {};
    if (includeJson) {
        headers["Content-Type"] = "application/json";
    }

    const token = getToken();
    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    return headers;
}

function toIsoDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

function addDays(date, days) {
    const result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
}

function initializeDates() {
    const today = new Date();
    const tomorrow = addDays(today, 1);
    const dayAfterTomorrow = addDays(today, 2);

    checkInInput.min = toIsoDate(today);
    checkOutInput.min = toIsoDate(tomorrow);

    const params = new URLSearchParams(window.location.search);
    checkInInput.value = params.get("checkIn") || toIsoDate(tomorrow);
    checkOutInput.value = params.get("checkOut") || toIsoDate(dayAfterTomorrow);
    guestsInput.value = params.get("guests") || "2";
}

function formatRoomType(type) {
    if (!type) {
        return "Room";
    }
    return type.charAt(0) + type.slice(1).toLowerCase();
}

function formatPrice(price) {
    const numericPrice = Number(price);
    if (!Number.isFinite(numericPrice)) {
        return "Price unavailable";
    }
    return new Intl.NumberFormat(undefined, {
        style: "currency",
        currency: "EUR"
    }).format(numericPrice);
}

async function readError(response) {
    try {
        const body = await response.json();
        if (typeof body.message === "string") {
            return body.message;
        }
        if (typeof body === "object") {
            const messages = Object.values(body).filter(value => typeof value === "string");
            if (messages.length) {
                return messages.join(" ");
            }
        }
    } catch {
        // Use the status text when the backend does not return JSON.
    }
    return response.statusText || "Unable to load rooms.";
}

async function apiRequest(url, options = {}) {
    const response = await fetch(url, options);
    if (response.status === 401) {
        throw new Error("Please sign in to view rooms and make a booking.");
    }
    if (!response.ok) {
        throw new Error(await readError(response));
    }
    return response.status === 204 ? null : response.json();
}

function createElement(tagName, className, text) {
    const element = document.createElement(tagName);
    if (className) {
        element.className = className;
    }
    if (text !== undefined) {
        element.textContent = text;
    }
    return element;
}

function createRoomCard(room) {
    const card = createElement("article", "room-card");

    const image = createElement("img", "room-card-image");
    image.src = `${roomImages[room.type] || roomImages.STANDARD}?auto=format&fit=crop&w=900&q=80`;
    image.alt = `${formatRoomType(room.type)} Room ${room.roomNumber}`;
    image.loading = "lazy";
    card.appendChild(image);

    const content = createElement("div", "room-card-content");
    content.appendChild(createElement("p", "room-type", `${formatRoomType(room.type)} room`));
    content.appendChild(createElement("h3", null, `Room ${room.roomNumber}`));
    content.appendChild(createElement(
        "p",
        "room-description",
        `Comfortable ${formatRoomType(room.type).toLowerCase()} accommodation for your stay.`
    ));

    const details = createElement("div", "room-card-details");
    details.appendChild(createElement("span", null, `Capacity: ${room.capacity} guest${room.capacity === 1 ? "" : "s"}`));
    details.appendChild(createElement("span", null, `Room number: ${room.roomNumber}`));
    content.appendChild(details);

    const footer = createElement("div", "room-card-footer");
    const price = createElement("div", "room-price", formatPrice(room.price));
    price.appendChild(createElement("span", null, "per night"));
    footer.appendChild(price);

    const bookButton = createElement("button", "room-book-button", "Book now");
    bookButton.type = "button";
    bookButton.addEventListener("click", () => bookRoom(room));
    footer.appendChild(bookButton);
    content.appendChild(footer);

    card.appendChild(content);
    return card;
}

function updateDateLimits() {
    if (!checkInInput.value) {
        return;
    }

    const checkIn = new Date(`${checkInInput.value}T00:00:00`);
    checkOutInput.min = toIsoDate(addDays(checkIn, 1));
    if (!checkOutInput.value || checkOutInput.value <= checkInInput.value) {
        checkOutInput.value = toIsoDate(addDays(checkIn, 1));
    }
}

async function loadRooms() {
    const checkIn = checkInInput.value;
    const checkOut = checkOutInput.value;
    const guests = Number(guestsInput.value);

    if (!checkIn || !checkOut) {
        emptyState.hidden = false;
        roomsGrid.replaceChildren();
        resultsText.textContent = "Choose check-in and check-out dates.";
        return;
    }

    if (checkOut <= checkIn) {
        emptyState.hidden = false;
        roomsGrid.replaceChildren();
        resultsText.textContent = "Check-out must be after check-in.";
        return;
    }

    roomsGrid.replaceChildren();
    resultsText.textContent = "Loading available rooms...";
    emptyState.hidden = true;

    const params = new URLSearchParams({
        checkIn,
        checkOut,
        page: String(state.page),
        size: String(state.size),
        sort: "roomNumber,asc"
    });

    try {
        const page = await apiRequest(`${API_BASE}/rooms/available?${params}`, {
            headers: getHeaders()
        });

        let rooms = page.content || [];
        rooms = rooms.filter(room => room.capacity >= guests);

        roomsGrid.replaceChildren();

        if (rooms.length === 0) {
            emptyState.hidden = false;
            resultsText.textContent = `No rooms available for ${guests} guest${guests === 1 ? "" : "s"}.`;
            return;
        }

        rooms.forEach(room => roomsGrid.appendChild(createRoomCard(room)));
        resultsText.textContent = `${page.totalElements} room${page.totalElements === 1 ? "" : "s"} available for ${guests} guest${guests === 1 ? "" : "s"}.`;
    } catch (error) {
        roomsGrid.replaceChildren();
        emptyState.hidden = false;
        resultsText.textContent = error.message;
    }
}

async function bookRoom(room) {
    if (!getToken()) {
        window.location.href = `login.html?redirect=${encodeURIComponent(window.location.href)}`;
        return;
    }

    const guests = Number(guestsInput.value);
    if (guests > room.capacity) {
        alert(`This room accommodates up to ${room.capacity} guests.`);
        return;
    }

    try {
        await apiRequest(`${API_BASE}/bookings`, {
            method: "POST",
            headers: getHeaders(true),
            body: JSON.stringify({
                checkIn: checkInInput.value,
                checkOut: checkOutInput.value,
                numberOfGuests: guests,
                roomId: room.id
            })
        });

        window.location.href = "bookings.html";
    } catch (error) {
        if (error.message.includes("sign in")) {
            window.location.href = `login.html?redirect=${encodeURIComponent(window.location.href)}`;
            return;
        }
        alert(error.message);
    }
}

roomsFilter.addEventListener("submit", event => {
    event.preventDefault();
    if (!roomsFilter.reportValidity()) {
        return;
    }

    updateDateLimits();
    state.page = 0;

    const params = new URLSearchParams({
        checkIn: checkInInput.value,
        checkOut: checkOutInput.value,
        guests: guestsInput.value
    });
    history.replaceState(null, "", `${window.location.pathname}?${params}`);
    loadRooms();
});

checkInInput.addEventListener("change", updateDateLimits);

initializeDates();
updateDateLimits();
loadRooms();