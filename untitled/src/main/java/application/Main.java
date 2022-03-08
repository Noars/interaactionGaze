package application;

import application.ui.*;
import gaze.MouseInfo;
import gaze.devicemanager.GazeDeviceManagerFactory;
import gaze.devicemanager.TobiiGazeDeviceManager;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import utils.CalibrationConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

@Slf4j
public class Main extends Application {

    @Getter
    TobiiGazeDeviceManager gazeDeviceManager;
    @Getter
    CalibrationPane calibrationPane;
    @Getter
    MainPane home;
    @Getter
    MouseInfo mouseInfo;
    @Getter
    OptionsPane optionsPane;
    @Getter
    OptionsCalibrationPane optionsCalibrationPane;

    DecoratedPane decoratedPane;

    boolean startWithCalibration = false;

    public static void main(String[] args) {

        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("nux")){
                File myFile = new File("args.txt");
                FileWriter myWritter = new FileWriter("args.txt");
                myWritter.write(args[0]);
                myWritter.close();
            }else{
                String userName = System.getProperty("user.name");
                File myFolder = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze");
                myFolder.mkdirs();
                File myFile = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\args.txt");
                if (!myFile.exists()){
                    FileWriter myWritter = new FileWriter("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\args.txt");
                    myWritter.write("true");
                    myWritter.close();
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setWidth(600);
        primaryStage.setHeight(250);
        primaryStage.setTitle("InteraactionGaze");

        mouseInfo = new MouseInfo();
        CalibrationConfig calibrationConfig = new CalibrationConfig();
        gazeDeviceManager = GazeDeviceManagerFactory.getInstance().createNewGazeListener(this, calibrationConfig);

        optionsPane = new OptionsPane(primaryStage, this);
        optionsCalibrationPane = new OptionsCalibrationPane(primaryStage, this, calibrationConfig);
        calibrationPane = new CalibrationPane(primaryStage, gazeDeviceManager, calibrationConfig);
        home = new MainPane(this, primaryStage);

        decoratedPane = new DecoratedPane(primaryStage);
        decoratedPane.setCenter(home);

        Scene calibScene = new Scene(decoratedPane, primaryStage.getWidth(), primaryStage.getHeight());
        calibScene.getStylesheets().add("style.css");
        primaryStage.setScene(calibScene);
        calibScene.setFill(Color.TRANSPARENT);
        // calibrationPane.installEventHandler(primaryStage, this);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        this.getGazeDeviceManager().setPause(false);

        String os = System.getProperty("os.name").toLowerCase();

        try {
            File myFile;
            if (os.contains("win")){
                String userName = System.getProperty("user.name");
                myFile = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\args.txt");
            }else {
                myFile = new File("args.txt");
            }

            Scanner myReader = new Scanner(myFile);
            String data = myReader.nextLine();

            if (Objects.equals(data, "true")){
                if (os.contains("win")){
                    startMessageCalibration(primaryStage);
                }else {
                    startCalibration(primaryStage);
                    startWithCalibration = true;
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("");
        }

        if (!startWithCalibration){
            this.getGazeDeviceManager().setPause(true);
        }

        primaryStage.show();
    }

    public void startMessageCalibration(Stage primaryStage) {
        primaryStage.show();
        Alert startAlert = new Alert(Alert.AlertType.INFORMATION);
        startAlert.setTitle("Start Calibration");
        startAlert.setHeaderText(null);
        startAlert.setContentText("Nous allons commencer avec une première calibration !");
        startAlert.show();
        SequentialTransition startSleep = new SequentialTransition(new PauseTransition(Duration.seconds(5)));
        startSleep.setOnFinished(event -> {
            startAlert.close();
            startCalibration(primaryStage);
            startWithCalibration = true;
        });
        startSleep.play();
    }

    public void startCalibration(Stage primaryStage) {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.getScene().setRoot(this.getCalibrationPane());
        calibrationPane.startCalibration(this);
    }

    public void goToOptionsCalibration(Stage primaryStage){
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.getOptionsCalibrationPane());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    public void goToOptions(Stage primaryStage) {
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.getOptionsPane());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    public void goToMain(Stage primaryStage) {
        primaryStage.setFullScreen(false);
        primaryStage.getScene().setRoot(decoratedPane);
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.getHome());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }
}
