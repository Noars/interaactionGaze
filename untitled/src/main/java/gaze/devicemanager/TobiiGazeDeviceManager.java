package gaze.devicemanager;

import application.Configuration;
import application.Cross;
import application.Main;
import gaze.MouseInfo;
import tobii.Tobii;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TobiiGazeDeviceManager extends AbstractGazeDeviceManager {

    private Cross cross;
private MouseInfo mouseInfo;
    private ExecutorService executorService;

    private PositionPollerRunnable positionPollerRunnable;

    public TobiiGazeDeviceManager(Main main) {
        super();
        this.cross = main.getCursor();
        this.mouseInfo = main.getMouseInfo();
    }
    public void setPause(boolean b){
        positionPollerRunnable.setPauseRequested(b);
    }

    public void init(Configuration configuration) {

        Tobii.gazePosition();

        try {
            positionPollerRunnable = new PositionPollerRunnable(configuration, cross, mouseInfo, this);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        executorService = Executors.newSingleThreadExecutor();
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
