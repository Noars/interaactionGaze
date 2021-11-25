package application;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class CalibrationPoint {

    Cross cross;
    Circle offset = null;
    List<Point2D> capturedCoordinates;


    public CalibrationPoint() {
       this.cross = new Cross();
       this.capturedCoordinates =  new LinkedList<>();
    }

    public double getCrossX(){
        return cross.getLayoutX();
    }

    public double getCrossY(){
        return cross.getLayoutY();
    }

    public double getOffsetX(){
        return offset.getCenterX();
    }

    public double getOffsetY(){
        return offset.getCenterY();
    }

}
