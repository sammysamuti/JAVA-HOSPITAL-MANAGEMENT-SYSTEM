package hms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ViewPatients {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Patient> patientsTable;

    private ObservableList<Patient> patientsData = FXCollections.observableArrayList();
    private Patient patientDAO = new Patient(); // Initialize your PatientDAO

    @FXML
    public void initialize() {
        // Initialize the table with data from the database
        patientsData.addAll(Patient.getAllPatients());

        // Set the data to the table
        patientsTable.setItems(patientsData);

        // Add a listener to the search field to filter the table
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterTable(newText);
        });
        
    }

    private void filterTable(String query) {
        // Implement your filtering logic here
        ObservableList<Patient> filteredList = FXCollections.observableArrayList();

        for (Patient patient : patientsData) {
            if (patient.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(patient);
            }
        }

        patientsTable.setItems(filteredList);
    }
}
