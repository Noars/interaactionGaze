package application;

import javafx.geometry.Point2D;
import lombok.Setter;

import java.awt.*;
import java.util.LinkedList;

public class Configuration {

    public final static String VERSION = "InterAACtionBox-Interface 1.0.0 Dev v.2021.18.11";

    public final static int MOUSE_INTERACTION = 0;
    public final static int GAZE_INTERACTION = 1;
    @Setter
    public int numberOfLastPositionsToCheck = 200;
    public LinkedList<Point2D> lastPositions = new LinkedList<>();
    public LinkedList<Point2D> currentPoint = new LinkedList<>();
    boolean userIsMoving = false;

    public Configuration() {
    }

    public boolean waitForUserMove() {
        return !userIsMoving || lasPositionDidntMoved();
    }

    public void analyse(double x, double y) {
        if (
                (currentPoint != null && currentPoint.size() > 0
                        && !isArround(x, currentPoint.getLast().getX()) && !isArround(y, currentPoint.getLast().getY()))
        ) {
            this.userIsMoving = true;
        }
    }

    public boolean isArround(double coord0, double coord1) {
        return coord0 <= coord1 + 2 && coord0 >= coord1 - 2;
    }

    public void updateLastPositions() {
        while (lastPositions.size() >= numberOfLastPositionsToCheck) {
            lastPositions.pop();
        }
        lastPositions.add(new Point2D(MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY()));
    }

    public boolean lasPositionDidntMoved() {
        if (lastPositions.size() == numberOfLastPositionsToCheck) {
            Point2D pos = lastPositions.get(0);
            for (int i = 0; i < numberOfLastPositionsToCheck; i++) {
                if (!lastPositions.get(i).equals(pos)) {
                    return false;
                }
            }
            lastPositions.clear();
            this.userIsMoving = false;
            return true;
        }
        return false;
    }
}
