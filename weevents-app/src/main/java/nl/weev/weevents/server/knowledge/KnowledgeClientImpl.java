package nl.weev.weevents.server.knowledge;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import nl.weev.knowledge.api.FactIn;

@Default
public class KnowledgeClientImpl implements KnowledgeClient {

    @Inject
    private Logger log;

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/queue/factIn")
    private Queue factInQueue;

    public KnowledgeClientImpl() {
        super();
    }

    @Override
    public void sendFactToQueue(final FactIn factIn) throws Exception {

        try {
            send(factIn);
            log.info("Message sent to Knowledge at factIn queue.");
        } catch (JMSException e) {
            log.severe("Failed to send message to knowledge. | " + e.getMessage());
            throw e;
        }
    }

    private void send(final FactIn factIn) throws JMSException {

        Connection connection = null;

        try {

            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            ObjectMessage message = session.createObjectMessage(factIn);
            MessageProducer producer = session.createProducer(factInQueue);
            producer.send(message);

        } finally {

            connection.close();
        }
    }
}
