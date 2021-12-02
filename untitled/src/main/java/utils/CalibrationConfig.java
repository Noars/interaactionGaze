package utils;

import application.ui.CalibrationPane;
import com.google.gson.Gson;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import lombok.Getter;
import lombok.Setter;
import utils.save.Coordinates;

import java.awt.*;
import java.io.*;

public class CalibrationConfig {

    CalibrationPoint[] calibrationPoints = new CalibrationPoint[9];
    @Getter
    @Setter
    double mainOffsetX = 0;

    @Getter
    @Setter
    double mainOffsetY = 0;

    @Getter
    boolean angleSetUpDone = false;

    double[] angle = new double[9];

    public CalibrationConfig(){
        loadSave();
    }

    public CalibrationPoint get(int i){
        return calibrationPoints[i];
    }

    public void handle(double eventX, double eventY) {

        double mouseAngle = angleBetween(get(CalibrationPane.CENTER).cross, get(CalibrationPane.TOP_LEFT).cross, eventX, eventY);

        int previousPoint;
        for (previousPoint = 0; previousPoint < 7; previousPoint++) {
            if (mouseAngle < angle[previousPoint + 1]) {
                break;
            }
        }
        int nextPoint = (previousPoint + 1) % 8;

        Point2D intersect = getPosInter(
                eventX, eventY,
                get(CalibrationPane.CENTER).getCrossX(), get(CalibrationPane.CENTER).getCrossY(),
                get(previousPoint).getCrossX(), get(previousPoint).getCrossY(),
                get(nextPoint).getCrossX(), get(nextPoint).getCrossY()
        );

        double interToNext = Point.distance(intersect.getX(), intersect.getY(), get(previousPoint).getCrossX(), get(previousPoint).getCrossY());
        double previousToNext = Point.distance(get(previousPoint).getCrossX(), get(previousPoint).getCrossY(), get(nextPoint).getCrossX(), get(nextPoint).getCrossY());

        double newX = (1 - (interToNext / previousToNext)) * get(previousPoint).getOffsetX() + ((interToNext / previousToNext)) * get(nextPoint).getOffsetX();
        double newY = (1 - (interToNext / previousToNext)) * get(previousPoint).getOffsetY() + ((interToNext / previousToNext)) * get(nextPoint).getOffsetY();

        double centerToMouse = Point.distance(eventX, eventY, get(CalibrationPane.CENTER).getCrossX(), get(CalibrationPane.CENTER).getCrossY());
        double centerToInter = Point.distance(intersect.getX(), intersect.getY(), get(CalibrationPane.CENTER).getCrossX(), get(CalibrationPane.CENTER).getCrossY());

        newX = (1 - (centerToMouse / centerToInter)) * get(CalibrationPane.CENTER).getOffsetX() + ((centerToMouse / centerToInter)) * newX;
        newY = (1 - (centerToMouse / centerToInter)) * get(CalibrationPane.CENTER).getOffsetY() + ((centerToMouse / centerToInter)) * newY;

        setMainOffsetX(-newX);
        setMainOffsetY(-newY);
    }



    Point2D getPosInter(double xcursor, double ycursor, double xcenter, double ycenter, double xcurent, double ycurent, double xprevious, double yprevious) {
        Point2D vector = new Point2D(xcursor - xcenter, ycursor - ycenter);
        double coef = 1;
        if (xcurent == xprevious) {
            coef = (xcurent - xcenter) / vector.getX();
        } else if (ycurent == yprevious) {
            coef = (ycurent - ycenter) / vector.getY();
        }
        return new Point2D(vector.getX() * coef + xcenter, vector.getY() * coef + ycenter);
    }

    private double angleBetween(Group center, Group current, Group previous) {

        return (Math.toDegrees(Math.atan2(current.getLayoutX() - center.getLayoutX(), current.getLayoutY() - center.getLayoutY()) -
                Math.atan2(previous.getLayoutX() - center.getLayoutX(), previous.getLayoutY() - center.getLayoutY())) + 360) % 360;
    }

    private double angleBetween(Group center, Group current, double mouseX, double mouseY) {

        return (Math.toDegrees(Math.atan2(current.getLayoutX() - center.getLayoutX(), current.getLayoutY() - center.getLayoutY()) -
                Math.atan2(mouseX - center.getLayoutX(), mouseY - center.getLayoutY())) + 360) % 360;
    }

    public void setAllAngles(){
        for (int angleIndex = 0; angleIndex <= 7; angleIndex++) {
            angle[angleIndex] = angleBetween(get(CalibrationPane.CENTER).cross, get(CalibrationPane.TOP_LEFT).cross, get(angleIndex).cross);
        }
        angleSetUpDone = true;
    }

    public boolean save() throws IOException {
        Coordinates[] coordinates = new Coordinates[9];
        for(int i = 0; i <9 ; i++){
            coordinates[i] = new Coordinates(this.calibrationPoints[i].offsetX,this.calibrationPoints[i].offsetY,
                    this.calibrationPoints[i].cross.getLayoutX(), this.calibrationPoints[i].cross.getLayoutY());
        }
        createDirectories();
        createFile();
        writeSaveToFile(coordinates);
        return true;
    }

    public boolean createDirectories(){
        File file = new File(System.getProperty("user.home")+"/interaactionPoint/profiles/calibrations/");
        return file.mkdirs();
    }

    public boolean createFile() throws IOException {
        File file = new File(System.getProperty("user.home")+"/interaactionPoint/profiles/calibrations/userCalibration.json");
        return file.createNewFile();
    }

    public void writeSaveToFile(Coordinates[] coordinates) throws IOException {
        FileWriter myWriter = new FileWriter(System.getProperty("user.home")+"/interaactionPoint/profiles/calibrations/userCalibration.json");
        myWriter.write(new Gson().toJson(coordinates));
        myWriter.close();
    }

    public boolean loadSave(){
        try {
            FileReader myReader =new FileReader(System.getProperty("user.home") + "/interaactionPoint/profiles/calibrations/userCalibration.json");
            Coordinates[] coordinates = new Gson().fromJson(myReader, Coordinates[].class);
            if(coordinates!= null && coordinates.length ==9) {
                for (int i = 0; i < 9; i++) {
                    calibrationPoints[i] = new CalibrationPoint(coordinates[i].offsetX, coordinates[i].offsetY, coordinates[i].calibrationX, coordinates[i].calibrationY);
                }
                setAllAngles();
                return true;
            } else {
                throw new FileNotFoundException();
            }
        } catch(FileNotFoundException e){
            for (int i = 0; i < 9; i++) {
                calibrationPoints[i] = new CalibrationPoint();
            }
            return false;
        }
    }
}
