package nl.weev.weevents.server.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import nl.weev.weevents.client.shared.EventInfoDTO;
import nl.weev.weevents.client.shared.PersonDTO;

@Entity
@SuppressWarnings("serial")
public class EventInfo implements Serializable {

    @Id
    private String key;

    private String name;

    private Date startDate;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "EVENT_PERSONS")
    private List<Person> participants;

    public EventInfo() {
        super();
    }

    public EventInfo(String key) {
        super();
        this.key = key;
    }

    public EventInfo(final EventInfoDTO eventInfo) {
        super();
        this.key = eventInfo.getKey();
        this.name = eventInfo.getName();
        this.startDate = eventInfo.getStartDate();

        for (PersonDTO person : eventInfo.getParticipants()) {
            getParticipants().add(new Person(person));
        }
    }

    public EventInfo(final nl.weev.knowledge.api.EventInfo eventInfo) {
        super();
        this.key = eventInfo.getKey();
        this.name = eventInfo.getName();
        this.startDate = eventInfo.getStartDate();

        for (nl.weev.knowledge.api.Person person : eventInfo.getParticipants()) {
            getParticipants().add(new Person(person));
        }
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
