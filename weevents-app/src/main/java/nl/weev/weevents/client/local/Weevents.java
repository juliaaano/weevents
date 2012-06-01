package nl.weev.weevents.client.local;

import javax.inject.Inject;

import nl.weev.weevents.client.shared.EventCreationRequest;
import nl.weev.weevents.client.shared.EventInfoDTO;
import nl.weev.weevents.client.shared.EventNotFoundException;
import nl.weev.weevents.client.shared.EventService;

import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.bus.client.api.base.DefaultErrorCallback;
import org.jboss.errai.bus.client.api.base.TransportIOException;
import org.jboss.errai.bus.client.framework.MessageBus;
import org.jboss.errai.common.client.protocols.MessageParts;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Main application entry point.
 */
@EntryPoint
public class Weevents {

    @Inject
    private Caller<EventService> eventService;

    @Inject
    private EventPanel eventPanel;

    private final MessageBus messageBus = ErraiBus.get();

    private final Button btnCreateEvent = new Button("Criar Novo Evento");

    private final Button btnClose = new Button();

    private final FlowPanel errorPanel = new FlowPanel();

    /**
     * @wbp.parser.entryPoint
     */
    @AfterInitialization
    public void buildUI() {

        // eventPanel = new EventPanel();
        eventPanel.setStyleName("wvts-EventPanel");
        errorPanel.setStyleName("wvts-ErrorPanel");

        btnCreateEvent.addStyleName("wvts-Button-createEvent");
        btnCreateEvent.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                createNewEvent();
            }
        });

        btnClose.setStylePrimaryName("wvts-Button-close");
        btnClose.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                renderWelcomScreen();
                History.newItem("");
            }
        });

        FlowPanel backgroundPanel = new FlowPanel();
        backgroundPanel.addStyleName("wvts-BackgroundPanel");

        RootPanel.get().add(backgroundPanel);

        FlowPanel mainPanel = new FlowPanel();
        mainPanel.addStyleName("wvts-MainPanel");

        backgroundPanel.add(mainPanel);

        FlowPanel eventHeader = new FlowPanel();
        eventHeader.addStyleName("wvts-EventHeader");

        eventHeader.add(btnCreateEvent);
        eventHeader.add(btnClose);

        mainPanel.add(eventHeader);
        mainPanel.add(eventPanel);
        mainPanel.add(errorPanel);

        setupDefaultBusErrorHandler();

        renderWelcomScreen();

        History.addValueChangeHandler(new HistoryHandler());
        History.fireCurrentHistoryState();
    }

    private void setupDefaultBusErrorHandler() {

        messageBus.subscribe(DefaultErrorCallback.CLIENT_ERROR_SUBJECT, new MessageCallback() {

            @Override
            public void callback(Message message) {

                Throwable caught = message.get(Throwable.class, MessageParts.Throwable);

                // Don't do anything here, other exceptions are already handled by the client.

                try {
                    throw caught;
                } catch (TransportIOException e) {
                    // Thrown in case the server can't be reached or an unexpected status code was returned.
                    GWT.log("Handling Default Message Bus Error...", caught);
                    displayErrorMessage(e.getMessage());
                } catch (Throwable throwable) {
                    // Handle system errors (e.g response marshalling errors) - that of course should never happen.
                    GWT.log("Handling Default Message Bus Error...", caught);
                }
            }
        });
    }

    private void createNewEvent() {

        eventService.call(new RemoteCallback<EventInfoDTO>() {
            @Override
            public void callback(EventInfoDTO response) {

                GWT.log("Event created with key: " + response.getKey());

                eventPanel.subscribeToBus(response.getKey());
                eventPanel.buildEventPanel(response);

                History.newItem("event=" + response.getKey(), false);

                renderNewEventScreen();
            }
        }, new ErrorCallback() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                GWT.log("Error calling remote service. | ", throwable);
                return true;
            }
        }).createNewEvent(new EventCreationRequest());
    }

    private void renderWelcomScreen() {

        btnCreateEvent.setEnabled(true);
        btnCreateEvent.removeStyleName("wvts-Button-createEvent-disabled");

        btnClose.setEnabled(false);
        btnClose.addStyleName("wvts-Button-close-disabled");

        eventPanel.addStyleDependentName("disabled");
        errorPanel.removeStyleDependentName("visible");
    }

    private void renderNewEventScreen() {

        btnCreateEvent.setEnabled(false);
        btnCreateEvent.addStyleName("wvts-Button-createEvent-disabled");
        btnClose.setEnabled(true);
        btnClose.removeStyleName("wvts-Button-close-disabled");
        eventPanel.removeStyleDependentName("disabled");
    }

    private void displayErrorMessage(String message) {

        btnCreateEvent.setEnabled(false);
        btnCreateEvent.addStyleName("wvts-Button-createEvent-disabled");

        btnClose.setEnabled(true);
        btnClose.removeStyleName("wvts-Button-close-disabled");

        eventPanel.addStyleDependentName("disabled");

        Label messageLabel = new Label();
        messageLabel.addStyleName("wvts-Label-errorMessage");
        messageLabel.setText(message);

        errorPanel.add(messageLabel);
        errorPanel.addStyleDependentName("visible");
    }

    private class HistoryHandler implements ValueChangeHandler<String> {

        @Override
        public void onValueChange(ValueChangeEvent<String> event) {

            String historyToken = event.getValue();

            if (!historyToken.isEmpty() && historyToken.length() > 6) {
                try {
                    if (historyToken.substring(0, 6).equals("event=")) {
                        String eventToken = historyToken.substring(6);
                        recoverEventState(eventToken);
                    } else {
                        displayErrorMessage("Token inválido - deve conter o prefixo 'event='");
                    }
                } catch (IndexOutOfBoundsException e) {
                    displayErrorMessage("Token inválido.");
                }
            }
        }

        private EventInfoDTO recoverEventState(String eventKey) {

            EventInfoDTO eventInfo = null;

            eventService.call(new RemoteCallback<EventInfoDTO>() {
                @Override
                public void callback(EventInfoDTO response) {
                    GWT.log("Got event information for key: " + response.getKey());
                    eventPanel.subscribeToBus(response.getKey());
                    eventPanel.buildEventPanel(response);
                    renderNewEventScreen();
                }
            }, new ErrorCallback() {
                @Override
                public boolean error(Message message, Throwable throwable) {
                    if (throwable instanceof EventNotFoundException) {
                        String eventKey = ((EventNotFoundException) throwable).getEventKey();
                        displayErrorMessage("Evento não encontrado para key: " + eventKey);
                    } else {
                        displayErrorMessage(throwable.getMessage());
                    }
                    return false;
                }
            }).retrieveEventInfo(eventKey);

            return eventInfo;
        }
    }

}