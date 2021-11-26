package application;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class CalibrationPoint {

    Cross cross;
    Circle circle = null;
    List<Point2D> capturedCoordinates;


    public CalibrationPoint() {
        this.cross = new Cross();
        this.capturedCoordinates = new LinkedList<>();
    }

    public double getCrossX() {
        return cross.getLayoutX();
    }

    public double getCrossY() {
        return cross.getLayoutY();
    }

    public double getCircleX() {
        return circle.getCenterX();
    }

    public double getCircleY() {
        return circle.getCenterY();
    }

    double getOffsetX() {
        return getCircleX() - getCrossX();
    }

    double getOffsetY() {
        return getCircleY() - getCrossY();
    }
}
