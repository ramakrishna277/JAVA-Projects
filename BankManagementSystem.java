import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// ── Transaction ───────────────────────────
class Transaction {
    String type, description;
    double amount, balance;
    LocalDateTime date;
    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    Transaction(String type, double amount, double balance, String description) {
        this.type        = type;
        this.amount      = amount;
        this.balance     = balance;
        this.description = description;
        this.date        = LocalDateTime.now();
    }

    public String toString() {
        return String.format("  %-12s | %-20s | %8.2f | Balance: %10.2f | %s",
            type, description, amount, balance, date.format(FMT));
    }
}

// ── Account ───────────────────────────────
class Account {
    static int counter = 1001;
    int accountNo;
    String holderName, accountType, pin;
    double balance;
    boolean isActive;
    List<Transaction> transactions = new ArrayList<>();
    LocalDateTime createdAt;

    Account(String holderName, String accountType, double initialDeposit, String pin) {
        this.accountNo    = counter++;
        this.holderName   = holderName;
        this.accountType  = accountType;
        this.balance      = initialDeposit;
        this.pin          = pin;
        this.isActive     = true;
        this.createdAt    = LocalDateTime.now();
        addTransaction("DEPOSIT", initialDeposit, "Initial deposit");
    }

    void addTransaction(String type, double amount, String desc) {
        transactions.add(new Transaction(type, amount, balance, desc));
    }

    boolean deposit(double amount) {
        if (amount <= 0) return false;
        balance += amount;
        addTransaction("DEPOSIT", amount, "Cash deposit");
        return true;
    }

    boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        addTransaction("WITHDRAW", amount, "Cash withdrawal");
        return true;
    }

    boolean transfer(Account target, double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        target.balance += amount;
        addTransaction("TRANSFER OUT", amount, "Transfer to A/C " + target.accountNo);
        target.addTransaction("TRANSFER IN", amount, "Transfer from A/C " + accountNo);
        return true;
    }

    void printStatement() {
        System.out.println("┌────────────────────────────────────────────────────────────────────");
        System.out.println("│ Account No : " + accountNo);
        System.out.println("│ Holder     : " + holderName);
        System.out.println("│ Type       : " + accountType);
        System.out.printf ("│ Balance    : Rs. %.2f%n", balance);
        System.out.println("│ Status     : " + (isActive ? "Active" : "Closed"));
        System.out.println("├── Transactions ────────────────────────────────────────────────────");
        if (transactions.isEmpty()) System.out.println("│  No transactions.");
        else transactions.forEach(System.out::println);
        System.out.println("└────────────────────────────────────────────────────────────────────");
    }

    public String toString() {
        return String.format("[%d] %-20s | %-10s | Rs. %10.2f | %s",
            accountNo, holderName, accountType, balance,
            isActive ? "Active" : "Closed");
    }
}

// ── Loan ──────────────────────────────────
class Loan {
    static int counter = 1;
    int loanId, accountNo;
    double principal, remaining;
    String loanType, status;
    double interestRate;
    int tenureMonths;
    LocalDateTime takenAt;

    Loan(int accountNo, String loanType, double amount, int tenureMonths) {
        this.loanId       = counter++;
        this.accountNo    = accountNo;
        this.loanType     = loanType;
        this.principal    = amount;
        this.remaining    = amount;
        this.tenureMonths = tenureMonths;
        this.interestRate = loanType.equals("Home") ? 7.5 :
                            loanType.equals("Car")  ? 9.0 : 12.0;
        this.status       = "Active";
        this.takenAt      = LocalDateTime.now();
    }

    double emi() {
        double r = interestRate / 12 / 100;
        return (principal * r * Math.pow(1 + r, tenureMonths))
             / (Math.pow(1 + r, tenureMonths) - 1);
    }

    boolean payEmi() {
        double emi = emi();
        if (remaining <= 0) return false;
        remaining = Math.max(0, remaining - emi);
        if (remaining == 0) status = "Closed";
        return true;
    }

    public String toString() {
        return String.format("[L%03d] A/C:%d | %-10s | Rs.%8.2f | EMI: Rs.%7.2f | Remaining: Rs.%8.2f | %s",
            loanId, accountNo, loanType, principal, emi(), remaining, status);
    }
}

// ── Bank ──────────────────────────────────
public class BankManagementSystem {
    static List<Account> accounts = new ArrayList<>();
    static List<Loan>    loans    = new ArrayList<>();
    static Scanner       sc       = new Scanner(System.in);

    public static void main(String[] args) {
        seedData();
        while (true) {
            printMenu();
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            System.out.println();
            switch (ch) {
                case "1"  -> listAccounts();
                case "2"  -> createAccount();
                case "3"  -> deposit();
                case "4"  -> withdraw();
                case "5"  -> transfer();
                case "6"  -> viewStatement();
                case "7"  -> checkBalance();
                case "8"  -> changePin();
                case "9"  -> closeAccount();
                case "10" -> applyLoan();
                case "11" -> viewLoans();
                case "12" -> payEmi();
                case "13" -> bankSummary();
                case "0"  -> { System.out.println("  Thank you for banking with us!"); return; }
                default   -> System.out.println("  Invalid choice.");
            }
            System.out.println();
        }
    }

    // ── Menu ──────────────────────────────
    static void printMenu() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║      BANK MANAGEMENT SYSTEM      ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  ACCOUNTS                         ║");
        System.out.println("║   1. List all accounts            ║");
        System.out.println("║   2. Create account               ║");
        System.out.println("║   7. Check balance                ║");
        System.out.println("║   6. View statement               ║");
        System.out.println("║   8. Change PIN                   ║");
        System.out.println("║   9. Close account                ║");
        System.out.println("║  TRANSACTIONS                     ║");
        System.out.println("║   3. Deposit                      ║");
        System.out.println("║   4. Withdraw                     ║");
        System.out.println("║   5. Transfer                     ║");
        System.out.println("║  LOANS                            ║");
        System.out.println("║  10. Apply for loan               ║");
        System.out.println("║  11. View loans                   ║");
        System.out.println("║  12. Pay EMI                      ║");
        System.out.println("║  REPORTS                          ║");
        System.out.println("║  13. Bank summary                 ║");
        System.out.println("║   0. Exit                         ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    // ── Account Operations ────────────────
    static void listAccounts() {
        if (accounts.isEmpty()) { System.out.println("  No accounts."); return; }
        System.out.println("  Account No | Holder               | Type       | Balance       | Status");
        System.out.println("  " + "─".repeat(75));
        accounts.forEach(System.out::println);
    }

    static void createAccount() {
        System.out.print("  Name         : "); String name = sc.nextLine();
        System.out.print("  Account Type (Savings/Current): "); String type = sc.nextLine();
        System.out.print("  Initial Deposit (Rs.): "); double dep = Double.parseDouble(sc.nextLine().trim());
        System.out.print("  Set PIN (4 digits)   : "); String pin = sc.nextLine().trim();
        Account acc = new Account(name, type, dep, pin);
        accounts.add(acc);
        System.out.println("  Account created! Account No: " + acc.accountNo);
    }

    static void deposit() {
        Account acc = getVerifiedAccount();
        if (acc == null) return;
        System.out.print("  Amount to deposit (Rs.): ");
        double amt = Double.parseDouble(sc.nextLine().trim());
        if (acc.deposit(amt))
            System.out.printf("  Deposited Rs.%.2f | New Balance: Rs.%.2f%n", amt, acc.balance);
        else
            System.out.println("  Invalid amount.");
    }

    static void withdraw() {
        Account acc = getVerifiedAccount();
        if (acc == null) return;
        System.out.print("  Amount to withdraw (Rs.): ");
        double amt = Double.parseDouble(sc.nextLine().trim());
        if (acc.withdraw(amt))
            System.out.printf("  Withdrawn Rs.%.2f | New Balance: Rs.%.2f%n", amt, acc.balance);
        else
            System.out.println("  Insufficient balance or invalid amount.");
    }

    static void transfer() {
        System.out.println("  -- From Account --");
        Account from = getVerifiedAccount();
        if (from == null) return;
        System.out.print("  Target Account No: ");
        int toNo = Integer.parseInt(sc.nextLine().trim());
        Account to = findAccount(toNo);
        if (to == null) { System.out.println("  Target account not found."); return; }
        System.out.print("  Amount to transfer (Rs.): ");
        double amt = Double.parseDouble(sc.nextLine().trim());
        if (from.transfer(to, amt))
            System.out.printf("  Transferred Rs.%.2f to A/C %d%n", amt, toNo);
        else
            System.out.println("  Insufficient balance or invalid amount.");
    }

    static void viewStatement() {
        Account acc = getVerifiedAccount();
        if (acc != null) acc.printStatement();
    }

    static void checkBalance() {
        Account acc = getVerifiedAccount();
        if (acc != null)
            System.out.printf("  Balance for %s (A/C %d): Rs. %.2f%n",
                acc.holderName, acc.accountNo, acc.balance);
    }

    static void changePin() {
        Account acc = getVerifiedAccount();
        if (acc == null) return;
        System.out.print("  New PIN: "); String newPin = sc.nextLine().trim();
        acc.pin = newPin;
        System.out.println("  PIN changed successfully!");
    }

    static void closeAccount() {
        Account acc = getVerifiedAccount();
        if (acc == null) return;
        acc.isActive = false;
        System.out.println("  Account " + acc.accountNo + " closed.");
    }

    // ── Loan Operations ───────────────────
    static void applyLoan() {
        System.out.print("  Account No: "); int ano = Integer.parseInt(sc.nextLine().trim());
        Account acc = findAccount(ano);
        if (acc == null) { System.out.println("  Account not found."); return; }
        System.out.print("  Loan Type (Home/Car/Personal): "); String type = sc.nextLine();
        System.out.print("  Loan Amount (Rs.)           : "); double amt  = Double.parseDouble(sc.nextLine().trim());
        System.out.print("  Tenure (months)             : "); int tenure  = Integer.parseInt(sc.nextLine().trim());
        Loan loan = new Loan(ano, type, amt, tenure);
        loans.add(loan);
        System.out.printf("  Loan approved! Loan ID: L%03d | EMI: Rs.%.2f/month%n",
            loan.loanId, loan.emi());
        acc.balance += amt;
        acc.addTransaction("LOAN CREDIT", amt, type + " loan disbursed");
    }

    static void viewLoans() {
        if (loans.isEmpty()) { System.out.println("  No loans."); return; }
        loans.forEach(System.out::println);
    }

    static void payEmi() {
        System.out.print("  Loan ID (number): "); int lid = Integer.parseInt(sc.nextLine().trim());
        Loan loan = loans.stream().filter(l -> l.loanId == lid).findFirst().orElse(null);
        if (loan == null) { System.out.println("  Loan not found."); return; }
        Account acc = findAccount(loan.accountNo);
        if (acc == null || acc.balance < loan.emi()) {
            System.out.println("  Insufficient balance for EMI."); return;
        }
        acc.balance -= loan.emi();
        acc.addTransaction("EMI PAYMENT", loan.emi(), "Loan L" + loan.loanId + " EMI");
        loan.payEmi();
        System.out.printf("  EMI of Rs.%.2f paid. Remaining loan: Rs.%.2f%n",
            loan.emi(), loan.remaining);
        if (loan.status.equals("Closed")) System.out.println("  Loan fully repaid!");
    }

    // ── Summary ───────────────────────────
    static void bankSummary() {
        double totalDeposits = accounts.stream().mapToDouble(a -> a.balance).sum();
        long   active        = accounts.stream().filter(a -> a.isActive).count();
        double totalLoans    = loans.stream().mapToDouble(l -> l.principal).sum();
        double outstanding   = loans.stream().mapToDouble(l -> l.remaining).sum();

        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║           BANK SUMMARY               ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.printf ("  ║  Total Accounts   : %-18d║%n", accounts.size());
        System.out.printf ("  ║  Active Accounts  : %-18d║%n", active);
        System.out.printf ("  ║  Total Deposits   : Rs. %-14.2f║%n", totalDeposits);
        System.out.printf ("  ║  Loans Issued     : %-18d║%n", loans.size());
        System.out.printf ("  ║  Total Loan Amt   : Rs. %-14.2f║%n", totalLoans);
        System.out.printf ("  ║  Outstanding Loan : Rs. %-14.2f║%n", outstanding);
        System.out.println("  ╚══════════════════════════════════════╝");
    }

    // ── Helpers ───────────────────────────
    static Account getVerifiedAccount() {
        System.out.print("  Account No : "); int no  = Integer.parseInt(sc.nextLine().trim());
        System.out.print("  PIN        : "); String pin = sc.nextLine().trim();
        Account acc = findAccount(no);
        if (acc == null)          { System.out.println("  Account not found."); return null; }
        if (!acc.isActive)        { System.out.println("  Account is closed."); return null; }
        if (!acc.pin.equals(pin)) { System.out.println("  Wrong PIN!");         return null; }
        return acc;
    }

    static Account findAccount(int no) {
        return accounts.stream().filter(a -> a.accountNo == no).findFirst().orElse(null);
    }

    // ── Seed Data ─────────────────────────
    static void seedData() {
        accounts.add(new Account("Arjun Kumar",  "Savings", 25000, "1111"));
        accounts.add(new Account("Priya Sharma", "Current", 50000, "2222"));
        accounts.add(new Account("Ravi Babu",    "Savings", 15000, "3333"));
        accounts.get(0).deposit(5000);
        accounts.get(1).transfer(accounts.get(0), 2000);
        loans.add(new Loan(1001, "Home", 500000, 240));
        loans.add(new Loan(1002, "Car",  300000, 60));
        System.out.println("  Demo data loaded. 3 accounts (PINs: 1111 / 2222 / 3333), 2 loans.\n");
    }
}
