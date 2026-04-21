import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// ── Product ───────────────────────────────
class Product {
    static int counter = 1;
    int id;
    String name, category;
    double price;
    int stock;

    Product(String name, String category, double price, int stock) {
        this.id       = counter++;
        this.name     = name;
        this.category = category;
        this.price    = price;
        this.stock    = stock;
    }

    public String toString() {
        return String.format("[%03d] %-22s | %-15s | Rs.%7.2f | Stock: %d",
            id, name, category, price, stock);
    }
}

// ── Cart Item ─────────────────────────────
class CartItem {
    Product product;
    int quantity;

    CartItem(Product product, int quantity) {
        this.product  = product;
        this.quantity = quantity;
    }

    double total() { return product.price * quantity; }

    public String toString() {
        return String.format("  %-22s | Rs.%6.2f x %2d = Rs.%8.2f",
            product.name, product.price, quantity, total());
    }
}

// ── Bill ──────────────────────────────────
class Bill {
    static int counter = 1;
    int billNo;
    String customerName;
    List<CartItem> items;
    double subtotal, discount, tax, total;
    LocalDateTime date;
    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    Bill(String customerName, List<CartItem> items, double discountPct) {
        this.billNo        = counter++;
        this.customerName  = customerName;
        this.items         = new ArrayList<>(items);
        this.date          = LocalDateTime.now();
        this.subtotal      = items.stream().mapToDouble(CartItem::total).sum();
        this.discount      = subtotal * discountPct / 100;
        this.tax           = (subtotal - discount) * 0.18; // 18% GST
        this.total         = subtotal - discount + tax;
    }

    void print() {
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║           SUPERMARKET BILL               ║");
        System.out.println("  ╠══════════════════════════════════════════╣");
        System.out.printf ("  ║  Bill No : %-30d║%n", billNo);
        System.out.printf ("  ║  Customer: %-30s║%n", customerName);
        System.out.printf ("  ║  Date    : %-30s║%n", date.format(FMT));
        System.out.println("  ╠══════════════════════════════════════════╣");
        items.forEach(System.out::println);
        System.out.println("  ├──────────────────────────────────────────┤");
        System.out.printf ("  ║  Subtotal : Rs. %25.2f║%n", subtotal);
        System.out.printf ("  ║  Discount : Rs. %25.2f║%n", discount);
        System.out.printf ("  ║  GST(18%%) : Rs. %25.2f║%n", tax);
        System.out.println("  ╠══════════════════════════════════════════╣");
        System.out.printf ("  ║  TOTAL    : Rs. %25.2f║%n", total);
        System.out.println("  ╚══════════════════════════════════════════╝");
    }

    public String toString() {
        return String.format("  Bill#%03d | %-18s | Rs.%8.2f | %s",
            billNo, customerName, total, date.format(FMT));
    }
}

// ── Customer ──────────────────────────────
class Customer {
    static int counter = 1;
    int id;
    String name, phone;
    int loyaltyPoints;
    List<Integer> billNos = new ArrayList<>();

    Customer(String name, String phone) {
        this.id            = counter++;
        this.name          = name;
        this.phone         = phone;
        this.loyaltyPoints = 0;
    }

    void addPoints(double total) {
        loyaltyPoints += (int)(total / 100); // 1 point per Rs.100
    }

    public String toString() {
        return String.format("[C%03d] %-20s | %s | Points: %d | Bills: %d",
            id, name, phone, loyaltyPoints, billNos.size());
    }
}

// ── SuperMarket ───────────────────────────
public class SuperMarket {
    static List<Product>  products  = new ArrayList<>();
    static List<Customer> customers = new ArrayList<>();
    static List<Bill>     bills     = new ArrayList<>();
    static List<CartItem> cart      = new ArrayList<>();
    static Customer       currentCustomer = null;
    static Scanner        sc        = new Scanner(System.in);

    public static void main(String[] args) {
        seedData();
        while (true) {
            printMenu();
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            System.out.println();
            switch (ch) {
                case "1"  -> listProducts();
                case "2"  -> searchProduct();
                case "3"  -> addProduct();
                case "4"  -> restockProduct();
                case "5"  -> selectCustomer();
                case "6"  -> addCustomer();
                case "7"  -> addToCart();
                case "8"  -> viewCart();
                case "9"  -> removeFromCart();
                case "10" -> checkout();
                case "11" -> viewBills();
                case "12" -> salesReport();
                case "13" -> lowStockAlert();
                case "14" -> listCustomers();
                case "0"  -> { System.out.println("  Thank you! Goodbye!"); return; }
                default   -> System.out.println("  Invalid choice.");
            }
            System.out.println();
        }
    }

    // ── Menu ──────────────────────────────
    static void printMenu() {
        String cust = currentCustomer != null ? currentCustomer.name : "None";
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         SUPERMARKET SYSTEM           ║");
        System.out.printf ("║  Customer: %-26s║%n", cust);
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  PRODUCTS                             ║");
        System.out.println("║   1. List products                    ║");
        System.out.println("║   2. Search product                   ║");
        System.out.println("║   3. Add new product                  ║");
        System.out.println("║   4. Restock product                  ║");
        System.out.println("║  CUSTOMERS                            ║");
        System.out.println("║   5. Select customer                  ║");
        System.out.println("║   6. Add new customer                 ║");
        System.out.println("║  SHOPPING CART                        ║");
        System.out.println("║   7. Add to cart                      ║");
        System.out.println("║   8. View cart                        ║");
        System.out.println("║   9. Remove from cart                 ║");
        System.out.println("║  10. Checkout & print bill            ║");
        System.out.println("║  REPORTS                              ║");
        System.out.println("║  11. View all bills                   ║");
        System.out.println("║  12. Sales report                     ║");
        System.out.println("║  13. Low stock alert                  ║");
        System.out.println("║  14. List customers                   ║");
        System.out.println("║   0. Exit                             ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    // ── Products ──────────────────────────
    static void listProducts() {
        if (products.isEmpty()) { System.out.println("  No products."); return; }
        System.out.println("  ID  | Name                   | Category        | Price      | Stock");
        System.out.println("  " + "─".repeat(72));
        products.forEach(p -> System.out.println("  " + p));
    }

    static void searchProduct() {
        System.out.print("  Search (name/category): "); String kw = sc.nextLine().toLowerCase();
        products.stream()
            .filter(p -> p.name.toLowerCase().contains(kw)
                      || p.category.toLowerCase().contains(kw))
            .forEach(p -> System.out.println("  " + p));
    }

    static void addProduct() {
        System.out.print("  Name     : "); String name = sc.nextLine();
        System.out.print("  Category : "); String cat  = sc.nextLine();
        System.out.print("  Price    : "); double price = Double.parseDouble(sc.nextLine().trim());
        System.out.print("  Stock    : "); int stock    = Integer.parseInt(sc.nextLine().trim());
        products.add(new Product(name, cat, price, stock));
        System.out.println("  Product added!");
    }

    static void restockProduct() {
        System.out.print("  Product ID: "); int id = Integer.parseInt(sc.nextLine().trim());
        Product p = findProduct(id);
        if (p == null) { System.out.println("  Product not found."); return; }
        System.out.print("  Add stock quantity: "); int qty = Integer.parseInt(sc.nextLine().trim());
        p.stock += qty;
        System.out.printf("  %s restocked. New stock: %d%n", p.name, p.stock);
    }

    // ── Customers ─────────────────────────
    static void selectCustomer() {
        System.out.print("  Customer ID (number): "); int id = Integer.parseInt(sc.nextLine().trim());
        Customer c = customers.stream().filter(x -> x.id == id).findFirst().orElse(null);
        if (c == null) { System.out.println("  Customer not found."); return; }
        currentCustomer = c;
        cart.clear();
        System.out.println("  Customer selected: " + c.name + " | Points: " + c.loyaltyPoints);
    }

    static void addCustomer() {
        System.out.print("  Name  : "); String name  = sc.nextLine();
        System.out.print("  Phone : "); String phone = sc.nextLine();
        Customer c = new Customer(name, phone);
        customers.add(c);
        System.out.println("  Customer added! ID: C" + String.format("%03d", c.id));
    }

    static void listCustomers() {
        if (customers.isEmpty()) { System.out.println("  No customers."); return; }
        customers.forEach(c -> System.out.println("  " + c));
    }

    // ── Cart ──────────────────────────────
    static void addToCart() {
        if (currentCustomer == null) {
            System.out.println("  Please select a customer first (option 5)."); return;
        }
        System.out.print("  Product ID: "); int id  = Integer.parseInt(sc.nextLine().trim());
        System.out.print("  Quantity  : "); int qty = Integer.parseInt(sc.nextLine().trim());
        Product p = findProduct(id);
        if (p == null)       { System.out.println("  Product not found.");          return; }
        if (p.stock < qty)   { System.out.println("  Not enough stock. Available: " + p.stock); return; }

        // If already in cart, update quantity
        for (CartItem ci : cart) {
            if (ci.product.id == id) {
                ci.quantity += qty;
                System.out.println("  Updated cart: " + p.name + " x" + ci.quantity);
                return;
            }
        }
        cart.add(new CartItem(p, qty));
        System.out.println("  Added to cart: " + p.name + " x" + qty);
    }

    static void viewCart() {
        if (cart.isEmpty()) { System.out.println("  Cart is empty."); return; }
        System.out.println("  ── Cart ─────────────────────────────────────");
        cart.forEach(System.out::println);
        double subtotal = cart.stream().mapToDouble(CartItem::total).sum();
        System.out.printf("  ─────────────────────────────────────────────%n");
        System.out.printf("  Subtotal: Rs. %.2f  |  Items: %d%n",
            subtotal, cart.stream().mapToInt(ci -> ci.quantity).sum());
    }

    static void removeFromCart() {
        System.out.print("  Product ID to remove: "); int id = Integer.parseInt(sc.nextLine().trim());
        boolean removed = cart.removeIf(ci -> ci.product.id == id);
        System.out.println(removed ? "  Removed from cart." : "  Item not in cart.");
    }

    // ── Checkout ──────────────────────────
    static void checkout() {
        if (currentCustomer == null) { System.out.println("  Select a customer first."); return; }
        if (cart.isEmpty())          { System.out.println("  Cart is empty.");           return; }

        System.out.print("  Discount % (0 if none): ");
        double discPct = Double.parseDouble(sc.nextLine().trim());

        // Deduct stock
        for (CartItem ci : cart) ci.product.stock -= ci.quantity;

        Bill bill = new Bill(currentCustomer.name, cart, discPct);
        bills.add(bill);
        currentCustomer.billNos.add(bill.billNo);
        currentCustomer.addPoints(bill.total);
        bill.print();

        System.out.printf("  Loyalty points earned: +%d | Total points: %d%n",
            (int)(bill.total / 100), currentCustomer.loyaltyPoints);

        cart.clear();
        currentCustomer = null;
    }

    // ── Reports ───────────────────────────
    static void viewBills() {
        if (bills.isEmpty()) { System.out.println("  No bills yet."); return; }
        bills.forEach(System.out::println);
    }

    static void salesReport() {
        if (bills.isEmpty()) { System.out.println("  No sales yet."); return; }
        double totalSales    = bills.stream().mapToDouble(b -> b.total).sum();
        double totalDiscount = bills.stream().mapToDouble(b -> b.discount).sum();
        double totalTax      = bills.stream().mapToDouble(b -> b.tax).sum();

        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║           SALES REPORT               ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.printf ("  ║  Total Bills      : %-18d║%n", bills.size());
        System.out.printf ("  ║  Total Sales      : Rs. %-14.2f║%n", totalSales);
        System.out.printf ("  ║  Total Discount   : Rs. %-14.2f║%n", totalDiscount);
        System.out.printf ("  ║  Total GST        : Rs. %-14.2f║%n", totalTax);
        System.out.println("  ╠══════════════════════════════════════╣");

        // Best selling product
        Map<String, Integer> sold = new HashMap<>();
        for (Bill b : bills)
            for (CartItem ci : b.items)
                sold.merge(ci.product.name, ci.quantity, Integer::sum);
        sold.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .ifPresent(e -> System.out.printf(
                "  ║  Best Seller      : %-18s║%n", e.getKey()));
        System.out.println("  ╚══════════════════════════════════════╝");
    }

    static void lowStockAlert() {
        System.out.println("  ── Low Stock (below 10 units) ───────────");
        boolean any = false;
        for (Product p : products) {
            if (p.stock < 10) {
                System.out.printf("  [%03d] %-22s | Stock: %d  RESTOCK NEEDED%n",
                    p.id, p.name, p.stock);
                any = true;
            }
        }
        if (!any) System.out.println("  All products well stocked.");
    }

    // ── Helpers ───────────────────────────
    static Product findProduct(int id) {
        return products.stream().filter(p -> p.id == id).findFirst().orElse(null);
    }

    // ── Seed Data ─────────────────────────
    static void seedData() {
        // Groceries
        products.add(new Product("Rice (5kg)",        "Groceries",  250.00, 50));
        products.add(new Product("Wheat Flour (2kg)", "Groceries",  85.00,  40));
        products.add(new Product("Sugar (1kg)",       "Groceries",  45.00,  60));
        products.add(new Product("Salt (1kg)",        "Groceries",  20.00,   8));
        // Dairy
        products.add(new Product("Milk (1L)",         "Dairy",      55.00,  30));
        products.add(new Product("Butter (100g)",     "Dairy",      55.00,  25));
        products.add(new Product("Cheese (200g)",     "Dairy",     120.00,   7));
        // Beverages
        products.add(new Product("Tea (250g)",        "Beverages", 110.00,  20));
        products.add(new Product("Coffee (100g)",     "Beverages", 180.00,  15));
        products.add(new Product("Juice (1L)",        "Beverages",  90.00,  35));
        // Snacks
        products.add(new Product("Biscuits",          "Snacks",     40.00,  50));
        products.add(new Product("Chips (100g)",      "Snacks",     30.00,  45));
        // Customers
        customers.add(new Customer("Arjun Kumar",  "9876543210"));
        customers.add(new Customer("Priya Sharma", "9123456789"));

        System.out.println("  Demo loaded: 12 products, 2 customers (ID: 1 and 2).\n");
    }
}
