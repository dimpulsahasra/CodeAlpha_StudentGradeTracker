import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class StudentGradeServer {

    static String DB_URL = "jdbc:mysql://localhost:3306/student_grade_tracker";
    static String DB_USER = "root";
    static String DB_PASSWORD = "root";

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        // GET STUDENTS
        server.createContext("/students", (HttpExchange exchange) -> {

            exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Origin", "*"
            );

            try {
                Connection conn = DriverManager.getConnection(
                        DB_URL, DB_USER, DB_PASSWORD
                );

                var stmt = conn.createStatement();

                var rs = stmt.executeQuery(
                        "SELECT id, name, marks, grade FROM students"
                );

                StringBuilder json = new StringBuilder("[");

                while (rs.next()) {
                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id")).append(",")
                            .append("\"name\":\"").append(rs.getString("name")).append("\",")
                            .append("\"marks\":").append(rs.getInt("marks")).append(",")
                            .append("\"grade\":\"").append(rs.getString("grade")).append("\"")
                            .append("},");
                }

                if (json.length() > 1) {
                    json.setLength(json.length() - 1);
                }

                json.append("]");

                byte[] response = json.toString().getBytes();

                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.getResponseBody().close();

                rs.close();
                stmt.close();
                conn.close();

            } catch (Exception e) {

                e.printStackTrace();

                String response = "Database error";

                exchange.sendResponseHeaders(500, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
            }
        });


        // ADD STUDENT
        server.createContext("/addStudent", (HttpExchange exchange) -> {

            exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Origin", "*"
            );

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                String data = new String(
                        exchange.getRequestBody().readAllBytes()
                );

                System.out.println(
                        "Received from frontend: " + data
                );

                try {

                    String[] parts = data.split(",");

                    String name = parts[0];
                    int marks = Integer.parseInt(parts[1]);

                    String grade;

                    if (marks >= 90) {
                        grade = "A+";
                    } else if (marks >= 80) {
                        grade = "A";
                    } else if (marks >= 70) {
                        grade = "B";
                    } else if (marks >= 60) {
                        grade = "C";
                    } else {
                        grade = "F";
                    }

                    Connection conn = DriverManager.getConnection(
                            DB_URL, DB_USER, DB_PASSWORD
                    );

                    String sql =
                            "INSERT INTO students (name, marks, grade) VALUES (?, ?, ?)";

                    PreparedStatement stmt =
                            conn.prepareStatement(sql);

                    stmt.setString(1, name);
                    stmt.setInt(2, marks);
                    stmt.setString(3, grade);

                    stmt.executeUpdate();

                    stmt.close();
                    conn.close();

                    String response =
                            "Student saved successfully!";

                    exchange.sendResponseHeaders(
                            200, response.length()
                    );

                    exchange.getResponseBody()
                            .write(response.getBytes());

                    exchange.getResponseBody().close();

                } catch (Exception e) {

                    e.printStackTrace();

                    String response = "Database error";

                    exchange.sendResponseHeaders(
                            500, response.length()
                    );

                    exchange.getResponseBody()
                            .write(response.getBytes());

                    exchange.getResponseBody().close();
                }

            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });


        // DELETE STUDENT USING POST
        server.createContext("/deleteStudent", (HttpExchange exchange) -> {

            exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Origin", "*"
            );

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                String data = new String(
                        exchange.getRequestBody().readAllBytes()
                );

                System.out.println(
                        "Delete request for ID: " + data
                );

                try {

                    int id = Integer.parseInt(data.trim());

                    Connection conn = DriverManager.getConnection(
                            DB_URL, DB_USER, DB_PASSWORD
                    );

                    String sql =
                            "DELETE FROM students WHERE id = ?";

                    PreparedStatement stmt =
                            conn.prepareStatement(sql);

                    stmt.setInt(1, id);

                    int rowsDeleted = stmt.executeUpdate();

                    stmt.close();
                    conn.close();

                    String response;

                    if (rowsDeleted > 0) {
                        response = "Student deleted successfully!";
                    } else {
                        response = "Student not found!";
                    }

                    exchange.sendResponseHeaders(
                            200, response.length()
                    );

                    exchange.getResponseBody()
                            .write(response.getBytes());

                    exchange.getResponseBody().close();

                } catch (Exception e) {

                    e.printStackTrace();

                    String response = "Delete failed";

                    exchange.sendResponseHeaders(
                            500, response.length()
                    );

                    exchange.getResponseBody()
                            .write(response.getBytes());

                    exchange.getResponseBody().close();
                }

            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });


        // START SERVER
        server.start();

        System.out.println(
                "Java Backend Server Started!"
        );

        System.out.println(
                "http://localhost:8080"
        );
    }
}