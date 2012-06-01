package nl.weev.weevents.server.dao;

import javax.enterprise.inject.Alternative;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import nl.weev.weevents.server.entity.EventInfo;

@Alternative
public class EventDaoImpl implements EventDao {

    private static EntityManager entityManager;

    public EventDaoImpl() {
        super();
        loadEntityManager("weevents");
    }

    public EventDaoImpl(String persistenceUnit) {
        super();
        loadEntityManager(persistenceUnit);
    }

    protected void loadEntityManager(String persistenceUnit) {

        if (entityManager == null || !entityManager.isOpen()) {

            EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit);
            entityManager = emf.createEntityManager();
        }
    }

    protected void closeResources() {

        entityManager.close();
        entityManager.getEntityManagerFactory().close();
    }

    @Override
    public EventInfo findByKey(String key) {

        return entityManager.find(EventInfo.class, key);
    }

    @Override
    public void create(EventInfo eventInfo) {

        entityManager.persist(eventInfo);
    }

    @Override
    public void update(EventInfo eventInfo) {

        entityManager.merge(eventInfo);
    }

}
