# Hotel Booking System

Backend application for managing hotel rooms, users and bookings.  
The project provides REST API for creating users, managing rooms and making hotel reservations.

## Technologies

- Java 17+
- Spring Boot
- Spring Data JPA (Hibernate)
- PostgreSQL
- Maven
- Swagger / OpenAPI
- JUnit 5 + Mockito

## Features

### Users
- Create new users
- Validate unique username and email
- Store user information and roles

### Rooms
- Create, update and delete rooms
- Get all available rooms
- Filter rooms by type, capacity and price

### Bookings
- Create hotel reservations
- Check room availability for selected dates
- Prevent booking conflicts
- Update booking status
- Cancel bookings
- Validate booking dates

## Database

The application uses PostgreSQL.

Main entities:

- User
- Room
- Booking
  
## API Documentation

After running the application, Swagger UI is available at:

(http://localhost:8080/swagger-ui/index.html)[]


## Running the Project

### 1. Clone repository

```bash
git clone https://github.com/etssu/hotel-booking.git
```

### 2. Configure database

Update application.properties:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hotel_booking
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Before running the application, configure your PostgreSQL database credentials in application.properties.

### 3. Run application

Run the application from your IDE.
