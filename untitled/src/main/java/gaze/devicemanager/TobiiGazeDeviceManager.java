package gaze.devicemanager;

import application.Configuration;
import application.Cross;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TobiiGazeDeviceManager extends AbstractGazeDeviceManager {

    private Cross cross;

    private ExecutorService executorService;

    private PositionPollerRunnable positionPollerRunnable;

    public TobiiGazeDeviceManager( Cross cross) {
        super();
        this.cross = cross;
    }

    public void init(Configuration configuration) {
        try {
            positionPollerRunnable = new PositionPollerRunnable(configuration, cross);
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
