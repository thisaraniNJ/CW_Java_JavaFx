package com.example.finalfinalcw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class UpdateProjectController {

    @FXML
    public TextField projectIdField;

    @FXML
    public TextField projectNameField;

    @FXML
    public TextField categoryField;

    @FXML
    public TextField teamMembersField;

    @FXML
    public TextArea briefDescriptionArea;

    @FXML
    public TextField countryField;

    @FXML
    public Button clickUpdate;

    @FXML
    public Button clickBack;

    private String fileName = "projectdetails.txt";
    private Map<String, String> previousState;

    @FXML
    void initialize() {
        clickUpdate.setOnAction(this::handleUpdate);
        clickBack.setOnAction(this::handleBack);
    }

    public void setPreviousState(Map<String, String> state) {
        this.previousState = state;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void handleUpdate(ActionEvent event) {
        String projectId = projectIdField.getText().trim();
        String projectName = projectNameField.getText().trim();
        String category = categoryField.getText().trim();
        String teamMembers = teamMembersField.getText().trim();
        String briefDescription = briefDescriptionArea.getText().trim();
        String country = countryField.getText().trim();

        if (projectId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a Project ID.");
            return;
        }

        List<String> lines = new ArrayList<>();
        boolean projectFound = false;
        String currentCategory = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Category: ")) {
                    currentCategory = line;
                    lines.add(line);
                } else {
                    String[] parts = line.split(";");
                    if (parts.length >= 6 && parts[0].equals(projectId)) {
                        // Update fields only if they are not empty
                        if (!projectName.isEmpty()) {
                            parts[1] = projectName;
                        }
                        if (!category.isEmpty()) {
                            parts[2] = category;
                        }
                        if (!teamMembers.isEmpty()) {
                            parts[3] = teamMembers;
                        }
                        if (!briefDescription.isEmpty()) {
                            parts[4] = briefDescription;
                        }
                        if (!country.isEmpty()) {
                            parts[5] = country;
                        }
                        projectFound = true;
                        lines.add(String.join(";", parts));
                    } else {
                        lines.add(line);
                    }
                }
            }

            if (projectFound) {
                // Sort lines by project ID
                sortLines(lines);

                // Write updated content back to file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                    for (String l : lines) {
                        writer.write(l);
                        writer.newLine();
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", "Project details updated successfully!");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Error", "Project with ID " + projectId + " not found.");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to read project details: " + e.getMessage());
        }
    }

    private void handleBack(ActionEvent event) {
        Stage stage = (Stage) clickBack.getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddingDetails.fxml"));
            Parent root = loader.load();

            AddingDetailsController addingController = loader.getController();
            addingController.setState(previousState);

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

    private void clearFields() {
        projectIdField.clear();
        projectNameField.clear();
        categoryField.clear();
        teamMembersField.clear();
        briefDescriptionArea.clear();
        countryField.clear();
    }

    private void sortLines(List<String> lines) {
        for (int i = 1; i < lines.size(); i++) {
            String key = lines.get(i);
            int j = i - 1;

            while (j >= 0 && compareProjects(lines.get(j), key) > 0) {
                lines.set(j + 1, lines.get(j));
                j = j - 1;
            }
            lines.set(j + 1, key);
        }
    }

    private int compareProjects(String project1, String project2) {
        String[] parts1 = project1.split(";");
        String[] parts2 = project2.split(";");
        String id1 = parts1[0];
        String id2 = parts2[0];
        return id1.compareTo(id2);
    }
}
