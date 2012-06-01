package nl.weev.weevents.client.shared;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class EventInfoRequest {

    private EventInfoDTO eventInfo;

    public EventInfoRequest() {
        super();
    }

    public EventInfoRequest(EventInfoDTO eventInfo) {
        super();
        this.eventInfo = eventInfo;
    }

    public void setEventInfo(EventInfoDTO eventInfo) {
        this.eventInfo = eventInfo;
    }

    public EventInfoDTO getEventInfo() {
        return eventInfo;
    }

}
