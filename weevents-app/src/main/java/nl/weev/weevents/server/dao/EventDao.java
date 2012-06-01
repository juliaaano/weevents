package nl.weev.weevents.server.dao;

import nl.weev.weevents.server.entity.EventInfo;

public interface EventDao {

    public EventInfo findByKey(String key);

    public void create(EventInfo eventInfo);
    
    public void update(EventInfo eventInfo);

}
