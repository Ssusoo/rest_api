package me.ssu.spring_rest_api.common;

import me.ssu.spring_rest_api.index.IndexController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

// TODO 인덱스 핸들러 리소스
// TODO Resource<Errors>(2.1.0.RELEASE) -> EntityModel<Errors>(2.2.5.RELEASE)
public class ErrorsResource extends Resource<Errors> {
    public ErrorsResource(Errors content, Link... links) {
        super(content, links);
        // TODO 인덱스 핸들러 리소스(_links.index)
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}