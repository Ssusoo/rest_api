package me.ssu.spring_rest_api.index;

import me.ssu.spring_rest_api.events.EventController;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

// TODO ResourceSupport(2.1.0.RELEASE) -> RepresentationModel(2.2.5.RELEASE)
@RestController
@RequestMapping(value = "/api")
public class IndexController {

    @GetMapping
    public ResourceSupport index() {

        var index = new ResourceSupport();
        index.add(linkTo(EventController.class).withRel("events"));

        return index;
    }
}