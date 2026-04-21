import java.util.ArrayList;
import java.util.Scanner;

public class EmployeeManagement {

    // Employee class
    static class Employee {
        int id;
        String name;
        String department;
        double salary;

        Employee(int id, String name, String department, double salary) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        void display() {
            System.out.println("ID: " + id + " | Name: " + name +
                " | Department: " + department + " | Salary: " + salary);
        }
    }

    static ArrayList<Employee> list = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static int nextId = 1;

    public static void main(String[] args) {
        int choice;

        do {
            System.out.println("\n===== Employee Management System =====");
            System.out.println("1. Add Employee");
            System.out.println("2. View All Employees");
            System.out.println("3. Search Employee by ID");
            System.out.println("4. Update Employee");
            System.out.println("5. Delete Employee");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addEmployee();
                case 2 -> viewAll();
                case 3 -> searchById();
                case 4 -> updateEmployee();
                case 5 -> deleteEmployee();
                case 6 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice.");
            }

        } while (choice != 6);
    }

    static void addEmployee() {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Department: ");
        String dept = sc.nextLine();
        System.out.print("Enter Salary: ");
        double salary = sc.nextDouble();
        sc.nextLine();

        list.add(new Employee(nextId++, name, dept, salary));
        System.out.println("Employee added successfully!");
    }

    static void viewAll() {
        if (list.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        System.out.println("\n--- Employee List ---");
        for (Employee e : list) {
            e.display();
        }
    }

    static void searchById() {
        System.out.print("Enter Employee ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        for (Employee e : list) {
            if (e.id == id) {
                e.display();
                return;
            }
        }
        System.out.println("Employee not found.");
    }

    static void updateEmployee() {
        System.out.print("Enter Employee ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        for (Employee e : list) {
            if (e.id == id) {
                System.out.print("New Name (" + e.name + "): ");
                e.name = sc.nextLine();
                System.out.print("New Department (" + e.department + "): ");
                e.department = sc.nextLine();
                System.out.print("New Salary (" + e.salary + "): ");
                e.salary = sc.nextDouble();
                sc.nextLine();
                System.out.println("Employee updated successfully!");
                return;
            }
        }
        System.out.println("Employee not found.");
    }

    static void deleteEmployee() {
        System.out.print("Enter Employee ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        for (Employee e : list) {
            if (e.id == id) {
                list.remove(e);
                System.out.println("Employee deleted successfully!");
                return;
            }
        }
        System.out.println("Employee not found.");
    }
}