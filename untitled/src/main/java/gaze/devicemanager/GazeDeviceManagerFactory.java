package gaze.devicemanager;

import application.Configuration;
import application.Cross;
import application.Main;
import lombok.Getter;

/**
 * Created by schwab on 16/08/2016.
 */
public class GazeDeviceManagerFactory {

    @Getter
    private static final GazeDeviceManagerFactory instance = new GazeDeviceManagerFactory();

    private GazeDeviceManagerFactory() {
    }

    public TobiiGazeDeviceManager createNewGazeListener(Main main) {

        final TobiiGazeDeviceManager gazeDeviceManager;
        gazeDeviceManager = new TobiiGazeDeviceManager(main);

        gazeDeviceManager.init(new Configuration());
        return gazeDeviceManager;
    }

}
