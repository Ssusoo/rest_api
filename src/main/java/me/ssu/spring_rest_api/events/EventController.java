package me.ssu.spring_rest_api.events;

import me.ssu.spring_rest_api.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    // TODO Repository 등록
    private final EventRepository eventRepository;

    // TODO Model Mapper 입력값 제한하기
    private final ModelMapper modelMapper;

    // TODO 특정한 값 Bad_Request 처리
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper,
                           EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    // TODO new ErrorsResource 리팩토링
    private ResponseEntity<ErrorsResource> badRequests(Errors errors) {
        // TODO 에러를 그냥 error로 던지는 게 아니라 ErrorsResource로 바꿔서 던짐.
        // TODO errors -> new ErrorsResource(errors)
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    @PostMapping
    // TODO Event -> EventDto
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto,
                                      Errors errors) {

        // TODO 일반적인 Bad_Request 처리
        // TODO .build - > .body(errors), JavaBean 준수 객체가 아님.
        if (errors.hasErrors()) {
            // return ResponseEntity.badRequest().body(errors);
            return badRequests(errors);
        }

        // TODO 특정한 값 Bad_Request 처리
        // TODO .build - > .body(errors), JavaBean 준수 객체가 아님.
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            // return ResponseEntity.badRequest().body(errors);
            return badRequests(errors);
        }

        // TODO 입력값 제한하기 Dto
        // TODO Event Dto에 있는 것을 Event 타입의 인스턴스로 만들어 달라
        Event event = modelMapper.map(eventDto, Event.class);

        // TODO ModelMapper 사용 전, 코드
//        Event event = Event.builder()
//                .name(eventDto.getName())
//                .build();

        // TODO 저장하기 전에 유료인지 무료인지 여부 업데이트(비즈니스 로직 적용)
        event.update();

        // TODO Repository
        // TODO Event -> EventDto
        Event newEvent = this.eventRepository.save(event);

        // TODO HATEOAS 적용-1
        // TODO ControllerLinkBuilder(2.1.0.RELEASE) -> WebMvcLinkBuilder(2.2.5.RELEASE)
        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class)
                .slash(newEvent.getId());
        URI createUri = selfLinkBuilder
                .toUri();

        // TODO HATEOAS 적용-2
        EventResource eventResource = new EventResource(event);
        // TODO _links.self, EventResource로 이동
//        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));

        // TODO REST Docs(profile) 추가
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createUri).body(eventResource);
    }

    // TODO Event 목록 조회 API
    @GetMapping
    public ResponseEntity queryEvent(Pageable pageable,
                                     PagedResourcesAssembler<Event> assembler) {
        // TODO 리소스로 변경하기 전(Event Data)
        Page<Event> page = eventRepository.findAll(pageable);

        // TODO Repository에서 받아온 Event를 리소스로 변경하기 위해 PagedResourcesAssembler<T> 사용함.
        // TODO PagedResources<Resource<Event>>(2.1.0.RELEASE) -> PagedModel<EntityModel<Event>>(2.2.5.RELEASE)
        // TODO toResource(2.1.0.RELEASE) -> toModel(2.2.5.RELEASE)
//        PagedResources<Resource<Event>> pagedResources = assembler.toResource(page);

        // TODO 리소스(toResource(page) -> 각각의 리소스로 변경(toResource(page, e -> new EventResource(e))
        var pagedResources = assembler
                .toResource(page, e -> new EventResource(e));

        // TODO profile 추가
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));

        return ResponseEntity.ok(pagedResources);
    }
}




