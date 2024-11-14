package com.example.finalfinalcw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteProjectController {

    @FXML
    public TextField projectIdField;

    @FXML
    public Button deleteButton;

    @FXML
    public Button backButton;

    private static String fileName = "projectdetails.txt"; // Static variable for the file name
    public Map<String, String> previousState; // Variable to store the previous state

    @FXML
    void initialize() {
        // Set action handlers for buttons
        deleteButton.setOnAction(this::handleDelete);
        backButton.setOnAction(this::handleBack);
    }

    public void setPreviousState(Map<String, String> state) {
        this.previousState = state;
    }

    public void handleDelete(ActionEvent event) {
        String projectId = projectIdField.getText().trim();

        if (projectId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a Project ID.");
            return;
        }

        List<String> lines = new ArrayList<>();
        boolean projectFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 7 && parts[0].equals(projectId)) {
                    projectFound = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace for more details
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to read project details: " + e.getMessage());
            return;
        }

        if (projectFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace(); // Print stack trace for more details
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update project details: " + e.getMessage());
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Project deleted successfully!");
            projectIdField.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Delete Error", "Project with ID " + projectId + " not found.");
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddingDetails.fxml"));
            Parent root = loader.load();

            AddingDetailsController addingDetailsController = loader.getController();
            addingDetailsController.setState(previousState);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load adding details view: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void setFileName(String testFileName) {
        fileName = testFileName;
    }
}
