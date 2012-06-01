package nl.weev.weevents.server.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import nl.weev.weevents.client.shared.PersonDTO;

@Entity
@SuppressWarnings("serial")
public class Person implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    private String firstName;

    private String description;

    @ManyToMany(mappedBy = "participants")
    private List<EventInfo> listEvents;

    public Person() {
        super();
    }

    public Person(String description) {
        super();
        this.description = description;
    }
    
    public Person(final PersonDTO person) {
        super();
        this.id = person.getId();
        this.firstName = person.getFirstName();
        this.description = person.getDescription();
    }

    public Person(final nl.weev.knowledge.api.Person person) {
        super();
        this.id = person.getId();
        this.firstName = person.getFirstName();
        this.description = person.getDescription();
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
