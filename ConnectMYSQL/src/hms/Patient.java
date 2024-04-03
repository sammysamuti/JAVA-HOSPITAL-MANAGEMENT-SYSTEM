package hms;

import com.mysql.cj.xdevapi.Statement;
import static hms.Doctor.connection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public class Patient extends Person {
    private String bloodGroup;
    

    static Connection connection = DatabaseConn.connectDB();

 public Patient(String name, String phoneNumber, String gender, Date dob, String email, String bloodGroup) throws ParseException {
        // Set fields directly without using super
        this.setName(name);
        this.setPhoneNumber(phoneNumber);
        this.setGender(gender);
        this.setDateOfBirth((java.sql.Date) dob);
        this.setEmail(email);

        // Set the new field specific to Patient
        this.bloodGroup = bloodGroup;
    }

    public Patient() {
        
    }


    public String getBloodGroup() {
        return bloodGroup;
    }

    // Setter method for bloodGroup
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

public static void addPatient(Patient patient, Connection connection) throws SQLException {
    String query = "INSERT INTO patients (name, phone_number, gender, date_of_birth, email, blood_group) VALUES (?, ?, ?, ?, ?, ?)";

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        preparedStatement.setString(1, patient.getName());
        preparedStatement.setString(2, patient.getPhoneNumber());
        preparedStatement.setString(3, patient.getGender());
        preparedStatement.setDate(4, patient.getDateOfBirth());
        preparedStatement.setString(5, patient.getEmail());
        preparedStatement.setString(6, patient.getBloodGroup());

        preparedStatement.executeUpdate();
        System.out.println("Patient added to the database successfully!");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public static void addPatientToDatabase(String name, String phone, String gender, Date dob, String email, String bloodGroup) throws ParseException {
    try {
        if (!name.isEmpty() && !phone.isEmpty() && !gender.isEmpty()) {
            System.out.println("Constructing the Patient object...");
            // Constructing the Patient object
            Patient patient = new Patient(name, phone, gender, dob, email, bloodGroup);

            System.out.println("Invoking addPatient method...");
            // Assuming addPatient is a static method, invoke it on the Patient class
            Patient.addPatient(patient, DatabaseConn.connectDB());

            System.out.println("Connection closed.");
        } else {
            System.out.println("Please fill in all required fields.");
            
        }
    } catch (SQLException e) {
        e.printStackTrace(); 
    }
}
  public static int getAndIncrementLatestPatientID() {
        String query = "SELECT MAX(patient_id) FROM Patients";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int latestPatientID = resultSet.getInt(1);
                return latestPatientID + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
    


    public static int getPatientIdByName(String patientName) {
        String query = "SELECT patient_id FROM Patients WHERE name = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, patientName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("patient_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if patient with the given name is not found
    }


    public static Patient getPatientData(int patientId) {
        String query = "SELECT * FROM Patients WHERE patient_id = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, patientId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Create a new Patient object with retrieved data
                Patient patient = new Patient();
                patient.setPersonID(resultSet.getInt("patient_id"));
                patient.setName(resultSet.getString("name"));
                patient.setPhoneNumber(resultSet.getString("phone_number"));
                patient.setGender(resultSet.getString("gender"));
                patient.setDateOfBirth(resultSet.getDate("date_of_birth"));
                patient.setEmail(resultSet.getString("email"));
                patient.setBloodGroup(resultSet.getString("blood_group"));

                return patient;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Return null if patient with the given ID is not found
    }



    public static ObservableList<Patient> getAllPatients() {
        List<Patient> patientsList = new ArrayList<>();
        String query = "SELECT * FROM Patients";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Patient patient = new Patient();
                patient.setPersonID(resultSet.getInt("patient_id"));
                patient.setName(resultSet.getString("name"));
                patient.setPhoneNumber(resultSet.getString("phone_number"));
                patient.setGender(resultSet.getString("gender"));
                patient.setDateOfBirth(resultSet.getDate("date_of_birth"));
                patient.setEmail(resultSet.getString("email"));
                patient.setBloodGroup(resultSet.getString("blood_group"));

                patientsList.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return FXCollections.observableArrayList(patientsList);
    }
public static int searchPatient(String name) {
        String query = "SELECT patient_id FROM patients WHERE name = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("patient_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return 1; 
    }
public static void fetchDataAndPopulateComboBox(ComboBox<String> comboBox) {
        try (
             PreparedStatement statement = connection.prepareStatement("SELECT name FROM patients");
             ResultSet resultSet = statement.executeQuery()) {

            // Process the ResultSet and populate the ComboBox
            while (resultSet.next()) {
                String patientName = resultSet.getString("name");

                // Add doctor name to the ComboBox
                comboBox.getItems().add(patientName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    

}
  public static boolean isPatientExistsByEmail(String email) {
    try (Connection connection = DatabaseConn.connectDB();
         PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM patients WHERE email = ?")) {

        statement.setString(1, email);

        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Return true if the count is greater than 0
            }
        }
    } catch (SQLException e) {
        e.printStackTrace(); 
    }
    return false; // Return false in case of an error or no matching record found
}
  public static boolean isPatientExistsById(int patientId) {
    try (Connection connection = DatabaseConn.connectDB();
         PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM patients WHERE patient_id = ?")) {

        statement.setInt(1, patientId);

        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Return true if the count is greater than 0
            }
        }
    } catch (SQLException e) {
        e.printStackTrace(); 
    }
    return false; // Return false in case of an error or no matching record found
}

}
