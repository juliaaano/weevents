package nl.weev.knowledge.engine;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import nl.weev.knowledge.api.FactIn;

@MessageDriven(name = "KnowledgeListenerMDB", messageListenerInterface = MessageListener.class, activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/factIn") })
@TransactionManagement(value = TransactionManagementType.CONTAINER)
@TransactionAttribute(value = TransactionAttributeType.REQUIRED)
public class KnowledgeListenerMDB implements MessageListener {

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/queue/factOut")
    private Queue factOutQueue;

    private DroolsSession droolsSession;

    @PostConstruct
    @SuppressWarnings("unused")
    private void setup() {

        // TODO Ideally this should be placed in jndi / cdi to be injected.
        droolsSession = new DroolsSession();
        setupFactOutSingleton();
    }

    private void setupFactOutSingleton() {

        // TODO Workaround that creates a singleton responsible to send feedback from drools rules.

        if (KnowledgeDispatcherSingleton.isInstaceNull()) {

            KnowledgeDispatcherSingleton.getInstance().setConnectionFactory(connectionFactory);
            KnowledgeDispatcherSingleton.getInstance().setFactOutQueue(factOutQueue);
        }
    }

    public void onMessage(Message message) {

        System.out.println("Message arrived at Knowledge.");

        FactIn factIn = null;

        try {

            factIn = (FactIn) ((ObjectMessage) message).getObject();
            droolsSession.getStatelessKnowledgeSession().execute(factIn.getRoot());

        } catch (ClassCastException ex) {

            ex.printStackTrace();

        } catch (JMSException ex) {

            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}