import java.util.Scanner;
public class Hostel {  
    static Scanner sc = new Scanner(System.in);
    static double totalBill = 0;
    static int itemCount = 0;  
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Welcome to GRAND HOTEL");
        System.out.println("========================================");  
        displayMenu();
    }
    public static void displayMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Biryani - Rs. 250");
        System.out.println("2. Butter Chicken - Rs. 300");
        System.out.println("3. Paneer Tikka - Rs. 280");
        System.out.println("4. Dosa - Rs. 120");
        System.out.println("5. Chole Bhature - Rs. 180");
        System.out.println("6. View Bill & Exit");
        System.out.println("========================================");
        boolean ordering = true;
        while (ordering) {
            System.out.print("\nSelect your choice (1-6): ");
            int choice = sc.nextInt();      
            switch (choice) {
                case 1:
                    showBiryaniSpecials();
                    break;
                case 2:
                    showButterChickenSpecials();
                    break;
                case 3:
                    showPaneerTikkaSpecials();
                    break;
                case 4:
                    showDosaSpecials();
                    break;
                case 5:
                    showCholeBhatureSpecials();
                    break;
                case 6:
                    displayBill();
                    ordering = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    } 
    public static void showBiryaniSpecials() {
        System.out.println("\n--- BIRYANI SPECIALS ---");
        System.out.println("1. Hyderabadi Biryani - Rs. 250");
        System.out.println("2. Lucknowi Biryani - Rs. 280");
        System.out.println("3. Kolkata Biryani - Rs. 270");
        System.out.println("4. Biryani with Egg - Rs. 260");
        System.out.println("5. Seafood Biryani - Rs. 320");
        System.out.println("0. Go Back");  
        processSpecial("Biryani");
    }  
    public static void showButterChickenSpecials() {
        System.out.println("\n--- BUTTER CHICKEN SPECIALS ---");
        System.out.println("1. Classic Butter Chicken - Rs. 300");
        System.out.println("2. Butter Chicken with Rice - Rs. 350");
        System.out.println("3. Butter Chicken Naan - Rs. 330");
        System.out.println("4. Butter Chicken Masala - Rs. 310");
        System.out.println("5. Premium Butter Chicken - Rs. 380");
        System.out.println("0. Go Back");     
        processSpecial("Butter Chicken");
    } 
    public static void showPaneerTikkaSpecials() {
        System.out.println("\n--- PANEER TIKKA SPECIALS ---");
        System.out.println("1. Plain Paneer Tikka - Rs. 280");
        System.out.println("2. Paneer Tikka with Green Chutney - Rs. 300");
        System.out.println("3. Paneer Tikka Skewer - Rs. 320");
        System.out.println("4. Paneer Tikka with Mint - Rs. 290");
        System.out.println("5. Premium Paneer Tikka - Rs. 340");
        System.out.println("0. Go Back");     
        processSpecial("Paneer Tikka");
    } 
    public static void showDosaSpecials() {
        System.out.println("\n--- DOSA SPECIALS ---");
        System.out.println("1. Plain Dosa - Rs. 120");
        System.out.println("2. Masala Dosa - Rs. 150");
        System.out.println("3. Paneer Dosa - Rs. 180");
        System.out.println("4. Cheese Dosa - Rs. 170");
        System.out.println("5. Butter Dosa - Rs. 140");
        System.out.println("0. Go Back");
        processSpecial("Dosa");
    }  
    public static void showCholeBhatureSpecials() {
        System.out.println("\n--- CHOLE BHATURE SPECIALS ---");
        System.out.println("1. Classic Chole Bhature - Rs. 180");
        System.out.println("2. Chole Bhature with Pickle - Rs. 200");
        System.out.println("3. Chole Bhature with Yogurt - Rs. 220");
        System.out.println("4. Premium Chole Bhature - Rs. 240");
        System.out.println("5. Chole Bhature with Extra Chole - Rs. 210");
        System.out.println("0. Go Back");  
        processSpecial("Chole Bhature");
    }
    public static void processSpecial(String itemName) {
        System.out.print("\nSelect your special (1-5) or 0 to go back: ");
        int choice = sc.nextInt();
        
        switch (choice) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                addToOrder(itemName, choice);
                break;
            case 0:
                System.out.println("Going back to main menu...");
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }
    public static void addToOrder(String itemName, int specialChoice) {
        double price = 0;
        String specialName = "";
        // Calculate price based on item and special choice
        String[] prices = getItemPrices(itemName);
        price = Double.parseDouble(prices[specialChoice - 1]);
        specialName = prices[5]; // Get the special name   
        System.out.print("How many? ");
        int quantity = sc.nextInt();   
        double itemTotal = price * quantity;
        totalBill += itemTotal;
        itemCount += quantity;   
        System.out.println("✓ Added " + quantity + " " + itemName + " to your order (Rs. " + itemTotal + ")");       
        showCurrentBill();
    }    
    public static String[] getItemPrices(String itemName) {
        String[] prices = new String[6]; 
        switch (itemName) {
            case "Biryani":
                prices[0] = "250"; prices[1] = "280"; prices[2] = "270"; 
                prices[3] = "260"; prices[4] = "320"; prices[5] = "Biryani";
                break;
            case "Butter Chicken":
                prices[0] = "300"; prices[1] = "350"; prices[2] = "330"; 
                prices[3] = "310"; prices[4] = "380"; prices[5] = "Butter Chicken";
                break;
            case "Paneer Tikka":
                prices[0] = "280"; prices[1] = "300"; prices[2] = "320"; 
                prices[3] = "290"; prices[4] = "340"; prices[5] = "Paneer Tikka";
                break;
            case "Dosa":
                prices[0] = "120"; prices[1] = "150"; prices[2] = "180"; 
                prices[3] = "170"; prices[4] = "140"; prices[5] = "Dosa";
                break;
            case "Chole Bhature":
                prices[0] = "180"; prices[1] = "200"; prices[2] = "220"; 
                prices[3] = "240"; prices[4] = "210"; prices[5] = "Chole Bhature";
                break;
        }  
        return prices;
    }
    public static void showCurrentBill() {
        System.out.println("\n========================================");
        System.out.println("         CURRENT BILL");
        System.out.println("========================================");
        System.out.println("Total Items Ordered: " + itemCount);
        System.out.println("Total Bill: Rs. " + totalBill);
        System.out.println("========================================");
    }
    public static void displayBill() {
        System.out.println("\n========================================");
        System.out.println("         BILLING INVOICE");
        System.out.println("========================================");
        System.out.println("Total Items Ordered: " + itemCount);
        System.out.println("Total Bill: Rs. " + totalBill);
        System.out.println("========================================");
        System.out.println("Thank you for visiting GRAND HOTEL!");
        System.out.println("========================================");
    }
}
