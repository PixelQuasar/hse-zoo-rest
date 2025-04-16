package com.zoo.hsezoorest.domain.event;

import java.time.LocalDateTime;

public interface DomainEvent {
    String getEventId();

    LocalDateTime getOccurredOn();

    String getEventType();
}
