package gaze.devicemanager;

import gaze.GazeMotionListener;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by schwab on 04/10/2017.
 */
@Slf4j
public abstract class AbstractGazeDeviceManager implements GazeDeviceManager {

    private final List<GazeMotionListener> gazeMotionListeners = new CopyOnWriteArrayList<>();
    @Getter
    private final Map<IdentityKey<Node>, GazeInfos> shapesEventFilter = Collections.synchronizedMap(new HashMap<>());
    @Getter
    private final Map<IdentityKey<Node>, GazeInfos> shapesEventHandler = Collections.synchronizedMap(new HashMap<>());
    private final List<Node> toRemove = new LinkedList<>();
    private final List<Node> toAdd = new LinkedList<>();

    public AbstractGazeDeviceManager() {

    }

    @Override
    public abstract void destroy();

    @Override
    public void addGazeMotionListener(GazeMotionListener listener) {
        this.gazeMotionListeners.add(listener);
    }

    @Override
    public void removeGazeMotionListener(GazeMotionListener listener) {
        this.gazeMotionListeners.remove(listener);
    }

    @Override
    public void addEventFilter(Node gs) {
        toAdd.add(gs);
    }

    public void add() {
        synchronized (shapesEventFilter) {
            List<Node> temp = new LinkedList<>(toAdd);
            for (Node node : temp) {
                shapesEventFilter.put(new IdentityKey<>(node), new GazeInfos(node));
                toAdd.remove(node);
            }
        }
    }

    @Override
    public void addEventHandler(Node gs) {
        synchronized (shapesEventFilter) {
            shapesEventHandler.put(new IdentityKey<>(gs), new GazeInfos(gs));
        }
    }

    @Override
    public void removeEventFilter(Node gs) {
        toRemove.add(gs);
        if (gs instanceof Pane) {
            for (Node child : ((Pane) gs).getChildren()) {
                removeEventFilter(child);
            }
        }
    }

    public void delete() {
        synchronized (shapesEventFilter) {
            List<Node> temp = new LinkedList<>(toRemove);
            for (Node node : temp) {
                GazeInfos removed = shapesEventFilter.remove(new IdentityKey<>(node));
                if (removed == null) {
                    log.warn("EventFilter to remove not found");
                } else {
                    if (removed.isOnGaze() || removed.isOnMouse()) {
                        Platform.runLater(
                                () ->
                                        removed.getNode().fireEvent(new GazeEvent(GazeEvent.GAZE_EXITED, System.currentTimeMillis(), 0, 0))
                        );
                    }
                }
                toRemove.remove(node);
            }
        }
    }

    @Override
    public void removeEventHandler(Node gs) {
        synchronized (shapesEventFilter) {
            GazeInfos removed = shapesEventHandler.remove(new IdentityKey<>(gs));
            if (removed == null) {
                log.warn("EventHandler to remove not found");
            }
        }
    }

    /**
     * Clear all Nodes in both EventFilter and EventHandler. There is no more event after this function is called
     */
    @Override
    public void clear() {
        synchronized (shapesEventFilter) {
            shapesEventFilter.clear();
            shapesEventHandler.clear();
            gazeMotionListeners.clear();
        }
    }

    public synchronized void onGazeUpdate(Point2D gazePositionOnScreen, String event) {
        // notifyAllGazeMotionListeners(gazePositionOnScreen);
        final double positionX = gazePositionOnScreen.getX();
        final double positionY = gazePositionOnScreen.getY();
        updatePosition(positionX, positionY, event);
    }

    void updatePosition(double positionX, double positionY, String event) {
        add();
        delete();

        synchronized (shapesEventFilter) {
            Collection<GazeInfos> c = shapesEventFilter.values();
            for (GazeInfos gi : c) {
                eventFire(positionX, positionY, gi, event, c);
            }
        }
    }

    public boolean contains(Node node, double positionX, double positionY) {
        Point2D localPosition = node.screenToLocal(positionX, positionY);
        if (localPosition != null) {
            return node.contains(localPosition.getX(), localPosition.getY());
        }
        return false;
    }


    public boolean eventFire(double positionX, double positionY, GazeInfos gi, String event, Collection<GazeInfos> c) {
        Node node = gi.getNode();
        if (!node.isDisable()) {

            Point2D localPosition = node.screenToLocal(positionX, positionY);

            if (localPosition != null && contains(node, positionX, positionY)) {
                if (event.equals("gaze")) {
                    if (gi.isOnGaze()) {
                        Platform.runLater(
                                () ->
                                        node.fireEvent(new GazeEvent(GazeEvent.GAZE_MOVED, gi.getTime(), localPosition.getX(), localPosition.getY()))
                        );
                        return true;
                    } else {

                        gi.setOnGaze(true);
                        gi.setTime(System.currentTimeMillis());
                        Platform.runLater(
                                () ->
                                        node.fireEvent(new GazeEvent(GazeEvent.GAZE_ENTERED, gi.getTime(), localPosition.getX(), localPosition.getY()))
                        );
                        return true;
                    }
                } else {
                    if (gi.isOnMouse()) {
                        Platform.runLater(
                                () ->
                                        node.fireEvent(new GazeEvent(GazeEvent.GAZE_MOVED, gi.getTime(), localPosition.getX(), localPosition.getY()))

                        );
                        return true;
                    } else {

                        gi.setOnMouse(true);
                        gi.setTime(System.currentTimeMillis());
                        Platform.runLater(
                                () ->
                                        node.fireEvent(new GazeEvent(GazeEvent.GAZE_ENTERED, gi.getTime(), localPosition.getX(), localPosition.getY()))

                        );
                        return true;
                    }
                }
            } else {// gaze is not on the shape
                if (event.equals("gaze")) {
                    if (gi.isOnGaze()) {// gaze was on the shape previously
                        gi.setOnGaze(false);
                        gi.setTime(-1);
                        if (localPosition != null) {
                            Platform.runLater(
                                    () ->
                                            node.fireEvent(new GazeEvent(GazeEvent.GAZE_EXITED, gi.getTime(), localPosition.getX(), localPosition.getY()))
                            );
                        }
                    }
                } else {
                    if (gi.isOnMouse()) {// gaze was on the shape previously
                        gi.setOnMouse(false);
                        gi.setTime(-1);
                        if (localPosition != null) {
                            Platform.runLater(
                                    () ->
                                            node.fireEvent(new GazeEvent(GazeEvent.GAZE_EXITED, gi.getTime(), localPosition.getX(), localPosition.getY()))

                            );
                        }
                    }
                }

            }
        }
        return false;
    }

}
