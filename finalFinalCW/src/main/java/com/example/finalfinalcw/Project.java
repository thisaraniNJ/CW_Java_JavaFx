package com.example.finalfinalcw;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class Project {
    private String projectId;
    private String projectName;
    private String category;
    private String teamMembers;
    private String briefDescription;
    private String country;
    private ImageView teamLogoImage;
    private String[] judgesPoints = new String[4]; // Array to store points as *
    private int totalScore; // Feild to store the total score of the project

    public Project(String projectId, String projectName, String category, String teamMembers, String briefDescription, String country, String teamLogoPath) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.category = category;
        this.teamMembers = teamMembers;
        this.briefDescription = briefDescription;
        this.country = country;

        if (teamLogoPath != null && !teamLogoPath.isEmpty()) {
            File file = new File(teamLogoPath);
            if (file.exists()) { // Check if the file exists
                Image image = new Image(file.toURI().toString());
                this.teamLogoImage = new ImageView(image);
            } else {
                this.teamLogoImage = new ImageView();
            }
        } else {
            this.teamLogoImage = new ImageView();
        }

        // Initialize judge points with empty strings
        for (int i = 0; i < judgesPoints.length; i++) {
            judgesPoints[i] = "";
        }
    }



    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(String teamMembers) {
        this.teamMembers = teamMembers;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ImageView getTeamLogoImage() {
        return teamLogoImage;
    }

    public void setTeamLogoImage(ImageView teamLogoImage) {
        this.teamLogoImage = teamLogoImage;
    }

    public String getJudge1Points() {
        return judgesPoints[0];
    }

    public void setJudge1Points(String points) {
        judgesPoints[0] = points;
        calculateTotalScore();
    }

    public String getJudge2Points() {
        return judgesPoints[1];
    }

    public void setJudge2Points(String points) {
        judgesPoints[1] = points;
        calculateTotalScore();
    }

    public String getJudge3Points() {
        return judgesPoints[2];
    }

    public void setJudge3Points(String points) {
        judgesPoints[2] = points;
        calculateTotalScore();
    }

    public String getJudge4Points() {
        return judgesPoints[3];
    }

    public void setJudge4Points(String points) {
        judgesPoints[3] = points;
        calculateTotalScore();
    }

    public int getTotalScore() {
        return totalScore;
    }

    private void calculateTotalScore() {
        totalScore = 0;
        for (String rating : judgesPoints) {
            totalScore += rating.length(); // Count stars
        }
    }
}
