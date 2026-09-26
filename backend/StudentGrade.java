import java.util.ArrayList;

public class StudentGrade {

    static ArrayList<String> names = new ArrayList<>();
    static ArrayList<Integer> marks = new ArrayList<>();

    public static String addStudent(String name, int mark) {
        names.add(name);
        marks.add(mark);
        return "Student added successfully!";
    }

    public static String getGrade(int mark) {
        if (mark >= 90) {
            return "A+";
        } else if (mark >= 80) {
            return "A";
        } else if (mark >= 70) {
            return "B";
        } else if (mark >= 60) {
            return "C";
        } else {
            return "F";
        }
    }

    public static void main(String[] args) {

        addStudent("Dimpul", 85);

        System.out.println("Student Grade Tracker");
        System.out.println("---------------------");

        for (int i = 0; i < names.size(); i++) {
            System.out.println(
                names.get(i) + " - " +
                marks.get(i) + " - Grade: " +
                getGrade(marks.get(i))
            );
        }
    }
}