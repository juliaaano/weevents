package nl.weev.weevents.server;

import static nl.weev.weevents.server.util.Converter.buildEventInfoDTO;

import java.net.SocketException;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import nl.weev.weevents.client.shared.EventInfoDTO;
import nl.weev.weevents.server.entity.EventInfo;

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.framework.RequestDispatcher;
import org.jboss.errai.common.client.protocols.MessageParts;

@ApplicationScoped
public class EventBusImpl {

    @Inject
    private Logger log;

    @Inject
    private RequestDispatcher dispatcher;

    public void sendToBus(EventInfo eventInfo) {

        sendToBus(buildEventInfoDTO(eventInfo));
    }

    public void sendToBus(EventInfoDTO eventInfoDTO) {

        MessageBuilder.createMessage().toSubject(eventInfoDTO.getKey()).with("eventInfo", eventInfoDTO)
                .errorsHandledBy(new ErrorCallback() {
                    @Override
                    public boolean error(Message message, Throwable throwable) {
                        Throwable caught = message.get(Throwable.class, MessageParts.Throwable);
                        if (caught instanceof SocketException) {
                            log.info("Client disconected. | " + caught.getMessage());
                            return false;
                        } else {
                            log.severe("Error at Bus with subscription. | " + caught.getMessage());
                        }
                        return true;
                    }
                }).sendGlobalWith(dispatcher);
    }
}
