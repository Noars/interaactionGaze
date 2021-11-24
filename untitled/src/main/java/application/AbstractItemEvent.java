package application;

import javafx.event.Event;
import javafx.event.EventType;

public abstract class AbstractItemEvent extends Event {

	public static final EventType<AbstractItemEvent> ITEM_EVENT_TYPE = new EventType<AbstractItemEvent>(ANY);

    public AbstractItemEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public abstract void invokeHandler(ItemEventHandler handler);

}