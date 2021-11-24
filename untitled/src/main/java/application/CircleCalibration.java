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

import java.util.LinkedList;
import java.util.List;

public class CircleCalibration extends Pane {

    private static final int NUMBER_OF_CALIBRATION_POINTS = 2;

    GazeDeviceManager gazeDeviceManager;
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    double x, y;
    Group cross;
    int test = 0;
    Pane gazeMenu;
    Stage primaryStage;

    List<Double> xs = new LinkedList<>();
    List<Double> ys = new LinkedList<>();

    Cursor g1;


    Group[] crossTable = new Group[NUMBER_OF_CALIBRATION_POINTS];
    Circle[] cercleTable = new Circle[NUMBER_OF_CALIBRATION_POINTS];

    public CircleCalibration(Pane gazeMenu, Stage primaryStage, Cursor g1, GazeDeviceManager gazeDeviceManager) {
        super();
        this.gazeDeviceManager = gazeDeviceManager;
        this.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setFocusTraversable(true);
        this.gazeMenu = gazeMenu;
        this.primaryStage = primaryStage;
        this.g1 = g1;
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

                    Pane mainGazeMenu = new Pane();
                    mainGazeMenu.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, new Insets(0, 0, 0, 0))));
                    mainGazeMenu.getChildren().add(gazeMenu);
                    primaryStage.getScene().setRoot(mainGazeMenu);


                    //primaryStage.setFullScreen(true);


                    g1.crossOffsetX = finalMoyenneX - width;
                    g1.crossOffsetY = finalMoyenneY - height;

                });

            });

        });


    }


    public void testNext() {

        double width = primaryScreenBounds.getWidth() / 10;
        double height = primaryScreenBounds.getHeight() / 10;
        if (test == NUMBER_OF_CALIBRATION_POINTS) {
            calibrAnim();
        } else if (test == 0) {
            cross.setLayoutX(width);
            cross.setLayoutY(height);
            nextCross();
        } else if (test == 1) {
            cross.setLayoutX(primaryScreenBounds.getWidth() - width);
            cross.setLayoutY(height);
            nextCross();
        } else if (test == 2) {
            cross.setLayoutX(width);
            cross.setLayoutY(primaryScreenBounds.getHeight() - height);
            nextCross();
        } else if (test == 3) {
            cross.setLayoutX(primaryScreenBounds.getWidth() - width);
            cross.setLayoutY(primaryScreenBounds.getHeight() - height);
            nextCross();
        } else if (test == 4) {
            cross.setLayoutX(primaryScreenBounds.getWidth() - width);
            cross.setLayoutY(primaryScreenBounds.getHeight() / 2);
            nextCross();
        } else if (test == 5) {
            cross.setLayoutX(primaryScreenBounds.getWidth() / 2);
            cross.setLayoutY(primaryScreenBounds.getHeight() - height);
            nextCross();
        } else if (test == 6) {
            cross.setLayoutX(width);
            cross.setLayoutY(primaryScreenBounds.getHeight() / 2);
            nextCross();
        } else if (test == 7) {
            cross.setLayoutX(primaryScreenBounds.getWidth() / 2);
            cross.setLayoutY(height);
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
        Group g1 = new Group();
        g1.getChildren().addAll(l1, l2);
        getChildren().add(g1);
        g1.setLayoutX(cross.getLayoutX());
        g1.setLayoutY(cross.getLayoutY());
        crossTable[test] = g1;
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

                sumxs = sumxs + (double) xs.get(i);
                sumys = sumys + (double) ys.get(i);
            }
            sumxs = sumxs / (double) xs.size();
            sumys = sumys / (double) ys.size();


            Circle c = new Circle();
            c.setRadius(5);
            c.setCenterX(sumxs);
            c.setCenterY(sumys);

            c.setFill(Color.DARKRED);

            getChildren().add(c);

            cercleTable[test - 1] = c;
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
