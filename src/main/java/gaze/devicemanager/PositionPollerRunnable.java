package gaze.devicemanager;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tobii.Tobii;
import utils.CalibrationConfig;

import java.awt.*;
import java.util.LinkedList;

@Slf4j
public class PositionPollerRunnable implements Runnable {

    private final TobiiGazeDeviceManager tobiiGazeDeviceManager;
    @Setter
    public int numberOfLastPositionsToCheck = 200;
    public LinkedList<Point2D> lastPositions = new LinkedList<>();
    public LinkedList<Point2D> currentPoint = new LinkedList<>();
    Robot robot = new Robot();
    boolean userIsMoving = false;
    private gaze.MouseInfo mouseInfo;
    @Setter
    private transient boolean stopRequested = false;
    @Setter
    private transient boolean pauseRequested = false;
    private CalibrationConfig calibrationConfig;

    public PositionPollerRunnable(gaze.MouseInfo mouseInfo, CalibrationConfig calibrationConfig, final TobiiGazeDeviceManager tobiiGazeDeviceManager) throws AWTException {
        this.mouseInfo = mouseInfo;
        this.tobiiGazeDeviceManager = tobiiGazeDeviceManager;
        this.calibrationConfig = calibrationConfig;
    }

    @Override
    public void run() {
        log.info("Run Position Poller Runnable");
        while (!stopRequested) {
            try {
                if (!pauseRequested) {
                    poll(calibrationConfig);
                }
            } catch (final RuntimeException e) {
                log.warn("Exception while polling position of main.gaze", e);
            }
            // sleep is mandatory to avoid too much calls to gazePosition()
            try {
                Thread.sleep(10);
            } catch (InterruptedException | RuntimeException e) {
                log.warn("Exception while sleeping until next poll", e);
            }
        }
    }

    @SuppressFBWarnings
    private void poll(CalibrationConfig calibrationConfig) {
        final float[] pointAsFloatArray = Tobii.gazePosition();

        final float xRatio = pointAsFloatArray[0];
        final float yRatio = pointAsFloatArray[1];

        final Rectangle2D screenDimension = Screen.getPrimary().getBounds();
        final double positionX = xRatio * screenDimension.getWidth();
        final double positionY = yRatio * screenDimension.getHeight();

        double offsetX = 0;
        double offsetY = 0;

        if (calibrationConfig.isAngleSetUpDone()) {
            calibrationConfig.handle(positionX, positionY);
            offsetX = calibrationConfig.getMainOffsetX();
            offsetY = calibrationConfig.getMainOffsetY();
        }

        if ( xRatio != 0.5 || yRatio != 0.5 ) {
            Point location = MouseInfo.getPointerInfo().getLocation();
            analyse(location.getX(), location.getY());
            if (waitForUserMove()) {
                final Point2D point = new Point2D(positionX + offsetX, positionY + offsetY);
                robot.mouseMove((int) point.getX(), (int) point.getY());
                mouseInfo.addPosition(point);
                currentPoint.add(point);
                Platform.runLater(() -> tobiiGazeDeviceManager.onGazeUpdate(point, "gaze"));
            } else {
                updateLastPositions();
            }
        }
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
