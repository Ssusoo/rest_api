package me.ssu.spring_rest_api.events;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

// TODO Resource<Event>(2.1.0.RELEASE) -> EntityModel<Event>(2.2.5.RELEASE)
public class EventResource extends Resource<Event> {

    public EventResource(Event event, Link... links) {
        super(event, links);
        // TODO self 링크 추가
        // add(new Link("http://localhost:8080/api/events/"+ event.getId()));
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }


    //    @JsonUnwrapped
//    private Event event;
//
//    public EventResource(Event event) {
//        this.event = event;
//    }
//
//    public Event getEvent() {
//        return event;
//    }
}