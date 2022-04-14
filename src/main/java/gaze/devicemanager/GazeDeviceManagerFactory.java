package gaze.devicemanager;

import application.Main;
import lombok.Getter;
import utils.CalibrationConfig;

/**
 * Created by schwab on 16/08/2016.
 */
public class GazeDeviceManagerFactory {

    @Getter
    private static final GazeDeviceManagerFactory instance = new GazeDeviceManagerFactory();

    private GazeDeviceManagerFactory() {
    }

    public TobiiGazeDeviceManager createNewGazeListener(Main main, CalibrationConfig calibrationConfig) {
        final TobiiGazeDeviceManager gazeDeviceManager;
        gazeDeviceManager = new TobiiGazeDeviceManager(main, calibrationConfig);

        gazeDeviceManager.init();
        return gazeDeviceManager;
    }

}
