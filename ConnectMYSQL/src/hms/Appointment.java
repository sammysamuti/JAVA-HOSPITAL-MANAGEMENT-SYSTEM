package hms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.scene.control.ComboBox;

public class Appointment {
    public int appointmentID;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private int patientId;
    private int doctorId;
    private String doctorName;
    private String patientName;
    
//    public String patientName;
    public int isProcessed;
    
    private Map<String, Object> details;

    public Appointment(int appointmentID, LocalTime appointmentTime, LocalDate appointmentDate, int doctorID, int patientID, int isProcessed) {
        this.appointmentID = appointmentID;
        this.appointmentTime = appointmentTime; 
        this.appointmentDate = appointmentDate;
        this.doctorId = doctorID;
        this.patientId = patientID;
        this.isProcessed = isProcessed;
    
    }
    
    public Appointment(int appointmentID, LocalTime appointmentTime, LocalDate appointmentDate, int patientID,int doctorID) {
        this.appointmentID = appointmentID;
        this.appointmentTime = appointmentTime;  
        this.appointmentDate = appointmentDate;
        this.doctorId = doctorID;
        this.patientId = patientID;
    
    }
    public Appointment(){}
    public Appointment(int appointmentId, int patientId, int doctorId, LocalDate appointmentDate, LocalTime appointmentTime, int isProcessed) {
        this.appointmentID = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.isProcessed = isProcessed;
    }
    
   


    public Appointment(int int1, LocalTime localTime, LocalDate localDate, int int2, int int3,
            Map<String, Object> details) {
    }


    public Appointment(int int1, LocalTime localTime, LocalDate localDate, int int2, int int3, String string,
            boolean boolean1) {
        //TODO Auto-generated constructor stub
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    // Setter for appointmentID
    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    // Getter for patientId
    public int getPatientID() {
        return patientId;
    }

    // Setter for patientId
    public void setPatientID(int patientId) {
        this.patientId = patientId;
    }
    

    // Getter for doctorId
    public int getDoctorID() {
        return doctorId;
    }

    // Setter for doctorId
    public void setDoctorID(int doctorId) {
        this.doctorId = doctorId;
    }
    
   public String getDoctorName(){
       return doctorName;
   }
   
   public void setDoctorName(String doctorName){
       this.doctorName=doctorName;
   }
   
   public String getPatientName(){
       return patientName;
   }
   
   public void setPatientName(String patientName){
       this.patientName=patientName;
   }

    // Getter for appointmentDate
    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    // Setter for appointmentDate
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    // Getter for appointmentTime
    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    // Setter for appointmentTime
    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    
    // Getter method for isProcessed
    public int isProcessed() {
        return isProcessed;
    }

    // Setter method for isProcessed
    public void setProcessed(int isProcessed) {
        this.isProcessed = isProcessed;
    }



  

    public static int getLatestAppointmentIdFromDB() {
        try (Connection connection = DatabaseConn.connectDB()) {
            String query = "SELECT MAX(appointment_id) FROM Appointments";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) { 
                if (resultSet.next()) {
                    return resultSet.getInt(1) + 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return 1; // Default value if there is an issue with the database query
    }

    public static boolean deleteAppointment(int appointmentID, Connection connection) {
        String deleteQuery = "DELETE FROM Appointments WHERE appointment_id = ?";
    
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            // Set parameter for the delete query
            deleteStatement.setInt(1, appointmentID);
    
            // Execution
            int rowsAffected = deleteStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                System.out.println("Appointment deleted successfully.");
                return true;
            }
    
            System.out.println("Appointment with ID " + appointmentID + " not found.");
            return false; //Not found
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Delete failed
        }
    }
    
     public static List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConn.connectDB();
            String sql = "SELECT * FROM appointments";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int appointmentID = resultSet.getInt("appointment_id");
                int patientId = resultSet.getInt("patient_id");
                int doctorId = resultSet.getInt("doctor_id");
                LocalDate appointmentDate = resultSet.getDate("appointment_date").toLocalDate();
                LocalTime appointmentTime = resultSet.getTime("appointment_time").toLocalTime();
                int isProcessed = resultSet.getInt("is_processed");

                Appointment appointment = new Appointment(patientId,  doctorId, appointmentID, appointmentDate, appointmentTime,
                         isProcessed);
                
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return appointments;
    }

    public static boolean addAppointment(Appointment appointment, Connection connection) {
        String insertQuery = "INSERT INTO Appointments (appointment_id, patient_id, doctor_id, appointment_date, appointment_time) VALUES (?, ?, ?, ?, ?)";
        String checkQuery = "SELECT COUNT(*) FROM Appointments WHERE appointment_id = ?";


        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {

            // Set parameters for the insert query
            insertStatement.setInt(1, appointment.getAppointmentID());
            insertStatement.setInt(2, appointment.getPatientID());
            insertStatement.setInt(3, appointment.getDoctorID());
            insertStatement.setObject(4, appointment.getAppointmentDate());
            insertStatement.setObject(5, appointment.getAppointmentTime());

            // Execute the insert query
            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected > 0) {
                // If the insertion was successful, check if the appointment exists
                checkStatement.setInt(1, appointment.getAppointmentID());
                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // Appointment added successfully
                    return true;
                }
            }

            return false; // If any step fails
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Insertion or checking failed
        }
    }


public static List<LocalTime> getAvailableTimeSlots(List<Appointment> existingAppointments, String docId, LocalDate selectedDate, ComboBox<LocalTime> timeComboBox) {
        List<LocalTime> allTimeSlots = generateAllTimeSlots();
        List<LocalTime> bookedTimeSlots = extractBookedTimeSlotsForDoctorAndDate(docId, selectedDate);

        allTimeSlots.removeAll(bookedTimeSlots);

        return allTimeSlots;
    }

  private static List<LocalTime> generateAllTimeSlots() {
        List<LocalTime> allTimeSlots = new ArrayList<>();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        while (!startTime.isAfter(endTime)) {
            allTimeSlots.add(startTime);
            startTime = startTime.plusMinutes(30);
        }
        return allTimeSlots;
    }
  

private static List<LocalTime> extractBookedTimeSlotsForDoctorAndDate(String doctorId, LocalDate selectedDate) {
    List<LocalTime> bookedTimeSlots = new ArrayList<>();
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    try {
        Connection connection = DatabaseConn.connectDB();
        String sql = "SELECT appointment_time FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        statement = connection.prepareStatement(sql);
        
        statement.setString(1, doctorId);
        statement.setDate(2, java.sql.Date.valueOf(selectedDate));

        resultSet = statement.executeQuery();

        while (resultSet.next()) {
            LocalTime appointmentTime = resultSet.getTime("appointment_time").toLocalTime();
            bookedTimeSlots.add(appointmentTime);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    return bookedTimeSlots;
}



public static void updateAvailableTimeSlots(List<LocalTime> availableTimeSlots, ComboBox<LocalTime> timeComboBox) {
        timeComboBox.getItems().clear();
        timeComboBox.getItems().addAll(availableTimeSlots);

        if (!availableTimeSlots.isEmpty()) {
            timeComboBox.getSelectionModel().selectFirst();
        }
    }

    public String getAppointmentId() {
        return String.valueOf(appointmentID); // Assuming appointmentId is an integer
    }
    
    public static boolean appointmentExists(int doctorID, LocalDate selectedDate, LocalTime appointmentTime, Connection connection) {
    try {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ?";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, doctorID);
        statement.setDate(2, java.sql.Date.valueOf(selectedDate));
        statement.setTime(3, java.sql.Time.valueOf(appointmentTime));

        ResultSet resultSet = statement.executeQuery();
        resultSet.next();

        int count = resultSet.getInt(1);

        resultSet.close();
        statement.close();

        // If count is greater than 0, an appointment exists
        return count > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false; // Handle the exception based on your requirements
    }
}

    
    public static void markAppointmentAsProcessed(Appointment appointment) {
    try {
        Connection connection = DatabaseConn.connectDB();
        String sql = "UPDATE appointments SET is_processed = 1 WHERE appointment_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, appointment.appointmentID);
        
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("Appointment marked as processed in the database.");

            appointment.isProcessed = 1; // Assuming 1 represents "processed"
        } else {
            System.out.println("Failed to mark the appointment as processed.");
        }

        // Close resources
        statement.close();
        connection.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
  public static boolean isAppointmentExistsById(int appointmentId) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_id = ?";

        try (
            Connection connection = DatabaseConn.connectDB();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, appointmentId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return false in case of an error or no result
        return false;
    }
  public static boolean updateAppointment(Appointment appointment, Connection connection) {
        // SQL query to update appointment details
        String updateQuery = "UPDATE appointments " +
                             "SET patient_id = ?, doctor_id = ?, " +
                             "appointment_date = ?, appointment_time = ? " +
                             "WHERE appointment_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            // Set the values for the parameters in the prepared statement
            preparedStatement.setInt(1, appointment.getPatientID());
            preparedStatement.setInt(2, appointment.getDoctorID());
            preparedStatement.setDate(3, java.sql.Date.valueOf(appointment.getAppointmentDate()));
            preparedStatement.setTime(4, java.sql.Time.valueOf(appointment.getAppointmentTime()));
            preparedStatement.setInt(5, appointment.getAppointmentID());

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }
  
  public static Appointment getAppointmentById(int appointmentId, Connection connection) {
        
        String selectQuery = "SELECT * FROM appointments WHERE appointment_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            
            preparedStatement.setInt(1, appointmentId);

            
            ResultSet resultSet = preparedStatement.executeQuery();

            
            if (resultSet.next()) {
                // Create an Appointment object and populate its properties
                Appointment appointment = new Appointment();
                appointment.setAppointmentID(resultSet.getInt("appointment_id"));
                appointment.setPatientID(resultSet.getInt("patient_id"));
                appointment.setDoctorID(resultSet.getInt("doctor_id"));
                appointment.setAppointmentDate(resultSet.getDate("appointment_date").toLocalDate());
                appointment.setAppointmentTime(resultSet.getTime("appointment_time").toLocalTime());
                appointment.setProcessed(resultSet.getInt("is_processed"));

                // Return the populated Appointment object
                return appointment;
            } else {
                // No appointment found with the given ID
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return null;
        }
    }
  public static boolean isDoctorAvailable(int doctorId, LocalDate appointmentDate, LocalTime appointmentTime, Connection connection) {
        try {
            // Checking if there is any appointment with the same doctor, date, and time
            String query = "SELECT COUNT(*) AS count FROM appointments " +
                           "WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ?";
            
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, doctorId);
                statement.setDate(2, java.sql.Date.valueOf(appointmentDate));
                statement.setTime(3, java.sql.Time.valueOf(appointmentTime));

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt("count");
                        return count == 0; // Doctor is available if there are no appointments at that time
                    }
                }
            }
        } catch (SQLException e) {
            
            e.printStackTrace();
        }

        
        return false;
    }
}
    




