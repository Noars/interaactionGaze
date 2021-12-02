package utils;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class CalibrationPoint {

    public Cross cross;
    public Circle circle = null;
    public List<Point2D> capturedCoordinates;
    @Getter
    public double offsetX;
    @Getter
    public double offsetY;


    public CalibrationPoint() {
        this.cross = new Cross();
        this.capturedCoordinates = new LinkedList<>();
    }

    public void setCross(Cross newcross){
        this.cross = newcross;
    }

    public void setCircle(Circle newCircle){
        this.circle = newCircle;
        this.offsetX = getCircleX() - getCrossX();
        this.offsetY = getCircleY() - getCrossY();

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

}
