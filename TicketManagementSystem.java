import java.time.LocalDate;
import java.util.*;

// ── Ticket Class ──────────────────────────
class Ticket {
    static int counter = 1;
    int id;
    String title, description, status, priority, assignedTo;
    LocalDate createdDate;
    List<String> comments = new ArrayList<>();

    Ticket(String title, String description, String priority) {
        this.id          = counter++;
        this.title       = title;
        this.description = description;
        this.priority    = priority;
        this.status      = "Open";
        this.assignedTo  = "Unassigned";
        this.createdDate = LocalDate.now();
    }

    void addComment(String comment) {
        comments.add(comment);
    }

    void printDetail() {
        System.out.println("┌─────────────────────────────────────");
        System.out.println("│ ID       : " + id);
        System.out.println("│ Title    : " + title);
        System.out.println("│ Desc     : " + description);
        System.out.println("│ Priority : " + priority);
        System.out.println("│ Status   : " + status);
        System.out.println("│ Assigned : " + assignedTo);
        System.out.println("│ Created  : " + createdDate);
        System.out.println("│ Comments : " + (comments.isEmpty() ? "None" : ""));
        comments.forEach(c -> System.out.println("│   - " + c));
        System.out.println("└─────────────────────────────────────");
    }

    public String toString() {
        return String.format("[%d] %-25s | %-8s | %-12s | %s",
            id, title, priority, status, assignedTo);
    }
}

// ── Main System ───────────────────────────
public class TicketManagementSystem {
    static List<Ticket>  tickets = new ArrayList<>();
    static Scanner       sc      = new Scanner(System.in);

    public static void main(String[] args) {
        // Sample data
        tickets.add(new Ticket("Login page broken",     "Users can't log in",         "High"));
        tickets.add(new Ticket("Add dark mode",         "Feature request from users",  "Low"));
        tickets.add(new Ticket("Fix payment gateway",   "Payments failing at checkout","High"));

        while (true) {
            printMenu();
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1" -> listTickets();
                case "2" -> createTicket();
                case "3" -> viewTicket();
                case "4" -> updateStatus();
                case "5" -> assignTicket();
                case "6" -> addComment();
                case "7" -> filterByStatus();
                case "8" -> filterByPriority();
                case "9" -> deleteTicket();
                case "0" -> { System.out.println("Goodbye!"); return; }
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    // ── Menu ──────────────────────────────
    static void printMenu() {
        System.out.println("\n╔══════════════════════════════╗");
        System.out.println("║   TICKET MANAGEMENT SYSTEM   ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║ 1. List all tickets          ║");
        System.out.println("║ 2. Create ticket             ║");
        System.out.println("║ 3. View ticket detail        ║");
        System.out.println("║ 4. Update status             ║");
        System.out.println("║ 5. Assign ticket             ║");
        System.out.println("║ 6. Add comment               ║");
        System.out.println("║ 7. Filter by status          ║");
        System.out.println("║ 8. Filter by priority        ║");
        System.out.println("║ 9. Delete ticket             ║");
        System.out.println("║ 0. Exit                      ║");
        System.out.println("╚══════════════════════════════╝");
    }

    // ── Features ──────────────────────────
    static void listTickets() {
        if (tickets.isEmpty()) { System.out.println("No tickets found."); return; }
        System.out.println("ID  | Title                     | Priority | Status       | Assigned");
        System.out.println("─".repeat(75));
        tickets.forEach(System.out::println);
    }

    static void createTicket() {
        System.out.print("Title       : "); String title = sc.nextLine();
        System.out.print("Description : "); String desc  = sc.nextLine();
        System.out.print("Priority (Low / High) : ");  String pri = sc.nextLine();
        tickets.add(new Ticket(title, desc, pri));
        System.out.println("Ticket created successfully!");
    }

    static void viewTicket() {
        System.out.print("Enter ticket ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Ticket t = findById(id);
        if (t != null) t.printDetail();
        else System.out.println("Ticket not found.");
    }

    static void updateStatus() {
        System.out.print("Enter ticket ID: "); int id = Integer.parseInt(sc.nextLine().trim());
        Ticket t = findById(id);
        if (t == null) { System.out.println("Not found."); return; }
        System.out.print("New status (Open / In Progress / Resolved / Closed): ");
        t.status = sc.nextLine();
        System.out.println("Status updated to: " + t.status);
    }

    static void assignTicket() {
        System.out.print("Enter ticket ID: "); int id = Integer.parseInt(sc.nextLine().trim());
        Ticket t = findById(id);
        if (t == null) { System.out.println("Not found."); return; }
        System.out.print("Assign to (name): ");
        t.assignedTo = sc.nextLine();
        System.out.println("Ticket assigned to: " + t.assignedTo);
    }

    static void addComment() {
        System.out.print("Enter ticket ID: "); int id = Integer.parseInt(sc.nextLine().trim());
        Ticket t = findById(id);
        if (t == null) { System.out.println("Not found."); return; }
        System.out.print("Comment: ");
        t.addComment(sc.nextLine());
        System.out.println("Comment added!");
    }

    static void filterByStatus() {
        System.out.print("Status to filter (Open / In Progress / Resolved / Closed): ");
        String s = sc.nextLine().trim();
        System.out.println("─".repeat(75));
        tickets.stream()
               .filter(t -> t.status.equalsIgnoreCase(s))
               .forEach(System.out::println);
    }

    static void filterByPriority() {
        System.out.print("Priority to filter (Low / High): ");
        String p = sc.nextLine().trim();
        System.out.println("─".repeat(75));
        tickets.stream()
               .filter(t -> t.priority.equalsIgnoreCase(p))
               .forEach(System.out::println);
    }

    static void deleteTicket() {
        System.out.print("Enter ticket ID to delete: "); int id = Integer.parseInt(sc.nextLine().trim());
        boolean removed = tickets.removeIf(t -> t.id == id);
        System.out.println(removed ? "Ticket deleted." : "Ticket not found.");
    }

    static Ticket findById(int id) {
        return tickets.stream().filter(t -> t.id == id).findFirst().orElse(null);
    }
}