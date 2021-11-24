package gaze.devicemanager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import gaze.EyeTracker;

/**
 * Created by schwab on 16/08/2016.
 */
public class GazeDeviceManagerFactory {

    @Getter
    private static final GazeDeviceManagerFactory instance = new GazeDeviceManagerFactory();

    private GazeDeviceManagerFactory() {
    }

    public GazeDeviceManager createNewGazeListener() {

        final GazeDeviceManager gazeDeviceManager;
        gazeDeviceManager = new TobiiGazeDeviceManager();

        gazeDeviceManager.init();
        return gazeDeviceManager;
    }

}
