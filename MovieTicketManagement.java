import java.util.ArrayList;
import java.util.Scanner;

public class MovieTicketManagement {

    // Movie class
    static class Movie {
        int id;
        String name;
        String timing;
        int totalSeats;
        int availableSeats;
        double ticketPrice;

        Movie(int id, String name, String timing, int totalSeats, double ticketPrice) {
            this.id = id;
            this.name = name;
            this.timing = timing;
            this.totalSeats = totalSeats;
            this.availableSeats = totalSeats;
            this.ticketPrice = ticketPrice;
        }

        void display() {
            System.out.println("ID: " + id + " | Movie: " + name +
                " | Timing: " + timing +
                " | Available Seats: " + availableSeats + "/" + totalSeats +
                " | Price: Rs." + ticketPrice);
        }
    }

    // Ticket/Booking class
    static class Booking {
        int bookingId;
        String customerName;
        Movie movie;
        int seats;
        double totalAmount;

        Booking(int bookingId, String customerName, Movie movie, int seats) {
            this.bookingId = bookingId;
            this.customerName = customerName;
            this.movie = movie;
            this.seats = seats;
            this.totalAmount = seats * movie.ticketPrice;
        }

        void display() {
            System.out.println("Booking ID: " + bookingId +
                " | Customer: " + customerName +
                " | Movie: " + movie.name +
                " | Timing: " + movie.timing +
                " | Seats: " + seats +
                " | Total: Rs." + totalAmount);
        }
    }

    static ArrayList<Movie> movies = new ArrayList<>();
    static ArrayList<Booking> bookings = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static int nextMovieId = 1;
    static int nextBookingId = 101;

    public static void main(String[] args) {
        loadMovies();
        int choice;

        do {
            System.out.println("\n===== Movie Ticket Management System =====");
            System.out.println("1. View All Movies");
            System.out.println("2. Book Ticket");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View All Bookings");
            System.out.println("5. Search Booking by ID");
            System.out.println("6. Add New Movie");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> viewMovies();
                case 2 -> bookTicket();
                case 3 -> cancelBooking();
                case 4 -> viewBookings();
                case 5 -> searchBooking();
                case 6 -> addMovie();
                case 7 -> System.out.println("Thank you! Goodbye!");
                default -> System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 7);
    }

    // Load some sample movies
    static void loadMovies() {
        movies.add(new Movie(nextMovieId++, "Leo",         "10:00 AM", 50, 150.0));
        movies.add(new Movie(nextMovieId++, "Jawan",       "01:00 PM", 50, 180.0));
        movies.add(new Movie(nextMovieId++, "Oppenheimer", "04:00 PM", 40, 200.0));
        movies.add(new Movie(nextMovieId++, "Animal",      "07:00 PM", 60, 170.0));
    }

    static void viewMovies() {
        if (movies.isEmpty()) {
            System.out.println("No movies available.");
            return;
        }
        System.out.println("\n--- Available Movies ---");
        for (Movie m : movies) {
            m.display();
        }
    }

    static void bookTicket() {
        viewMovies();
        System.out.print("\nEnter Movie ID to book: ");
        int movieId = sc.nextInt();
        sc.nextLine();

        Movie selected = null;
        for (Movie m : movies) {
            if (m.id == movieId) {
                selected = m;
                break;
            }
        }

        if (selected == null) {
            System.out.println("Movie not found.");
            return;
        }

        System.out.print("Enter Customer Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Number of Seats: ");
        int seats = sc.nextInt();
        sc.nextLine();

        if (seats <= 0) {
            System.out.println("Invalid seat count.");
            return;
        }

        if (seats > selected.availableSeats) {
            System.out.println("Not enough seats. Available: " + selected.availableSeats);
            return;
        }

        selected.availableSeats -= seats;
        Booking b = new Booking(nextBookingId++, name, selected, seats);
        bookings.add(b);

        System.out.println("\n Booking Confirmed!");
        System.out.println("---------------------------");
        b.display();
        System.out.println("---------------------------");
    }

    static void cancelBooking() {
        System.out.print("Enter Booking ID to cancel: ");
        int id = sc.nextInt();
        sc.nextLine();

        for (Booking b : bookings) {
            if (b.bookingId == id) {
                b.movie.availableSeats += b.seats;
                bookings.remove(b);
                System.out.println("Booking ID " + id + " cancelled. Seats released.");
                return;
            }
        }
        System.out.println("Booking not found.");
    }

    static void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        System.out.println("\n--- All Bookings ---");
        for (Booking b : bookings) {
            b.display();
        }
    }

    static void searchBooking() {
        System.out.print("Enter Booking ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        for (Booking b : bookings) {
            if (b.bookingId == id) {
                System.out.println("\n--- Booking Details ---");
                b.display();
                return;
            }
        }
        System.out.println("Booking not found.");
    }

    static void addMovie() {
        System.out.print("Enter Movie Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Timing (e.g. 06:00 PM): ");
        String timing = sc.nextLine();
        System.out.print("Enter Total Seats: ");
        int seats = sc.nextInt();
        System.out.print("Enter Ticket Price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        movies.add(new Movie(nextMovieId++, name, timing, seats, price));
        System.out.println("Movie added successfully!");
    }
}
