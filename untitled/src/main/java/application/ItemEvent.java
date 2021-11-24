package application;

import javafx.event.EventType;

public class ItemEvent extends AbstractItemEvent {

    public static final EventType<AbstractItemEvent> ITEM_EVENT_TYPE_1 = new EventType(ITEM_EVENT_TYPE, "CustomEvent1");

    public ItemEvent() {
        super(ITEM_EVENT_TYPE_1);
    }

    @Override
    public void invokeHandler(ItemEventHandler handler) {
        handler.onEvent();
    }

}