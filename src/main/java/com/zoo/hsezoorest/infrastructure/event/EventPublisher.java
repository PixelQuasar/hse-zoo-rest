package com.zoo.hsezoorest.infrastructure.event;

import com.zoo.hsezoorest.domain.event.DomainEvent;

public interface EventPublisher {
    void publish(DomainEvent event);
}

