package nl.weev.weevents.client.shared;

import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Errai RPC interface that specifies which methods the client can invoke on the server-side service.
 * 
 * @author Juliano Mohr <juliano.mohr@weev.nl>
 * @see nl.weev.weevents.server.EventServiceImpl
 */
@Remote
public interface EventService {

    public EventInfoDTO retrieveEventInfo(String keyId) throws EventNotFoundException;

    public EventInfoDTO createNewEvent(EventCreationRequest eventCreationRequest);

    public void updateEvent(EventInfoRequest eventInfoRequest) throws EventServiceException;
}
