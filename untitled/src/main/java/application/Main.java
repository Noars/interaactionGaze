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
import tobii.Tobii;
import utils.CalibrationConfig;

import java.util.Arrays;

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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        String tobiiNotConnected = Arrays.toString(Tobii.gazePosition());

        try {
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

            System.out.println(tobiiNotConnected);
            System.out.println(Arrays.toString(Tobii.gazePosition()));

            if (!Arrays.toString(Tobii.gazePosition()).equals(tobiiNotConnected)){
                primaryStage.show();
            }else {
                tobiiNotConnectedMessage(primaryStage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void tobiiNotConnectedMessage(Stage primaryStage){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Eye Tracker");
        alert.setHeaderText(null);
        alert.setContentText("Eye Tracker non détecté ! Ou non calibré !");
        alert.show();
        SequentialTransition sleep = new SequentialTransition(new PauseTransition(Duration.seconds(10)));
        sleep.setOnFinished(event -> {
            alert.close();
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            System.exit(0);
        });
        sleep.play();
    }
}
