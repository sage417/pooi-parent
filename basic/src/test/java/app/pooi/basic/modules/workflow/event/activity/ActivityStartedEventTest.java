package app.pooi.basic.modules.workflow.event.activity;

import app.pooi.basic.workflow.event.ActivityStartedEvent;
import app.pooi.basic.workflow.event.EventPayload;
import app.pooi.basic.workflow.event.Header;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Test;

public class ActivityStartedEventTest {

    @Test
    @SneakyThrows
    public void serialize() {
        ObjectMapper objectMapper = new ObjectMapper();
        EventPayload eventPayload = new EventPayload()
                .setHeader(new Header("eventId", "type")).setEvent(new ActivityStartedEvent());
        String jsonStr = objectMapper.writeValueAsString(eventPayload);
    }

}