package application.ui;

import application.Main;
import gaze.devicemanager.GazeDeviceManager;
import gaze.devicemanager.GazeEvent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.CalibrationConfig;
import utils.CalibrationPoint;
import utils.Cross;

import java.io.IOException;

public class CalibrationPane extends Pane {

    public static final int TOP_LEFT = 0;
    public static final int TOP_CENTER = 1;
    public static final int TOP_RIGHT = 2;
    public static final int RIGHT_CENTER = 3;
    public static final int BOTTOM_RIGHT = 4;
    public static final int BOTTOM_CENTER = 5;
    public static final int BOTTOM_LEFT = 6;
    public static final int LEFT_CENTER = 7;
    public static final int CENTER = 8;
    public static final int TESTENDED = 9;

    GazeDeviceManager gazeDeviceManager;
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    Cross calibrationCross;
    int currentTest = TOP_LEFT;

    Point2D curCoord = new Point2D(0, 0);
    Stage primaryStage;

    CalibrationConfig calibrationPoints;

    public CalibrationPane(Stage primaryStage, GazeDeviceManager gazeDeviceManager, CalibrationConfig mainCalibrationConfig) {
        super();
        this.gazeDeviceManager = gazeDeviceManager;
        this.primaryStage = primaryStage;
        this.calibrationPoints = mainCalibrationConfig;

        //this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setFocusTraversable(true);
    }


    public void startCalibration(Main main) {
        calibrationCross = new Cross();
        getChildren().add(calibrationCross);

        EventHandler<Event> event = e -> {
            if (e.getEventType() == GazeEvent.GAZE_MOVED) {
                curCoord = getGazePosition(((GazeEvent) e).getX(), ((GazeEvent) e).getY());
            } else if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
                curCoord = new Point2D(((MouseEvent) e).getX(), ((MouseEvent) e).getY());
            }
        };

        Timeline rotateCalibrationCross = new Timeline();
        rotateCalibrationCross.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(1), // set start position at 0
                        new KeyValue(calibrationCross.rotateProperty(), 0),
                        new KeyValue(calibrationCross.rotateProperty(), 360)));
        rotateCalibrationCross.setCycleCount(Timeline.INDEFINITE);
        rotateCalibrationCross.play();

        this.addEventHandler(GazeEvent.GAZE_MOVED, event);
        //this.addEventHandler(MouseEvent.MOUSE_MOVED, event);
        gazeDeviceManager.addEventFilter(this);

        startCurrentTest(main);
    }

    public void endCalibration(Main main) {

        Button backHome = new Button("Valider");
        backHome.setOnAction((e) -> {
            main.goToMain(primaryStage);

        });

        this.getChildren().add(backHome);

        calibrationPoints.setAllAngles();
        try {
            calibrationPoints.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startCurrentTest(Main main) {

        double width = primaryScreenBounds.getWidth() / 10;
        double height = primaryScreenBounds.getHeight() / 10;

        if (currentTest == TESTENDED) {
            calibrationCross.setOpacity(0);
            endCalibration(main);
        } else {
            if (currentTest == TOP_LEFT) {
                calibrationCross.setLayoutX(width);
                calibrationCross.setLayoutY(height);
            } else if (currentTest == TOP_CENTER) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() / 2);
                calibrationCross.setLayoutY(height);
            } else if (currentTest == TOP_RIGHT) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() - width);
                calibrationCross.setLayoutY(height);
            } else if (currentTest == RIGHT_CENTER) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() - width);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() / 2);
            } else if (currentTest == BOTTOM_RIGHT) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() - width);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() - height);
            } else if (currentTest == BOTTOM_CENTER) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() / 2);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() - height);
            } else if (currentTest == BOTTOM_LEFT) {
                calibrationCross.setLayoutX(width);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() - height);
            } else if (currentTest == LEFT_CENTER) {
                calibrationCross.setLayoutX(width);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() / 2);
            } else if (currentTest == CENTER) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() / 2);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() / 2);
            }
            calibrationPoints.get(currentTest).setCross(nextCross());
        }
    }

    public Cross nextCross() {
        Cross newCross = new Cross();
        newCross.setLayoutX(calibrationCross.getLayoutX());
        newCross.setLayoutY(calibrationCross.getLayoutY());
        this.getChildren().add(newCross);
        return newCross;
    }

    public void addValue(int numberOfCoordinateToTest) {
        if (calibrationPoints.get(currentTest).capturedCoordinates.size() < numberOfCoordinateToTest) {
            Circle c = new Circle();
            c.setRadius(5);
            c.setCenterX(curCoord.getX());
            c.setCenterY(curCoord.getY());
            c.setFill(Color.RED);
            c.setOpacity(1);

            calibrationPoints.get(currentTest).capturedCoordinates.add(curCoord);

            getChildren().add(c);
        } else if (calibrationPoints.get(currentTest).capturedCoordinates.size() == numberOfCoordinateToTest && calibrationPoints.get(currentTest).circle == null) {
            double coordXsum = 0, coordYsum = 0;

            for (Point2D gazedCoordinate : calibrationPoints.get(currentTest).capturedCoordinates) {
                coordXsum = coordXsum + gazedCoordinate.getX();
                coordYsum = coordYsum + gazedCoordinate.getY();
            }
            coordXsum = coordXsum / (double) calibrationPoints.get(currentTest).capturedCoordinates.size();
            coordYsum = coordYsum / (double) calibrationPoints.get(currentTest).capturedCoordinates.size();

            Circle newCircle = new Circle();
            newCircle.setRadius(5);
            newCircle.setCenterX(coordXsum);
            newCircle.setCenterY(coordYsum);
            newCircle.setFill(Color.LIGHTSKYBLUE);

            getChildren().add(newCircle);
            calibrationPoints.get(currentTest).setCircle(newCircle);
        }
    }

    public void installEventHandler(final Stage keyNode, Main main) {
        final EventHandler<KeyEvent> keyEventHandler = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                    if (currentTest < 9) {
                        addValue(10);
                    }
                } else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
                    if (currentTest < 9 && calibrationPoints.get(currentTest).circle != null) {
                        currentTest++;
                        startCurrentTest(main);
                    }
                }
            }
        };

        keyNode.getScene().setOnKeyPressed(keyEventHandler);
        keyNode.getScene().setOnKeyReleased(keyEventHandler);
    }

    public Point2D getGazePosition(double x, double y) {
        Point2D pointCoord = new Point2D(x, y);
        Point2D localPointCoord = this.screenToLocal(pointCoord.getX(), pointCoord.getY());
        if (localPointCoord != null) {
            return localPointCoord;
        }
        System.out.println(x + " " + y);
        return pointCoord;
    }
}
