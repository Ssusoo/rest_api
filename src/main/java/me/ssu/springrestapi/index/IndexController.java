package me.ssu.springrestapi.index;

import me.ssu.springrestapi.events.EventController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

// TODO ResourceSupport(2.1.0.RELEASE) -> RepresentationModel(2.2.5.RELEASE)
@RestController
@RequestMapping(value = "/api")
public class IndexController {

    @GetMapping
    public RepresentationModel index() {

        var index = new RepresentationModel();
        index.add(linkTo(EventController.class).withRel("events"));

        return index;
    }
}