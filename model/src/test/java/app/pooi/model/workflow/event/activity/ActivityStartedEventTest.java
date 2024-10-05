package app.pooi.model.workflow.event.activity;

import app.pooi.model.workflow.event.ActivityStartedEvent;
import app.pooi.model.workflow.event.EventPayload;
import app.pooi.model.workflow.event.Header;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class ActivityStartedEventTest {

    @Test
    @SneakyThrows
    public void serialize() {
        ObjectMapper objectMapper = new ObjectMapper();
        EventPayload eventPayload = new EventPayload()
                .setHeader(new Header("eventId", "type")).setEvent(new ActivityStartedEvent());
        String jsonStr = objectMapper.writeValueAsString(eventPayload);
    }

}