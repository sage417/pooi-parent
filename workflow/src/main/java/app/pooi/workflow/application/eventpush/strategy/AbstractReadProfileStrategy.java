package app.pooi.workflow.application.eventpush.strategy;

import app.pooi.workflow.application.eventpush.PushStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractReadProfileStrategy implements PushStrategy {

    protected final ObjectMapper objectMapper = new ObjectMapper();
}
