package hms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPanel extends Application {

    public static int userId;

    private Stage window;
    private Scene login;

    Connection connection = DatabaseConn.connectDB();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-font-weight: bold;");
        final TextField emailField = new TextField();
        emailField.setPromptText("Enter email");
        emailField.setMinWidth(300);
        emailField.setMaxWidth(300);

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        final PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setMinWidth(300);
        passwordField.setMaxWidth(300);

        final ComboBox<String> userComboBox = new ComboBox<>();
        userComboBox.getItems().addAll("Receptionist", "Doctor");
        userComboBox.setPromptText("Select User");

        // Add a login button to trigger the login process
        Button loginButton = new Button("Login");
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleLogin(emailField.getText(), passwordField.getText(), userComboBox.getValue());
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(emailLabel, emailField, passwordLabel, passwordField, userComboBox, loginButton);

        login = new Scene(layout, 600, 400);
        layout.setAlignment(Pos.CENTER);
        window.setScene(login);
        window.setTitle("Login Panel");
        window.show();
    }

    // Method to handle the login based on the selected user
private void handleLogin(String email, String password, String selectedUser) {
   String query = "SELECT user_id, is_doctor FROM Users WHERE email = ? AND password = ?";
    
    try {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, email);
        preparedStatement.setString(2, password);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            int userId = resultSet.getInt("user_id");
            boolean isDoctor = resultSet.getBoolean("is_doctor");

            if (isDoctor && "Doctor".equals(selectedUser)) {
                
                DoctorPanel.userId = userId;
                DoctorPanel docpanel = new DoctorPanel();
                docpanel.start(new Stage());
               
            } else if (!isDoctor && "Receptionist".equals(selectedUser)) {
                
                RecpPanel.userId = userId;
                RecpPanel recpPanel = new RecpPanel();
                recpPanel.start(new Stage());
               
            } else {
                showAlert("Invalid user selection", "Please select a valid user type.");
            }
        } else {
            showAlert("Invalid credentials", "Please enter valid email and password.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}



    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }
}
