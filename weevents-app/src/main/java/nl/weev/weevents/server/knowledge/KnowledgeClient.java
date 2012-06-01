package nl.weev.weevents.server.knowledge;

import nl.weev.knowledge.api.FactIn;

public interface KnowledgeClient {

    public void sendFactToQueue(FactIn factIn) throws Exception;

}
