package me.ssu.spring_rest_api.events;


import org.springframework.hateoas.RepresentationModel;
// TODO ResourceSupport ->  RepresentationModel<Event>
public class EventResource extends RepresentationModel {

    private Event event;

    public EventResource(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
