package com.example.finalfinalcw;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AddingDetailsControllerTest {

    private AddingDetailsController controller;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> {
            //JavaFX platform
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("This is not working...");
        }
    }

    @BeforeEach
    void setUp() {
        // Initialize the controller and its components
        controller = new AddingDetailsController();
        controller.projectIdField = new TextField();
        controller.projectNameField = new TextField();
        controller.categoryField = new TextField();
        controller.teamMembersField = new TextField();
        controller.briefDescriptionArea = new TextArea();
        controller.countryField = new TextField();
        controller.addProjectButton = new Button();
        controller.deleteProjectButton = new Button();
        controller.updateButton = new Button();
        controller.viewButton = new Button();
        controller.uploadImageButton = new Button();
        controller.spotlightButton = new Button();
        controller.initialize();
    }

    @Test
    void testAddProject() {
        Platform.runLater(() -> {
            controller.projectIdField.setText("001");
            controller.projectNameField.setText("New Tec");
            controller.categoryField.setText("Photography");
            controller.teamMembersField.setText("Nimal, Kamal");
            controller.briefDescriptionArea.setText("In to the new tec");
            controller.countryField.setText("Sri Lanka");
            controller.teamLogo = new File("images/tlogoforjava/1.jpg");

            controller.handleAddProject(null);

            List<Map<String, String>> projects = controller.projects;
            assertEquals(1, projects.size());
            Map<String, String> project = projects.get(0);
            assertEquals("001", project.get("projectId"));
            assertEquals("New Tec", project.get("projectName"));
            assertEquals("Photography", project.get("category"));
            assertEquals("Nimal, Kamal", project.get("teamMembers"));
            assertEquals("In to the new tec", project.get("briefDescription"));
            assertEquals("Sri Lanka", project.get("country"));
            assertEquals("images/tlogoforjava/1.jpg", project.get("teamLogo"));
        });
    }

    @Test
    void testDuplicateProject() {
        controller.projects.add(createProject("001", "New Tec"));
        controller.projects.add(createProject("002", "Hello World"));

        assertTrue(controller.isDuplicateProject("001", "New Tec"));
        assertTrue(controller.isDuplicateProject("003", "New Tec"));
        assertFalse(controller.isDuplicateProject("003", "Roo"));
    }

    private Map<String, String> createProject(String id, String name) {
        Map<String, String> project = new HashMap<>();
        project.put("projectId", id);
        project.put("projectName", name);
        project.put("category", "Category");
        project.put("teamMembers", "Members");
        project.put("briefDescription", "Description");
        project.put("country", "Country");
        project.put("teamLogo", "path/to/logo.jpg");
        return project;
    }
}
