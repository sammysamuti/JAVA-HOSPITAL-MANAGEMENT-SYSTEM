package hms;

import java.sql.Connection;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javafx.stage.Stage;
import javafx.util.Callback;

public class RecpPanel extends Application {
    public static int userId;
    Stage window;
    
    Connection connection = DatabaseConn.connectDB();

    Scene createAppointment, searchPatient, appointment, Appsscene;
    
    private void showAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}

    Appointment appointment2 = new Appointment(0, null, null, 0, 0);

    public void start(Stage primaryStage) {
        window = primaryStage;
               
        Button addPatientButton = new Button("Add Patient");
         window.setTitle("Patient Management System");

        // Appointments Page
        Button createButton = new Button("Create Appointment");
        createButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                window.setScene(createAppointment);
            }
        });

      
 
        
        Button deleteButton = new Button("Delete");

       TextField appointmentIdField = new TextField();
       appointmentIdField.setPromptText("Appointment ID");
       appointmentIdField.setMaxWidth(150);

       Button editButton = new Button("Edit Appointment");
       editButton.setOnAction(e -> {
    try {
        // Parsing the appointment ID from the TextField
        int appointmentId = Integer.parseInt(appointmentIdField.getText());

        // Checking if the appointment ID exists
        boolean exists = Appointment.isAppointmentExistsById(appointmentId);

        if (exists) {
    // Fetching the existing appointment details (you may need to implement this method)
    Appointment existingAppointment = Appointment.getAppointmentById(appointmentId,connection);

    // Checking if the fetched appointment is not null
    if (existingAppointment != null) {
        // Create a new stage for editing
        Stage editStage = new Stage();
        editStage.setTitle("Edit Appointment");
        
        // Create input fields for editing
        ComboBox<Integer> doctorEditComboBox = new ComboBox<>();
        List<Integer> allDoctorIDs = Doctor.getAllDoctorIDsFromDatabase();

       doctorEditComboBox.getItems().addAll(allDoctorIDs);
        ComboBox<LocalTime> timeEditComboBox = new ComboBox<>();
        List<LocalTime> allHoursOfDay = new ArrayList<>();
        for (int hour = 9; hour <= 17; hour++) {
        allHoursOfDay.add(LocalTime.of(hour, 0));
        allHoursOfDay.add(LocalTime.of(hour, 30));
        }

        timeEditComboBox.getItems().addAll(allHoursOfDay);

        DatePicker dateEditPicker = new DatePicker();
        dateEditPicker.setDayCellFactory(picker -> new DateCell() {
       @Override
        public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);

        LocalDate currentDate = LocalDate.now();

        // Disable dates before the current date
        setDisable(empty || date.isBefore(currentDate));
    }
     });
        
        // Set the initial values based on the existing appointment
        doctorEditComboBox.setValue(existingAppointment.getDoctorID()); 
        timeEditComboBox.setValue(existingAppointment.getAppointmentTime());
        
        dateEditPicker.setValue(existingAppointment.getAppointmentDate());
        LocalDate existingDate = existingAppointment.getAppointmentDate();
            if (existingDate.isAfter(LocalDate.now())) {
                dateEditPicker.setValue(existingDate);
            }
        // Create a button for updating the appointment
        Button updateButton = new Button("Update Appointment");
        
updateButton.setOnAction(updateEvent -> {
    try {
        // Get the edited values from the input fields
        Integer editedDoctorId = doctorEditComboBox.getValue();
        LocalTime editedAppointmentTime = timeEditComboBox.getValue();
        LocalDate editedAppointmentDate = dateEditPicker.getValue();

        // Check if any fields are not selected
        if (editedDoctorId == null || editedAppointmentTime == null || editedAppointmentDate == null) {
            System.out.println("Please fill in all fields.");
            return;
        }

        // Check if the selected doctor is available at the specified time and date
        boolean isDoctorAvailable = Appointment.isDoctorAvailable(editedDoctorId, editedAppointmentDate, editedAppointmentTime, connection);

        if (!isDoctorAvailable) {
            System.out.println("Selected doctor is not available at the specified time and date.");
            
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Selected doctor is not available at the specified time and date.");

        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
            return;
        }

        // Perform the edit operation here, for example, updating the appointment details
        existingAppointment.setDoctorID(Doctor.searchDoctor(editedDoctorId));
        existingAppointment.setAppointmentTime(editedAppointmentTime);
        existingAppointment.setAppointmentDate(editedAppointmentDate);

        // Update the appointment in the database
        boolean isUpdateSuccess = Appointment.updateAppointment(existingAppointment, connection);

        if (isUpdateSuccess) {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
    successAlert.setTitle("Success");
    successAlert.setHeaderText(null);
    successAlert.setContentText("Appointment with ID " + appointmentId + " edited successfully!");

    successAlert.showAndWait();
            editStage.close();
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    errorAlert.setTitle("Error");
    errorAlert.setHeaderText(null);
    errorAlert.setContentText("Failed to edit appointment with ID " + appointmentId);

    errorAlert.showAndWait();
        }
    } catch (NumberFormatException ex) {
        // Handle the case where parsing patient or doctor ID fails
        System.out.println("Invalid patient or doctor ID. Please enter numeric values.");
    }
});


        // Create a layout for the edit stage
        VBox editLayout = new VBox(10);
        editLayout.getChildren().addAll(
                new Label("Edit Appointment"),
                new Label("Doctor: "), doctorEditComboBox,
                new Label("Time: "), timeEditComboBox,
                new Label("Date: "), dateEditPicker,
                updateButton
        );
        editLayout.setAlignment(Pos.CENTER);

        // Set the scene for the edit stage
        Scene editScene = new Scene(editLayout, 400, 500);
        editStage.setScene(editScene);

        // Show the edit stage
        editStage.show();
    } else {
        // Handle the case where the fetched appointment is null
        System.out.println("Appointment with ID " + appointmentId + " not found.");
        // Optionally, you can show an alert or perform other actions
    }
    Scene[] previousSceneHolder={null};

    // Store the main scene as the previous scene before switching
    previousSceneHolder[0] = primaryStage.getScene();

    // Set the scene or perform other actions as needed
    window.setScene(searchPatient);
}
 else {
            // Appointment doesn't exist, show an alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Appointment with ID " + appointmentId + " does not exist.");

            // Customize the alert buttons (in this case, just an OK button)
            alert.getButtonTypes().setAll(ButtonType.OK);

            // Show the alert and wait for the user's response
            alert.showAndWait();
        }
    } catch (NumberFormatException ex) {
        // Handle the case where the input is not a valid integer
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Please enter a valid integer for the Appointment ID.");

        // Customize the alert buttons (in this case, just an OK button)
        alert.getButtonTypes().setAll(ButtonType.OK);

        // Show the alert and wait for the user's response
        alert.showAndWait();
    }
});

        
        
        deleteButton.setOnAction(e -> {
            try {
                int appointmentId = Integer.parseInt(appointmentIdField.getText());

                // Call the deleteAppointment method
                boolean isSuccess = Appointment.deleteAppointment(appointmentId, DatabaseConn.connectDB());

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle(isSuccess ? "Success" : "Error");
                
                alert.setHeaderText(null);
                alert.setContentText(isSuccess ? "Appointment deleted successfully!"
                        : "Error deleting appointment. Please try again.");

                alert.showAndWait();

            } catch (NumberFormatException ex) {
                // Handle the case where parsing appointment ID fails
                System.out.println("Invalid appointment ID. Please enter a numeric value.");
            }
        });

        Button viewButton = new Button("View Appointments");

        
        
       

        Button[] buttons = {addPatientButton, viewButton, createButton, editButton, deleteButton};

        for (Button button : buttons) {
            button.setMinWidth(150);  // Set the desired min width for all buttons
        }

        // Button VBox
        VBox buttonBox = new VBox(10);
        buttonBox.setPadding(new Insets(30, 30, 30, 30));
        Text titleText = new Text("Receptionist Panel");
        titleText.setFont(Font.font(null, FontWeight.BOLD, 30));
        buttonBox.getChildren().addAll(titleText, addPatientButton, createButton,appointmentIdField, editButton, deleteButton);
        buttonBox.setStyle("-fx-background-color: lightgray;");

        // Table
        TableView<Appointment> appointmentsTable = new TableView<>();
        
        populateTableForAllDoctors(appointmentsTable);
        // Layout
        HBox root = new HBox(10);
        root.getChildren().addAll(buttonBox, appointmentsTable);

        // Set layout properties
        HBox.setHgrow(appointmentsTable, Priority.ALWAYS);

        // Set Scene
        Scene scene = new Scene(root,1000,400);
        primaryStage.setScene(scene);

        // Set Stage properties
        primaryStage.setMaxWidth(900);  // Set the desired max width for the whole stage

        // Show the stage
        primaryStage.show();
        
       
        AnchorPane anchorPane = new AnchorPane();

        // Create Labels
        Label nameLabel = new Label("Patient Name");
        Label doctorLabel = new Label("Doctor Name");
        Label dateLabel = new Label("Appointment Date");
        Label timeLabel = new Label("Appointment Time");

        // Create TextFields
        final TextField nameTextField = new TextField();
        final ComboBox<String> doctorComboBox = new ComboBox<>();
        doctorComboBox.setPromptText("Select Doctor");
        Doctor.fetchDataAndPopulateComboBox(doctorComboBox);

        int docId = Doctor.searchDoctor(nameTextField.getText());

        final ComboBox<String> patientComboBox = new ComboBox<>();
        patientComboBox.setPromptText("Select Patient");
        Patient.fetchDataAndPopulateComboBox(patientComboBox);

        int patId = Patient.searchPatient(nameTextField.getText());

        // Create ComboBox for Appointment Time
      

        // Create DatePicker
        final DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Choose Date");
        
        datePicker.setValue(LocalDate.now());

        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                // Disable past dates
                setDisable(date.isBefore(LocalDate.now()));
            }
        });
        
        LocalDate selectedDate = datePicker.getValue();
        
        final ComboBox<LocalTime> timeComboBox = new ComboBox<>();
        List<LocalTime> availableTimeSlots = Appointment.getAvailableTimeSlots(Doctor.getAppointmentsForDoctor(docId), String.valueOf(docId), selectedDate, timeComboBox);
        System.out.println(availableTimeSlots);
        Appointment.updateAvailableTimeSlots(availableTimeSlots, timeComboBox);

        // Create Button
        Button createAppointmentButton = new Button("Create Appointment");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
    // Set the main scene as the current scene
    primaryStage.setScene(scene);
    });
       Scene[] previousSceneHolder = { null };
  createAppointmentButton.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        try {
            String selectedDoctorName = doctorComboBox.getValue();
            if (selectedDoctorName == null) {
                System.out.println("Please select a doctor.");
                return;
            } else {
                System.out.println("Selected doctor: " + selectedDoctorName);
            }

            String selectedPatientName = patientComboBox.getValue();
             if (selectedPatientName == null) {
                System.out.println("Please select a patient.");
                return;
            } else {
                System.out.println("Selected patient: " + selectedPatientName);
            }
            // Check if timeComboBox has a selected item
            LocalTime appointmentTime = timeComboBox.getSelectionModel().getSelectedItem();
            if (appointmentTime == null) {
                System.out.println("Please select an appointment time.");
                return;
            } else {
                
                System.out.println("Selected appointment time: " + appointmentTime);
            }

            // Check if a date is selected
            
            if (selectedDate == null) {
                System.out.println("Please select an appointment date.");
                return;
            } else {
                System.out.println("Selected appointment date: " + selectedDate);
            }
            
            

            int patientID = Patient.searchPatient(selectedPatientName);
            int doctorID = Doctor.searchDoctor(selectedDoctorName);
            
            Connection connection = DatabaseConn.connectDB();
            if (Appointment.appointmentExists(doctorID, selectedDate, appointmentTime, connection)) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("An appointment already exists at the selected time for the doctor. Please choose a different time.");

            alert.showAndWait();
            return; // Stop further execution
        }

            System.out.println("Patient ID: " + patientID);
            System.out.println("Doctor ID: " + doctorID);

            Appointment appointment = new Appointment(
                    Appointment.getLatestAppointmentIdFromDB(),
                    appointmentTime, selectedDate, patientID, doctorID);

            System.out.println("Appointment Time (after creating Appointment object): " + appointment.getAppointmentTime());

            

            boolean isSuccess = Appointment.addAppointment(appointment, connection);

            if (isSuccess) {
                System.out.println("Appointment added successfully!");
            } else {
                System.out.println("Failed to add appointment.");
            }

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(isSuccess ? "Success" : "Error");
            alert.setHeaderText(null);
            alert.setContentText(
                    isSuccess ? "Appointment created successfully!"
                            : "Error creating appointment. Please try again.");

            alert.showAndWait();

            // Now you have the data from the input fields
            System.out.println("Patient Name: " + selectedPatientName);
            System.out.println("Doctor Name: " + selectedDoctorName);
            System.out.println("Appointment Time: " + appointmentTime);
            System.out.println("Selected Date: " + selectedDate);

            // Store the main scene as the previous scene before switching
            previousSceneHolder[0] = primaryStage.getScene();

            // Set the scene or perform other actions as needed
            window.setScene(searchPatient);
        } catch (NumberFormatException e) {
            // Handle the case where parsing patient or doctor ID fails
            System.out.println("Invalid patient or doctor ID. Please enter numeric values.");
        }
    }
});


        // Set layout constraints using AnchorPane
        AnchorPane.setTopAnchor(nameLabel, 18.0);
        AnchorPane.setLeftAnchor(nameLabel, 28.0);
        AnchorPane.setTopAnchor(patientComboBox, 43.0);
        AnchorPane.setLeftAnchor(patientComboBox, 28.0);

        AnchorPane.setTopAnchor(doctorLabel, 84.0);
        AnchorPane.setLeftAnchor(doctorLabel, 28.0);
        AnchorPane.setTopAnchor(doctorComboBox, 109.0); // Use doctorComboBox instead of doctorTextField
        AnchorPane.setLeftAnchor(doctorComboBox, 28.0); // Use doctorComboBox instead of doctorTextField

        AnchorPane.setTopAnchor(dateLabel, 22.0);
        AnchorPane.setLeftAnchor(dateLabel, 329.0);
        AnchorPane.setTopAnchor(datePicker, 43.0);
        AnchorPane.setLeftAnchor(datePicker, 322.0);

        AnchorPane.setTopAnchor(timeLabel, 84.0);
        AnchorPane.setLeftAnchor(timeLabel, 328.0);
        AnchorPane.setTopAnchor(timeComboBox, 109.0);
        AnchorPane.setLeftAnchor(timeComboBox, 322.0);

        AnchorPane.setTopAnchor(appointmentIdField, 50.0);
        AnchorPane.setLeftAnchor(appointmentIdField, 20.0);

        AnchorPane.setTopAnchor(deleteButton, 80.0);
        AnchorPane.setLeftAnchor(deleteButton, 20.0);

        AnchorPane.setTopAnchor(createAppointmentButton, 182.0);
        AnchorPane.setLeftAnchor(createAppointmentButton, 378.0);

        AnchorPane.setTopAnchor(backButton, 20.0);
        AnchorPane.setRightAnchor(backButton, 20.0);

        // Add nodes to the AnchorPane
        anchorPane.getChildren().addAll(nameLabel, patientComboBox, doctorLabel, doctorComboBox, dateLabel, datePicker,
                timeLabel, timeComboBox, createAppointmentButton, backButton);

        // Create a Scene
        createAppointment = new Scene(anchorPane, 600, 400);
    addPatientButton.setOnAction(e -> {
    // Create UI components
    TextField nameInput = new TextField();
    TextField phoneInput = new TextField();
    TextField genderInput = new TextField();
    
    TextFormatter<String> textFormatter = new TextFormatter<>(new StringConverter<String>() {
            
            public String toString(String object) {
                return object;
            }

            
            public String fromString(String string) {
                // Ensure the input is either "M" or "F"
                return string.matches("[MF]") ? string : "";
            }
        });

    genderInput.setTextFormatter(textFormatter);

    
    
    
    DatePicker dobPicker = new DatePicker();
   
    dobPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                // Disable dates after today
                setDisable(date.isAfter(LocalDate.now()));
            }
        });
    
    
    
    TextField emailInput = new TextField();
    TextField bloodGroupInput = new TextField();

    Button submit = new Button("Add Patient");

    // Set up HBox for buttons
HBox buttonsBox = new HBox(10);
buttonsBox.getChildren().addAll(submit, backButton);

// Set up layout
VBox layout = new VBox(10);
layout.setPadding(new Insets(10, 10, 10, 10));
layout.getChildren().addAll(
        new Label("Name: "), nameInput,
        new Label("Phone: "), phoneInput,
        new Label("Gender(M/F): "), genderInput,
        new Label("Date of Birth: "), dobPicker,
        new Label("Email: "), emailInput,
        new Label("Blood Group: "), bloodGroupInput,
        buttonsBox  // Add the HBox containing buttons
);
   submit.setOnAction(event -> {
    // Validate fields
    String name = nameInput.getText();
    String phone = phoneInput.getText();
    String gender = genderInput.getText();
    LocalDate dob = dobPicker.getValue();
    String email = emailInput.getText();
    String bloodGroup = bloodGroupInput.getText();
    
    
    // Additional validations
    if (name.isEmpty() || phone.isEmpty() || gender.isEmpty() || dob == null || email.isEmpty() || bloodGroup.isEmpty()) {
        showAlert("Error", "Please fill in all required fields.");
        return;
    }
    
    if (name.length() < 10 || !name.contains(" ")) {
        showAlert("Error", "Invalid name. Should be at least 3 characters long and contain a space.");
        return;
    }

    // Validate phone format (10 digits starting with 09)
    if (!phone.matches("^09\\d{8}$")) {
        showAlert("Error", "Invalid phone number format. Should start with '09' and have 10 digits.");
        return;
    }

    // Validate email format
    if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
        showAlert("Error", "Invalid email format.");
        return;
    }

    // Validate blood group format
    if (!bloodGroup.matches("^(A|B|AB|O)[+-]$")) {
        showAlert("Error", "Invalid blood group format. Should be one of: A, B, AB, O, followed by + or -.");
        return;
    }

    if ( !name.isEmpty() && !phone.isEmpty() && !gender.isEmpty() ) {
        java.sql.Date sqlDob = java.sql.Date.valueOf(dob);

        // Call the method to add patient to the database
    if ( !name.isEmpty()) { 
            
           if (Patient.isPatientExistsByEmail(email)) {
        showAlert("Error", "Patient with the same email already exists.");
        System.out.println("Patient with the same email already exists.");
    } else {
        try {
            // Check name before calling the method
            Patient.addPatientToDatabase(name, phone, gender, sqlDob, email, bloodGroup);
            } catch (ParseException ex) {
                Logger.getLogger(RecpPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

            showAlert("Success", "Patient added successfully!");
            System.out.println("Patient added successfully!");
        }} else {
            System.out.println("Name cannot be null or empty.");
            showAlert("Error", "Please fill in all required fields.");
            // You might want to show a message to the user or take appropriate action.
        }
     // Store the main scene as the previous scene before switching
            previousSceneHolder[0] = primaryStage.getScene();

            // Set the scene or perform other actions as needed
            window.setScene(searchPatient);
    } else {
        showAlert("Error", "Please fill in all required fields.");
        System.out.println("Please fill in all required fields.");
        // You might want to show a message to the user or take appropriate action.
    }
});
   
    // Set up scene
    Scene scenes = new Scene(layout, 300, 500);
    primaryStage.setScene(scenes);
    primaryStage.show();
    

});
        
    }

    public static void main(String[] args) {
        launch(args);
    }


public static void populateTableForAllDoctors(TableView<Appointment> appointmentsTable) {
    ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

    try {
        Connection connection = DatabaseConn.connectDB();
        String sql = "SELECT * FROM appointments WHERE is_processed = 0 ORDER BY doctor_id, appointment_date, appointment_time";
        PreparedStatement statement = connection.prepareStatement(sql);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            int appointmentId = resultSet.getInt("appointment_id");
            int patientId = resultSet.getInt("patient_id");
            int doctorId = resultSet.getInt("doctor_id");
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


        TableColumn<Appointment, Integer> doctorIdColumn = new TableColumn<>("Doctor ID");
        doctorIdColumn.setCellValueFactory(new PropertyValueFactory<>("doctorID"));

        TableColumn<Appointment, java.sql.Date> appointmentDateColumn = new TableColumn<>("Appointment Date");
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));

        TableColumn<Appointment, Time> appointmentTimeColumn = new TableColumn<>("Appointment Time");
        appointmentTimeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));

        // Add new columns to the TableView
        appointmentsTable.getColumns().addAll(appointmentIdColumn, patientIdColumn, doctorIdColumn,
                appointmentDateColumn, appointmentTimeColumn);

        // Set the items in the TableView
        appointmentsTable.setItems(appointmentsList);
        appointmentsTable.refresh();


        // Close resources
        resultSet.close();
        statement.close();
        connection.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
