package gaze;

import javafx.geometry.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class MouseInfo {
    LinkedList<Point2D> previousPosition = new LinkedList<>();
    private static final int POINTS_TO_TEST = 50;

    synchronized public void addPosition(Point2D point){
         if(previousPosition.size() >= POINTS_TO_TEST){
             previousPosition.pop();
         };
         previousPosition.add(point);
     }

    public boolean isBeingDwelled(){
        if(previousPosition.size()>=POINTS_TO_TEST) {
            Point2D meanValue = getMeanValue();
            boolean isCloseEnough = true;
            int i = 0;
            while (isCloseEnough && i < previousPosition.size()) {
                if (Point.distance(meanValue.getX(), meanValue.getY(), previousPosition.get(i).getX(), previousPosition.get(i).getY()) > 50) {
                    isCloseEnough = false;
                }
                i++;
            }
            return isCloseEnough;
        }
        return false;
     }

     synchronized Point2D getMeanValue(){
        double x = 0,y = 0;
         for (Point2D point:previousPosition) {
             x+=point.getX();
             y+=point.getY();
         }
         return new Point2D(x/previousPosition.size(), y/previousPosition.size());
     }
}
