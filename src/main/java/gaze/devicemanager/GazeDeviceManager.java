package gaze.devicemanager;

import gaze.GazeMotionListener;
import javafx.scene.Node;

public interface GazeDeviceManager {

    void addGazeMotionListener(GazeMotionListener listener);

    void removeGazeMotionListener(GazeMotionListener listener);

    void addEventFilter(Node gs);

    void addEventHandler(Node gs);

    void removeEventFilter(Node gs);

    void removeEventHandler(Node gs);

    void clear();
}
