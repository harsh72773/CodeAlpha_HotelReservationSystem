import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CodeAlpha_HotelReservationSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String args[]) {
        FileStorageManager storageManager = new FileStorageManager("data");
        HotelService hotelService = new HotelService(storageManager);

        System.out.println("=================================================");
        System.out.println("     WELCOME TO THE HOTEL RESERVATION SYSTEM");
        System.out.println("=================================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    searchRooms(hotelService);
                    break;
                case "2":
                    bookRoom(hotelService);
                    break;
                case "3":
                    cancelReservation(hotelService);
                    break;
                case "4":
                    viewReservation(hotelService);
                    break;
                case "5":
                    viewAllReservations(hotelService);
                    break;
                case "6":
                    running = false;
                    System.out.println("Thank you for using the Hotel Reservation System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--------------- MAIN MENU ---------------");
        System.out.println("1. Search Rooms");
        System.out.println("2. Book a Room");
        System.out.println("3. Cancel a Reservation");
        System.out.println("4. View Booking Details");
        System.out.println("5. View All Reservations");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    // ---------- MENU ACTIONS ----------

    private static void searchRooms(HotelService hotelService) {
        System.out.println("\nSearch by:");
        System.out.println("1. All available rooms");
        System.out.println("2. Filter by category (STANDARD / DELUXE / SUITE)");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        List<Room> results;
        if (choice.equals("2")) {
            System.out.print("Enter category (STANDARD, DELUXE, SUITE): ");
            String catInput = scanner.nextLine().trim();
            try {
                RoomCategory category = RoomCategory.fromString(catInput);
                results = hotelService.searchAvailableRoomsByCategory(category);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid category entered.");
                return;
            }
        } else {
            results = hotelService.searchAvailableRooms();
        }

        if (results.isEmpty()) {
            System.out.println("No available rooms found.");
        } else {
            System.out.println("\nAvailable Rooms:");
            for (Room room : results) {
                System.out.println("  " + room);
            }
        }
    }

    private static void bookRoom(HotelService hotelService) {
        System.out.print("\nEnter room number to book: ");
        int roomNumber;
        try {
            roomNumber = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid room number.");
            return;
        }

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine().trim();

        LocalDate checkIn = readDate("Enter check-in date (yyyy-MM-dd): ");
        if (checkIn == null)
            return;

        LocalDate checkOut = readDate("Enter check-out date (yyyy-MM-dd): ");
        if (checkOut == null)
            return;

        System.out.print("Enter payment method (CARD / UPI / CASH): ");
        String paymentMethod = scanner.nextLine().trim();

        HotelService.BookingResult result = hotelService.bookRoom(
                roomNumber, name, phone, checkIn, checkOut, paymentMethod);

        System.out.println("\n" + result.message);
        if (result.success) {
            System.out.println("Booking Details:");
            System.out.println("  " + result.reservation);
        }
    }

    private static void cancelReservation(HotelService hotelService) {
        System.out.print("\nEnter reservation ID to cancel: ");
        String reservationId = scanner.nextLine().trim();
        String message = hotelService.cancelReservation(reservationId);
        System.out.println(message);
    }

    private static void viewReservation(HotelService hotelService) {
        System.out.print("\nEnter reservation ID to view: ");
        String reservationId = scanner.nextLine().trim();
        hotelService.viewReservation(reservationId).ifPresentOrElse(
                r -> System.out.println("\nBooking Details:\n  " + r),
                () -> System.out.println("No reservation found with ID " + reservationId));
    }

    private static void viewAllReservations(HotelService hotelService) {
        List<Reservation> all = hotelService.getAllReservations();
        if (all.isEmpty()) {
            System.out.println("\nNo reservations have been made yet.");
            return;
        }
        System.out.println("\nAll Reservations:");
        for (Reservation r : all) {
            System.out.println("  " + r);
        }
    }

    private static LocalDate readDate(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return LocalDate.parse(input, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            return null;
        }
    }
}

// =====================================================================
// MODEL CLASSES
// =====================================================================

// Categories of room offered, each with a default base price/night.
enum RoomCategory {
}

enum ReservationStatus {
    CONFIRMED,
    CANCELLED
}

// A single hotel room; knows how to serialize/deserialize itself for File I/O.
class Room {

}

class Reservation {

}

class PaymentResult {

}

class PaymentSimulator {

}

class FileStorageManager {

}

// =====================================================================
// BUSINESS LOGIC
// =====================================================================

class HotelService {

}
