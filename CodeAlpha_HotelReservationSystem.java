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
        if (checkIn == null) return;

        LocalDate checkOut = readDate("Enter check-out date (yyyy-MM-dd): ");
        if (checkOut == null) return;

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
    STANDARD(2000.0),
    DELUXE(3500.0),
    SUITE(6000.0);

    private final double basePrice;

    RoomCategory(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public static RoomCategory fromString(String value) {
        return RoomCategory.valueOf(value.trim().toUpperCase());
    }
}

enum ReservationStatus {
    CONFIRMED,
    CANCELLED
}

// A single hotel room; knows how to serialize/deserialize itself for File I/O.
class Room {
    private int roomNumber;
    private RoomCategory category;
    private double pricePerNight;
    private boolean available;

    public Room(int roomNumber, RoomCategory category, double pricePerNight, boolean available) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.available = available;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomCategory getCategory() {
        return category;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String toFileLine() {
        return roomNumber + "|" + category + "|" + pricePerNight + "|" + available;
    }

    public static Room fromFileLine(String line) {
        String[] parts = line.split("\\|");
        int roomNumber = Integer.parseInt(parts[0].trim());
        RoomCategory category = RoomCategory.fromString(parts[1]);
        double price = Double.parseDouble(parts[2].trim());
        boolean available = Boolean.parseBoolean(parts[3].trim());
        return new Room(roomNumber, category, price, available);
    }

    @Override
    public String toString() {
        return String.format("Room #%-4d | %-8s | Rs.%-8.2f/night | %s",
                roomNumber, category, pricePerNight, available ? "AVAILABLE" : "BOOKED");
    }
}

// A booking made against a Room by a guest; also self-serializing for File I/O.
class Reservation {
    private String reservationId;
    private int roomNumber;
    private String guestName;
    private String guestPhone;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalAmount;
    private ReservationStatus status;
    private String transactionId;

    public Reservation(String reservationId, int roomNumber, String guestName, String guestPhone,
            LocalDate checkIn, LocalDate checkOut, double totalAmount,
            ReservationStatus status, String transactionId) {
        this.reservationId = reservationId;
        this.roomNumber = roomNumber;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.status = status;
        this.transactionId = transactionId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public String toFileLine() {
        return reservationId + "|" + roomNumber + "|" + guestName + "|" + guestPhone + "|" +
                checkIn + "|" + checkOut + "|" + totalAmount + "|" + status + "|" + transactionId;
    }

    public static Reservation fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        return new Reservation(
                p[0],
                Integer.parseInt(p[1]),
                p[2],
                p[3],
                LocalDate.parse(p[4]),
                LocalDate.parse(p[5]),
                Double.parseDouble(p[6]),
                ReservationStatus.valueOf(p[7]),
                p[8]
        );
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %s | Room #%d | Guest: %s (%s) | %s -> %s (%d nights) | Total: Rs.%.2f | Status: %s | Txn: %s",
                reservationId, roomNumber, guestName, guestPhone, checkIn, checkOut,
                getNights(), totalAmount, status, transactionId);
    }
}

// =====================================================================
// PAYMENT SIMULATION
// =====================================================================

class PaymentResult {
    private final boolean success;
    private final String transactionId;
    private final String message;

    public PaymentResult(boolean success, String transactionId, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getMessage() {
        return message;
    }
}

// Simulates a payment gateway - no real money or network calls involved.
class PaymentSimulator {

    private static final Random RANDOM = new Random();

    public PaymentResult processPayment(double amount, String method) {
        System.out.println("\nConnecting to payment gateway...");
        simulateDelay();
        System.out.println("Processing " + method + " payment of Rs." + String.format("%.2f", amount) + " ...");
        simulateDelay();

        boolean success = RANDOM.nextInt(100) < 95; // 95% success rate

        if (success) {
            String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            System.out.println("Payment successful! Transaction ID: " + txnId);
            return new PaymentResult(true, txnId, "Payment approved");
        } else {
            System.out.println("Payment declined by gateway. Please try again.");
            return new PaymentResult(false, null, "Payment declined");
        }
    }

    private void simulateDelay() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// =====================================================================
// FILE I/O STORAGE
// =====================================================================

// Handles all reading/writing of Room and Reservation data to disk.
class FileStorageManager {

    private final String roomsFilePath;
    private final String reservationsFilePath;

    public FileStorageManager(String dataDirectory) {
        File dir = new File(dataDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.roomsFilePath = dataDirectory + File.separator + "rooms.txt";
        this.reservationsFilePath = dataDirectory + File.separator + "reservations.txt";
    }

    public List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        File file = new File(roomsFilePath);

        if (!file.exists()) {
            rooms = seedDefaultRooms();
            saveRooms(rooms);
            return rooms;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    rooms.add(Room.fromFileLine(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading rooms file: " + e.getMessage());
        }
        return rooms;
    }

    public void saveRooms(List<Room> rooms) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(roomsFilePath))) {
            for (Room room : rooms) {
                writer.write(room.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving rooms file: " + e.getMessage());
        }
    }

    private List<Room> seedDefaultRooms() {
        List<Room> rooms = new ArrayList<>();
        int roomNumber = 101;
        for (int i = 0; i < 4; i++) {
            rooms.add(new Room(roomNumber++, RoomCategory.STANDARD, RoomCategory.STANDARD.getBasePrice(), true));
        }
        for (int i = 0; i < 3; i++) {
            rooms.add(new Room(roomNumber++, RoomCategory.DELUXE, RoomCategory.DELUXE.getBasePrice(), true));
        }
        for (int i = 0; i < 2; i++) {
            rooms.add(new Room(roomNumber++, RoomCategory.SUITE, RoomCategory.SUITE.getBasePrice(), true));
        }
        return rooms;
    }

    public List<Reservation> loadReservations() {
        List<Reservation> reservations = new ArrayList<>();
        File file = new File(reservationsFilePath);

        if (!file.exists()) {
            return reservations;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    reservations.add(Reservation.fromFileLine(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading reservations file: " + e.getMessage());
        }
        return reservations;
    }

    public void saveReservations(List<Reservation> reservations) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reservationsFilePath))) {
            for (Reservation r : reservations) {
                writer.write(r.toFileLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving reservations file: " + e.getMessage());
        }
    }
}

// =====================================================================
// BUSINESS LOGIC
// =====================================================================

class HotelService {

}
