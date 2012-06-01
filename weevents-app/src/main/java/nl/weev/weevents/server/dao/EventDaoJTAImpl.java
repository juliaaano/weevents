package nl.weev.weevents.server.dao;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import nl.weev.weevents.server.entity.EventInfo;

@Default
public class EventDaoJTAImpl implements EventDao {

    @Inject
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;

    public EventDaoJTAImpl() {
        super();
    }

    @Override
    public EventInfo findByKey(String key) {

        return entityManager.find(EventInfo.class, key);
    }

    @Override
    public void create(EventInfo eventInfo) {

        beginTransaction();
        entityManager.persist(eventInfo);
        commitTransaction();
    }

    @Override
    public void update(EventInfo eventInfo) {

        beginTransaction();
        entityManager.merge(eventInfo);
        commitTransaction();
    }

    protected void beginTransaction() {

        try {
            userTransaction.begin();
        } catch (Exception e) {
            throw new RuntimeException("Failed to begin transaction: " + e.getMessage());
        }
    }

    protected void commitTransaction() {

        try {
            userTransaction.commit();
        } catch (Exception e) {
            rollbackTransaction();
            throw new RuntimeException("Failed to commit transaction: " + e.getMessage());
        }
    }

    protected void rollbackTransaction() {
        try {
            userTransaction.rollback();
        } catch (SystemException e) {
            throw new RuntimeException("Failed to rollback transaction: " + e.getMessage());
        }
    }

}
