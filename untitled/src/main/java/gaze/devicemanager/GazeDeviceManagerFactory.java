package gaze.devicemanager;

import application.Configuration;
import application.Cursor;
import lombok.Getter;

/**
 * Created by schwab on 16/08/2016.
 */
public class GazeDeviceManagerFactory {

    @Getter
    private static final GazeDeviceManagerFactory instance = new GazeDeviceManagerFactory();

    private GazeDeviceManagerFactory() {
    }

    public GazeDeviceManager createNewGazeListener( Cursor cursor) {

        final TobiiGazeDeviceManager gazeDeviceManager;
        gazeDeviceManager = new TobiiGazeDeviceManager(cursor);

        //gazeDeviceManager.init(new Configuration());
        return gazeDeviceManager;
    }

}
