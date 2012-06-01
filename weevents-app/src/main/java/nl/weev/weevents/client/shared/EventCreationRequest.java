package nl.weev.weevents.client.shared;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class EventCreationRequest {

    private EventInfoDTO eventInfo;

    public EventCreationRequest() {
        super();
    }

    public EventCreationRequest(EventInfoDTO eventInfo) {
        super();
        this.eventInfo = eventInfo;
    }

    public EventInfoDTO getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(EventInfoDTO eventInfo) {
        this.eventInfo = eventInfo;
    }

}
