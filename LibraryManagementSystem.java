
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

// ─────────────────────────────────────────
//  MODEL CLASSES
// ─────────────────────────────────────────

class Book {
    private String id, title, author, isbn;
    private boolean available;

    public Book(String id, String title, String author, String isbn) {
        this.id = id; this.title = title; this.author = author;
        this.isbn = isbn; this.available = true;
    }

    public String getId()        { return id; }
    public String getTitle()     { return title; }
    public String getAuthor()    { return author; }
    public String getIsbn()      { return isbn; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean v) { available = v; }

    @Override
    public String toString() {
        return String.format("  [%-5s] %-35s | %-22s | ISBN: %-15s | %s",
            id, "\"" + title + "\"", author, isbn, available ? "✔ Available" : "✘ Borrowed");
    }
}

// ─────────────────────────────────────────

class Member {
    protected String memberId, name, email;
    protected List<String> borrowedBookIds = new ArrayList<>();

    public Member(String memberId, String name, String email) {
        this.memberId = memberId; this.name = name; this.email = email;
    }

    public String getMemberId()              { return memberId; }
    public String getName()                  { return name; }
    public String getEmail()                 { return email; }
    public List<String> getBorrowedBookIds() { return borrowedBookIds; }
    public void borrowBook(String id)        { borrowedBookIds.add(id); }
    public void returnBook(String id)        { borrowedBookIds.remove(id); }
    public String getRole()                  { return "Member"; }

    @Override
    public String toString() {
        return String.format("  [%-5s] %-20s | %-25s | Role: %-10s | Borrowed: %d book(s)",
            memberId, name, email, getRole(), borrowedBookIds.size());
    }
}

// ─────────────────────────────────────────

class Librarian extends Member {
    private String employeeId;

    public Librarian(String memberId, String name, String email, String employeeId) {
        super(memberId, name, email);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() { return employeeId; }

    @Override
    public String getRole() { return "Librarian"; }

    @Override
    public String toString() {
        return super.toString() + " | EmpID: " + employeeId;
    }
}

// ─────────────────────────────────────────

class Transaction {
    private String transactionId, memberId, bookId;
    private LocalDate borrowDate, dueDate, returnDate;
    private boolean returned;
    private static final int LOAN_DAYS = 14;

    public Transaction(String txId, String memberId, String bookId) {
        this.transactionId = txId; this.memberId = memberId; this.bookId = bookId;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(LOAN_DAYS);
        this.returned = false;
    }

    public String getTransactionId() { return transactionId; }
    public String getMemberId()      { return memberId; }
    public String getBookId()        { return bookId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate()    { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned()      { return returned; }

    public void markReturned() {
        this.returnDate = LocalDate.now();
        this.returned = true;
    }

    @Override
    public String toString() {
        return String.format("  [%-8s] Member: %-5s | Book: %-5s | Borrowed: %s | Due: %s | %s",
            transactionId, memberId, bookId, borrowDate, dueDate,
            returned ? "Returned: " + returnDate : "ACTIVE");
    }
}

// ─────────────────────────────────────────
//  SERVICE CLASSES
// ─────────────────────────────────────────

class FineCalculator {
    private static final double FINE_PER_DAY = 5.0;

    public double calculate(Transaction tx) {
        LocalDate end = tx.isReturned() ? tx.getReturnDate() : LocalDate.now();
        long overdue = ChronoUnit.DAYS.between(tx.getDueDate(), end);
        return overdue > 0 ? overdue * FINE_PER_DAY : 0.0;
    }
}

// ─────────────────────────────────────────

class FileStorage {
    private static final String BOOKS_FILE   = "books.txt";
    private static final String MEMBERS_FILE = "members.txt";

    public void saveBooks(Collection<Book> books) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : books)
                pw.println(b.getId()+","+b.getTitle()+","+b.getAuthor()+","+b.getIsbn()+","+b.isAvailable());
        }
        System.out.println("  Books saved to " + BOOKS_FILE);
    }

    public List<Book> loadBooks() throws IOException {
        List<Book> list = new ArrayList<>();
        File f = new File(BOOKS_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", 5);
                if (p.length == 5) {
                    Book b = new Book(p[0], p[1], p[2], p[3]);
                    b.setAvailable(Boolean.parseBoolean(p[4]));
                    list.add(b);
                }
            }
        }
        return list;
    }

    public void saveMembers(Collection<Member> members) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MEMBERS_FILE))) {
            for (Member m : members)
                pw.println(m.getMemberId()+","+m.getName()+","+m.getEmail()+","+m.getRole());
        }
        System.out.println("  Members saved to " + MEMBERS_FILE);
    }

    public List<Member> loadMembers() throws IOException {
        List<Member> list = new ArrayList<>();
        File f = new File(MEMBERS_FILE);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", 4);
                if (p.length >= 3)
                    list.add(new Member(p[0], p[1], p[2]));
            }
        }
        return list;
    }
}

// ─────────────────────────────────────────

class Library {
    private Map<String, Book>    books       = new LinkedHashMap<>();
    private Map<String, Member>  members     = new LinkedHashMap<>();
    private List<Transaction>    transactions = new ArrayList<>();
    private FineCalculator       fineCalc    = new FineCalculator();
    private FileStorage          storage     = new FileStorage();
    private int                  txCounter   = 1;

    // ── Book Operations ──────────────────

    public void addBook(Book b) {
        books.put(b.getId(), b);
        System.out.println("  ✔ Book added: \"" + b.getTitle() + "\"");
    }

    public void removeBook(String id) {
        Book b = books.remove(id);
        System.out.println(b != null
            ? "  ✔ Removed: \"" + b.getTitle() + "\""
            : "  ✘ Book not found: " + id);
    }

    public void listBooks() {
        if (books.isEmpty()) { System.out.println("  No books found."); return; }
        books.values().forEach(System.out::println);
    }

    public void searchBooks(String kw) {
        String k = kw.toLowerCase();
        List<Book> results = new ArrayList<>();
        for (Book b : books.values())
            if (b.getTitle().toLowerCase().contains(k) ||
                b.getAuthor().toLowerCase().contains(k) ||
                b.getIsbn().contains(k)) results.add(b);
        if (results.isEmpty()) System.out.println("  No books found for: " + kw);
        else results.forEach(System.out::println);
    }

    // ── Member Operations ────────────────

    public void registerMember(Member m) {
        members.put(m.getMemberId(), m);
        System.out.println("  ✔ Registered: " + m.getName() + " (" + m.getRole() + ")");
    }

    public void listMembers() {
        if (members.isEmpty()) { System.out.println("  No members found."); return; }
        members.values().forEach(System.out::println);
    }

    // ── Borrow / Return ──────────────────

    public boolean borrowBook(String memberId, String bookId) {
        Member m = members.get(memberId);
        Book   b = books.get(bookId);
        if (m == null) { System.out.println("  ✘ Member not found: " + memberId); return false; }
        if (b == null) { System.out.println("  ✘ Book not found: "   + bookId);   return false; }
        if (!b.isAvailable()) {
            System.out.println("  ✘ \"" + b.getTitle() + "\" is currently borrowed.");
            return false;
        }
        String txId = String.format("TXN%03d", txCounter++);
        Transaction tx = new Transaction(txId, memberId, bookId);
        transactions.add(tx);
        b.setAvailable(false);
        m.borrowBook(bookId);
        System.out.printf("  ✔ Borrowed: \"%s\" → %s | Due: %s%n",
            b.getTitle(), m.getName(), tx.getDueDate());
        return true;
    }

    public boolean returnBook(String memberId, String bookId) {
        Member m = members.get(memberId);
        Book   b = books.get(bookId);
        if (m == null || b == null) {
            System.out.println("  ✘ Invalid member or book ID."); return false;
        }
        Transaction tx = transactions.stream()
            .filter(t -> t.getMemberId().equals(memberId)
                      && t.getBookId().equals(bookId)
                      && !t.isReturned())
            .findFirst().orElse(null);
        if (tx == null) { System.out.println("  ✘ No active borrow found."); return false; }
        tx.markReturned();
        b.setAvailable(true);
        m.returnBook(bookId);
        double fine = fineCalc.calculate(tx);
        System.out.printf("  ✔ Returned: \"%s\" by %s%n", b.getTitle(), m.getName());
        if (fine > 0) System.out.printf("  ⚠ Overdue fine: ₹%.2f%n", fine);
        else          System.out.println("  ✔ No fine. Returned on time.");
        return true;
    }

    // ── Reports ──────────────────────────

    public void printTransactions() {
        if (transactions.isEmpty()) { System.out.println("  No transactions yet."); return; }
        transactions.forEach(System.out::println);
    }

    public void printOverdue() {
        System.out.println("  Checking overdue...");
        boolean any = false;
        for (Transaction tx : transactions) {
            if (!tx.isReturned()) {
                double fine = fineCalc.calculate(tx);
                if (fine > 0) {
                    Book   b = books.get(tx.getBookId());
                    Member mm = members.get(tx.getMemberId());
                    System.out.printf("  ⚠ \"%s\" | %s | Fine: ₹%.2f%n",
                        b.getTitle(), mm.getName(), fine);
                    any = true;
                }
            }
        }
        if (!any) System.out.println("  ✔ No overdue books.");
    }

    // ── Persistence ──────────────────────

    public void saveData() throws IOException {
        storage.saveBooks(books.values());
        storage.saveMembers(members.values());
    }

    public void loadData() throws IOException {
        for (Book b   : storage.loadBooks())   addBook(b);
        for (Member m : storage.loadMembers()) registerMember(m);
    }
}

// ─────────────────────────────────────────
//  MAIN — Interactive Console Menu
// ─────────────────────────────────────────

public class LibraryManagementSystem {

    static Library lib = new Library();
    static Scanner sc  = new Scanner(System.in);

    public static void main(String[] args) {
        seedData();

        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();
            System.out.println();
            switch (choice) {
                case "1"  -> lib.listBooks();
                case "2"  -> addBook();
                case "3"  -> removeBook();
                case "4"  -> searchBook();
                case "5"  -> lib.listMembers();
                case "6"  -> addMember();
                case "7"  -> borrowBook();
                case "8"  -> returnBook();
                case "9"  -> lib.printTransactions();
                case "10" -> lib.printOverdue();
                case "11" -> saveData();
                case "0"  -> { System.out.println("  Goodbye!"); return; }
                default   -> System.out.println("  Invalid option.");
            }
            System.out.println();
        }
    }

    // ── Menu ─────────────────────────────

    static void printMenu() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     LIBRARY MANAGEMENT SYSTEM        ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  BOOKS                                ║");
        System.out.println("║   1. List all books                   ║");
        System.out.println("║   2. Add a book                       ║");
        System.out.println("║   3. Remove a book                    ║");
        System.out.println("║   4. Search books                     ║");
        System.out.println("║  MEMBERS                              ║");
        System.out.println("║   5. List all members                 ║");
        System.out.println("║   6. Add a member                     ║");
        System.out.println("║  TRANSACTIONS                         ║");
        System.out.println("║   7. Borrow a book                    ║");
        System.out.println("║   8. Return a book                    ║");
        System.out.println("║   9. Transaction history              ║");
        System.out.println("║  10. Overdue books & fines            ║");
        System.out.println("║  SYSTEM                               ║");
        System.out.println("║  11. Save data to files               ║");
        System.out.println("║   0. Exit                             ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("  Choice: ");
    }

    // ── Helpers ──────────────────────────

    static void addBook() {
        System.out.print("  Book ID   : "); String id     = sc.nextLine().trim();
        System.out.print("  Title     : "); String title  = sc.nextLine().trim();
        System.out.print("  Author    : "); String author = sc.nextLine().trim();
        System.out.print("  ISBN      : "); String isbn   = sc.nextLine().trim();
        lib.addBook(new Book(id, title, author, isbn));
    }

    static void removeBook() {
        System.out.print("  Book ID to remove: ");
        lib.removeBook(sc.nextLine().trim());
    }

    static void searchBook() {
        System.out.print("  Search (title / author / ISBN): ");
        lib.searchBooks(sc.nextLine().trim());
    }

    static void addMember() {
        System.out.print("  Member ID : "); String id    = sc.nextLine().trim();
        System.out.print("  Name      : "); String name  = sc.nextLine().trim();
        System.out.print("  Email     : "); String email = sc.nextLine().trim();
        System.out.print("  Librarian? (y/n): ");
        String yn = sc.nextLine().trim();
        if (yn.equalsIgnoreCase("y")) {
            System.out.print("  Employee ID: ");
            lib.registerMember(new Librarian(id, name, email, sc.nextLine().trim()));
        } else {
            lib.registerMember(new Member(id, name, email));
        }
    }

    static void borrowBook() {
        System.out.print("  Member ID : "); String mid = sc.nextLine().trim();
        System.out.print("  Book ID   : "); String bid = sc.nextLine().trim();
        lib.borrowBook(mid, bid);
    }

    static void returnBook() {
        System.out.print("  Member ID : "); String mid = sc.nextLine().trim();
        System.out.print("  Book ID   : "); String bid = sc.nextLine().trim();
        lib.returnBook(mid, bid);
    }

    static void saveData() {
        try { lib.saveData(); }
        catch (IOException e) { System.out.println("  ✘ Save failed: " + e.getMessage()); }
    }

    // ── Seed Data ────────────────────────

    static void seedData() {
        System.out.println("  Loading demo data...\n");
        lib.addBook(new Book("B001", "Clean Code",               "Robert C. Martin", "978-0132350884"));
        lib.addBook(new Book("B002", "Effective Java",           "Joshua Bloch",     "978-0134685991"));
        lib.addBook(new Book("B003", "Design Patterns",          "Gang of Four",     "978-0201633610"));
        lib.addBook(new Book("B004", "The Pragmatic Programmer", "Hunt & Thomas",    "978-0135957059"));
        lib.addBook(new Book("B005", "Head First Java",          "Sierra & Bates",   "978-0596009205"));

        lib.registerMember(new Member("M001", "Arjun Kumar",  "arjun@email.com"));
        lib.registerMember(new Member("M002", "Priya Sharma", "priya@email.com"));
        lib.registerMember(new Librarian("M003", "Ravi Admin", "ravi@lib.com", "EMP001"));
        System.out.println();
    }
}