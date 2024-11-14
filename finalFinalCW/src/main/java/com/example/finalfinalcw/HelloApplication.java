package com.example.finalfinalcw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        clearProjectFile();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("AddingDetails.fxml")));
        primaryStage.setTitle("TechExpo Project Details");
        primaryStage.setScene(new Scene(root, 900, 710));
        primaryStage.show();
    }
    private void clearProjectFile() {
        File file = new File("projectdetails.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(""); // Clear the file content
        } catch (IOException e) {
            System.err.println("Failed to clear project details file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
