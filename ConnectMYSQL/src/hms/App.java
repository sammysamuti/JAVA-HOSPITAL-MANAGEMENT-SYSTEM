package hms;

import java.io.IOException;
import java.sql.Connection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// import javax.swing.table.DefaultTableModel;




public class App 
{
    public static void main( String[] args )
    {

    Connection connection = DatabaseConn.connectDB();

    if (connection == null) {
        System.out.println("Failed to obtain a database connection. Exiting...");
        return; // or handle appropriately
    }

    try {
       
    } catch (Exception e) {
        System.out.println("An error occurred during application startup: " + e.getMessage());
        e.printStackTrace();
    }
       

   }


}

