package application;

import gaze.devicemanager.GazeDeviceManager;
import gaze.devicemanager.GazeEvent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;

public class CircleCalibration extends Pane {

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

    Point2D curCoord;
    Stage primaryStage;

    CalibrationPoint[] calibrationPoints = new CalibrationPoint[9];

    double[] angle = new double[9];

    public CircleCalibration(Stage primaryStage, Cross cursor, GazeDeviceManager gazeDeviceManager) {
        super();
        this.gazeDeviceManager = gazeDeviceManager;
        this.primaryStage = primaryStage;
        this.cursor = cursor;

        for(int i = 0; i <9; i++){
            calibrationPoints[i] = new CalibrationPoint();
        }

        this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setFocusTraversable(true);
        installEventHandler(this);
    }


    public void startCalibration() {
        calibrationCross = new Cross();
        getChildren().add(calibrationCross);

        EventHandler<Event> event = e -> {
            if (e.getEventType() == GazeEvent.GAZE_MOVED) {
                curCoord = getGazePosition(((GazeEvent) e).getX(), ((GazeEvent) e).getY());
            } else if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
                curCoord = new Point2D (((MouseEvent) e).getX(),((MouseEvent) e).getY());
            }
        };

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(1), // set start position at 0
                        new KeyValue(calibrationCross.rotateProperty(), 0),
                        new KeyValue(calibrationCross.rotateProperty(), 360)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        //this.addEventHandler(GazeEvent.GAZE_MOVED, event);
        this.addEventHandler(MouseEvent.MOUSE_MOVED, event);
        gazeDeviceManager.addEventFilter(this);

        startCurrentTest();
    }

    public void calibrAnim() {

        this.getChildren().add(this.cursor);

        for (int angleIndex = 0; angleIndex <= 7; angleIndex++) {
            angle[angleIndex] = angleBetween(calibrationPoints[CENTER].cross, calibrationPoints[TOP_LEFT].cross, calibrationPoints[angleIndex].cross);
        }

        this.setOnMouseMoved(mouseEvent -> {
            double mouseAngle = angleBetween(calibrationPoints[CENTER].cross, calibrationPoints[TOP_LEFT].cross, mouseEvent.getX(), mouseEvent.getY());
            int sectionIndex;
            for (sectionIndex = 0; sectionIndex < 7; sectionIndex++) {
                if (mouseAngle < angle[sectionIndex + 1]) {
                    break;
                }
            }

            Point2D intersect = getPosInter(mouseEvent.getX(), mouseEvent.getY(),
                    calibrationPoints[CENTER].getCrossX(), calibrationPoints[CENTER].getCrossY(),
                    calibrationPoints[sectionIndex].getCrossX(), calibrationPoints[sectionIndex].getCrossY(),
                    calibrationPoints[(sectionIndex + 1) % 8].getCrossX(), calibrationPoints[(sectionIndex + 1) % 8].getCrossY()
            );

            double smallDistance = Point.distance(intersect.getX(), intersect.getY(), calibrationPoints[sectionIndex].getCrossX(), calibrationPoints[sectionIndex].getCrossY());
            double bigDistance = Point.distance(calibrationPoints[sectionIndex].getCrossX(), calibrationPoints[sectionIndex].getCrossY(), calibrationPoints[(sectionIndex + 1) % 8].getCrossX(), calibrationPoints[(sectionIndex + 1) % 8].getCrossY());

            double newX = (1 - (smallDistance / bigDistance)) * getOffsetX(sectionIndex) + ((smallDistance / bigDistance)) * getOffsetX((sectionIndex + 1) % 8);
            double newY = (1 - (smallDistance / bigDistance)) * getOffsetY(sectionIndex) + ((smallDistance / bigDistance)) * getOffsetY((sectionIndex + 1) % 8);

            double distanceToCursor = Point.distance(mouseEvent.getX(), mouseEvent.getY(), calibrationPoints[CENTER].getCrossX(), calibrationPoints[CENTER].getCrossY());
            double distanceToIntersect = Point.distance(intersect.getX(), intersect.getY(), calibrationPoints[CENTER].getCrossX(), calibrationPoints[CENTER].getCrossY());

            newX = (1 - (distanceToCursor / distanceToIntersect)) * getOffsetX(CENTER) + ((distanceToCursor / distanceToIntersect)) * newX;
            newY = (1 - (distanceToCursor / distanceToIntersect)) * getOffsetY(CENTER) + ((distanceToCursor / distanceToIntersect)) * newY;

            cursor.setLayoutX(mouseEvent.getX() + newX);
            cursor.setLayoutY(mouseEvent.getY() + newY);


        });
    }

    double getOffsetX(int calibrationPointIndex) {
        return calibrationPoints[calibrationPointIndex].getOffsetX() - calibrationPoints[calibrationPointIndex].getCrossX();
    }

    double getOffsetY(int calibrationPointIndex) {
        return calibrationPoints[calibrationPointIndex].getOffsetY() - calibrationPoints[calibrationPointIndex].getCrossY();
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

    public void startCurrentTest() {

        double width = primaryScreenBounds.getWidth() / 10;
        double height = primaryScreenBounds.getHeight() / 10;

        if (currentTest == TESTENDED) {
            calibrationCross.setOpacity(0);
            calibrAnim();
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
        } else if (calibrationPoints[currentTest].capturedCoordinates.size() == numberOfCoordinateToTest && calibrationPoints[currentTest].offset == null) {
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
            calibrationPoints[currentTest].offset = newCircle;
        }
    }

    public void installEventHandler(final Node keyNode) {
        final EventHandler<KeyEvent> keyEventHandler = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                    if (currentTest < 9) {
                        addValue(10);
                    }
                } else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
                    if (currentTest < 9 && calibrationPoints[currentTest].offset != null) {
                        currentTest++;
                        startCurrentTest();
                    }
                }
            }
        };

        keyNode.setOnKeyPressed(keyEventHandler);
        keyNode.setOnKeyReleased(keyEventHandler);
    }

    public Point2D getGazePosition(double x, double y) {
        Point2D pointCoord = new Point2D(x, y);
        Point2D localPointCoord = this.screenToLocal(pointCoord.getX(), pointCoord.getY());
        if (localPointCoord != null) {
            return localPointCoord;
        }
        return pointCoord;
    }
}
