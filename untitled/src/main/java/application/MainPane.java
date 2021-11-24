package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;

import java.io.File;
import java.io.FileWriter;

public class MainPane extends Pane {


    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

    Button buttonStart;
    int repetition = 0;
    int currentIndex = -1;
    int totalAttempt = 0;
    int goodAttempt = 0;
    int click = 0;
    Text textX;
    Text textY;
    CircleTests circleTest;
    File file;

    public MainPane(CircleTests ct, File fichier) {
        super();
        circleTest = ct;
        file = fichier;
        this.getChildren().add(circleTest);
        createInfoViewer();
        installEventHandler(circleTest);
        setButtonStart();
        setButtonRestart();
    }

    public Button getButtonStart() {
        return buttonStart;
    }

    public void updateInfoViewer() {
        textX.setText("Good : " + this.goodAttempt);
        textY.setText("Total : " + this.totalAttempt);
    }

    public void createInfoViewer() {
        textX = new Text();
        textX.setFont(new Font(20));
        textX.setWrappingWidth(100);
        textX.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 100);
        textX.setY(primaryScreenBounds.getMinY() + 20);
        textX.setFill(Color.DARKSEAGREEN);
        textY = new Text();
        textY.setFont(new Font(20));
        textY.setWrappingWidth(100);
        textY.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 100);
        textY.setY(primaryScreenBounds.getMinY() + textX.getY() + 20);
        textY.setFill(Color.PALEVIOLETRED);
        this.getChildren().addAll(textX, textY);
        updateInfoViewer();

    }

    public void setButtonStart() {
        buttonStart = new Button("Start Test " + circleTest.numberOfParts);
        buttonStart.setPrefWidth(primaryScreenBounds.getWidth() / 10);
        buttonStart.setPrefHeight(primaryScreenBounds.getHeight() / 10);
        buttonStart.toFront();
        buttonStart.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - buttonStart.getPrefHeight() - 20);
        buttonStart.setOnMouseMoved(e -> {
            buttonStart.setOpacity(1);
        });
        buttonStart.setOnMouseExited(e -> {
            buttonStart.setOpacity(0.2);
        });
        this.getChildren().add(buttonStart);
        buttonStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                onStartPause();
            }
        });

    }

    public void comeBack() {
        if (click != 0) {
            circleTest.setHandler();
        }
    }

    public void onStartPause() {
        if (click == 0) {
            click = 1;
            buttonStart.setText("Pause");
            currentIndex = circleTest.choosePart();
            circleTest.setHandler();
        } else if (click == 1) {
            click = 2;
            buttonStart.setText("Play");
            circleTest.area.setFill(Color.LIGHTGREY.brighter().darker());
            circleTest.start = false;
            circleTest.g.setCursorOff();

        } else if (click == 2) {
            click = 1;
            buttonStart.setText("Pause");
        }
    }

    public void setButtonRestart() {
        Button restart = new Button("Restart test " + circleTest.numberOfParts);
        restart.setPrefWidth(primaryScreenBounds.getWidth() / 10);
        restart.setPrefHeight(primaryScreenBounds.getHeight() / 10);
        restart.toFront();
        restart.setLayoutY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - restart.getPrefHeight() - 20);
        restart.setLayoutX(primaryScreenBounds.getMinX() + buttonStart.getPrefWidth());

        restart.setOpacity(0.2);
        restart.setOnMouseMoved(e -> {
            restart.setOpacity(1);
        });

        restart.setOnMouseExited(e -> {
            restart.setOpacity(0.2);
        });

        this.getChildren().add(restart);

        restart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                totalAttempt = 0;
                goodAttempt = 0;
                updateInfoViewer();

            }
        });

    }


    public void installEventHandler(final Node keyNode) {
        // handler for enter key press / release events, other keys are
        // handled by the parent (keyboard) node handler
        final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
            public void handle(final KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.P) {
                    if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                        onStartPause();
                    }
                }
            }
        };

        keyNode.setOnKeyPressed(keyEventHandler);
        keyNode.setOnKeyReleased(keyEventHandler);
    }

    public void checkAngle(Line l, boolean t) {

        //TODO check the following idea
        Point2D val = circleTest.c.sceneToLocal(l.getEndX(), l.getEndY());

        double angle1 = calculateAngle(circleTest.lineList.get(currentIndex).getEndX(),
                circleTest.lineList.get(currentIndex).getEndY(),
                val.getX(), val.getY()
        );
        double angle2 = calculateAngle(val.getX(), val.getY(),
                circleTest.lineList.get((currentIndex + 1) % circleTest.numberOfParts).getEndX(),
                circleTest.lineList.get((currentIndex + 1) % circleTest.numberOfParts).getEndY()
        );
        double angle3 = calculateAngle(circleTest.lineList.get(currentIndex).getEndX(),
                circleTest.lineList.get(currentIndex).getEndY(),
                circleTest.lineList.get((currentIndex + 1) % circleTest.numberOfParts).getEndX(),
                circleTest.lineList.get((currentIndex + 1) % circleTest.numberOfParts).getEndY()
        );
        this.totalAttempt++;
        double res = angle1 + angle2 - angle3;
        if (-0.1 < res && res < 0.1) {
            this.goodAttempt++;
            l.setStroke(Color.DARKSEAGREEN);
            l.setStrokeWidth(2);
            circleTest.area.setFill(Color.DARKSEAGREEN);
        } else {
            l.setStroke(Color.PALEVIOLETRED);
            l.setStrokeWidth(2);
            circleTest.area.setFill(Color.PALEVIOLETRED);
        }
        updateInfoViewer();

        if (totalAttempt == 25) {
            onStartPause();
        }

        if (t) {
            this.currentIndex = circleTest.choosePart();
        }

    }

    public void writeResults() {
        if (totalAttempt > 0) {
            int i = 0;
            try {
                file.createNewFile();
                final FileWriter writer = new FileWriter(file, true);
                try {
                    writer.write("Test number " + circleTest.numberOfParts + " of size " + circleTest.size + "\n");
                    writer.write(this.goodAttempt + "/" + this.totalAttempt + "\n");
                } finally {
                    writer.close();
                }
            } catch (Exception e) {
                System.out.println("Impossible de creer le fichier");
            }
        }
    }

    public double calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.atan2(y1 - 0, x1 - circleTest.size / 5) -
                Math.atan2(y2 - 0, x2 - circleTest.size / 5);
        angle = Math.toDegrees(angle);
        if (angle < 0) {
            angle += 360;
        }
        if (angle > 180) {
            return 360 - angle;
        }
        return angle;
    }
}
