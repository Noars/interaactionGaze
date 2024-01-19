package gaze;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.LinkedList;

public class MouseInfo {
    private static final int POINTS_TO_TEST = 10;
    public int DWELL_TIME = 2000;
    public int SIZE_TARGET = 50;
    public Color COLOR_BACKGROUND = Color.WHITE;
    public double redColor = 1.0;
    public double blueColor = 1.0;
    public double greenColor = 1.0;
    public String nameUser = "default";
    LinkedList<Point2D> previousPosition = new LinkedList<>();
    @Getter
    @Setter
    boolean clikcActivated = true;
    Timeline dwellTimeline;
    boolean dwellStarted;
    @Getter
    private IntegerProperty dwellRatio = new SimpleIntegerProperty(0);

    public MouseInfo() {
        initTimer();
        launchScriptMouseCursor();
    }

    synchronized public void addPosition(Point2D point) {
        if (previousPosition.size() >= POINTS_TO_TEST) {
            previousPosition.pop();
        }
        previousPosition.add(point);

        if (clikcActivated && isBeingDwelled() && !dwellStarted()) {
            startTimer();
        } else if ((!clikcActivated || !isBeingDwelled()) && dwellStarted) {
            stopTimer();
        }
    }

    public boolean isBeingDwelled() {
        LinkedList<Point2D> temp = new LinkedList<Point2D>(previousPosition);
        if (temp.size() >= POINTS_TO_TEST) {
            Point2D meanValue = getMeanValue();
            boolean isCloseEnough = true;
            int i = 0;
            while (isCloseEnough && i < temp.size()) {
                if (Point.distance(meanValue.getX(), meanValue.getY(), temp.get(i).getX(), temp.get(i).getY()) > 30) {
                    isCloseEnough = false;
                }
                i++;
            }
            return isCloseEnough;
        }
        return false;
    }

    Point2D getMeanValue() {
        double x = 0, y = 0;
        for (Point2D point : previousPosition) {
            x += point.getX();
            y += point.getY();
        }
        return new Point2D(x / previousPosition.size(), y / previousPosition.size());
    }

    public void initTimer() {
        dwellTimeline = new Timeline();
        dwellTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(dwellRatio, 0), new KeyValue(dwellRatio, 0)),
                new KeyFrame(Duration.millis(DWELL_TIME), new KeyValue(dwellRatio, 0), new KeyValue(dwellRatio, 100)));
        dwellTimeline.setOnFinished((e) -> {
            Robot bot;
            try {
                int x = (int) java.awt.MouseInfo.getPointerInfo().getLocation().getX();
                int y = (int) java.awt.MouseInfo.getPointerInfo().getLocation().getY();
                bot = new Robot();
                bot.mouseMove(x, y);
                bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            } catch (AWTException awte) {
                awte.printStackTrace();
            }
            dwellStarted = false;
            dwellRatio.set(0);
        });
    }

    public void startTimer() {
        dwellStarted = true;
        initTimer();
        dwellTimeline.play();
    }

    public void stopTimer() {
        dwellStarted = false;
        dwellTimeline.stop();
    }

    public boolean dwellStarted() {
        return dwellStarted;
    }

    public void hideShowMouseCursor(){
        try {
            Robot bot = new Robot();
            bot.keyPress(KeyCode.ALT.getCode());
            bot.keyPress(KeyCode.P.getCode());
            bot.keyRelease(KeyCode.ALT.getCode());
            bot.keyRelease(KeyCode.P.getCode());
        } catch (AWTException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void launchScriptMouseCursor(){
        try {
            //Dev
            /*Runtime.getRuntime().exec("cmd.exe " +
                    "/c " +
                    System.getProperty("user.home") + "\\Documents\\GitHub\\interaactionGaze\\src\\main\\resources\\AutoHotkey_2.0.11\\AutoHotkey64.exe " +
                    System.getProperty("user.home") + "\\Documents\\GitHub\\interaactionGaze\\src\\main\\resources\\AutoHotkey_2.0.11\\scripts\\hideShowMouseCursor.ahk");*/
            //Release
            Runtime.getRuntime().exec("cmd.exe " +
                    "/c " +
                    "C:\\PROGRA~2\\InteraactionGaze\\lib\\AutoHotkey\\AutoHotkey64.exe " +
                    "C:\\PROGRA~2\\InteraactionGaze\\lib\\AutoHotkey\\scripts\\hideShowMouseCursor.ahk");
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

    public void closeScriptMouseCursor(){
        try {
            Robot bot = new Robot();
            bot.keyPress(KeyCode.ALT.getCode());
            bot.keyPress(KeyCode.E.getCode());
            bot.keyRelease(KeyCode.ALT.getCode());
            bot.keyRelease(KeyCode.E.getCode());
        } catch (AWTException ex) {
            throw new RuntimeException(ex);
        }
    }
}
