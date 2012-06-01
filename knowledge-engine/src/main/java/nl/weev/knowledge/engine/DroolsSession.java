package nl.weev.knowledge.engine;

import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;

public class DroolsSession {

    private Boolean isTest;

    private static KnowledgeAgent knowledgeAgent;

    private static StatefulKnowledgeSession statefulKnowledgeSession;

    public DroolsSession() {

        super();
        this.isTest = Boolean.FALSE;
        createKnowledgeBase();
    }

    public DroolsSession(Boolean isTest) {

        super();
        this.isTest = isTest;
        createKnowledgeBase();
    }

    protected void createKnowledgeBase() {

        if (knowledgeAgent == null) {

            knowledgeAgent = KnowledgeAgentFactory.newKnowledgeAgent("mainKnowledgeAgent");
            knowledgeAgent.applyChangeSet(ResourceFactory.newClassPathResource("nl/weev/knowledge/changeset.xml"));
            ResourceFactory.getResourceChangeNotifierService().start();
            ResourceFactory.getResourceChangeScannerService().start();
        }
    }

    public StatefulKnowledgeSession getStatefulKnowledgeSession() {

        if (statefulKnowledgeSession == null) {
            statefulKnowledgeSession = knowledgeAgent.getKnowledgeBase().newStatefulKnowledgeSession();
            statefulKnowledgeSession.setGlobal("isTestRunning", isTest);
        }

        return statefulKnowledgeSession;
    }

    public StatelessKnowledgeSession getStatelessKnowledgeSession() {

        StatelessKnowledgeSession statelessKnowledgeSession = knowledgeAgent.newStatelessKnowledgeSession();
        statelessKnowledgeSession.setGlobal("isTestRunning", isTest);

        return statelessKnowledgeSession;
    }
}
