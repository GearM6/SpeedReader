package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends Application {
    public static File file;
    public static ArrayList<String> fileText = new ArrayList<>();
    public static double speed = 200;
    public static AtomicBoolean isPlaying = new AtomicBoolean(false);
    public static Iterator<String> word;
    public static String currentWord = "";
    public static Label displayedWord = new Label("hello.");

    @Override
    public void start(Stage primaryStage) throws Exception{
        FileChooser fileChooser = new FileChooser();
        Button chooseFileButton = new Button("Select a .txt file");
        Button startButton = new Button("Play");
        Button pauseButton = new Button("Pause");
        startButton.setVisible(false);
        pauseButton.setVisible(false);
        displayedWord.getStyleClass().add("selected-word");
        chooseFileButton.setOnAction(event -> {
            file = fileChooser.showOpenDialog(primaryStage);
            try {
                fileText.clear();
                Scanner reader = new Scanner(file);
                displayedWord.setText("Preparing File");
                while(reader.hasNext()){
                    fileText.add(reader.next());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                if(fileText.size() > 0){
                    word = fileText.iterator();
                    displayedWord.setText(word.next());
                    startButton.setVisible(true);
                }
                else {
                    displayedWord.setText("Error.");
                }
            }

        });


        startButton.setOnAction(event -> {
            isPlaying.set(!isPlaying.get());
            Task<Void> timer = new Task<Void>() {
                @Override
                protected Void call() {
                    while(word.hasNext() && isPlaying.get()){
                        currentWord = word.next();
                        Platform.runLater(() -> displayedWord.setText(currentWord));
                        try {
                            Thread.sleep((60*1000/(long) speed));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            };
            new Thread(timer).start();
            startButton.setText(startButton.getText().equals("Play") ? "Pause" : "Play");
        });

        HBox controls = new HBox(startButton, pauseButton);
        Slider speedController = new Slider();
        speedController.setMin(150);
        speedController.setMax(600);
        speedController.setPrefWidth(150);
        speedController.setValue(speed);
        Label currentSpeedLabel = new Label((Math.round(speedController.getValue())) + " WPM");

        speedController.valueProperty().addListener(event -> {
            speed = (speedController.getValue());
            currentSpeedLabel.setText(Math.round(speed) + " WPM");
        });
        HBox speedDisplay = new HBox(speedController, currentSpeedLabel);
        speedDisplay.getStyleClass().add("speed-controller");
        VBox vBox = new VBox(20, chooseFileButton, displayedWord, startButton, speedDisplay);
        primaryStage.setTitle("Speed Reader");
        Scene mainScene = new Scene(vBox, 520, 275);
        mainScene.getStylesheets().add("./styles.css");
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
