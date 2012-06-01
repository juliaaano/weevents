package nl.weev.knowledge.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventInfo extends Root {

    private static final long serialVersionUID = -7925504763804141398L;

    private String key;

    private String name;

    private Date startDate;

    private List<Person> participants;

    public EventInfo() {
        super();
    }

    public EventInfo(String key) {
        super();
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<Person> getParticipants() {

        if (participants == null) {
            participants = new ArrayList<Person>();
        }

        return participants;
    }

    public void setParticipants(List<Person> participants) {
        this.participants = participants;
    }

}
