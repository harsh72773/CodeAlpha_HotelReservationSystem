# 🏨 Hotel Reservation System

A console-based Hotel Reservation System developed in Java as part of the **CodeAlpha Java Development Internship**. This project allows users to search, book, and manage hotel room reservations while demonstrating Object-Oriented Programming (OOP), File Handling, Collections, and Exception Handling concepts.

---

## 📌 Features

- 🔍 Search available hotel rooms
- 🏷️ Room categorization:
  - Standard
  - Deluxe
  - Suite
- 🛏️ Book hotel rooms
- ❌ Cancel reservations
- 📋 View booking details
- 📑 View all reservations
- 💳 Simulated payment processing
- 🆔 Automatic Reservation ID and Transaction ID generation
- 📅 Check-in and Check-out date validation
- 💾 File-based storage for rooms and reservations
- ⚠️ Input validation and exception handling

---

## 🛠️ Technologies Used

- Java
- Object-Oriented Programming (OOP)
- Java Collections Framework
- File I/O
- Exception Handling
- LocalDate API
- UUID Generation

---

## 📂 Project Structure

```
HotelReservationSystem/
│
├── CodeAlpha_HotelReservationSystem.java
│
└── data/
    ├── rooms.txt
    └── reservations.txt
```

## 📋 Application Menu

```
--------------- MAIN MENU ---------------

1. Search Rooms
2. Book a Room
3. Cancel a Reservation
4. View Booking Details
5. View All Reservations
6. Exit
```

---

## 🏷️ Room Categories

| Category | Price/Night |
|-----------|------------:|
| Standard | ₹2,000 |
| Deluxe | ₹3,500 |
| Suite | ₹6,000 |

---

## 💳 Booking Workflow

1. Search available rooms.
2. Select a room number.
3. Enter guest information.
4. Choose check-in and check-out dates.
5. Select a payment method (CARD / UPI / CASH).
6. Simulated payment is processed.
7. Reservation is confirmed and stored in a file.

---

## 📁 Data Storage

The application stores data locally using text files.

### rooms.txt

Stores:
- Room Number
- Category
- Price
- Availability Status

### reservations.txt

Stores:
- Reservation ID
- Guest Details
- Room Number
- Stay Duration
- Total Amount
- Reservation Status
- Transaction ID

This allows reservation data to persist even after closing the application.

---

## 📚 OOP Concepts Used

- Classes & Objects
- Encapsulation
- Enums
- Composition
- Abstraction
- Collections
- Modular Design
