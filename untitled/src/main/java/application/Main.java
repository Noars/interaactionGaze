package application;

import gaze.devicemanager.GazeDeviceManager;
import gaze.devicemanager.GazeDeviceManagerFactory;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {

    GazeDeviceManager gazeDeviceManager = GazeDeviceManagerFactory.getInstance().createNewGazeListener();

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

            BorderPane bp = new BorderPane();
            Scene calibScene = new Scene(bp, primaryStage.getWidth(), primaryStage.getHeight());
            primaryStage.setScene(calibScene);

            Cursor cursor = new Cursor();

            GazeMenu gzm = new GazeMenu(180, cursor, gazeDeviceManager);

            for (int j = 0; j < 26; j++) {
                gzm.add(new Circle());
            }

            gzm.draw();
            gzm.setLayoutX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() / 2);
            gzm.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() / 2);

            gzm.activatedSection.addListener(e -> {
                char index = gzm.getChat(gzm.activatedSection.get());
                if (gzm.enabled.get() && gzm.activatedSection.get() != -1) {

                    setChar(gzm, index);
                    gzm.colorSelected(gzm.activatedSection.get(), Color.DARKSEAGREEN);
                    gzm.enabled.set(false);

                } else if (gzm.enabled.get() && index != '\0') {

                    setChar(gzm, index);
                    gzm.circles[gzm.activatedSection.get()].setFill(Color.INDIANRED);
                    gzm.enabled.set(false);

                } else if (index == '\0') {

                    gzm.enabled.set(true);
				}

            });

            CircleCalibration cc = new CircleCalibration(gzm, primaryStage, cursor, gazeDeviceManager);


            bp.setCenter(cc);


            cc.startCalibration();
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setChar(GazeMenu gzm, char index) {
        Text letter = new Text("" + index);
        letter.setX(gzm.shadows[gzm.activatedSection.get()].getCenterX() - letter.getBoundsInLocal().getWidth() / 2);
        letter.setY(gzm.shadows[gzm.activatedSection.get()].getCenterY());
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, // set start position at 0
                        new KeyValue(letter.scaleXProperty(), 0),
                        new KeyValue(letter.scaleYProperty(), 0)),
                new KeyFrame(Duration.millis(100), // set start position at 0
                        new KeyValue(letter.scaleXProperty(), 2.5),
                        new KeyValue(letter.scaleYProperty(), 2.5)
                ));
        timeline.play();
    }


}
