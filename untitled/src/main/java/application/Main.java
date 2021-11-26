package application;

import gaze.devicemanager.GazeDeviceManager;
import gaze.devicemanager.GazeDeviceManagerFactory;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {

            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX(primaryScreenBounds.getMinX());
            primaryStage.setY(primaryScreenBounds.getMinY());
            primaryStage.setWidth(primaryScreenBounds.getWidth());
            primaryStage.setHeight(primaryScreenBounds.getHeight());
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");

            Cross cursor = new Cross();
            GazeDeviceManager gazeDeviceManager = GazeDeviceManagerFactory.getInstance().createNewGazeListener(cursor);

            CircleCalibration cc = new CircleCalibration(primaryStage, cursor, gazeDeviceManager);
            Scene calibScene = new Scene(cc, primaryStage.getWidth(), primaryStage.getHeight());
            primaryStage.setScene(calibScene);
            calibScene.setCursor(Cursor.CROSSHAIR);
//            primaryStage.initStyle(StageStyle.TRANSPARENT);
            calibScene.setFill(Color.LIGHTGRAY);
            cc.installEventHandler(primaryStage);
            cc.startCalibration();
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
