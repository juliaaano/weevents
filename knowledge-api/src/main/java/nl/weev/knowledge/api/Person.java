package nl.weev.knowledge.api;

import java.util.ArrayList;
import java.util.List;

public class Person extends Root {

    private static final long serialVersionUID = -1408268171248730240L;

    private long id;

    private String firstName;

    private String description;

    private List<EventInfo> listEvents;

    public Person() {
        super();
    }

    public Person(String description) {
        super();
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EventInfo> getListEvents() {

        if (listEvents == null) {
            listEvents = new ArrayList<EventInfo>();
        }
        return listEvents;
    }

    public void setListEvents(List<EventInfo> listEvents) {
        this.listEvents = listEvents;
    }

}
