package com.zoo.hsezoorest.infrastructure.event;

import com.zoo.hsezoorest.domain.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        log.info("Publishing domain event: {} ({})", event.getEventType(), event.getEventId());
        applicationEventPublisher.publishEvent(event);
    }
}
