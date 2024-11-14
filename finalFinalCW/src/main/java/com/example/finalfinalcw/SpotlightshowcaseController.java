package com.example.finalfinalcw;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpotlightshowcaseController {

    @FXML
    private TableView<Project> spotlightTable;

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

    @FXML
    private TableColumn<Project, String> judge1Column;

    @FXML
    private TableColumn<Project, String> judge2Column;

    @FXML
    private TableColumn<Project, String> judge3Column;

    @FXML
    private TableColumn<Project, String> judge4Column;

    @FXML
    private Button backbutton;
    @FXML
    private Button calculateWinnersButton;
    @FXML
    private Button nextButton;
    @FXML
    private VBox graphContainer;

    private ObservableList<Project> spotlightProjectsList;

    @FXML
    public void initialize() {
        spotlightTable.setEditable(true); // Make the table editable

        projectIdColumn.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        teamMembersColumn.setCellValueFactory(new PropertyValueFactory<>("teamMembers"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("briefDescription"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));

        logoColumn.setCellValueFactory(new PropertyValueFactory<>("teamLogoImage"));
        logoColumn.setCellFactory(param -> new TableCell<Project, ImageView>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(item.getImage());
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    setGraphic(imageView);
                }
            }
        });

        setupStarRatingColumn(judge1Column, "judge1Points");
        setupStarRatingColumn(judge2Column, "judge2Points");
        setupStarRatingColumn(judge3Column, "judge3Points");
        setupStarRatingColumn(judge4Column, "judge4Points");

        spotlightProjectsList = FXCollections.observableArrayList();
        spotlightTable.setItems(spotlightProjectsList); // Set items for the table

        backbutton.setOnAction(this::handleBackButton);
        calculateWinnersButton.setOnAction(this::calculateWinners);
        nextButton.setOnAction(this::handleNextButton);
    }

    private void setupStarRatingColumn(TableColumn<Project, String> column, String property) {
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setCellFactory(param -> new TableCell<Project, String>() {

            private TextField editor; // Create a TextField for editing

            {
                editor = new TextField();
                editor.setMaxWidth(60);

                editor.setOnAction(event -> {
                    String input = editor.getText();
                    if (input.matches("[*]{0,5}")) { // Allow 0 to 5 stars
                        updateRating(input);
                        commitEdit(input); // Commit the edit
                    } else {
                        editor.setStyle("-fx-border-color: red;"); // Indicate invalid input
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setText(item);
                    setGraphic(null);
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (editor != null) {
                    editor.setText(getItem());
                    setGraphic(editor);
                    editor.requestFocus();
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setGraphic(null);
                setText(getItem());
            }

            private void updateRating(String rating) {
                if (rating == null) return;

                Project project = getTableView().getItems().get(getIndex());
                TableColumn<Project, String> column = getTableColumn();

                // Determine the index of the column and update the corresponding judge points
                if (column.equals(judge1Column)) {
                    project.setJudge1Points(rating);
                } else if (column.equals(judge2Column)) {
                    project.setJudge2Points(rating);
                } else if (column.equals(judge3Column)) {
                    project.setJudge3Points(rating);
                } else if (column.equals(judge4Column)) {
                    project.setJudge4Points(rating);
                }
            }
        });
        column.setEditable(true);
    }

    public void setSpotlightProjects(List<Map<String, String>> spotlightProjects) {
        spotlightProjectsList.clear();
        for (Map<String, String> projectData : spotlightProjects) {
            Project project = new Project(
                    projectData.get("projectId"),
                    projectData.get("projectName"),
                    projectData.get("category"),
                    projectData.get("teamMembers"),
                    projectData.get("briefDescription"),
                    projectData.get("country"),
                    projectData.get("teamLogo")
            );
            spotlightProjectsList.add(project);
        }
    }

    private void calculateWinners(ActionEvent event) {
        List<Project> sortedProjects = sortProjectsByTotalScore(spotlightProjectsList);

        if (sortedProjects.isEmpty()) {
            System.out.println("No projects to evaluate.");
            return;
        }

        Project winningProject = sortedProjects.get(0);
        System.out.println("Winning Project: " + winningProject.getProjectName() + " with score: " + winningProject.getTotalScore());
    }

    private void handleNextButton(ActionEvent event) {
        // Sort projects by total score in descending order
        List<Project> sortedProjects = sortProjectsByTotalScore(spotlightProjectsList);

        // Get the top 3 projects
        List<Project> top3Projects = sortedProjects.subList(0, Math.min(3, sortedProjects.size()));

        // Display the top 3 projects in a graph
        displayTopProjectsinaGraph(top3Projects);
    }

    private void displayTopProjectsinaGraph(List<Project> topProjects) {
        // Clear the graph container
        graphContainer.getChildren().clear();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Project Name and Country");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Score");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top Projects");

        for (Project project : topProjects) {
            series.getData().add(new XYChart.Data<>(project.getProjectName() + " (" + project.getCountry() + ")", project.getTotalScore()));
        }

        barChart.getData().add(series);
        graphContainer.getChildren().add(barChart);
    }

    private void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddingDetails.fxml"));
            Parent root = loader.load();

            // Get the controller from the FXMLLoader
            AddingDetailsController addingDetailsController = loader.getController();
            addingDetailsController.disableButtons();  // Call method to disable buttons

            Stage stage = (Stage) backbutton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException exception) {
            exception.printStackTrace(); // Print exception if loading fails
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }

    private List<Project> sortProjectsByTotalScore(ObservableList<Project> projects) {
        List<Project> sortedList = new ArrayList<>(projects);
        for (int i = 1; i < sortedList.size(); i++) {
            Project key = sortedList.get(i);
            int j = i - 1;

            while (j >= 0 && sortedList.get(j).getTotalScore() < key.getTotalScore()) {
                sortedList.set(j + 1, sortedList.get(j));
                j = j - 1;
            }
            sortedList.set(j + 1, key);
        }
        return sortedList;
    }
}
