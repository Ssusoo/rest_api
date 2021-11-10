package me.ssu.spring_rest_api.events;

import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    void  builder() {
        Event event = Event.builder()
                .name("Inflearn Spring Rest API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    void javaBean() {
        // Given
        String description = "Spring";
        String name = "Event";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}







