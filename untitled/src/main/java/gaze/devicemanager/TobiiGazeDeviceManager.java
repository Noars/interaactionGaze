package gaze.devicemanager;

import application.Main;
import gaze.MouseInfo;
import tobii.Tobii;
import utils.CalibrationConfig;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TobiiGazeDeviceManager extends AbstractGazeDeviceManager {

    private MouseInfo mouseInfo;
    private ExecutorService executorService;

    private PositionPollerRunnable positionPollerRunnable;
    private CalibrationConfig calibrationConfig;

    public TobiiGazeDeviceManager(Main main, CalibrationConfig calibrationConfig) {
        super();
        this.mouseInfo = main.getMouseInfo();
        this.calibrationConfig = calibrationConfig;
    }

    public void setPause(boolean b) {
        positionPollerRunnable.setPauseRequested(b);
    }

    public void init() {

        Tobii.gazePosition();

        try {
            positionPollerRunnable = new PositionPollerRunnable( mouseInfo, calibrationConfig, this);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        executorService = Executors.newFixedThreadPool(4,
                (Runnable r) -> {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                }
        );
        executorService.submit(positionPollerRunnable);
    }


    @Override
    public void destroy() {
        positionPollerRunnable.setStopRequested(true);
        ExecutorService executorService = this.executorService;
        if (executorService != null) {
            executorService.shutdown();
        }
    }

}
