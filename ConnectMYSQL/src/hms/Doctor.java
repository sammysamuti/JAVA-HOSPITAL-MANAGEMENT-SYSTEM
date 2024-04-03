package hms;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;


public class Doctor extends Person{
    private Boolean is_processed = false;
    private Date joiningDate;
    private BigDecimal salary;
    private int doctorID;

   static Connection connection = DatabaseConn.connectDB();



  public Doctor(int personID, String name, String phoneNumber, String gender, Date dateOfBirth, String email,
                  Boolean is_processed, Date joiningDate, BigDecimal salary) {
        super(personID, name, phoneNumber, gender, dateOfBirth, email);
        this.is_processed = is_processed;
        this.joiningDate = joiningDate;
        this.salary = salary;
    }

    // Empty constructor
    public Doctor() {
        super(); // Calls the default constructor of the superclass if available
    }
    
 // Getter for doctorId
    public int getDoctorID() {
        return doctorID;
    }

    // Setter for doctorId
    public void setDoctorID(int doctorId) {
        this.doctorID = doctorId;
    }
public Boolean getIsProcessed() {
    return is_processed;
}

public void setIsProcessed(Boolean isProcessed) {
    this.is_processed = isProcessed;
}

public static int searchDoctor(String name) {
        String query = "SELECT doc_id FROM Doctors WHERE name = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("doc_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return -1; 
    }

public static ObservableList<Appointment> getAppointmentsForDoctor(int doctorID) {
    ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    if (doctorID != -1) {
        String query = "SELECT a.appointment_id, a.appointment_time, a.appointment_date, a.doctor_id, a.patient_id, p.name, a.is_processed " +
                       "FROM Appointments a " +
                       "JOIN Patients p ON a.patient_id = p.patient_id " +
                       "WHERE a.doctor_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, doctorID);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                   
                    Map<String, Object> details = new HashMap<>();
                    details.put("name", resultSet.getString("name"));
                    details.put("isProcessed", resultSet.getBoolean("is_processed"));

                    // Create the Appointment without patientName and isProcessed
                    Appointment appointment = new Appointment(
                            resultSet.getInt("appointment_id"),
                            resultSet.getTime("appointment_time").toLocalTime(),
                            resultSet.getDate("appointment_date").toLocalDate(),
                            resultSet.getInt("doctor_id"),
                            resultSet.getInt("patient_id"),
                            details
                    );

                    appointments.add(appointment);
                }

                System.out.println(appointments);
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }
    return appointments;
}
    

public static void fetchDataAndPopulateComboBox(ComboBox<String> comboBox) {
        try (
             PreparedStatement statement = connection.prepareStatement("SELECT name FROM doctors");
             ResultSet resultSet = statement.executeQuery()) {

            // Process the ResultSet and populate the ComboBox
            while (resultSet.next()) {
                String doctorName = resultSet.getString("name");

                // Add doctor name to the ComboBox
                comboBox.getItems().add(doctorName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void setJoiningDate(Date date) {
        this.joiningDate = date;
    }

    // Getter for joiningDate
    public Date getJoiningDate() {
        return joiningDate;
    }

    // Setter for salary
    public void setSalary(BigDecimal bigDecimal) {
        this.salary = bigDecimal;
    }

    // Getter for salary
    public BigDecimal getSalary() {
        return salary;
    }


    public static List<Doctor> getAllDoctorsData() {
        List<Doctor> doctors = new ArrayList<>();
        String query = "SELECT * FROM Doctors";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                // Create a new Doctor object with retrieved data
                Doctor doctor = new Doctor();
                doctor.setPersonID(resultSet.getInt("doc_id"));
                doctor.setName(resultSet.getString("name"));
                doctor.setPhoneNumber(resultSet.getString("phone_number"));
                doctor.setGender(resultSet.getString("gender"));
                doctor.setDateOfBirth(resultSet.getDate("date_of_birth"));
                doctor.setEmail(resultSet.getString("email"));
                doctor.setJoiningDate(resultSet.getDate("joining_date"));
                doctor.setSalary(resultSet.getBigDecimal("salary"));

                doctors.add(doctor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doctors;
    }


    public static boolean addDoctor(Doctor doctor, Connection connection) {
        String insertQuery = "INSERT INTO Doctors (doc_id, name, phone_number, gender, date_of_birth, email, joining_date, salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String checkQuery = "SELECT COUNT(*) FROM Doctors WHERE doc_id = ?";
    
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
    
            // Set parameters for the insert query
            insertStatement.setInt(1, doctor.getPersonID());
            insertStatement.setString(2, doctor.getName());
            insertStatement.setString(3, doctor.getPhoneNumber());
            insertStatement.setString(4, doctor.getGender());
            insertStatement.setObject(5, doctor.getDateOfBirth());
            insertStatement.setString(6, doctor.getEmail());
            insertStatement.setObject(7, doctor.getJoiningDate());
            insertStatement.setBigDecimal(8, doctor.getSalary());
    
            // Execute the insert query
            int rowsAffected = insertStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                // If the insertion was successful, check if the doctor exists
                checkStatement.setInt(1, doctor.getPersonID());
                ResultSet resultSet = checkStatement.executeQuery();
    
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    System.out.println("Doctor Added Successfully!");
                    return true;
                }
            }
    
            return false; // If there is any failure
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Insertion or checking failed
        }
    }

    public void viewInfo() {
        
        throw new UnsupportedOperationException("Unimplemented method 'viewInfo'");
    }
    public static int searchDoctor(int id) {
        String query = "SELECT is_doctor FROM users WHERE user_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return id; 
    }
    public static List<Integer> getAllDoctorIDsFromDatabase() {
        List<Integer> doctorIDs = new ArrayList<>();

        try {
            String query = "SELECT doc_id FROM doctors";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int doctorID = resultSet.getInt("doc_id");
                    doctorIDs.add(doctorID);
                }
            }
        } catch (SQLException e) {
            // Handle exceptions according to your application's needs
            e.printStackTrace();
        }

        return doctorIDs;
    }
}


