package gaze.devicemanager;

import application.Configuration;
import application.Cross;
import lombok.Getter;

/**
 * Created by schwab on 16/08/2016.
 */
public class GazeDeviceManagerFactory {

    @Getter
    private static final GazeDeviceManagerFactory instance = new GazeDeviceManagerFactory();

    private GazeDeviceManagerFactory() {
    }

    public TobiiGazeDeviceManager createNewGazeListener(Cross cross) {

        final TobiiGazeDeviceManager gazeDeviceManager;
        gazeDeviceManager = new TobiiGazeDeviceManager(cross);

        gazeDeviceManager.init(new Configuration());
        return gazeDeviceManager;
    }

}
