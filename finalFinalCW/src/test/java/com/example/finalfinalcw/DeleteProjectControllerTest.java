package com.example.finalfinalcw;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class DeleteProjectControllerTest {

    private DeleteProjectController controller;
    private static final String testf = "test_projectdetails.txt";
    private Stage testStage;

    @BeforeAll
    static void initToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown);
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("This is not working...");
        }
    }

    @BeforeEach
    void setUp() {
        // Initialize controller and its components
        controller = new DeleteProjectController();
        controller.projectIdField = new TextField();
        controller.deleteButton = new Button();
        controller.backButton = new Button();

        // Set the test file name for file operations
        DeleteProjectController.setFileName(testf);

        // Initialize the controller
        controller.initialize();

        // Setup a simple scene and add the backButton to it
        Platform.runLater(() -> {
            testStage = new Stage();
            Scene scene = new Scene(controller.backButton);
            testStage.setScene(scene);
            testStage.show();
        });

        // Wait for the JavaFX thread to finish
        waitForRunLater();
    }

    @AfterEach
    void tearDown() {
        new File(testf).delete();
        Platform.runLater(() -> testStage.close());
    }

    @Test
    void testDeleteProject() throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testf))) {
            writer.write("001;New Tec;Photography;Nimal, Kamal;Tn to tec;Sri Lanka;images/tlogoforjava/1.png");
            writer.newLine();
            writer.write("002;Hello World;Album Creation;John, Angi;Welcome to our world;England;images/tlogoforjava/2.png");
        }

        // Project ID that needs to remove
        controller.projectIdField.setText("001");

        // Invoke the delete handler within the JavaFX thread
        Platform.runLater(() -> controller.handleDelete(null));

        // JavaFX finishing
        waitForRunLater();

        // Is it removed?
        try (BufferedReader reader = new BufferedReader(new FileReader(testf))) {
            String line = reader.readLine();
            assertEquals("002;Hello World;Album Creation;John, Angi;Welcome to our world;England;images/tlogoforjava/2.png", line);
            assertNull(reader.readLine());
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
