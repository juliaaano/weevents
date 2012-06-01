package nl.weev.weevents.client.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
@SuppressWarnings("serial")
public class PersonDTO implements Serializable {

    private long id;

    private String firstName;

    private String description;

    private List<EventInfoDTO> listEvents;

    public PersonDTO() {
        super();
    }

    public PersonDTO(String description) {
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

    public List<EventInfoDTO> getListEvents() {

        if (listEvents == null) {
            listEvents = new ArrayList<EventInfoDTO>();
        }
        return listEvents;
    }

    public void setListEvents(List<EventInfoDTO> listEvents) {
        this.listEvents = listEvents;
    }

}
