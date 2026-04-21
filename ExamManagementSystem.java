import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// ── Student ───────────────────────────────
class Student {
    static int counter = 1;
    int id;
    String name, email;
    List<Integer> enrolledExamIds = new ArrayList<>();

    Student(String name, String email) {
        this.id = counter++;
        this.name = name;
        this.email = email;
    }

    public String toString() {
        return String.format("[S%03d] %-20s | %s", id, name, email);
    }
}

// ── Question ──────────────────────────────
class Question {
    String questionText;
    String[] options;
    int correctOption; // 1-4

    Question(String questionText, String[] options, int correctOption) {
        this.questionText  = questionText;
        this.options       = options;
        this.correctOption = correctOption;
    }

    void display(int qNo) {
        System.out.println("Q" + qNo + ". " + questionText);
        for (int i = 0; i < options.length; i++)
            System.out.println("   " + (i + 1) + ". " + options[i]);
    }
}

// ── Exam ──────────────────────────────────
class Exam {
    static int counter = 1;
    int id;
    String title, subject;
    int durationMinutes, totalMarks;
    List<Question> questions = new ArrayList<>();
    boolean isActive;
    LocalDateTime createdAt;

    Exam(String title, String subject, int durationMinutes) {
        this.id              = counter++;
        this.title           = title;
        this.subject         = subject;
        this.durationMinutes = durationMinutes;
        this.totalMarks      = 0;
        this.isActive        = true;
        this.createdAt       = LocalDateTime.now();
    }

    void addQuestion(Question q) {
        questions.add(q);
        totalMarks += 10; // 10 marks per question
    }

    public String toString() {
        return String.format("[E%03d] %-25s | %-15s | %d Qs | %d marks | %s",
            id, title, subject, questions.size(), totalMarks,
            isActive ? "Active" : "Inactive");
    }
}

// ── Result ────────────────────────────────
class Result {
    int studentId, examId;
    int score, totalMarks;
    String grade;
    LocalDateTime attemptedAt;

    Result(int studentId, int examId, int score, int totalMarks) {
        this.studentId   = studentId;
        this.examId      = examId;
        this.score       = score;
        this.totalMarks  = totalMarks;
        this.grade       = calcGrade(score, totalMarks);
        this.attemptedAt = LocalDateTime.now();
    }

    String calcGrade(int score, int total) {
        double pct = (score * 100.0) / total;
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B";
        if (pct >= 60) return "C";
        if (pct >= 50) return "D";
        return "F";
    }

    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("  Student: S%03d | Exam: E%03d | Score: %d/%d | Grade: %s | %s",
            studentId, examId, score, totalMarks, grade, attemptedAt.format(fmt));
    }
}

// ── Exam Management System ────────────────
public class ExamManagementSystem {
    static List<Student>  students = new ArrayList<>();
    static List<Exam>     exams    = new ArrayList<>();
    static List<Result>   results  = new ArrayList<>();
    static Scanner        sc       = new Scanner(System.in);

    public static void main(String[] args) {
        seedData();
        while (true) {
            printMenu();
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();
            System.out.println();
            switch (choice) {
                case "1"  -> listStudents();
                case "2"  -> addStudent();
                case "3"  -> listExams();
                case "4"  -> createExam();
                case "5"  -> addQuestion();
                case "6"  -> viewExam();
                case "7"  -> enrollStudent();
                case "8"  -> attemptExam();
                case "9"  -> viewResults();
                case "10" -> studentReport();
                case "11" -> examReport();
                case "12" -> toppers();
                case "0"  -> { System.out.println("Goodbye!"); return; }
                default   -> System.out.println("Invalid choice.");
            }
            System.out.println();
        }
    }

    // ── Menu ──────────────────────────────
    static void printMenu() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║     EXAM MANAGEMENT SYSTEM       ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  STUDENTS                         ║");
        System.out.println("║   1. List students                ║");
        System.out.println("║   2. Add student                  ║");
        System.out.println("║  EXAMS                            ║");
        System.out.println("║   3. List exams                   ║");
        System.out.println("║   4. Create exam                  ║");
        System.out.println("║   5. Add question to exam         ║");
        System.out.println("║   6. View exam details            ║");
        System.out.println("║  ENROLLMENT & ATTEMPT             ║");
        System.out.println("║   7. Enroll student in exam       ║");
        System.out.println("║   8. Attempt exam                 ║");
        System.out.println("║  RESULTS & REPORTS                ║");
        System.out.println("║   9. View all results             ║");
        System.out.println("║  10. Student report               ║");
        System.out.println("║  11. Exam report                  ║");
        System.out.println("║  12. Top scorers                  ║");
        System.out.println("║   0. Exit                         ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    // ── Students ──────────────────────────
    static void listStudents() {
        if (students.isEmpty()) { System.out.println("No students."); return; }
        students.forEach(System.out::println);
    }

    static void addStudent() {
        System.out.print("Name  : "); String name  = sc.nextLine();
        System.out.print("Email : "); String email = sc.nextLine();
        students.add(new Student(name, email));
        System.out.println("Student added!");
    }

    // ── Exams ─────────────────────────────
    static void listExams() {
        if (exams.isEmpty()) { System.out.println("No exams."); return; }
        exams.forEach(System.out::println);
    }

    static void createExam() {
        System.out.print("Title    : "); String title   = sc.nextLine();
        System.out.print("Subject  : "); String subject = sc.nextLine();
        System.out.print("Duration (minutes): "); int dur = Integer.parseInt(sc.nextLine().trim());
        exams.add(new Exam(title, subject, dur));
        System.out.println("Exam created!");
    }

    static void addQuestion() {
        System.out.print("Exam ID (number): "); int eid = Integer.parseInt(sc.nextLine().trim());
        Exam exam = findExam(eid);
        if (exam == null) { System.out.println("Exam not found."); return; }

        System.out.print("Question text: "); String qtext = sc.nextLine();
        String[] opts = new String[4];
        for (int i = 0; i < 4; i++) {
            System.out.print("Option " + (i + 1) + ": ");
            opts[i] = sc.nextLine();
        }
        System.out.print("Correct option (1-4): "); int correct = Integer.parseInt(sc.nextLine().trim());
        exam.addQuestion(new Question(qtext, opts, correct));
        System.out.println("Question added! (10 marks)");
    }

    static void viewExam() {
        System.out.print("Exam ID: "); int eid = Integer.parseInt(sc.nextLine().trim());
        Exam exam = findExam(eid);
        if (exam == null) { System.out.println("Exam not found."); return; }

        System.out.println("┌──────────────────────────────────────");
        System.out.println("│ ID       : E" + String.format("%03d", exam.id));
        System.out.println("│ Title    : " + exam.title);
        System.out.println("│ Subject  : " + exam.subject);
        System.out.println("│ Duration : " + exam.durationMinutes + " mins");
        System.out.println("│ Total    : " + exam.totalMarks + " marks");
        System.out.println("│ Status   : " + (exam.isActive ? "Active" : "Inactive"));
        System.out.println("├── Questions ──────────────────────────");
        if (exam.questions.isEmpty()) System.out.println("│ No questions added.");
        else for (int i = 0; i < exam.questions.size(); i++)
            exam.questions.get(i).display(i + 1);
        System.out.println("└──────────────────────────────────────");
    }

    // ── Enroll ────────────────────────────
    static void enrollStudent() {
        System.out.print("Student ID (number): "); int sid = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Exam ID (number)   : "); int eid = Integer.parseInt(sc.nextLine().trim());
        Student student = findStudent(sid);
        Exam    exam    = findExam(eid);
        if (student == null) { System.out.println("Student not found."); return; }
        if (exam    == null) { System.out.println("Exam not found.");    return; }
        if (student.enrolledExamIds.contains(eid)) {
            System.out.println("Already enrolled!"); return;
        }
        student.enrolledExamIds.add(eid);
        System.out.println(student.name + " enrolled in \"" + exam.title + "\"");
    }

    // ── Attempt Exam ──────────────────────
    static void attemptExam() {
        System.out.print("Student ID (number): "); int sid = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Exam ID (number)   : "); int eid = Integer.parseInt(sc.nextLine().trim());

        Student student = findStudent(sid);
        Exam    exam    = findExam(eid);
        if (student == null) { System.out.println("Student not found."); return; }
        if (exam    == null) { System.out.println("Exam not found.");    return; }
        if (!student.enrolledExamIds.contains(eid)) {
            System.out.println("Student not enrolled in this exam."); return;
        }
        if (exam.questions.isEmpty()) {
            System.out.println("No questions in this exam."); return;
        }

        // Check already attempted
        boolean alreadyDone = results.stream()
            .anyMatch(r -> r.studentId == sid && r.examId == eid);
        if (alreadyDone) { System.out.println("Already attempted this exam!"); return; }

        System.out.println("\n=== Starting: " + exam.title + " ===");
        System.out.println("Total Questions: " + exam.questions.size()
            + " | Marks per Q: 10 | Duration: " + exam.durationMinutes + " mins\n");

        int score = 0;
        for (int i = 0; i < exam.questions.size(); i++) {
            Question q = exam.questions.get(i);
            q.display(i + 1);
            System.out.print("Your answer (1-4): ");
            int ans = Integer.parseInt(sc.nextLine().trim());
            if (ans == q.correctOption) {
                score += 10;
                System.out.println("Correct!\n");
            } else {
                System.out.println("Wrong! Correct was: " + q.correctOption + "\n");
            }
        }

        Result result = new Result(sid, eid, score, exam.totalMarks);
        results.add(result);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Exam Complete!");
        System.out.println("Score : " + score + " / " + exam.totalMarks);
        System.out.println("Grade : " + result.grade);
        double pct = (score * 100.0) / exam.totalMarks;
        System.out.printf("Marks : %.1f%%%n", pct);
        System.out.println(pct >= 50 ? "Result: PASS" : "Result: FAIL");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    // ── Reports ───────────────────────────
    static void viewResults() {
        if (results.isEmpty()) { System.out.println("No results yet."); return; }
        results.forEach(System.out::println);
    }

    static void studentReport() {
        System.out.print("Student ID (number): "); int sid = Integer.parseInt(sc.nextLine().trim());
        Student student = findStudent(sid);
        if (student == null) { System.out.println("Student not found."); return; }

        System.out.println("── Report for: " + student.name + " ──────────────────");
        System.out.println("  Email    : " + student.email);
        System.out.println("  Enrolled : " + student.enrolledExamIds.size() + " exam(s)");

        List<Result> myResults = results.stream()
            .filter(r -> r.studentId == sid).toList();

        if (myResults.isEmpty()) { System.out.println("  No attempts yet."); return; }

        int total = 0;
        for (Result r : myResults) {
            Exam e = findExam(r.examId);
            String examTitle = e != null ? e.title : "Unknown";
            System.out.printf("  %-25s | %d/%d | Grade: %s%n",
                examTitle, r.score, r.totalMarks, r.grade);
            total += r.score;
        }
        System.out.println("  ─────────────────────────────────────────");
        System.out.printf("  Average Score: %.1f%n", (double) total / myResults.size());
    }

    static void examReport() {
        System.out.print("Exam ID (number): "); int eid = Integer.parseInt(sc.nextLine().trim());
        Exam exam = findExam(eid);
        if (exam == null) { System.out.println("Exam not found."); return; }

        List<Result> examResults = results.stream()
            .filter(r -> r.examId == eid).toList();

        System.out.println("── Exam Report: " + exam.title + " ─────────────────");
        System.out.println("  Subject    : " + exam.subject);
        System.out.println("  Attempts   : " + examResults.size());

        if (examResults.isEmpty()) { System.out.println("  No attempts yet."); return; }

        int sum = 0, highest = 0, lowest = exam.totalMarks;
        for (Result r : examResults) {
            Student s = findStudent(r.studentId);
            String sname = s != null ? s.name : "Unknown";
            System.out.printf("  %-20s | %d/%d | Grade: %s%n",
                sname, r.score, r.totalMarks, r.grade);
            sum += r.score;
            if (r.score > highest) highest = r.score;
            if (r.score < lowest)  lowest  = r.score;
        }
        System.out.println("  ─────────────────────────────────────────");
        System.out.printf("  Average : %.1f | Highest: %d | Lowest: %d%n",
            (double) sum / examResults.size(), highest, lowest);
        long passed = examResults.stream()
            .filter(r -> (r.score * 100.0 / r.totalMarks) >= 50).count();
        System.out.printf("  Pass Rate: %.1f%%%n",
            (passed * 100.0) / examResults.size());
    }

    static void toppers() {
        if (results.isEmpty()) { System.out.println("No results yet."); return; }
        System.out.println("── Top Scorers ──────────────────────────────");
        results.stream()
            .sorted((a, b) -> Double.compare(
                (b.score * 100.0 / b.totalMarks),
                (a.score * 100.0 / a.totalMarks)))
            .limit(5)
            .forEach(r -> {
                Student s = findStudent(r.studentId);
                Exam    e = findExam(r.examId);
                System.out.printf("  %-20s | %-20s | %d/%d | %s%n",
                    s != null ? s.name : "?",
                    e != null ? e.title : "?",
                    r.score, r.totalMarks, r.grade);
            });
    }

    // ── Helpers ───────────────────────────
    static Student findStudent(int id) {
        return students.stream().filter(s -> s.id == id).findFirst().orElse(null);
    }

    static Exam findExam(int id) {
        return exams.stream().filter(e -> e.id == id).findFirst().orElse(null);
    }

    // ── Seed Data ─────────────────────────
    static void seedData() {
        // Students
        students.add(new Student("Arjun Kumar",  "arjun@mail.com"));
        students.add(new Student("Priya Sharma", "priya@mail.com"));
        students.add(new Student("Ravi Babu",    "ravi@mail.com"));

        // Exam 1 - Java
        Exam e1 = new Exam("Java Basics Test", "Java", 30);
        e1.addQuestion(new Question("What is JVM?",
            new String[]{"Java Virtual Machine","Java Variable Method","Java Void Main","None"}, 1));
        e1.addQuestion(new Question("Which keyword creates an object?",
            new String[]{"class","new","static","void"}, 2));
        e1.addQuestion(new Question("What is the default value of int?",
            new String[]{"null","1","0","-1"}, 3));
        exams.add(e1);

        // Exam 2 - Python
        Exam e2 = new Exam("Python Fundamentals", "Python", 20);
        e2.addQuestion(new Question("Which symbol is used for comments in Python?",
            new String[]{"//","/* */","#","--"}, 3));
        e2.addQuestion(new Question("What is the output of print(2**3)?",
            new String[]{"6","8","9","None"}, 2));
        exams.add(e2);

        // Enroll students
        students.get(0).enrolledExamIds.add(1);
        students.get(1).enrolledExamIds.add(1);
        students.get(2).enrolledExamIds.add(2);

        System.out.println("Demo data loaded. 3 students, 2 exams ready.\n");
    }
}
