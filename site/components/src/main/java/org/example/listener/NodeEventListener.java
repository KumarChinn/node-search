package org.example.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

/**
 * Created by chinnku on Nov, 2021
 * Node Event Listener to Observe any changes to the nodes on the given path
 */
public class NodeEventListener implements EventListener {

    private static Logger LOGGER = LoggerFactory.getLogger(NodeEventListener.class);

    /**
     * @param eventIterator
     */
    @Override
    public void onEvent(EventIterator eventIterator) {
        try {
            while (eventIterator.hasNext()) {
                Event event = eventIterator.nextEvent();
                LOGGER.info("Node Event Occurred - Event Type: " + valueOf(event.getType()) + ", Event Id: " + event.getIdentifier() + ", Event Date" + event.getDate() + ", Event Path: " + event.getPath());
            }
        } catch (Exception e) {
            LOGGER.error("Error while processing event: ", e.getMessage());
        }
    }

    /**
     * @param eventType
     * @return
     */
    public static String valueOf(int eventType) {
        if (eventType == Event.NODE_ADDED) {
            return "NODE_ADDED";
        } else if (eventType == Event.NODE_MOVED) {
            return "NODE_MOVED";
        } else if (eventType == Event.NODE_REMOVED) {
            return "NODE_REMOVED";
        } else if (eventType == Event.PROPERTY_ADDED) {
            return "PROPERTY_ADDED";
        } else if (eventType == Event.PROPERTY_CHANGED) {
            return "PROPERTY_CHANGED";
        } else if (eventType == Event.PROPERTY_REMOVED) {
            return "PROPERTY_REMOVED";
        } else if (eventType == Event.PERSIST) {
            return "PERSIST";
        } else {
            return "UNKNOWN_EVENT_TYPE";
        }
    }
}
