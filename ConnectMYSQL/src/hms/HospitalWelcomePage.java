package hms;


import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;




public class HospitalWelcomePage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hospital Management System");

        // Create welcome message
        Text welcomeText = new Text("Welcome to the Hospital Management System");
        welcomeText.setFont(Font.font(20));
       

        // Create login button
       Button loginButton = new Button("Login");
       loginButton.setStyle("-fx-background-color: black; -fx-border-radius: 10px; -fx-padding: 10px 20px; -fx-text-fill: white;");

// Adding hover effect 
loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: black;  -fx-padding: 10px 30px; -fx-text-fill: white;"));
loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: gray; -fx-padding: 10px 30px; -fx-text-fill: white;"));

loginButton.setFont(Font.font(16));



loginButton.setOnAction(event -> {
      
            LoginPanel login = new LoginPanel();
            try {
                login.start(new Stage());
            } catch (Exception ex) {
                Logger.getLogger(HospitalWelcomePage.class.getName()).log(Level.SEVERE, null, ex);
            }
                primaryStage.show();

       
});


        // Create layout
        VBox centerLayout = new VBox(20);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.getChildren().addAll( welcomeText, loginButton);

        // Create a horizontal layout for padding
        HBox paddingLayout = new HBox(centerLayout);
        paddingLayout.setAlignment(Pos.CENTER);

        // Add padding to the layout
        BorderPane.setAlignment(paddingLayout, Pos.CENTER);
        BorderPane.setMargin(paddingLayout, new Insets(50));

        // Create and set the scene
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(paddingLayout);
         borderPane.setStyle("-fx-background-color: lightgray;");
        Scene scene = new Scene(borderPane, 600, 400);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

 
}
