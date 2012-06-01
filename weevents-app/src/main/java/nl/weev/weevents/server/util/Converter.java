package nl.weev.weevents.server.util;

import nl.weev.knowledge.api.FactIn;
import nl.weev.weevents.client.shared.EventInfoDTO;
import nl.weev.weevents.server.entity.EventInfo;
import nl.weev.weevents.server.entity.Person;

public class Converter {

    public static EventInfoDTO buildEventInfoDTO(final EventInfo eventInfoEntity) {

        EventInfoDTO evenInfo = new EventInfoDTO();

        evenInfo.setKey(eventInfoEntity.getKey());
        evenInfo.setName(eventInfoEntity.getName());
        evenInfo.setStartDate(eventInfoEntity.getStartDate());

        for (Person personEntity : eventInfoEntity.getParticipants()) {

            nl.weev.weevents.client.shared.PersonDTO person = new nl.weev.weevents.client.shared.PersonDTO();

            person.setFirstName(personEntity.getFirstName());
            person.setDescription(personEntity.getDescription());

            evenInfo.getParticipants().add(person);
        }

        return evenInfo;
    }

    public static FactIn buildFactIn(final EventInfo eventInfo) {

        nl.weev.knowledge.api.EventInfo eventInfoApi = new nl.weev.knowledge.api.EventInfo();

        eventInfoApi.setKey(eventInfo.getKey());
        eventInfoApi.setName(eventInfo.getName());
        eventInfoApi.setStartDate(eventInfo.getStartDate());

        for (Person person : eventInfo.getParticipants()) {
            nl.weev.knowledge.api.Person personApi = new nl.weev.knowledge.api.Person();
            personApi.setDescription(person.getDescription());
            personApi.setFirstName(person.getFirstName());
            personApi.setId(person.getId());
            eventInfoApi.getParticipants().add(personApi);
        }

        return new FactIn(eventInfoApi);
    }

}
