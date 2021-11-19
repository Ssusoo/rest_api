package me.ssu.spring_rest_api.events;

import me.ssu.spring_rest_api.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
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

    // TODO ModelMapper EventDto
    private final ModelMapper modelMapper;

    // TODO Validation
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper,
                           EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    // TODO new ErrorsResource 리팩토링
    private ResponseEntity<ErrorsResource> badRequests(Errors errors) {

        // TODO errors -> new ErrorsResource(errors)
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    @PostMapping
    // TODO Event -> EventDto
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto,
                                      Errors errors) {

        // TODO 입력값 제한하기 (201 -> 400)
        // TODO Bad_Request 응답 받기
        // TODO .build - > .body(errors), JavaBean 준수 객체가 아님.
        // TODO new ErrorsResource(errors) - > 리팩토링 badRequests
        if (errors.hasErrors()) {
            return badRequests(errors);
        }

        // TODO Validation 처리
        // TODO Bad_Request 응답 받기
        // TODO .build - > .body(errors), JavaBean 준수 객체가 아님.
        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
            // TODO 리팩토링 badRequests 재사용
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

        // TODO 저장하기 전에 유료인지 무료인지 여부 업데이트
        event.update();

        // TODO Repository
        // TODO Event -> EventDto
        Event newEvent = this.eventRepository.save(event);

        // TODO ControllerLinkBuilder -> WebMvcLinkBuilder
        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class)
                .slash(newEvent.getId());
        URI createUri = selfLinkBuilder.toUri();

        // TODO Resource -> EntityModel
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class)
                .slash(newEvent.getId())
                .withRel("query-events"));

        // TODO selfLink는 EventResource에 넣어줌.
        // eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-event"));

        // TODO profile 추가
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = eventRepository.findAll(pageable);

        // TODO
        var pagedResources =
                assembler.toResource(page, e -> new EventResource(e));

        // TODO
        // return ResponseEntity.ok(page);
        // TODO
        return ResponseEntity.ok(pagedResources);
    }
}
