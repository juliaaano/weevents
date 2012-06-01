package nl.weev.weevents.client.shared;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class EventNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 2333334450895359658L;

    private String eventKey;

    public EventNotFoundException() {
        super();
    }

    public EventNotFoundException(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getEventKey() {
        return eventKey;
    }

}
