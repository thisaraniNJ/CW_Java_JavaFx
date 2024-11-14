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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class AddingDetailsController {
    @FXML
    public Button viewButton;
    @FXML
    public Button spotlightButton;
    @FXML
    public Button updateButton;
    @FXML
    public Button addProjectButton;
    @FXML
    public Button uploadImageButton;
    @FXML
    public Button deleteProjectButton;

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

    public transient File teamLogo;
    public List<Map<String, String>> projects = new ArrayList<>();

    @FXML
    public void initialize() {
        addProjectButton.setOnAction(this::handleAddProject);
        uploadImageButton.setOnAction(this::handleUploadImage);
        deleteProjectButton.setOnAction(this::handleDeleteProject);
        updateButton.setOnAction(this::handleUpdateProject);
        viewButton.setOnAction(this::handleViewProject);
        spotlightButton.setOnAction(this::handleSpotlightShowcase);

        loadProjectsFromFile();
    }



    public void handleAddProject(ActionEvent event) {
        String projectId = projectIdField.getText().trim();
        String projectName = projectNameField.getText().trim();
        String category = categoryField.getText().trim();
        String teamMembers = teamMembersField.getText().trim();
        String briefDescription = briefDescriptionArea.getText().trim();
        String country = countryField.getText().trim();

        if (projectId.isEmpty() || projectName.isEmpty() || category.isEmpty() ||
                teamMembers.isEmpty() || briefDescription.isEmpty() || country.isEmpty() || teamLogo == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return;
        }

        if (isDuplicateProject(projectId, projectName)) {
            showAlert(Alert.AlertType.ERROR, "Duplicate Project", "Duplicate project ID or project name.");
            return;
        }

        Map<String, String> project = new HashMap<>();
        project.put("projectId", projectId);
        project.put("projectName", projectName);
        project.put("category", category);
        project.put("teamMembers", teamMembers);
        project.put("briefDescription", briefDescription);
        project.put("country", country);
        project.put("teamLogo", teamLogo.getAbsolutePath());

        projects.add(project);
        saveProjectsToFile();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Project added successfully!");
        clearFields();
    }

    public boolean isDuplicateProject(String projectId, String projectName) {
        return projects.stream()
                .anyMatch(p -> p.get("projectId").equals(projectId) || p.get("projectName").equals(projectName));
    }

    public void setState(Map<String, String> state) {
        projectIdField.setText(state.get("projectId"));
        projectNameField.setText(state.get("projectName"));
        categoryField.setText(state.get("category"));
        teamMembersField.setText(state.get("teamMembers"));
        briefDescriptionArea.setText(state.get("briefDescription"));
        countryField.setText(state.get("country"));

        String teamLogoPath = state.get("teamLogo");
        if (teamLogoPath != null) {
            teamLogo = new File(teamLogoPath);
        } else {
            teamLogo = null;
        }
    }


    public Map<String, String> getState() {
        Map<String, String> state = new HashMap<>();
        state.put("projectId", projectIdField.getText());
        state.put("projectName", projectNameField.getText());
        state.put("category", categoryField.getText());
        state.put("teamMembers", teamMembersField.getText());
        state.put("briefDescription", briefDescriptionArea.getText());
        state.put("country", countryField.getText());
        if (teamLogo != null) {
            state.put("teamLogo", teamLogo.getAbsolutePath());
        }
        return state;
    }

    private void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        teamLogo = fileChooser.showOpenDialog(new Stage());
        if (teamLogo != null) {
            System.out.println("Image selected: " + teamLogo.getName());
        }
    }


    private void handleViewProject(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewProjects.fxml"));
            Parent root = loader.load();

            ViewProjectsController viewController = loader.getController();
            viewController.setPreviousState(getState());

            Stage stage = (Stage) viewButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load view projects: " + e.getMessage());
        }
    }


    private void handleUpdateProject(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateProject.fxml"));
            Parent root = loader.load();

            UpdateProjectController updateController = loader.getController();
            updateController.setPreviousState(getState());

            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load update project view: " + e.getMessage());
        }
    }

    private void handleDeleteProject(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DeleteProject.fxml"));
            Parent root = loader.load();

            DeleteProjectController deleteController = loader.getController();
            deleteController.setPreviousState(getState());

            Stage stage = (Stage) deleteProjectButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load delete project view: " + e.getMessage());
        }
    }

    private void handleSpotlightShowcase(ActionEvent event) {
        try {
            // Disable buttons and fields
            disableButtonsAndFields();

            // Get a random project from each category
            List<Map<String, String>> spotlightProjects = getRandomProjectsByCategory();

            // Pass these projects to the spotlight showcase controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("spotlightshowcase.fxml"));
            Parent root = loader.load();

            SpotlightshowcaseController controller = loader.getController();
            controller.setSpotlightProjects(spotlightProjects);

            Stage stage = (Stage) spotlightButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(e -> {
                // Do not re-enable buttons and fields
            });
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load spotlight showcase view: " + e.getMessage());
        }
    }


    private List<Map<String, String>> getRandomProjectsByCategory() {
        Map<String, List<Map<String, String>>> projectsByCategory = new HashMap<>();
        for (Map<String, String> project : projects) {
            String category = project.get("category");
            projectsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(project);
        }

        List<Map<String, String>> randomProjects = new ArrayList<>();
        for (List<Map<String, String>> projectList : projectsByCategory.values()) {
            if (!projectList.isEmpty()) {
                int randomIndex = new Random().nextInt(projectList.size());
                randomProjects.add(projectList.get(randomIndex));
            }
        }
        return randomProjects;
    }

    private void loadProjectsFromFile() {
        File file = new File("projectdetails.txt");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Category:")) {
                    // Skip category lines
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length == 7) {
                    Map<String, String> project = new HashMap<>();
                    project.put("projectId", parts[0]);
                    project.put("projectName", parts[1]);
                    project.put("category", parts[2]);
                    project.put("teamMembers", parts[3]);
                    project.put("briefDescription", parts[4]);
                    project.put("country", parts[5]);
                    project.put("teamLogo", parts[6]);
                    projects.add(project);
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load project details: " + e.getMessage());
        }
    }

    private void saveProjectsToFile() {
        File file = new File("projectdetails.txt");

        // Group projects by category
        Map<String, List<Map<String, String>>> projectsByCategory = new HashMap<>();
        for (Map<String, String> project : projects) {
            String category = project.get("category");
            if (!projectsByCategory.containsKey(category)) {
                projectsByCategory.put(category, new ArrayList<>());
            }
            projectsByCategory.get(category).add(project);
        }

        // Get all categories and sort them alphabetically
        List<String> categories = new ArrayList<>(projectsByCategory.keySet());
        customSort(categories);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String category : categories) {
                writer.write("Category: " + category);
                writer.newLine();
                for (Map<String, String> project : projectsByCategory.get(category)) {
                    writer.write(String.join(";",
                            project.get("projectId"),
                            project.get("projectName"),
                            project.get("category"),
                            project.get("teamMembers"),
                            project.get("briefDescription"),
                            project.get("country"),
                            project.get("teamLogo")
                    ));
                    writer.newLine();
                }
                writer.newLine(); // Add a blank line between categories
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save project details: " + e.getMessage());
        }
    }

    // sort the list of categories alphabetically
    private void customSort(List<String> categories) {
        for (int i = 0; i < categories.size() - 1; i++) {
            for (int j = i + 1; j < categories.size(); j++) {
                if (categories.get(i).compareTo(categories.get(j)) > 0) {
                    String temp = categories.get(i);
                    categories.set(i, categories.get(j));
                    categories.set(j, temp);
                }
            }
        }
    }


    private void clearFields() {
        projectIdField.clear();
        projectNameField.clear();
        categoryField.clear();
        teamMembersField.clear();
        briefDescriptionArea.clear();
        countryField.clear();
        teamLogo = null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void disableButtons() {
        addProjectButton.setDisable(true);
        deleteProjectButton.setDisable(true);
        updateButton.setDisable(true);
        viewButton.setDisable(true);

    }
    private void disableButtonsAndFields() {
        addProjectButton.setDisable(true);
        deleteProjectButton.setDisable(true);
        updateButton.setDisable(true);
        projectIdField.setDisable(true);
        projectNameField.setDisable(true);
        categoryField.setDisable(true);
        teamMembersField.setDisable(true);
        briefDescriptionArea.setDisable(true);
        countryField.setDisable(true);
        uploadImageButton.setDisable(true);
    }


    @FXML
    public void handleExit(ActionEvent event) {
        Stage stage = (Stage) addProjectButton.getScene().getWindow();
        stage.close();
    }
}
