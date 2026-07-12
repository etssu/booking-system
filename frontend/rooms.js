const roomsFilter = document.getElementById("roomsFilter");
const guestsInput = document.getElementById("guestsInput");
const roomsGrid = document.getElementById("roomsGrid");
const resultsText = document.getElementById("resultsText");

const rooms = [];

function addRooms(type, count, capacity, price, imageUrl) {
    for (let i = 1; i <= count; i++) {
        rooms.push({
            id: rooms.length + 1,
            name: `${type} Room`,
            type: type,
            capacity: capacity,
            price: price,
            imageUrl: imageUrl
        });
    }
}

addRooms(
    "Presidential",
    3,
    6,
    350,
    "https://images.unsplash.com/photo-1590490360182-c33d57733427"
);

addRooms(
    "Suite",
    3,
    2,
    180,
    "https://images.unsplash.com/photo-1578683010236-d716f9a3f461"
);


addRooms(
    "Suite",
    2,
    4,
    220,
    "https://images.unsplash.com/photo-1578683010236-d716f9a3f461"
);

addRooms(
    "Deluxe",
    8,
    2,
    120,
    "https://images.unsplash.com/photo-1566665797739-1674de7a421a"
);


addRooms(
    "Deluxe",
    2,
    3,
    140,
    "https://images.unsplash.com/photo-1566665797739-1674de7a421a"
);

addRooms(
    "Standard",
    15,
    2,
    80,
    "https://images.unsplash.com/photo-1566073771259-6a8506099945"
);


addRooms(
    "Standard",
    5,
    4,
    100,
    "https://images.unsplash.com/photo-1566073771259-6a8506099945"
);

function showRooms(roomList) {
    roomsGrid.innerHTML = "";

    roomList.forEach(function(room) {
        const roomCard = document.createElement("div");
        roomCard.classList.add("room-card");

        roomCard.innerHTML = `
            <img src="${room.imageUrl}" alt="${room.name}">

            <div class="room-content">
                <h3>${room.name}</h3>
                <p>For ${room.capacity} ${room.capacity === 1 ? "guest" : "guests"}</p>
                <p class="room-price">${room.price}€ / night / person</p>
                <button>Book now</button>
            </div>
        `;

        roomsGrid.appendChild(roomCard);
    });
}

showRooms(rooms);

roomsFilter.addEventListener("submit", function(event) {
    event.preventDefault();

    const guests = Number(guestsInput.value);

    if (guests <= 0) {
        showRooms(rooms);
        resultsText.textContent = "All available rooms";
        return;
    }

    const filteredRooms = rooms.filter(function(room) {
        return room.capacity === guests;
    });

    showRooms(filteredRooms);

    if (filteredRooms.length === 0) {
        resultsText.textContent = `No rooms found for exactly ${guests} guests`;
    } else {
        resultsText.textContent = `${filteredRooms.length} rooms found for exactly ${guests} guests`;
    }
});