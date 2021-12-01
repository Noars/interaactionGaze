package application.ui;

import application.CalibrationPoint;
import application.Cross;
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
import javafx.scene.Group;
import javafx.scene.Node;
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

import java.awt.*;

public class CalibrationPane extends Pane {

    private static final int TOP_LEFT = 0;
    private static final int TOP_CENTER = 1;
    private static final int TOP_RIGHT = 2;
    private static final int RIGHT_CENTER = 3;
    private static final int BOTTOM_RIGHT = 4;
    private static final int BOTTOM_CENTER = 5;
    private static final int BOTTOM_LEFT = 6;
    private static final int LEFT_CENTER = 7;
    private static final int CENTER = 8;
    private static final int TESTENDED = 9;

    GazeDeviceManager gazeDeviceManager;
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    Cross calibrationCross;
    Cross cursor;
    int currentTest = TOP_LEFT;

    Point2D curCoord = new Point2D(0, 0);
    Stage primaryStage;

    CalibrationPoint[] calibrationPoints = new CalibrationPoint[9];

    double[] angle = new double[9];

    public CalibrationPane(Stage primaryStage, Cross cursor, GazeDeviceManager gazeDeviceManager) {
        super();
        this.gazeDeviceManager = gazeDeviceManager;
        this.primaryStage = primaryStage;
        this.cursor = cursor;

        for (int i = 0; i < 9; i++) {
            calibrationPoints[i] = new CalibrationPoint();
        }

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
                curCoord = new Point2D (((MouseEvent) e).getX(),((MouseEvent) e).getY());
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
        backHome.setOnAction((e)->{
            main.goToMain(primaryStage);

        });

        this.getChildren().add(backHome);

        for (int angleIndex = 0; angleIndex <= 7; angleIndex++) {
            angle[angleIndex] = angleBetween(calibrationPoints[CENTER].cross, calibrationPoints[TOP_LEFT].cross, calibrationPoints[angleIndex].cross);
        }

        EventHandler<Event> event = e -> {
            if (e.getEventType() == GazeEvent.GAZE_MOVED) {
                handle(((GazeEvent) e).getX(), ((GazeEvent) e).getY());
            } else if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
                handle(((MouseEvent) e).getX(), ((MouseEvent) e).getY());
            }
        };

        this.addEventHandler(GazeEvent.GAZE_MOVED, event);
        //this.addEventHandler(MouseEvent.MOUSE_MOVED, event);
        gazeDeviceManager.addEventFilter(this);

    }

    void handle(double eventX, double eventY) {

        double mouseAngle = angleBetween(calibrationPoints[CENTER].cross, calibrationPoints[TOP_LEFT].cross, eventX, eventY);

        int previousPoint;
        for (previousPoint = 0; previousPoint < 7; previousPoint++) {
            if (mouseAngle < angle[previousPoint + 1]) {
                break;
            }
        }
        int nextPoint = (previousPoint + 1) % 8;

        Point2D intersect = getPosInter(
                eventX, eventY,
                calibrationPoints[CENTER].getCrossX(), calibrationPoints[CENTER].getCrossY(),
                calibrationPoints[previousPoint].getCrossX(), calibrationPoints[previousPoint].getCrossY(),
                calibrationPoints[nextPoint].getCrossX(), calibrationPoints[nextPoint].getCrossY()
        );

        double interToNext = Point.distance(intersect.getX(), intersect.getY(), calibrationPoints[previousPoint].getCrossX(), calibrationPoints[previousPoint].getCrossY());
        double previousToNext = Point.distance(calibrationPoints[previousPoint].getCrossX(), calibrationPoints[previousPoint].getCrossY(), calibrationPoints[nextPoint].getCrossX(), calibrationPoints[nextPoint].getCrossY());

        double newX = (1 - (interToNext / previousToNext)) * calibrationPoints[previousPoint].getOffsetX() + ((interToNext / previousToNext)) * calibrationPoints[nextPoint].getOffsetX();
        double newY = (1 - (interToNext / previousToNext)) * calibrationPoints[previousPoint].getOffsetY() + ((interToNext / previousToNext)) * calibrationPoints[nextPoint].getOffsetY();

        double centerToMouse = Point.distance(eventX, eventY, calibrationPoints[CENTER].getCrossX(), calibrationPoints[CENTER].getCrossY());
        double centerToInter = Point.distance(intersect.getX(), intersect.getY(), calibrationPoints[CENTER].getCrossX(), calibrationPoints[CENTER].getCrossY());

        newX = (1 - (centerToMouse / centerToInter)) * calibrationPoints[CENTER].getOffsetX() + ((centerToMouse / centerToInter)) * newX;
        newY = (1 - (centerToMouse / centerToInter)) * calibrationPoints[CENTER].getOffsetY() + ((centerToMouse / centerToInter)) * newY;

        cursor.setTranslateX(-newX);
        cursor.setTranslateY(-newY);

    }


    Point2D getPosInter(double xcursor, double ycursor, double xcenter, double ycenter, double xcurent, double ycurent, double xprevious, double yprevious) {
        Point2D vector = new Point2D(xcursor - xcenter, ycursor - ycenter);
        double coef = 1;
        if (xcurent == xprevious) {
            coef = (xcurent - xcenter) / vector.getX();
        } else if (ycurent == yprevious) {
            coef = (ycurent - ycenter) / vector.getY();
        }
        return new Point2D(vector.getX() * coef + xcenter, vector.getY() * coef + ycenter);
    }

    private double angleBetween(Group center, Group current, Group previous) {

        return (Math.toDegrees(Math.atan2(current.getLayoutX() - center.getLayoutX(), current.getLayoutY() - center.getLayoutY()) -
                Math.atan2(previous.getLayoutX() - center.getLayoutX(), previous.getLayoutY() - center.getLayoutY())) + 360) % 360;
    }

    private double angleBetween(Group center, Group current, double mouseX, double mouseY) {

        return (Math.toDegrees(Math.atan2(current.getLayoutX() - center.getLayoutX(), current.getLayoutY() - center.getLayoutY()) -
                Math.atan2(mouseX - center.getLayoutX(), mouseY - center.getLayoutY())) + 360) % 360;
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
            calibrationPoints[currentTest].cross = nextCross();
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
        if (calibrationPoints[currentTest].capturedCoordinates.size() < numberOfCoordinateToTest) {
            Circle c = new Circle();
            c.setRadius(5);
            c.setCenterX(curCoord.getX());
            c.setCenterY(curCoord.getY());
            c.setFill(Color.LIGHTGOLDENRODYELLOW);
            c.setOpacity(1);

            calibrationPoints[currentTest].capturedCoordinates.add(curCoord);

            getChildren().add(c);
        } else if (calibrationPoints[currentTest].capturedCoordinates.size() == numberOfCoordinateToTest && calibrationPoints[currentTest].circle == null) {
            double coordXsum = 0, coordYsum = 0;

            for (Point2D gazedCoordinate : calibrationPoints[currentTest].capturedCoordinates) {
                coordXsum = coordXsum + gazedCoordinate.getX();
                coordYsum = coordYsum + gazedCoordinate.getY();
            }
            coordXsum = coordXsum / (double) calibrationPoints[currentTest].capturedCoordinates.size();
            coordYsum = coordYsum / (double) calibrationPoints[currentTest].capturedCoordinates.size();

            Circle newCircle = new Circle();
            newCircle.setRadius(5);
            newCircle.setCenterX(coordXsum);
            newCircle.setCenterY(coordYsum);
            newCircle.setFill(Color.LIGHTSKYBLUE);

            getChildren().add(newCircle);
            calibrationPoints[currentTest].circle = newCircle;
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
                    if (currentTest < 9 && calibrationPoints[currentTest].circle != null) {
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
