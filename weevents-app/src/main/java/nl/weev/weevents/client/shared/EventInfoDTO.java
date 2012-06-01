package nl.weev.weevents.client.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
@SuppressWarnings("serial")
public class EventInfoDTO implements Serializable {

    private String key;

    private String name;

    private Date startDate;

    private List<PersonDTO> participants;

    public EventInfoDTO() {
        super();
    }

    public EventInfoDTO(String key) {
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

    public List<PersonDTO> getParticipants() {

        if (participants == null) {
            participants = new ArrayList<PersonDTO>();
        }

        return participants;
    }

    public void setParticipants(List<PersonDTO> participants) {
        this.participants = participants;
    }

}
