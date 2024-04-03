package hms;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import java.util.Map;
import javafx.scene.layout.Priority;

public class DoctorPanel extends Application {

    public static int userId;

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showDoctorPanel();
    }

    public void showDoctorPanel() {
        System.out.println(userId);
        AnchorPane root = new AnchorPane();

        TextField patientIdTextField = new TextField();
        patientIdTextField.setPromptText("Enter Patient ID");

        Button viewPatientsButton = new Button("View Patient Information");
        viewPatientsButton.setStyle("-fx-padding: 10 53;");

       TableView<Appointment> appointmentsTable = new TableView<>();

        // Call the method to populate the table
        populateTableForDoctor(appointmentsTable, userId);

//         Button action to view patients
      viewPatientsButton.setOnAction(event -> {
    String enteredPatientId = patientIdTextField.getText();

    try {
        int selectedPatientId = Integer.parseInt(enteredPatientId);
        displayPatientInfo(selectedPatientId);
    } catch (NumberFormatException e) {
        showAlert("Invalid Patient ID", "Please enter a valid patient ID.");
    }
});
        VBox buttons = new VBox(10);
        buttons.setPadding(new Insets(30, 30, 30, 30));
        buttons.getChildren().addAll(
                new Text("Doctor Panel") {{
                    setFont(javafx.scene.text.Font.font(null, FontWeight.BOLD, 30));
                }},
                patientIdTextField,
                viewPatientsButton);

        buttons.setStyle("-fx-background-color: lightgray;");
        HBox aside = new HBox(100);
        aside.setPadding(new Insets(30, 30, 30, 30));

       appointmentsTable.setPrefSize(600, 400);
        
        root.getChildren().addAll(aside);
        aside.getChildren().addAll(buttons, appointmentsTable);

        Scene scene = new Scene(root, 1000, 500);

        primaryStage.setTitle("Doctor's Panel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayPatientInfo(int patientId, TableView<Patient> patientsTable) {
        Patient selectedPatient = Patient.getPatientData(patientId);

        if (selectedPatient != null) {
            patientsTable.getItems().clear();
            patientsTable.getItems().add(selectedPatient);
        } else {
            System.out.println("Patient with ID " + patientId + " not found.");
        }
    }
    
public static void populateTableForDoctor(TableView<Appointment> appointmentsTable, int doctorId) {
    ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

    try {
        Connection connection = DatabaseConn.connectDB();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? AND is_processed = 0 ORDER BY appointment_date, appointment_time";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, doctorId);  

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            int appointmentId = resultSet.getInt("appointment_id");
            int patientId = resultSet.getInt("patient_id");
            java.sql.Date appointmentDate = resultSet.getDate("appointment_date");
            Time appointmentTime = resultSet.getTime("appointment_time");
            int isProcessed = resultSet.getInt("is_processed");

            // Create an Appointment object and add it to the list
            Appointment appointment = new Appointment(appointmentId, patientId, doctorId, appointmentDate.toLocalDate(), appointmentTime.toLocalTime(), isProcessed);
            appointmentsList.add(appointment);
        }
        
        

        // Clear existing columns in the TableView
        appointmentsTable.getColumns().clear();

        // Set up new columns for the TableView
        TableColumn<Appointment, Integer> appointmentIdColumn = new TableColumn<>("Appointment ID");
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));

        TableColumn<Appointment, Integer> patientIdColumn = new TableColumn<>("Patient ID");
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("patientID"));


        TableColumn<Appointment, java.sql.Date> appointmentDateColumn = new TableColumn<>("Appointment Date");
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));

        TableColumn<Appointment, Time> appointmentTimeColumn = new TableColumn<>("Appointment Time");
        appointmentTimeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));


            TableColumn<Appointment, Void> changeStatusColumn = new TableColumn<>("Change Status");
        changeStatusColumn.setCellFactory(param -> new TableCell<Appointment, Void>() {
            private final Button changeStatusButton = new Button("Mark as Processed");

            {
                changeStatusButton.setOnAction(event -> {
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    Appointment.markAppointmentAsProcessed(appointment); 
                });
                
                appointmentsTable.refresh();
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(changeStatusButton);
                }
            }
        });

        // Add new columns to the TableView
        appointmentsTable.getColumns().addAll(appointmentIdColumn, patientIdColumn,
                appointmentDateColumn, appointmentTimeColumn);
        
        appointmentsTable.getColumns().add(changeStatusColumn);


        // Set the items in the TableView
        appointmentsTable.setItems(appointmentsList);

        // Close resources
        resultSet.close();
        statement.close();
        connection.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private void displayPatientInfo(int patientId) {
    Patient patient = Patient.getPatientData(patientId);

    if (patient != null) {
        // Patient found, display information in an alert window
        String patientInfo = "Patient ID: " + patient.getPersonID() + "\n"
                + "Name: " + patient.getName() + "\n"
                + "Phone Number: " + patient.getPhoneNumber() + "\n"
                + "Gender: " + patient.getGender() + "\n"
                + "Date of Birth: " + patient.getDateOfBirth() + "\n"
                + "Email: " + patient.getEmail() + "\n"
                + "Blood Group: " + patient.getBloodGroup();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Patient Information");
        alert.setHeaderText(null);
        alert.setContentText(patientInfo);
        alert.showAndWait();
    } else {
        showAlert("Patient Not Found", "Patient with ID " + patientId + " not found.");
    }
}

// Method to show an alert for invalid input
private void showAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}


    public static void main(String[] args) {
        launch(args);
    }
}
