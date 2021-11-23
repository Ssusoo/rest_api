package me.ssu.spring_rest_api.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

// TODO ResourceSupport ->  RepresentationModel<EventResource>
// TODO Resource
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