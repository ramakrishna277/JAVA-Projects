package PRACTICEPROBLEMS.PROBLEMS;
import java.util.*;

class Student {
    int studentId;
    String name;
    int age;
    String course;
    double marks;

    Scanner sc = new Scanner(System.in);

    
    void inputDetails() {
        System.out.print("Enter Student ID: ");
        studentId = sc.nextInt();

        sc.nextLine();

        System.out.print("Enter Name: ");
        name = sc.nextLine();

        System.out.print("Enter Age: ");
        age = sc.nextInt();

        sc.nextLine();

        System.out.print("Enter Course: ");
        course = sc.nextLine();

        System.out.print("Enter Marks: ");
        marks = sc.nextDouble();
    }

    
    void displayDetails() {
        System.out.println(studentId + " "
                + name + " "
                + age + " "
                + course + " "
                + marks);
    }
}

public class Studentmanagementsystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ArrayList<Student> list = new ArrayList<>();

        int choice;

        do {

            System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search Student by ID");
            System.out.println("4. Display Students with Marks Above X");
            System.out.println("5. Calculate Average Marks");
            System.out.println("6. Sort Students by Marks");
            System.out.println("7. Update Student Record");
            System.out.println("8. Delete Student Record");
            System.out.println("9. Exit");

            System.out.print("Enter Choice: ");
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    Student s = new Student();
                    s.inputDetails();
                    list.add(s);
                    System.out.println("Student Added Successfully");
                    break;

                case 2:
                    if (list.isEmpty()) {
                        System.out.println("No Records Found");
                    } else {
                        for (Student st : list) {
                            st.displayDetails();
                        }
                    }
                    break;

                case 3:
                    System.out.print("Enter Student ID to Search: ");
                    int id = sc.nextInt();

                    boolean found = false;

                    for (Student st : list) {
                        if (st.studentId == id) {
                            st.displayDetails();
                            found = true;
                        }
                    }

                    if (!found)
                        System.out.println("Student Not Found");

                    break;

                case 4:
                    System.out.print("Enter Marks Value: ");
                    double m = sc.nextDouble();

                    for (Student st : list) {
                        if (st.marks > m) {
                            st.displayDetails();
                        }
                    }

                    break;

                case 5:
                    double total = 0;

                    for (Student st : list) {
                        total += st.marks;
                    }

                    if (list.size() > 0)
                        System.out.println("Average Marks = "
                                + (total / list.size()));
                    else
                        System.out.println("No Data Available");

                    break;

                case 6:
                    Collections.sort(list,
                            (a, b) -> Double.compare(a.marks, b.marks));

                    System.out.println("Students Sorted by Marks");

                    break;

                case 7:
                    System.out.print("Enter Student ID to Update: ");
                    int updateId = sc.nextInt();

                    for (Student st : list) {
                        if (st.studentId == updateId) {
                            st.inputDetails();
                            System.out.println("Record Updated");
                        }
                    }

                    break;

                case 8:
                    System.out.print("Enter Student ID to Delete: ");
                    int deleteId = sc.nextInt();

                    Iterator<Student> it = list.iterator();

                    while (it.hasNext()) {

                        Student st = it.next();

                        if (st.studentId == deleteId) {

                            it.remove();

                            System.out.println("Record Deleted");
                        }
                    }

                    break;

                case 9:
                    System.out.println("Exiting Program...");
                    break;

                default:
                    System.out.println("Invalid Choice");

            }

        } while (choice != 9);

    }
}
