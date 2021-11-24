package application;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Cursor extends Group {

    double crossOffsetX = 0;
    double crossOffsetY = 0;

    public Cursor() {
        Line l1 = new Line();
        Line l2 = new Line();
        l1.setStartX(-10);
        l1.setEndX(10);
        l2.setStartY(-10);
        l2.setEndY(10);
        Circle center = new Circle();
        center.setRadius(2);
        getChildren().addAll(l1, l2, center);
        setMouseTransparent(true);
    }


    public void setCursorOn() {
        ((Line) getChildren().get(0)).setStroke(Color.CORAL);
        ((Line) getChildren().get(0)).setStrokeWidth(8);
        ((Line) getChildren().get(1)).setStroke(Color.CORAL);
        ((Line) getChildren().get(1)).setStrokeWidth(8);
    }

    public void setCursorOff() {
        ((Line) getChildren().get(0)).setStroke(Color.BLACK);
        ((Line) getChildren().get(0)).setStrokeWidth(1);
        ((Line) getChildren().get(1)).setStroke(Color.BLACK);
        ((Line) getChildren().get(1)).setStrokeWidth(1);
    }

}
