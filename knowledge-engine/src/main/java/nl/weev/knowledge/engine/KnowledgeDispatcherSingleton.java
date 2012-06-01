package nl.weev.knowledge.engine;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import nl.weev.knowledge.api.FactOut;

public class KnowledgeDispatcherSingleton {

    private static KnowledgeDispatcherSingleton INSTANCE;

    private ConnectionFactory connectionFactory;

    private Queue factOutQueue;

    private KnowledgeDispatcherSingleton() {
        super();
    }

    protected void setConnectionFactory(final ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    protected void setFactOutQueue(final Queue factOutQueue) {
        this.factOutQueue = factOutQueue;
    }

    protected void send(final FactOut factOut) throws JMSException {

        Connection connection = null;

        try {

            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            ObjectMessage message = session.createObjectMessage(factOut);
            MessageProducer producer = session.createProducer(factOutQueue);
            producer.send(message);

        } finally {

            connection.close();
        }
    }

    public synchronized void sendFactToQueue(final FactOut factOut) {

        try {
            send(factOut);
            System.out.println("Message sent from Knowledge at factOut queue.");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static KnowledgeDispatcherSingleton getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new KnowledgeDispatcherSingleton();
        }

        return INSTANCE;
    }

    public static boolean isInstaceNull() {

        return (INSTANCE == null) ? true : false;
    }

}
