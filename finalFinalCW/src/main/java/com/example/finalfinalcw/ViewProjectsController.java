package com.example.finalfinalcw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewProjectsController {

    @FXML
    private Button backButton;


    @FXML
    private TableView<Project> projectTable;

    @FXML
    private TableColumn<Project, String> projectIdColumn;

    @FXML
    private TableColumn<Project, String> projectNameColumn;

    @FXML
    private TableColumn<Project, String> categoryColumn;

    @FXML
    private TableColumn<Project, String> teamMembersColumn;

    @FXML
    private TableColumn<Project, String> descriptionColumn;

    @FXML
    private TableColumn<Project, String> countryColumn;

    @FXML
    private TableColumn<Project, ImageView> logoColumn;

    private static final String FILENAME = "projectdetails.txt";
    private Map<String, String> previousState;

    @FXML
    public void initialize() {
        backButton.setOnAction(this::handleBack);

        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        teamMembersColumn.setCellValueFactory(new PropertyValueFactory<>("teamMembers"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        logoColumn.setCellValueFactory(new PropertyValueFactory<>("logo"));

        loadProjectData();
    }

    public void setPreviousState(Map<String, String> state) {
        this.previousState = state;
    }

    private void loadProjectData() {
        List<Project> projects = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 7) {
                    String projectId = parts[0];
                    String projectName = parts[1];
                    String category = parts[2];
                    String teamMembers = parts[3];
                    String description = parts[4];
                    String country = parts[5];
                    String logoPath = parts[6];

                    ImageView logo = new ImageView(new Image("file:" + logoPath));
                    logo.setFitWidth(30);
                    logo.setFitHeight(30);

                    projects.add(new Project(projectId, projectName, category, teamMembers, description, country, logo));
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load project details: " + e.getMessage());
        }

        // Sort projects by Project ID using custom sorting algorithm
        bubbleSort(projects);

        projectTable.getItems().setAll(projects);
    }

    private void bubbleSort(List<Project> projects) {
        int n = projects.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (compare(projects.get(j), projects.get(j + 1)) > 0) {
                    // Swap projects[j+1] and projects[j]
                    Project temp = projects.get(j);
                    projects.set(j, projects.get(j + 1));
                    projects.set(j + 1, temp);
                }
            }
        }
    }

    private int compare(Project p1, Project p2) {
        // Custom comparison logic based on Project ID (assuming alphanumeric sorting)
        return p1.getProjectId().compareTo(p2.getProjectId());
    }

    private void handleBack(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();

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

    public static class Project {
        private String projectId;
        private String projectName;
        private String category;
        private String teamMembers;
        private String description;
        private String country;
        private ImageView logo;

        public Project(String projectId, String projectName, String category, String teamMembers, String description, String country, ImageView logo) {
            this.projectId = projectId;
            this.projectName = projectName;
            this.category = category;
            this.teamMembers = teamMembers;
            this.description = description;
            this.country = country;
            this.logo = logo;
        }

        public String getProjectId() {
            return projectId;
        }

        public String getProjectName() {
            return projectName;
        }

        public String getCategory() {
            return category;
        }

        public String getTeamMembers() {
            return teamMembers;
        }

        public String getDescription() {
            return description;
        }

        public String getCountry() {
            return country;
        }

        public ImageView getLogo() {
            return logo;
        }
    }
}
