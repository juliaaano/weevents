package nl.weev.weevents.server;

import static nl.weev.weevents.server.util.Converter.buildEventInfoDTO;
import static nl.weev.weevents.server.util.Converter.buildFactIn;

import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import nl.weev.weevents.client.shared.EventCreationRequest;
import nl.weev.weevents.client.shared.EventInfoDTO;
import nl.weev.weevents.client.shared.EventInfoRequest;
import nl.weev.weevents.client.shared.EventNotFoundException;
import nl.weev.weevents.client.shared.EventService;
import nl.weev.weevents.client.shared.EventServiceException;
import nl.weev.weevents.server.dao.EventDao;
import nl.weev.weevents.server.entity.EventInfo;
import nl.weev.weevents.server.knowledge.KnowledgeClient;
import nl.weev.weevents.server.knowledge.KnowledgeListener;

import org.jboss.errai.bus.server.annotations.Service;

/**
 * CDI service that can be called from either the client side (via Errai RPC) or the server side.
 * 
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@ApplicationScoped
@Service
public class EventServiceImpl implements EventService {

    @Inject
    private Logger log;

    @Inject
    private EventDao eventDao;

    @Inject
    private EventBusImpl eventBus;

    @Inject
    private KnowledgeClient knowledgeClient;

    @Inject
    private KnowledgeListener knowledgeMessageListener;

    @PostConstruct
    @SuppressWarnings("unused")
    private void setupKnowledgeListener() {

        // knowledgeMessageListener.activate();
        KnowledgeListener.activate(knowledgeMessageListener);
    }

    protected static String generateUniqueEventKey() {

        return UUID.randomUUID().toString();
    }

    @Override
    public EventInfoDTO retrieveEventInfo(String keyId) throws EventNotFoundException {

        EventInfo eventInfo = eventDao.findByKey(keyId);

        if (eventInfo == null) {

            log.info("Event not found for keyId: " + keyId);
            throw new EventNotFoundException(keyId);
        }

        return buildEventInfoDTO(eventInfo);
    }

    @Override
    public EventInfoDTO createNewEvent(EventCreationRequest eventCreationRequest) {

        String eventKey = generateUniqueEventKey();

        EventInfo eventInfo = new EventInfo(eventKey);

        eventDao.create(eventInfo);

        return buildEventInfoDTO(eventInfo);
    }

    @Override
    public void updateEvent(EventInfoRequest eventInfoRequest) throws EventServiceException {

        EventInfo eventInfo = new EventInfo(eventInfoRequest.getEventInfo());

        eventBus.sendToBus(eventInfo); // Broadcasts back to clients.

        eventDao.update(eventInfo);

        try {
            // Send it over to Knowledge queue.
            knowledgeClient.sendFactToQueue(buildFactIn(eventInfo));
        } catch (Exception e) {
            log.warning("There's been a problem while sending fact to knowledge queue. Application will continue without use of Knowledge Engine.");
        }
    }

}
