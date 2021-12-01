package application;

import application.ui.CalibrationPane;
import application.ui.MainPane;
import application.ui.OptionsPane;
import gaze.MouseInfo;
import gaze.devicemanager.GazeDeviceManagerFactory;
import gaze.devicemanager.TobiiGazeDeviceManager;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main extends Application {

    @Getter
    TobiiGazeDeviceManager gazeDeviceManager;
    @Getter
    CalibrationPane calibrationPane;
    @Getter
    MainPane home;
    @Getter
    Cross cursor;
    @Getter
    MouseInfo mouseInfo;
    @Getter
    OptionsPane optionsPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            primaryStage.setTitle("InteraactionGaze");

            cursor = new Cross();
            mouseInfo = new MouseInfo();
            gazeDeviceManager = GazeDeviceManagerFactory.getInstance().createNewGazeListener(this);

            optionsPane = new OptionsPane(primaryStage, this);
            calibrationPane = new CalibrationPane(primaryStage, cursor, gazeDeviceManager);
            home = new MainPane(this, primaryStage);
            Scene calibScene = new Scene(home, primaryStage.getWidth(), primaryStage.getHeight());
            calibScene.getStylesheets().add("style.css");
            primaryStage.setScene(calibScene);
            //primaryStage.initStyle(StageStyle.UNDECORATED);
            calibScene.setFill(Color.LIGHTGRAY);
            calibrationPane.installEventHandler(primaryStage, this);
            calibrationPane.startCalibration(this);
            primaryStage.show();
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
        primaryStage.getScene().setCursor(Cursor.CROSSHAIR);
    }

    public void goToOptions(Stage primaryStage) {
        primaryStage.getScene().setRoot(this.getOptionsPane());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    public void goToMain(Stage primaryStage) {
        primaryStage.setFullScreen(false);
        primaryStage.getScene().setRoot(this.getHome());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

}
