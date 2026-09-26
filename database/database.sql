CREATE DATABASE student_grade_tracker;

USE student_grade_tracker;

CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    marks INT,
    grade VARCHAR(5)
);