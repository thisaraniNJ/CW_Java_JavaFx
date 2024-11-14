package com.example.finalfinalcw;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class UpdateProjectControllerTest {

    private UpdateProjectController controller;
    private static final String testf = "test_projectdetails.txt";

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        // Initialize JavaFX Toolkit
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("JavaFX platform could not be started.");
        }
    }

    @BeforeEach
    void setUp() {
        // Initialize controller and its components
        controller = new UpdateProjectController();
        controller.projectIdField = new TextField();
        controller.projectNameField = new TextField();
        controller.categoryField = new TextField();
        controller.teamMembersField = new TextField();
        controller.briefDescriptionArea = new TextArea();
        controller.countryField = new TextField();
        controller.clickUpdate = new Button();
        controller.clickBack = new Button();

        // Initialize the controller
        controller.initialize();

        // Set the test file name for file operations
        controller.setFileName(testf);
    }

    @AfterEach
    void tearDown() {
        // Clean up test file after each test
        new File(testf).delete();
    }

    @Test
    void testUpdateProject() throws IOException {
        // Create a sample file with project details
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testf))) {
            writer.write("001;New Tec;Photography;Nimal, Kamal;Tn to tec;Sri Lanka;images/tlogoforjava/1.png");
            writer.newLine();
            writer.write("002;Hello World;Album Creation;John, Angi;Welcome to our world;England;images/tlogoforjava/2.png");
        }

        // Set project ID and new details to be updated
        controller.projectIdField.setText("001");
        controller.projectNameField.setText("Wonder");
        controller.categoryField.setText("Photo Editor");
        controller.teamMembersField.setText("Sama");
        controller.briefDescriptionArea.setText("Wonder Worlds");
        controller.countryField.setText("Australia");

        // Call update handler within the JavaFX
        Platform.runLater(() -> controller.handleUpdate(null));

        // JavaFX finishing
        waitForRunLater();

        // Make sure that project has been updated
        try (BufferedReader reader = new BufferedReader(new FileReader(testf))) {
            String line = reader.readLine();
            assertEquals("001;Wonder;Photo Editor;Sama;Wonder Worlds;Australia;images/tlogoforjava/1.png", line);
            line = reader.readLine();
            assertEquals("002;Hello World;Album Creation;John, Angi;Welcome to our world;England;images/tlogoforjava/2.png", line);
        }
    }

    private void waitForRunLater() {
        // Wait for the Platform.runLater() calls to finish
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
