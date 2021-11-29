package gaze.devicemanager;

import application.Configuration;
import application.Cross;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import tobii.Tobii;

import java.awt.*;

@Slf4j
public class PositionPollerRunnable implements Runnable {

    private final TobiiGazeDeviceManager tobiiGazeDeviceManager;
    private final Configuration configuration;
    Robot robot = new Robot();
    private Cross cross;
    private gaze.MouseInfo mouseInfo;
    @Setter
    private transient boolean stopRequested = false;
    @Setter
    private transient boolean pauseRequested = false;

    public PositionPollerRunnable(Configuration configuration, Cross cross, gaze.MouseInfo mouseInfo, final TobiiGazeDeviceManager tobiiGazeDeviceManager) throws AWTException {
        this.configuration = configuration;
        this.cross = cross;
        this.mouseInfo = mouseInfo;
        this.tobiiGazeDeviceManager = tobiiGazeDeviceManager;
    }

    @Override
    public void run() {
        while (!stopRequested) {
            try {
                if(!pauseRequested) {
                    configuration.analyse(MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY());
                    poll();
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

    private void poll() {
        final float[] pointAsFloatArray = Tobii.gazePosition();

        final float xRatio = pointAsFloatArray[0];
        final float yRatio = pointAsFloatArray[1];

        final Rectangle2D screenDimension = Screen.getPrimary().getBounds();
        final double positionX = xRatio * screenDimension.getWidth();
        final double positionY = yRatio * screenDimension.getHeight();


        final double offsetX = cross.getTranslateX();
        final double offsetY = cross.getTranslateY();
        if (configuration.waitForUserMove()) {

            final Point2D point = new Point2D(positionX + offsetX, positionY + offsetY);
            robot.mouseMove((int) point.getX(), (int) point.getY());
            Point2D newPoint = new Point2D(MouseInfo.getPointerInfo().getLocation().getX(),
                    MouseInfo.getPointerInfo().getLocation().getY());
            mouseInfo.addPosition(newPoint);
            configuration.currentPoint.add(newPoint);
            Platform.runLater(() -> tobiiGazeDeviceManager.onGazeUpdate(point, "gaze"));
        } else {
            configuration.updateLastPositions();
        }
    }

}
