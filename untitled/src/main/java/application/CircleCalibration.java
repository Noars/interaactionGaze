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
import javafx.scene.control.Label;
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

import java.util.LinkedList;
import java.util.List;

public class CircleCalibration extends Pane {

    private static final int NUMBER_OF_CALIBRATION_POINTS = 9;

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
    Group cross;
    int test = 0;
    Stage primaryStage;

    List<Double> xs = new LinkedList<>();
    List<Double> ys = new LinkedList<>();

    Cursor cursor;


    Group[] fixedCrossTable = new Group[NUMBER_OF_CALIBRATION_POINTS];
    Group[] crossTable = new Group[NUMBER_OF_CALIBRATION_POINTS];

    Circle[] fixedCercleTable = new Circle[NUMBER_OF_CALIBRATION_POINTS];
    Circle[] cercleTable = new Circle[NUMBER_OF_CALIBRATION_POINTS];

    double[] angle = new double[NUMBER_OF_CALIBRATION_POINTS];

    public CircleCalibration(Stage primaryStage, Cursor cursor, GazeDeviceManager gazeDeviceManager) {
        super();
        this.gazeDeviceManager = gazeDeviceManager;
        this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setFocusTraversable(true);
        this.primaryStage = primaryStage;
        this.cursor = cursor;
        installEventHandler(this);
    }


    public void startCalibration() {
        Line horizontalLine = new Line();
        Line verticalLine = new Line();
        horizontalLine.setStartX(-10);
        horizontalLine.setEndX(10);
        verticalLine.setStartY(-10);
        verticalLine.setEndY(10);
        cross = new Group(horizontalLine, verticalLine);
        getChildren().add(cross);

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
                        new KeyValue(cross.rotateProperty(), 0),
                        new KeyValue(cross.rotateProperty(), 360)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        //this.addEventHandler(GazeEvent.GAZE_MOVED, event);
        this.addEventHandler(MouseEvent.MOUSE_MOVED, event);
        gazeDeviceManager.addEventFilter(this);

        testNext();

    }

    public void calibrAnim() {

        double width = primaryScreenBounds.getWidth() / 2;
        double height = primaryScreenBounds.getHeight() / 2;

        Timeline t = new Timeline();

        for (int i = 0; i < NUMBER_OF_CALIBRATION_POINTS; i++) {
            t.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                    new KeyValue(crossTable[i].layoutXProperty(), width),
                    new KeyValue(crossTable[i].layoutYProperty(), height),
                    new KeyValue(cercleTable[i].centerXProperty(), width + cercleTable[i].getCenterX() - crossTable[i].getLayoutX()),
                    new KeyValue(cercleTable[i].centerYProperty(), height + cercleTable[i].getCenterY() - crossTable[i].getLayoutY())));
        }
        t.play();

        t.setOnFinished(e -> {
            Timeline t2 = new Timeline();
            double moyenneX = 0;
            for (int i = 0; i < NUMBER_OF_CALIBRATION_POINTS; i++) {
                moyenneX = moyenneX + cercleTable[i].getCenterX();
            }
            moyenneX = moyenneX / NUMBER_OF_CALIBRATION_POINTS;
            double moyenneY = 0;
            for (int i = 0; i < NUMBER_OF_CALIBRATION_POINTS; i++) {
                moyenneY = moyenneY + cercleTable[i].getCenterY();
            }
            moyenneY = moyenneY / NUMBER_OF_CALIBRATION_POINTS;

            for (int i = 0; i < NUMBER_OF_CALIBRATION_POINTS; i++) {
                t2.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                        new KeyValue(cercleTable[i].centerXProperty(), moyenneX)));
                t2.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                        new KeyValue(cercleTable[i].centerYProperty(), moyenneY)));
                t2.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                        new KeyValue(cercleTable[i].fillProperty(), Color.INDIANRED)));
            }
            t2.play();

            double finalMoyenneX = moyenneX;
            double finalMoyenneY = moyenneY;
            t2.setOnFinished(e2 -> {

                Timeline t3 = new Timeline();

                for (int i = 0; i < NUMBER_OF_CALIBRATION_POINTS; i++) {
                    t3.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                            new KeyValue(cercleTable[i].fillProperty(),
                                    Color.DARKSEAGREEN)));

                }
                t3.play();

                t3.setOnFinished(e3 -> {
                    angle[0] = angleBetween(fixedCrossTable[CircleCalibration.CENTER], fixedCrossTable[CircleCalibration.TOP_LEFT], fixedCrossTable[CircleCalibration.TOP_LEFT]);
                    angle[1] = angleBetween(fixedCrossTable[CircleCalibration.CENTER], fixedCrossTable[CircleCalibration.TOP_LEFT], fixedCrossTable[CircleCalibration.TOP_CENTER]);
                    angle[2] = angleBetween(fixedCrossTable[CircleCalibration.CENTER], fixedCrossTable[CircleCalibration.TOP_LEFT], fixedCrossTable[CircleCalibration.TOP_RIGHT]);
                    angle[3] = angleBetween(fixedCrossTable[CircleCalibration.CENTER], fixedCrossTable[CircleCalibration.TOP_LEFT], fixedCrossTable[CircleCalibration.RIGHT_CENTER]);
                    angle[4] = angleBetween(fixedCrossTable[CircleCalibration.CENTER], fixedCrossTable[CircleCalibration.TOP_LEFT], fixedCrossTable[CircleCalibration.BOTTOM_RIGHT]);
                    angle[5] = angleBetween(fixedCrossTable[CircleCalibration.CENTER], fixedCrossTable[CircleCalibration.TOP_LEFT], fixedCrossTable[CircleCalibration.BOTTOM_CENTER]);
                    angle[6] = angleBetween(fixedCrossTable[CircleCalibration.CENTER], fixedCrossTable[CircleCalibration.TOP_LEFT], fixedCrossTable[CircleCalibration.BOTTOM_LEFT]);
                    angle[7] = angleBetween(fixedCrossTable[CircleCalibration.CENTER], fixedCrossTable[CircleCalibration.TOP_LEFT], fixedCrossTable[CircleCalibration.LEFT_CENTER]);


                    StringBuilder s = new StringBuilder();
                    for (int angleIndex = 0; angleIndex < 8; angleIndex++) {
                        s.append("\n").append(angle[angleIndex]);
                        System.out.println(angle[angleIndex]);
                    }

                    Label text = new Label("test");
                    text.setLayoutX(fixedCrossTable[CircleCalibration.CENTER].getLayoutX() + 100);
                    this.getChildren().add(text);

                    System.out.println("calibration done");
                    cursor.crossOffsetX = finalMoyenneX - width;
                    cursor.crossOffsetY = finalMoyenneY - height;
                    this.getChildren().add(cursor);
                    this.setOnMouseMoved(mouseEvent -> {
                        //TODO find the good angle and set the corresponding offset
                        double angleCursor = angleBetween(fixedCrossTable[CircleCalibration.CENTER], fixedCrossTable[CircleCalibration.TOP_LEFT], mouseEvent.getX(), mouseEvent.getY());
                        int i;
                        for (i = 0; i < 7; i++) {
                            if (angleCursor < angle[i + 1]) {
                                break;
                            }
                        }
                        text.setText(s + "\n" + getValueOf(i) + " " + angleCursor);
                        cursor.setLayoutX(mouseEvent.getX() - (fixedCercleTable[i].getCenterX() - fixedCrossTable[i].getLayoutX()));
                        cursor.setLayoutY(mouseEvent.getY() - (fixedCercleTable[i].getCenterY() - fixedCrossTable[i].getLayoutY()));
                    });
                });

            });

        });


    }

    String getValueOf(int i) {
        switch (i) {
            case TOP_LEFT:
                return "TOP_LEFT";
            case TOP_RIGHT:
                return "TOP_RIGHT";
            case BOTTOM_LEFT:
                return "BOTTOM_LEFT";
            case BOTTOM_RIGHT:
                return "BOTTOM_RIGHT";
            case RIGHT_CENTER:
                return "RIGHT_CENTER";
            case BOTTOM_CENTER:
                return "BOTTOM_CENTER";
            case LEFT_CENTER:
                return "LEFT_CENTER";
            case TOP_CENTER:
                return "TOP_CENTER";
            case CENTER:
                return "CENTER";
            default:
                return "WHAAT ?";
        }
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
        if (test == NUMBER_OF_CALIBRATION_POINTS) {
            cross.setOpacity(0);
            calibrAnim();
        } else if (test == 0) {
            cross.setLayoutX(width);
            cross.setLayoutY(height);
            nextCross();
        } else if (test == 1) {
            cross.setLayoutX(primaryScreenBounds.getWidth() / 2);
            cross.setLayoutY(height);
            nextCross();
        } else if (test == 2) {
            cross.setLayoutX(primaryScreenBounds.getWidth() - width);
            cross.setLayoutY(height);
            nextCross();
        } else if (test == 3) {
            cross.setLayoutX(primaryScreenBounds.getWidth() - width);
            cross.setLayoutY(primaryScreenBounds.getHeight() / 2);
            nextCross();
        } else if (test == 4) {
            cross.setLayoutX(primaryScreenBounds.getWidth() - width);
            cross.setLayoutY(primaryScreenBounds.getHeight() - height);
            nextCross();
        } else if (test == 5) {
            cross.setLayoutX(primaryScreenBounds.getWidth() / 2);
            cross.setLayoutY(primaryScreenBounds.getHeight() - height);
            nextCross();
        } else if (test == 6) {
            cross.setLayoutX(width);
            cross.setLayoutY(primaryScreenBounds.getHeight() - height);
            nextCross();
        } else if (test == 7) {
            cross.setLayoutX(width);
            cross.setLayoutY(primaryScreenBounds.getHeight() / 2);
            nextCross();
        } else if (test == 8) {
            cross.setLayoutX(primaryScreenBounds.getWidth() / 2);
            cross.setLayoutY(primaryScreenBounds.getHeight() / 2);
            nextCross();
        }
        test++;
    }

    public void nextCross() {
        Line l1 = new Line();
        Line l2 = new Line();
        l1.setStartX(-10);
        l1.setEndX(10);
        l2.setStartY(-10);
        l2.setEndY(10);
        Group newCross = new Group();
        newCross.getChildren().addAll(l1, l2);
        getChildren().add(newCross);
        newCross.setLayoutX(cross.getLayoutX());
        newCross.setLayoutY(cross.getLayoutY());

        Group newCrossFixed = new Group();
        newCrossFixed.getChildren().addAll(l1, l2);
        getChildren().add(newCrossFixed);
        newCrossFixed.setLayoutX(cross.getLayoutX());
        newCrossFixed.setLayoutY(cross.getLayoutY());

        fixedCrossTable[test] = newCrossFixed;
        crossTable[test] = newCross;
    }

    public void addValue(int iteration) {
        if (xs.size() < iteration) {
            Circle c = new Circle();
            c.setRadius(5);
            c.setCenterX(x);
            c.setCenterY(y);
            c.setFill(Color.LIGHTGOLDENRODYELLOW);
            c.setOpacity(0);

            xs.add(x);
            ys.add(y);

            getChildren().add(c);
        } else if (xs.size() == iteration && cercleTable[test - 1] == null) {
            double sumxs = 0, sumys = 0;

            for (int i = 0; i < xs.size(); i++) {

                sumxs = sumxs + xs.get(i);
                sumys = sumys + ys.get(i);
            }
            sumxs = sumxs / (double) xs.size();
            sumys = sumys / (double) ys.size();


            Circle newCircleFixed = new Circle();
            newCircleFixed.setRadius(8);
            newCircleFixed.setCenterX(sumxs);
            newCircleFixed.setCenterY(sumys);
            newCircleFixed.setFill(Color.LIGHTSKYBLUE);

            getChildren().add(newCircleFixed);
            fixedCercleTable[test - 1] = newCircleFixed;

            Circle newCircle = new Circle();
            newCircle.setRadius(5);
            newCircle.setCenterX(sumxs);
            newCircle.setCenterY(sumys);
            newCircle.setFill(Color.DARKRED);

            getChildren().add(newCircle);
            cercleTable[test - 1] = newCircle;
        }
    }

    public void displayCircle() {

        if (cercleTable[test - 1] != null) {

            xs.clear();
            ys.clear();

            testNext();
        }

    }

    public void installEventHandler(final Node keyNode) {
        final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
            public void handle(final KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.SPACE) {
                    if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                        if (test <= NUMBER_OF_CALIBRATION_POINTS) {
                            addValue(1);
                        }
                    } else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
                        if (test <= NUMBER_OF_CALIBRATION_POINTS) {
                            displayCircle();
                        }
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
