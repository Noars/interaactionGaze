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
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

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

    GazeDeviceManager gazeDeviceManager;
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    double x, y;
    Cross calibrationCross;
    int test = 0;
    Stage primaryStage;

    List<Point2D> gazedCoordinates = new LinkedList<>();

    Cross cursor;


    Group[] crossTable = new Group[9];
    Circle[] circleTable = new Circle[9];

    double[] angle = new double[9];

    public CircleCalibration(Stage primaryStage, Cross cursor, GazeDeviceManager gazeDeviceManager) {
        super();
        this.gazeDeviceManager = gazeDeviceManager;
        this.primaryStage = primaryStage;
        this.cursor = cursor;

        this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setFocusTraversable(true);
        installEventHandler(this);
    }


    public void startCalibration() {
        calibrationCross = new Cross();
        getChildren().add(calibrationCross);

        EventHandler<Event> event = e -> {
            double[] pos = new double[2];
            if (e.getEventType() == GazeEvent.GAZE_MOVED) {
                pos = getGazePosition(((GazeEvent) e).getX(), ((GazeEvent) e).getY());
            } else if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
                pos[0] = ((MouseEvent) e).getX();
                pos[1] = ((MouseEvent) e).getY();
            }
            x = pos[0];
            y = pos[1];

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

        testNext();

    }

    public void calibrAnim() {

        this.getChildren().add(this.cursor);

        for (int angleIndex = 0; angleIndex <= 7; angleIndex++) {
            angle[angleIndex] = angleBetween(crossTable[CircleCalibration.CENTER], crossTable[CircleCalibration.TOP_LEFT], crossTable[angleIndex]);
        }

        this.setOnMouseMoved(mouseEvent -> {
            double mouseAngle = angleBetween(crossTable[CircleCalibration.CENTER], crossTable[CircleCalibration.TOP_LEFT], mouseEvent.getX(), mouseEvent.getY());
            int sectionIndex;
            for (sectionIndex = 0; sectionIndex < 7; sectionIndex++) {
                if (mouseAngle < angle[sectionIndex + 1]) {
                    break;
                }
            }

            Point2D intersect = getPosInter(mouseEvent.getX(), mouseEvent.getY(),
                    crossTable[CENTER].getLayoutX(), crossTable[CENTER].getLayoutY(),
                    crossTable[sectionIndex].getLayoutX(), crossTable[sectionIndex].getLayoutY(),
                    crossTable[(sectionIndex + 1) % 8].getLayoutX(), crossTable[(sectionIndex + 1) % 8].getLayoutY()
            );

            double smallDistance = Point.distance(intersect.getX(), intersect.getY(), crossTable[sectionIndex].getLayoutX(), crossTable[sectionIndex].getLayoutY());
            double bigDistance = Point.distance(crossTable[sectionIndex].getLayoutX(), crossTable[sectionIndex].getLayoutY(), crossTable[(sectionIndex + 1) % 8].getLayoutX(), crossTable[(sectionIndex + 1) % 8].getLayoutY());

            double newX = (1 - (smallDistance / bigDistance)) * getOffsetX(sectionIndex) + ((smallDistance / bigDistance)) * getOffsetX((sectionIndex + 1) % 8);
            double newY = (1 - (smallDistance / bigDistance)) * getOffsetY(sectionIndex) + ((smallDistance / bigDistance)) * getOffsetY((sectionIndex + 1) % 8);

            double distanceToCursor = Point.distance(mouseEvent.getX(), mouseEvent.getY(), crossTable[CENTER].getLayoutX(), crossTable[CENTER].getLayoutY());
            double distanceToIntersect = Point.distance(intersect.getX(), intersect.getY(), crossTable[CENTER].getLayoutX(), crossTable[CENTER].getLayoutY());

            newX = (1 - (distanceToCursor / distanceToIntersect)) * getOffsetX(CENTER) + ((distanceToCursor / distanceToIntersect)) * newX;
            newY = (1 - (distanceToCursor / distanceToIntersect)) * getOffsetY(CENTER) + ((distanceToCursor / distanceToIntersect)) * newY;

            cursor.setLayoutX(mouseEvent.getX() + newX);
            cursor.setLayoutY(mouseEvent.getY() + newY);


        });
    }

    double getOffsetX(int calibrationPointIndex) {
        return circleTable[calibrationPointIndex].getCenterX() - crossTable[calibrationPointIndex].getLayoutX();
    }

    double getOffsetY(int calibrationPointIndex) {
        return circleTable[calibrationPointIndex].getCenterY() - crossTable[calibrationPointIndex].getLayoutY();
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

    public void testNext() {

        double width = primaryScreenBounds.getWidth() / 10;
        double height = primaryScreenBounds.getHeight() / 10;

        if (test == 9) {
            calibrationCross.setOpacity(0);
            calibrAnim();
        } else {
            if (test == 0) {
                calibrationCross.setLayoutX(width);
                calibrationCross.setLayoutY(height);
            } else if (test == 1) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() / 2);
                calibrationCross.setLayoutY(height);
            } else if (test == 2) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() - width);
                calibrationCross.setLayoutY(height);
            } else if (test == 3) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() - width);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() / 2);
            } else if (test == 4) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() - width);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() - height);
            } else if (test == 5) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() / 2);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() - height);
            } else if (test == 6) {
                calibrationCross.setLayoutX(width);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() - height);
            } else if (test == 7) {
                calibrationCross.setLayoutX(width);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() / 2);
            } else if (test == 8) {
                calibrationCross.setLayoutX(primaryScreenBounds.getWidth() / 2);
                calibrationCross.setLayoutY(primaryScreenBounds.getHeight() / 2);
            }

            crossTable[test] = nextCross();
        }
        test++;
    }

    public Cross nextCross() {
        Cross newCross = new Cross();
        newCross.setLayoutX(calibrationCross.getLayoutX());
        newCross.setLayoutY(calibrationCross.getLayoutY());
        this.getChildren().add(newCross);
        return newCross;
    }

    public void addValue(int numberOfCoordinateToTest) {
        if (gazedCoordinates.size() < numberOfCoordinateToTest) {
            Circle c = new Circle();
            c.setRadius(5);
            c.setCenterX(x);
            c.setCenterY(y);
            c.setFill(Color.LIGHTGOLDENRODYELLOW);
            c.setOpacity(1);

            gazedCoordinates.add(new Point2D(x, y));

            getChildren().add(c);
        } else if (gazedCoordinates.size() == numberOfCoordinateToTest && circleTable[test - 1] == null) {
            double coordXsum = 0, coordYsum = 0;

            for (Point2D gazedCoordinate : gazedCoordinates) {
                coordXsum = coordXsum + gazedCoordinate.getX();
                coordYsum = coordYsum + gazedCoordinate.getY();
            }
            coordXsum = coordXsum / (double) gazedCoordinates.size();
            coordYsum = coordYsum / (double) gazedCoordinates.size();

            Circle newCircle = new Circle();
            newCircle.setRadius(5);
            newCircle.setCenterX(coordXsum);
            newCircle.setCenterY(coordYsum);
            newCircle.setFill(Color.LIGHTSKYBLUE);

            getChildren().add(newCircle);
            circleTable[test - 1] = newCircle;
        }
    }

    public void displayCircle() {

        if (circleTable[test - 1] != null) {
            gazedCoordinates.clear();
            testNext();
        }

    }

    public void installEventHandler(final Node keyNode) {
        final EventHandler<KeyEvent> keyEventHandler = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                    if (test <= 9) {
                        addValue(10);
                    }
                } else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
                    if (test <= 9) {
                        displayCircle();
                    }
                }
            }
        };

        keyNode.setOnKeyPressed(keyEventHandler);
        keyNode.setOnKeyReleased(keyEventHandler);
    }

    public double[] getGazePosition(double x, double y) {


        double[] res = {x, y};

        Point2D p = this.screenToLocal(res[0], res[1]);
        if (p != null) {
            res[0] = p.getX();
            res[1] = p.getY();
        }

        return res;
    }
}
