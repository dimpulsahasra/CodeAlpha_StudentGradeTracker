console.log("Student Grade Tracker loaded!");


// LOAD STUDENTS FROM DATABASE
function loadStudents() {

    fetch("http://localhost:8080/students")
        .then(response => response.json())
        .then(students => {

            const studentList =
                document.getElementById("studentList");

            studentList.innerHTML = "";

            students.forEach(student => {

                studentList.innerHTML += `
                    <div class="student-record">

                        <span>
                            ${student.name} -
                            ${student.marks} -
                            Grade: ${student.grade}
                        </span>

                        <button onclick="deleteStudent(${student.id})">
                            🗑️ Delete
                        </button>

                    </div>
                `;
            });

            // UPDATE DASHBOARD
            updateDashboard(students);
        })
        .catch(error => {
            console.error("Error loading students:", error);
        });
}


// DASHBOARD
function updateDashboard(students) {

    const total =
        students.length;

    let totalMarks = 0;
    let highest = 0;

    students.forEach(student => {

        totalMarks += Number(student.marks);

        if (Number(student.marks) > highest) {
            highest = Number(student.marks);
        }
    });

    const average =
        total > 0
            ? (totalMarks / total).toFixed(1)
            : 0;

    let performance = "-";

    if (average >= 90) {
        performance = "Excellent";
    } else if (average >= 75) {
        performance = "Very Good";
    } else if (average >= 60) {
        performance = "Good";
    } else if (average > 0) {
        performance = "Needs Improvement";
    }


    document.getElementById("totalStudents").textContent =
        total;

    document.getElementById("averageMarks").textContent =
        average;

    document.getElementById("highestMarks").textContent =
        highest;

    document.getElementById("performance").textContent =
        performance;
}


// ADD STUDENT
function addStudent() {

    const name =
        document.getElementById("studentName").value;

    const marks =
        document.getElementById("marks").value;

    if (name === "" || marks === "") {
        alert("Please enter student name and marks");
        return;
    }

    if (marks < 0 || marks > 100) {
        alert("Marks must be between 0 and 100");
        return;
    }

    const grade =
        marks >= 90 ? "A+" :
        marks >= 80 ? "A" :
        marks >= 70 ? "B" :
        marks >= 60 ? "C" : "F";


    fetch("http://localhost:8080/addStudent", {

        method: "POST",

        headers: {
            "Content-Type": "text/plain"
        },

        body: name + "," + marks
    })

    .then(response => response.text())

    .then(data => {

        console.log(data);

        alert(
            "Student: " + name +
            "\nMarks: " + marks +
            "\nGrade: " + grade
        );

        document.getElementById("studentName").value = "";
        document.getElementById("marks").value = "";

        loadStudents();
    })

    .catch(error => {

        console.error("Backend error:", error);

        alert("Java backend connection failed!");
    });
}


// DELETE STUDENT
function deleteStudent(id) {

    console.log("DELETE CLICKED:", id);

    if (!confirm("Are you sure you want to delete this student?")) {
        return;
    }

    fetch("http://localhost:8080/deleteStudent", {

        method: "POST",

        headers: {
            "Content-Type": "text/plain"
        },

        body: id.toString()
    })

    .then(response => response.text())

    .then(data => {

        console.log("Delete response:", data);

        if (data === "Student deleted successfully!") {

            alert("Student deleted successfully!");

            loadStudents();

        } else {

            alert(data);
        }
    })

    .catch(error => {

        console.error("Delete error:", error);

        alert("Failed to delete student!");
    });
}


// LOAD DATABASE RECORDS
loadStudents();