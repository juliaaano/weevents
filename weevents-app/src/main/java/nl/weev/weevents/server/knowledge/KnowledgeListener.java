package nl.weev.weevents.server.knowledge;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import nl.weev.knowledge.api.FactOut;
import nl.weev.weevents.server.EventBusImpl;
import nl.weev.weevents.server.dao.EventDao;
import nl.weev.weevents.server.entity.EventInfo;

@ApplicationScoped
public class KnowledgeListener implements MessageListener {

    @Inject
    private Logger log;

    @Inject
    private EventDao eventDao;

    @Inject
    private EventBusImpl eventBus;

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/queue/factOut")
    private Queue factOutQueue;

    /*
     * https://community.jboss.org/thread/169423
     * https://community.jboss.org/wiki/ShouldICacheJMSConnectionsAndJMSSessions
     * 
     * TODO SHOULD USE JMS WITH SPRING!!! http://onjava.com/lpt/a/6490
     */

    @Deprecated
    public void activate() {

        try {

            Connection connection = connectionFactory.createConnection();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer messageConsumer = session.createConsumer(factOutQueue);

            messageConsumer.setMessageListener(this); // U can't setup a Listener on a RA connection Factory. Use a
                                                      // regular connection factory (not the RA one)

            log.info("Success to activate KnowledgeListener.");

        } catch (JMSException e) {
            log.severe("Failed to activate KnowledgeListener. | " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void activate(final KnowledgeListener knowledgeMessageListener) {

        try {

            InitialContext initialContext = new InitialContext();

            Queue queue = (Queue) initialContext.lookup("/queue/factOut");

            ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("/ConnectionFactory");

            Connection connection = connectionFactory.createConnection();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer messageConsumer = session.createConsumer(queue);

            messageConsumer.setMessageListener(knowledgeMessageListener);

            connection.start();

        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {

        log.info("Message arrived at Weevents.");
        log.info("Processing message (onMessage) from Knowledge - queue/factOut.");

        FactOut factOut = null;
        try {

            factOut = (FactOut) ((ObjectMessage) message).getObject();

        } catch (JMSException e) {
            log.warning("Failed to consume message from Knowledge." + e.getMessage());
            e.printStackTrace();
        }

        processFactOutMessage(factOut);
    }

    private boolean processFactOutMessage(FactOut factOut) {

        EventInfo eventInfo = new EventInfo((nl.weev.knowledge.api.EventInfo) factOut.getRoot());

        if (eventInfo.getKey() == null || eventInfo.getKey().isEmpty()) {
            log.warning("Failed to process message from Knowledge because EventInfo key is empty. Message disposed.");
            return false;
        }

        eventBus.sendToBus(eventInfo); // Broadcasts to front-end clients.
        eventDao.update(eventInfo);

        return true;
    }
}
