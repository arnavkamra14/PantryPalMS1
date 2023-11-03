package PantryPal;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Insets;
import javafx.scene.text.*;
import java.io.*;
import java.rmi.RMISecurityException;
import java.util.ArrayList;
import java.util.Collections;
import javax.sound.sampled.*;
//import java.util.Scanner;

class Recipe extends HBox {

    private Button deleteButton;
    private Button startButton;
    private Button stopButton;
    private Label recordingLabel;
    // private AudioFormat audioFormat;

    String defaultButtonStyle = "-fx-border-color: #000000; -fx-font: 12 arial; -fx-pref-height: 100px;";
    String defaultLabelStyle = "-fx-font: 13 arial; -fx-pref-width: 175px; -fx-pref-height: 50px; -fx-text-fill: red; visibility: hidden";

    Recipe() {
        this.setPrefSize(400, 100); // sets size of recipe
        this.setStyle("-fx-background-color: #DAE5EA; -fx-border-width: 0;");

        deleteButton = new Button("X");
        deleteButton.setTextAlignment(TextAlignment.CENTER);
        deleteButton.setPrefHeight(Double.MAX_VALUE);
        deleteButton.setStyle(
                "-fx-background-color: #FFA9A9; -fx-border-width: 0; -fx-border-color: #8B0000; -fx-font-weight: bold");
        this.getChildren().add(deleteButton);

        startButton = new Button("Start Recording Ingredients");
        startButton.setStyle(defaultButtonStyle);

        stopButton = new Button("Stop Recording");
        stopButton.setStyle(defaultButtonStyle);

        recordingLabel = new Label("Recording...");
        recordingLabel.setStyle(defaultLabelStyle);
        recordingLabel.setTextAlignment(TextAlignment.CENTER);

        this.getChildren().addAll(startButton, stopButton, recordingLabel);

        // audioFormat = getAudioFormat();

    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getStopButton() {
        return stopButton;
    }

    public Label getRecordingLabel() {
        return recordingLabel;
    }

}

class RecipeList extends VBox {

    RecipeList() {
        this.setSpacing(3); // sets spacing between recipes
        this.setPrefSize(500, 560);
        this.setStyle("-fx-background-color: #F0F8FF;");
    }
}

class Footer extends HBox {

    private Button addButton;

    Footer() {
        this.setPrefSize(500, 60);
        this.setStyle("-fx-background-color: #F0F8FF;");
        this.setSpacing(15);

        // set a default style for buttons - background color, font size, italics
        String defaultButtonStyle = "-fx-font-style: italic; -fx-background-color: #FFFFFF;  -fx-font-weight: bold; -fx-font: 11 arial;";

        addButton = new Button("Create a Recipe"); // text displayed on add button
        addButton.setStyle(defaultButtonStyle); // styling the button

        this.getChildren().addAll(addButton);
        this.setAlignment(Pos.CENTER); // aligning the buttons to center
    }

    public Button getAddButton() {
        return addButton;
    }
}

class Header extends HBox {

    Header() {
        this.setPrefSize(500, 60); // Size of the header
        this.setStyle("-fx-background-color: #F0F8FF;");

        Text titleText = new Text("PantryPal"); // Text of the Header
        titleText.setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
        this.getChildren().add(titleText);
        this.setAlignment(Pos.CENTER); // Align the text to the Center
    }
}

class AppFrame extends BorderPane {

    private Header header;
    private Footer footer;
    private RecipeList recipeList;

    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;

    private Button addButton;
    private Label recordingLabel;

    AppFrame() {
        // Initialise the header Object
        header = new Header();

        // Create a recipeList Object to hold the recipes
        recipeList = new RecipeList();

        // Initialise the Footer Object
        footer = new Footer();

        /*
         * Website Title: How to create a scroll pane using java fx
         * Source:
         * https://www.tutorialspoint.com/how-to-create-a-scroll-pane-using-javafx
         * Date: 10/5/1023
         * Usage: Example of a scroll pane to see how ScrollPane() works
         */
        ScrollPane scroll = new ScrollPane(recipeList);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        // Add header to the top of the BorderPane
        this.setTop(header);
        // Add scroller to the centre of the BorderPane
        this.setCenter(scroll);
        // Add footer to the bottom of the BorderPane
        this.setBottom(footer);

        // Initialise Button Variables through the getters in Footer
        addButton = footer.getAddButton();

        // Get the audio format
        audioFormat = getAudioFormat();

        // Call Event Listeners for the Buttons
        addListeners();
    }

    public void addListeners() {

        // Add button functionality
        addButton.setOnAction(e -> {
            // Create a new task
            Recipe recipe = new Recipe();
            // Add task to tasklist
            recipeList.getChildren().add(recipe);
            // Add deleteButton
            Button deleteButton = recipe.getDeleteButton();
            Button startButton = recipe.getStartButton();
            Button stopButton = recipe.getStopButton();
            recordingLabel = recipe.getRecordingLabel();

            deleteButton.setOnAction(e1 -> {
                recipeList.getChildren().remove(recipe);
            });

            // Start Button
            startButton.setOnAction(e2 -> {
                startRecording();
            });

            // Stop Button
            stopButton.setOnAction(e3 -> {
                stopRecording();
            });
        });
    }

    private AudioFormat getAudioFormat() {
        // the number of samples of audio per second.
        // 44100 represents the typical sample rate for CD-quality audio.
        float sampleRate = 44100;

        // the number of bits in each sample of a sound that has been digitized.
        int sampleSizeInBits = 16;

        // the number of audio channels in this format (1 for mono, 2 for stereo).
        int channels = 2;

        // whether the data is signed or unsigned.
        boolean signed = true;

        // whether the audio data is stored in big-endian or little-endian order.
        boolean bigEndian = false;

        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }

    private void startRecording() {
        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        try {
                            // the format of the TargetDataLine
                            DataLine.Info dataLineInfo = new DataLine.Info(
                                    TargetDataLine.class,
                                    audioFormat);
                            // the TargetDataLine used to capture audio data from the microphone
                            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                            targetDataLine.open(audioFormat);
                            targetDataLine.start();
                            recordingLabel.setVisible(true);

                            // the AudioInputStream that will be used to write the audio data to a file
                            AudioInputStream audioInputStream = new AudioInputStream(
                                    targetDataLine);

                            // the file that will contain the audio data
                            File audioFile = new File("recording.wav");
                            AudioSystem.write(
                                    audioInputStream,
                                    AudioFileFormat.Type.WAVE,
                                    audioFile);
                            recordingLabel.setVisible(false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
        t.start();
    }

    private void stopRecording() {
        targetDataLine.stop();
        targetDataLine.close();
    }
}

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        AppFrame root = new AppFrame();
        primaryStage.setTitle("PantryPal");
        primaryStage.setScene(new Scene(root, 500, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
