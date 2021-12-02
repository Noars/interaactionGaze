package utils.save;

public class Coordinates {
    public double offsetX;
    public double offsetY;
    public double calibrationX;
    public double calibrationY;

    public Coordinates(double offsetX, double offsetY, double calibrationX, double calibrationY){
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.calibrationX = calibrationX;
        this.calibrationY = calibrationY;
    }
}
