package me.ssu.spring_rest_api.common;

import me.ssu.spring_rest_api.index.IndexController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

// TODO Index로 가는 링크, 에러 리소스
// TODO Resource<Errors>(2.1.0.RELEASE) -> EntityModel<Errors>(2.2.5.RELEASE)
public class ErrorsResource extends Resource<Errors> {
    public ErrorsResource(Errors content, Link... links) {
        super(content, links);
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}


