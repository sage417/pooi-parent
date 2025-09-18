package app.pooi.workflow.application.eventpush.strategy;

import app.pooi.basic.rest.CommonResult;
import app.pooi.basic.workflow.event.EventPayload;
import app.pooi.workflow.application.eventpush.PushStrategy;
import app.pooi.workflow.domain.model.workflow.eventpush.EventPushProfile;
import app.pooi.workflow.infrastructure.persistence.entity.workflow.eventpush.EventRecordEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
class HttpPushStrategy extends AbstractReadProfileStrategy implements PushStrategy {


    private final RestTemplate restTemplate;

    public HttpPushStrategy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SneakyThrows
    @Override
    public void push(EventPushProfile eventPushProfile, EventRecordEntity eventRecordDO) {
        EventPayload eventPayload = objectMapper.readValue(eventRecordDO.getEvent(), EventPayload.class);
        restTemplate.postForEntity("http://localhost:8080/mock/event", eventPayload, CommonResult.class);
    }

    record HttpProfile(String method, String url) {
    }
}
