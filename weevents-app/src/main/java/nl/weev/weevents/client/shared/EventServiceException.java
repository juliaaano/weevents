package nl.weev.weevents.client.shared;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class EventServiceException extends RuntimeException {

    private static final long serialVersionUID = 6003476827047936289L;

    public EventServiceException() {
        super();
    }

    public EventServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
