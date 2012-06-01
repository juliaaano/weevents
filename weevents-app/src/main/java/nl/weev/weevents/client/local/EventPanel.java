package nl.weev.weevents.client.local;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import nl.weev.weevents.client.shared.EventInfoDTO;
import nl.weev.weevents.client.shared.EventInfoRequest;
import nl.weev.weevents.client.shared.EventService;
import nl.weev.weevents.client.shared.PersonDTO;

import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.bus.client.framework.MessageBus;
import org.jboss.errai.ioc.client.api.Caller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

@ApplicationScoped
public class EventPanel extends FlowPanel {

    @Inject
    private Caller<EventService> eventService;

    private final MessageBus messageBus = ErraiBus.get();

    private String eventKey;

    private final TextBox nameInput = new TextBox();
    private final TextArea participantsInput = new TextArea();
    private final DateBox startDateInput = new DateBox();

    /**
     * @wbp.parser.constructor
     */
    public EventPanel() {

        super();

        setStyleName("wvts-EventPanel");

        Label nameLabel = new Label("Nome do Evento");
        nameLabel.addStyleName("wvts-Label-eventName");
        add(nameLabel);
        Label startDateLabel = new Label("Data");
        startDateLabel.addStyleName("wvts-Label-eventStartDate");
        add(startDateLabel);
        Label participantsLabel = new Label("Participantes");
        participantsLabel.addStyleName("wvts-Label-eventParticipants");
        add(participantsLabel);

        nameInput.addStyleName("wvts-TextBox-eventName");
        startDateInput.addStyleName("wvts-DateBox-eventStartDate");
        participantsInput.addStyleName("wvts-TextArea-eventParticipants");

        nameInput.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                fireOnChange();
            }
        });
        startDateInput.addValueChangeHandler(new ValueChangeHandler<Date>() {
            public void onValueChange(ValueChangeEvent<Date> event) {
                fireOnChange();
            }
        });
        participantsInput.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                fireOnChange();
            }
        });

        startDateInput.setFormat(new DefaultFormat(DateTimeFormat.getFormat("dd-MM-yyyy")));

        add(nameInput);
        add(startDateInput);
        add(participantsInput);
    }

    private void fireOnChange() {

        final EventInfoDTO eventInfoDTO = buildEventInfoObject();

        eventService.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                GWT.log("Event update request sent for event key: " + eventInfoDTO.getKey());
            }
        }, new ErrorCallback() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                GWT.log("Error at remote call to update event.", throwable);
                return false;
            }
        }).updateEvent(new EventInfoRequest(eventInfoDTO));
    }

    private EventInfoDTO buildEventInfoObject() {

        EventInfoDTO eventInfo = new EventInfoDTO(eventKey);

        eventInfo.setName(nameInput.getText().trim());
        eventInfo.setStartDate(startDateInput.getValue());
        eventInfo.setParticipants(parseParticipantsToPersonList(participantsInput.getText()));

        return eventInfo;
    }

    private String parseParticipantsToText(List<PersonDTO> participants) {

        StringBuilder participantsText = new StringBuilder();

        for (int i = 0; i < participants.size(); i++) {

            PersonDTO person = participants.get(i);

            if (i > 0) {
                participantsText.append(" ");
            }

            if (person.getFirstName() != null) {
                participantsText.append(person.getFirstName());
            } else {
                participantsText.append(person.getDescription());
            }
            participantsText.append(";");
        }

        return participantsText.toString();
    }

    private List<PersonDTO> parseParticipantsToPersonList(String participants) {

        List<PersonDTO> listPerson = new ArrayList<PersonDTO>();

        participants = participants.trim();

        if (!participants.isEmpty()) {
            String[] participantsArray = participants.split(";");
            for (String participantDescprition : participantsArray) {
                participantDescprition = participantDescprition.trim();
                if (!participantDescprition.isEmpty()) {
                    listPerson.add(new PersonDTO(participantDescprition));
                }
            }
        }

        return listPerson;
    }

    private void feedEventInfoScreen(final EventInfoDTO eventInfo) {

        nameInput.setText(eventInfo.getName());
        startDateInput.setValue(eventInfo.getStartDate());
        participantsInput.setText("");
        participantsInput.setText(parseParticipantsToText(eventInfo.getParticipants()));
    }

    public void subscribeToBus(String eventKey) {

        messageBus.subscribe(eventKey, new MessageCallback() {

            public void callback(Message message) {

                EventInfoDTO eventInfoDTO = message.get(EventInfoDTO.class, "eventInfo");
                feedEventInfoScreen(eventInfoDTO);
            }
        });
    }

    public void buildEventPanel(final EventInfoDTO eventInfo) {

        this.eventKey = eventInfo.getKey();
        feedEventInfoScreen(eventInfo);
    }

}
