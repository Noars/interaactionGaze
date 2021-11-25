package gaze.devicemanager;

import application.Configuration;
import application.Cursor;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TobiiGazeDeviceManager extends AbstractGazeDeviceManager {

    private  Cursor cursor;

    private ExecutorService executorService;

    private PositionPollerRunnable positionPollerRunnable;

    public TobiiGazeDeviceManager( Cursor cursor) {
        super();
        this.cursor = cursor;
    }

    public void init(Configuration configuration) {
        try {
            positionPollerRunnable = new PositionPollerRunnable(configuration,cursor);
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
