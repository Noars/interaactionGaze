package application;

import application.ui.CalibrationPane;
import application.ui.DecoratedPane;
import application.ui.MainPane;
import application.ui.OptionsPane;
import gaze.MouseInfo;
import gaze.devicemanager.GazeDeviceManagerFactory;
import gaze.devicemanager.TobiiGazeDeviceManager;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import utils.CalibrationConfig;

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

    DecoratedPane decoratedPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            primaryStage.setTitle("InteraactionGaze");

            mouseInfo = new MouseInfo();
            CalibrationConfig calibrationConfig = new CalibrationConfig();
            gazeDeviceManager = GazeDeviceManagerFactory.getInstance().createNewGazeListener(this, calibrationConfig);

            optionsPane = new OptionsPane(primaryStage, this);
            calibrationPane = new CalibrationPane(primaryStage, gazeDeviceManager, calibrationConfig);
            home = new MainPane(this, primaryStage);

            decoratedPane = new DecoratedPane(primaryStage);
            decoratedPane.setCenter(home);

            Scene calibScene = new Scene(decoratedPane, primaryStage.getWidth(), primaryStage.getHeight());
            calibScene.getStylesheets().add("style.css");
            primaryStage.setScene(calibScene);
            calibScene.setFill(Color.TRANSPARENT);
            calibrationPane.installEventHandler(primaryStage, this);
            primaryStage.initStyle(StageStyle.TRANSPARENT);

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
        calibrationPane.startCalibration(this);
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
