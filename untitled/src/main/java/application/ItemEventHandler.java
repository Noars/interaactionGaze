package application;

import javafx.event.EventHandler;

public abstract class ItemEventHandler implements EventHandler<AbstractItemEvent> {

    public abstract void onEvent();

    @Override
    public void handle(AbstractItemEvent event) {
        event.invokeHandler(this);
    }
}