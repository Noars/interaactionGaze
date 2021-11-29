package application;

import application.ui.Home;
import gaze.MouseInfo;
import gaze.devicemanager.GazeDeviceManager;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Getter
    TobiiGazeDeviceManager gazeDeviceManager;
    @Getter
    CircleCalibration cc;
    @Getter
    Home home;
    @Getter
    Cross cursor;
    @Getter
    MouseInfo mouseInfo;

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setWidth(500);
            primaryStage.setHeight(200);

            cursor = new Cross();
            mouseInfo = new MouseInfo();
            gazeDeviceManager = GazeDeviceManagerFactory.getInstance().createNewGazeListener(this);

            cc = new CircleCalibration(primaryStage, cursor, gazeDeviceManager);
            home = new Home(this,primaryStage);
             Scene calibScene = new Scene(home, primaryStage.getWidth(), primaryStage.getHeight());
            primaryStage.setScene(calibScene);
            calibScene.setCursor(Cursor.CROSSHAIR);
            calibScene.setFill(Color.LIGHTGRAY);
            cc.installEventHandler(primaryStage);
            cc.startCalibration();
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
        primaryStage.getScene().setRoot(this.getCc());
    }

}
